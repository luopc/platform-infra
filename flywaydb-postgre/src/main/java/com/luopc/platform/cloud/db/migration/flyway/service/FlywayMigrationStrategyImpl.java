package com.luopc.platform.cloud.db.migration.flyway.service;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;

/**
 * @author Robin
 */
@Component
@Slf4j
public class FlywayMigrationStrategyImpl implements FlywayMigrationStrategy {
    @Override
    public void migrate(Flyway flyway) {
        flyway.migrate();
    }

}
