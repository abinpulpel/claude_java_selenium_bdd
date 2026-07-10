# claude_java_selenium_bdd

Enterprise-grade, reusable Java Selenium test automation framework built on
**Cucumber BDD** and the **Page Object Model**. Designed so QA engineers can
start writing executable specifications immediately by importing existing
step-definition conventions, page objects, configuration, and reporting — no
framework plumbing required.

## Overview

This framework provides a production-ready foundation for behavior-driven UI
test automation: Gherkin feature files as living documentation, thread-safe
cross-browser driver management, environment-aware configuration, scenario-
scoped dependency injection via PicoContainer, structured logging, combined
Cucumber + ExtentReports HTML reporting with failure screenshots, and CI/CD +
Docker support out of the box.

It is not a tutorial project — it follows SOLID, DRY, and Clean Architecture
principles, and every component is meant to be extended, not replaced.

## Architecture

```
                ┌───────────────────────┐
                │   Feature Files (.feature) │  Gherkin specs (src/test/resources/features)
                └────────────┬──────────┘
                             │ drives
                ┌────────────▼──────────┐
                │   Step Definitions     │  (LoginSteps, ...)
                └────────────┬──────────┘
                             │ injected with (PicoContainer)
                ┌────────────▼──────────┐
                │      TestContext       │  scenario-scoped state
                │  └─ PageObjectManager  │  lazy page-object factory
                └────────────┬──────────┘
                             │ uses
                ┌────────────▼──────────┐
                │     Page Objects       │  (BasePage, LoginPage, HomePage)
                └────────────┬──────────┘
        ┌────────────────────┼────────────────────┐
        │                    │                     │
┌───────▼───────┐   ┌────────▼────────┐   ┌────────▼────────┐
│  WaitUtils     │   │  DriverManager   │   │  ConfigManager   │
│  (explicit     │   │  (ThreadLocal    │   │  (properties +   │
│   waits)       │   │   WebDriver)     │   │   env overlays)  │
└───────────────┘   └────────┬────────┘   └─────────────────┘
                               │ built by
                      ┌────────▼────────┐
                      │  DriverFactory   │  (local + remote Grid)
                      └─────────────────┘

Hooks (Cucumber) ──▶ ScreenshotUtils + Log4j2 + Extent-Cucumber-Adapter report
```

- **Gherkin as documentation**: feature files describe behavior in plain
  language; step definitions are the only code that touches the framework.
- **Dependency injection (PicoContainer)**: `TestContext` is instantiated
  once per scenario and injected into every step definition class that needs
  it, giving natural thread-safety under parallel execution.
- **Factory pattern**: `PageObjectManager` lazily builds and caches page
  objects per scenario; `DriverFactory` builds the correct driver for any
  browser/execution mode.
- **Singleton**: `ConfigManager` is a process-wide, lazily-initialized
  singleton.
- **ThreadLocal isolation**: `DriverManager` keeps per-thread driver state so
  parallel scenario execution never leaks a browser session across threads.
- **Facade**: `Hooks` fronts driver lifecycle, logging, and failure-
  screenshot capture behind Cucumber's `@Before`/`@After` hooks.

## Folder Structure

```
claude_java_selenium_bdd/
├── .github/workflows/ci.yml        # GitHub Actions CI pipeline
├── src/
│   ├── main/java/com/claude/framework/
│   │   ├── config/                 # ConfigManager
│   │   ├── driver/                 # DriverFactory, DriverManager
│   │   ├── enums/                  # BrowserType
│   │   ├── exceptions/             # FrameworkException
│   │   ├── pages/                  # BasePage + sample page objects
│   │   └── utils/                  # WaitUtils, ScreenshotUtils, JsonDataReader
│   ├── main/resources/             # config.properties + per-env overlays, log4j2.xml
│   └── test/
│       ├── java/com/claude/bdd/
│       │   ├── context/            # TestContext, PageObjectManager (PicoContainer DI)
│       │   ├── hooks/              # Hooks (driver lifecycle, screenshots)
│       │   ├── stepdefinitions/    # LoginSteps
│       │   └── runners/            # TestRunner (Cucumber + TestNG)
│       └── resources/features/     # Gherkin .feature files
├── testng.xml                      # Suite definition, parallel scenario execution
├── Dockerfile
├── docker-compose.yml              # Selenium Grid (hub + chrome/firefox nodes)
├── pom.xml
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
├── CHANGELOG.md
└── LICENSE
```

## Technology Stack

| Concern            | Technology                          |
|---------------------|--------------------------------------|
| Language            | Java 21                              |
| Build tool           | Maven                                |
| BDD engine           | Cucumber 7.20                        |
| Dependency injection | Cucumber-PicoContainer               |
| Browser automation   | Selenium 4.27                        |
| Driver management     | WebDriverManager                     |
| Test engine          | TestNG 7.10                          |
| Reporting            | Cucumber HTML/JSON + ExtentReports Cucumber7 Adapter |
| Logging              | Log4j2                               |
| Data / JSON support  | Jackson                              |
| Static analysis      | SpotBugs                             |
| Code coverage        | JaCoCo                               |
| Containerization      | Docker, Docker Compose, Selenium Grid |
| CI/CD                | GitHub Actions                       |

