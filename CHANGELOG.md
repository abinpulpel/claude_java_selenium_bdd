# Changelog

All notable changes to this project are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-07-10

### Added
- Initial enterprise-grade BDD framework scaffold: Maven build (`pom.xml`) targeting Java 21.
- Cucumber 7 + TestNG runner (`TestRunner`) with parallel scenario execution via `@DataProvider(parallel = true)`.
- Scenario-scoped dependency injection via Cucumber-PicoContainer (`TestContext`, `PageObjectManager`).
- `Hooks` class wiring driver lifecycle, logging, and failure-screenshot capture into Cucumber's `@Before`/`@After`.
- Page Object Model base classes (`BasePage`, sample `LoginPage`/`HomePage`) reused from the core automation library.
- Thread-safe `DriverFactory` / `DriverManager` supporting Chrome, Firefox, Edge, Safari, local and remote (Selenium Grid) execution.
- Centralized `ConfigManager` with environment overlays (`qa`, `staging`, `prod`) and system-property overrides.
- `WaitUtils`, `ScreenshotUtils`, and `JsonDataReader` reusable utilities.
- Sample `login.feature` with a scenario, a negative scenario, and a data-driven Scenario Outline.
- Combined reporting: Cucumber native HTML/JSON reports and ExtentReports via `extentreports-cucumber7-adapter`.
- TestNg suite (`testng.xml`) configured for parallel scenario execution.
- GitHub Actions CI workflow: static analysis, cross-browser scenario matrix, coverage, and artifact upload.
- Docker and Docker Compose support for local Selenium Grid execution.
- Log4j2 logging configuration with console and rolling file appenders.
- Project governance docs: README, CONTRIBUTING, CODE_OF_CONDUCT, LICENSE (MIT).
