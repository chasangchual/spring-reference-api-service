package com.surefor.service.cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class CleanupSteps {
    @Autowired
    private DataSource dataSource;

    @Before
    public void beforeScenario() {
        cleanUpTables();
    }

    @After
    public void afterScenario() {
        cleanUpTables();
    }

    @Transactional
    void cleanUpTables() {
        log.info("clean up tables");
        try {
            Connection connection = dataSource.getConnection();
            // delete entity tables
            connection.createStatement().executeUpdate("TRUNCATE tms_user CASCADE");

            // delete audit tables
            connection.createStatement().executeUpdate("DELETE FROM tms_user_aud");
            connection.createStatement().executeUpdate("DELETE FROM user_detail_aud");
            connection.createStatement().executeUpdate("DELETE FROM user_role_aud");
            connection.createStatement().executeUpdate("DELETE FROM user_email_aud");
            connection.createStatement().executeUpdate("DELETE FROM user_phone_aud");
            connection.createStatement().executeUpdate("DELETE FROM user_address_aud");

            connection.createStatement().executeUpdate("DELETE FROM revinfo");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
