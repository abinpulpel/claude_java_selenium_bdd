package com.claude.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Entry point TestNG uses to discover and execute Cucumber scenarios.
 * Extending {@link AbstractTestNGCucumberTests} and overriding
 * {@code scenarios()} with {@code @DataProvider(parallel = true)} lets
 * TestNG run scenarios concurrently across the thread pool sized by
 * {@code testng.xml}'s {@code data-provider-thread-count}.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.claude.bdd.stepdefinitions", "com.claude.bdd.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-html-report.html",
                "json:target/cucumber-reports/cucumber.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true,
        tags = "not @ignore"
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
