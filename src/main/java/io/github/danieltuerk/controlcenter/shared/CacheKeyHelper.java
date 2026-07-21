package io.github.danieltuerk.controlcenter.shared;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CacheKeyHelper {

    public static String createCacheKey(Class<?> clazz, long itemId, Object... additionalFields) {
        final var formatted = "%s:%d".formatted(clazz.getName(), itemId);
        if (additionalFields == null) {
            return formatted;
        }
        return "%s-%s".formatted(formatted, Arrays.stream(additionalFields)
                .map(Object::toString)
                .collect(Collectors.joining("-")));
    }
}
