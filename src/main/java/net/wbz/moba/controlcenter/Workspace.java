package net.wbz.moba.controlcenter;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class Workspace {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String BUS_DUMP_PREFIX = "bus";

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

    public String createBusDump(BusDump busDump) {
        Path jsonFile = dumpFolder.resolve(
                ("%s_%s.json").formatted(BUS_DUMP_PREFIX, LocalDateTime.now().format(DATE_TIME_FORMATTER)));

        try {
            objectMapper.writeValue(jsonFile.toFile(), busDump);
            return jsonFile.getFileName().toString();
        } catch (IOException e) {
            log.error("Could not write bus dump file: {}", jsonFile, e);
            throw new RuntimeException(e);
        }
    }

    public List<String> listBusDumps() {
        try (Stream<Path> paths = Files.list(dumpFolder)) {
            return paths
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.startsWith(BUS_DUMP_PREFIX) && name.endsWith(".json"))
                    .sorted(Comparator.reverseOrder())
                    .toList();
        } catch (IOException e) {
            log.error("Could not list bus dump files in: {}", dumpFolder, e);
            throw new RuntimeException(e);
        }
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

    public record BusDump(byte[] bus0Data, byte[] bus1Data) {
    }

}