## Prerequisites

- Java 21 (Temurin recommended)
- Maven 3.9+
- Docker (optional, for Grid execution)
- Chrome/Firefox/Edge installed locally for non-headless runs

## Installation

```bash
git clone https://github.com/abinpulpel/claude_java_selenium_bdd.git
cd claude_java_selenium_bdd
mvn clean install -DskipTests
```

## Configuration

Configuration lives in `src/main/resources/config.properties` with
per-environment overlays (`config-qa.properties`, `config-staging.properties`,
`config-prod.properties`). Any key can be overridden at runtime via `-D`:

```bash
mvn clean test -Dbrowser=firefox -Denv=staging -Dheadless=true
```

Key configuration options:

| Property                  | Description                              | Default |
|----------------------------|--------------------------------------------|---------|
| `browser`                  | chrome / firefox / edge / safari            | chrome  |
| `headless`                 | Run without a visible browser window        | false   |
| `env`                      | Selects the config overlay file             | qa      |
| `remote.execution`         | Run against a Selenium Grid                 | false   |
| `remote.grid.url`          | Grid hub URL                                | -       |
| `implicit.wait.seconds`    | Implicit wait                               | 10      |
| `explicit.wait.seconds`    | Default explicit wait in `WaitUtils`        | 20      |
| `retry.max.count`          | Reserved for retry-analyzer style extensions | 2       |

## Writing Scenarios

Add Gherkin scenarios under `src/test/resources/features/` and matching step
definitions under `src/test/java/com/claude/bdd/stepdefinitions/`:

```gherkin
@smoke
Scenario: Successful login with valid credentials
  Given the user is on the login page
  When the user logs in with username "standard_user" and password "correct_password"
  Then the user should see the home page welcome banner
```

Filter which scenarios run using Cucumber tags:

```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
```

## Execution

```bash
# Full suite via TestNg suite file
mvn clean test

# Tag-filtered run
mvn clean test -Dcucumber.filter.tags="@regression"
```

### Parallel Execution

Scenario-level parallelism is enabled via `TestRunner`'s
`@DataProvider(parallel = true)` override, sized by `testng.xml`'s
`data-provider-thread-count` attribute. Each scenario runs on its own thread
with its own `TestContext`/`WebDriver`, so no additional synchronization is
required in step definitions.

### Headless Execution

```bash
mvn clean test -Dheadless=true
```

### Cross-Browser Execution

```bash
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

### Docker / Selenium Grid

```bash
docker compose up --build
```

Spins up a Selenium Hub, Chrome and Firefox nodes, and a `test-runner`
container that executes the suite against the Grid (`remote.execution=true`).

## CI/CD

`.github/workflows/ci.yml` runs on every push/PR to `main`: static analysis
(SpotBugs), a cross-browser headless scenario matrix (Chrome + Firefox),
JaCoCo coverage, and uploads Surefire results, Cucumber HTML/JSON reports,
the Extent HTML report, and failure screenshots as build artifacts. It can
also be triggered manually with a chosen browser/environment via
`workflow_dispatch`.

## Reporting

Every run produces three complementary reports:

- Cucumber's native HTML/JSON reports under `target/cucumber-reports/`.
- An ExtentReports HTML report (via `extentreports-cucumber7-adapter`) under
  `target/reports/`, with embedded screenshots for failed scenarios.
- TestNG/Surefire XML results under `target/surefire-reports/`.

## Logging

Log4j2 (`src/main/resources/log4j2.xml`) writes to the console and to
rolling daily log files under `target/logs/automation.log`.

## Troubleshooting

| Symptom                                  | Likely cause / fix                                                       |
|--------------------------------------------|----------------------------------------------------------------------------|
| `IllegalStateException: No WebDriver...`   | A step ran outside the Cucumber `Hooks` lifecycle — check `glue` package.  |
| Driver binary download fails                | Check network access; WebDriverManager needs outbound HTTPS.              |
| Step definition not found                   | Confirm the package is listed in `TestRunner`'s `@CucumberOptions(glue = ...)`. |
| Tests pass locally, fail in CI              | Confirm `-Dheadless=true` and that the app under test is reachable from CI.|
| Flaky scenarios                             | Check for a missing explicit wait in `WaitUtils`, or a locator race in `Hooks`. |
| Grid session won't start                    | Confirm `docker compose ps` shows the hub and node containers healthy.    |

## Contribution Guide

See [CONTRIBUTING.md](CONTRIBUTING.md) for branching, commit conventions,
Gherkin style guide, and the pull request checklist. See
[CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for community standards.

## License

Distributed under the [MIT License](LICENSE).

## Roadmap

- [ ] API-layer test support (Rest Assured) alongside UI scenarios
- [ ] BrowserStack / LambdaTest cloud execution profiles
- [ ] Cucumber `DataTable` transformers for structured test data
- [ ] Accessibility (axe-core) checks in the `Hooks` lifecycle
- [ ] Retry logic for flaky scenarios via a custom `IRetryAnalyzer`
