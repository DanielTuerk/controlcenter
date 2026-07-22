package io.github.danieltuerk.controlcenter.it;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Map;

public class ITDatabaseTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.datasource.jdbc.url", "jdbc:h2:mem:it-test;DB_CLOSE_DELAY=-1",
            "quarkus.datasource.username", "it-test",
            "quarkus.datasource.password", "it-test"
        );
    }
}
