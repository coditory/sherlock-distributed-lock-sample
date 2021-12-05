package com.coditory.sandbox.sherlock.postgres;

import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.SherlockMigrator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.util.Properties;

import static com.coditory.sherlock.SqlSherlockBuilder.sqlSherlock;

public class PostgresMigration {
    public static void main(String[] args) throws SQLException {
        Sherlock sherlock = initSherlock();

        SherlockMigrator migrator = new SherlockMigrator("db-migration", sherlock)
            .addChangeSet("change set 1", () -> System.out.println(">>> Change set 1"))
            .addChangeSet("change set 2", () -> System.out.println(">>> Change set 2"))
            .addChangeSet("change set 3", () -> System.out.println(">>> Change set 3"));

        System.out.println(">>> Starting migration");
        migrator.migrate();
        System.out.println(">>> Migration finished");
    }

    private static Sherlock initSherlock() throws SQLException {
        return sqlSherlock()
            .withClock(Clock.systemDefaultZone())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withConnection(connect())
            .withLocksTable("LOCKS")
            .build();
    }

    private static Connection connect() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", "postgres");
        connectionProps.put("password", "postgres");
        return DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/test", connectionProps);
    }
}
