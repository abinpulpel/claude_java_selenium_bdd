package com.claude.bdd.stepdefinitions;

import com.claude.bdd.context.TestContext;
import com.claude.framework.config.ConfigManager;
import com.claude.framework.driver.DriverManager;
import com.claude.framework.pages.HomePage;
import com.claude.framework.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

/**
 * Step definitions for the login feature. Constructor-injected with the
 * scenario-scoped {@link TestContext} by Cucumber-PicoContainer; never
 * touches {@code WebDriver} or vendor Selenium classes directly, only page
 * objects obtained through {@link TestContext#getPageObjectManager()}.
 */
public class LoginSteps {

    private final TestContext testContext;

    public LoginSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        String baseUrl = ConfigManager.getInstance().get("base.url");
        DriverManager.getDriver().get(baseUrl);
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsInWithUsernameAndPassword(String username, String password) {
        LoginPage loginPage = testContext.getPageObjectManager().getLoginPage();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
    }

    @Then("the user should see the home page welcome banner")
    public void theUserShouldSeeTheHomePageWelcomeBanner() {
        HomePage homePage = testContext.getPageObjectManager().getHomePage();
        Assert.assertTrue(homePage.isWelcomeBannerDisplayed(), "Welcome banner should be visible after a successful login");
    }

    @Then("an error message should be displayed")
    public void anErrorMessageShouldBeDisplayed() {
        LoginPage loginPage = testContext.getPageObjectManager().getLoginPage();
        Assert.assertTrue(loginPage.isErrorDisplayed(), "An error message should be displayed for invalid credentials");
    }

    @Then("the login result should be {string}")
    public void theLoginResultShouldBe(String result) {
        if ("success".equals(result)) {
            theUserShouldSeeTheHomePageWelcomeBanner();
        } else {
            anErrorMessageShouldBeDisplayed();
        }
    }
}
