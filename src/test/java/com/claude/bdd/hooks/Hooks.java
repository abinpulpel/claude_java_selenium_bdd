package com.claude.bdd.hooks;

import com.claude.framework.config.ConfigManager;
import com.claude.framework.driver.DriverFactory;
import com.claude.framework.driver.DriverManager;
import com.claude.framework.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Cucumber lifecycle hooks wiring driver setup/teardown, logging, and
 * failure screenshots around every scenario. Registered automatically via
 * the {@code glue} package on {@code TestRunner} so no step definition class
 * needs to know driver management exists (Facade pattern: a simple
 * lifecycle hook fronting driver, logging, and reporting subsystems).
 */
public class Hooks {

    private static final Logger LOGGER = LogManager.getLogger(Hooks.class);

    @Before
    public void setUp(Scenario scenario) {
        LOGGER.info("Starting scenario: {}", scenario.getName());
        DriverManager.setDriver(DriverFactory.createDriver());
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            attachScreenshot(scenario);
        }
        LOGGER.info("Finished scenario: {} - {}", scenario.getName(), scenario.getStatus());
        DriverManager.quitDriver();
    }

    private void attachScreenshot(Scenario scenario) {
        try {
            String screenshotDir = ConfigManager.getInstance().get("screenshot.dir", "target/screenshots");
            String path = ScreenshotUtils.capture(DriverManager.getDriver(), scenario.getName(), screenshotDir);
            if (path != null) {
                byte[] bytes = Files.readAllBytes(Path.of(path));
                scenario.attach(bytes, "image/png", scenario.getName());
            }
        } catch (IOException | IllegalStateException e) {
            LOGGER.warn("Failed to attach failure screenshot: {}", e.getMessage());
        }
    }
}
