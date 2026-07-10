# Contributing to claude_java_selenium_bdd

Thanks for investing time in improving this framework. This guide covers how
to get set up, how to write Gherkin, and the standards a pull request needs
to meet.

## Prerequisites

- Java 21 (Temurin recommended)
- Maven 3.9+
- Docker (optional, for Selenium Grid execution)
- Google Chrome / Firefox / Edge installed locally for non-headless runs

## Getting Started

```bash
git clone https://github.com/abinpulpel/claude_java_selenium_bdd.git
cd claude_java_selenium_bdd
mvn clean install -DskipTests
```

## Running Scenarios

```bash
# Default (Chrome, qa environment)
mvn clean test

# Specific browser / environment
mvn clean test -Dbrowser=firefox -Denv=staging -Dheadless=true

# Tag-filtered
mvn clean test -Dcucumber.filter.tags="@smoke"

# Against a Dockerized Selenium Grid
docker compose up --build
```

## Gherkin Style Guide

- One `Feature` per file, named after the business capability under test
  (e.g. `login.feature`, not `LoginTests.feature`).
- Write steps from the user's perspective in present tense: `Given the user
  is on the login page`, not `Given navigate to login`.
- Prefer `Scenario Outline` + `Examples` over near-duplicate scenarios that
  only differ by input data.
- Keep a `Background` for setup shared by every scenario in a feature file.
- Tag scenarios meaningfully (`@smoke`, `@regression`, `@ignore`) so CI and
  local runs can filter by tag.
- Step definitions must stay declarative — push logic into page objects or
  utilities, never inline WebDriver calls in a step method.

## Branching & Commits

- Branch from `main` using `feature/<short-description>` or `fix/<short-description>`.
- Follow [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`).
- Keep commits focused; squash noisy WIP history before opening a PR.

## Code Standards

- Follow existing package structure: `config`, `driver`, `pages`, `utils`
  (reusable core library) and `context`, `hooks`, `stepdefinitions`, `runners`
  (Cucumber glue).
- Every public class and method needs a Javadoc comment explaining *why*, not just *what*.
- No hard-coded waits (`Thread.sleep`) — use `WaitUtils`.
- No duplicated locators or logic across page objects — extract to `BasePage` or a shared utility.
- Step definitions receive shared state only via `TestContext` (constructor injection), never via static fields.
- Run `mvn com.github.spotbugs:spotbugs-maven-plugin:check` and resolve findings before submitting.

## Pull Request Checklist

- [ ] Scenarios pass locally: `mvn clean test`
- [ ] New/changed code has Javadoc
- [ ] No new SpotBugs findings
- [ ] README/CHANGELOG updated if behavior or setup steps changed
- [ ] PR description explains the change and links any related issue

## Reporting Issues

Open a GitHub issue with reproduction steps, expected vs. actual behavior,
browser/environment used, and relevant log, Cucumber report, or screenshot
output from `target/logs`, `target/cucumber-reports`, or `target/screenshots`.
