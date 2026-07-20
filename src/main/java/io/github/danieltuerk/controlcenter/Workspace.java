package io.github.danieltuerk.controlcenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.danieltuerk.controlcenter.shared.StateEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class Workspace {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String BUS_DUMP_PREFIX = "bus";
    private static final String EVENT_DUMP_PREFIX = "event";

    public record BusDump(byte[] bus0Data, byte[] bus1Data) {
    }

    public record EventDump(List<DumpEntry> entries) {
        public record DumpEntry(String clazzName, Map<String, StateEvent> events) {
        }
    }

    private final Path dumpFolder;

    private final ObjectMapper objectMapper;

    public Workspace(@ConfigProperty(name = "home.dir") Path homeDir, ObjectMapper objectMapper) {
        dumpFolder = Path.of(homeDir + "/dump");
        this.objectMapper = objectMapper;
    }

    void onStart(@Observes StartupEvent event) {
        createPath(dumpFolder);
    }

    private void createPath(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create home directory: " + path, e);
        }
    }

    private String createDump(String prefix, Object dump) {
        Path jsonFile = dumpFolder.resolve(
                ("%s_%s.json").formatted(prefix, LocalDateTime.now().format(DATE_TIME_FORMATTER)));

        try {
            objectMapper.writeValue(jsonFile.toFile(), dump);
            return jsonFile.getFileName().toString();
        } catch (IOException e) {
            log.error("Could not write dump file: {}", jsonFile, e);
            throw new RuntimeException(e);
        }
    }

    private List<String> listDumps(String prefix) {
        try (Stream<Path> paths = Files.list(dumpFolder)) {
            return paths
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.startsWith(prefix) && name.endsWith(".json"))
                    .sorted(Comparator.reverseOrder())
                    .toList();
        } catch (IOException e) {
            log.error("Could not list dump files with prefix '{}' in: {}", prefix, dumpFolder, e);
            throw new RuntimeException(e);
        }
    }

    public String createBusDump(BusDump busDump) {
        return createDump(BUS_DUMP_PREFIX, busDump);
    }

    public List<String> listBusDumps() {
        return listDumps(BUS_DUMP_PREFIX);
    }

    public BusDump loadBusDump(String busDumpFile) {
        Path jsonFile = dumpFolder.resolve(busDumpFile);
        try {
            return objectMapper.readValue(jsonFile.toFile(), BusDump.class);
        } catch (IOException e) {
            log.error("Could not read bus dump file: {}", jsonFile, e);
            throw new RuntimeException(e);
        }
    }

    public String createEventDump(Map<String, Map<String, StateEvent>> events) {
        return createDump(EVENT_DUMP_PREFIX, new EventDump(events.entrySet().stream()
                .map(x -> new EventDump.DumpEntry(x.getKey(), x.getValue())).toList()));
    }

    public List<String> listEventDumps() {
        return listDumps(EVENT_DUMP_PREFIX);
    }

    private record RawDumpEntry(String clazzName, Map<String, Object> events) {
    }

    private record RawEventDump(List<RawDumpEntry> entries) {
    }

    public EventDump loadEventDump(String eventDumpFile) {
        Path jsonFile = dumpFolder.resolve(eventDumpFile);
        try {
            RawEventDump rawEventDump = objectMapper.readValue(jsonFile.toFile(), RawEventDump.class);

            List<EventDump.DumpEntry> entries = new ArrayList<>();
            for (RawDumpEntry rawEntry : rawEventDump.entries()) {
                Class<?> eventClass = Class.forName(rawEntry.clazzName());
                Map<String, StateEvent> events = new LinkedHashMap<>();
                for (Map.Entry<String, Object> rawEvent : rawEntry.events().entrySet()) {
                    events.put(rawEvent.getKey(), (StateEvent) objectMapper.convertValue(rawEvent.getValue(), eventClass));
                }
                entries.add(new EventDump.DumpEntry(rawEntry.clazzName(), events));
            }
            return new EventDump(entries);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Could not read event dump file: {}", jsonFile, e);
            throw new RuntimeException(e);
        }
    }

}
