package com.claude.bdd.context;

/**
 * Scenario-scoped state shared across step definition classes.
 *
 * <p>Cucumber-PicoContainer creates exactly one instance of this class per
 * scenario and injects it into every step definition class whose
 * constructor asks for it. That makes it the natural home for anything a
 * scenario needs to carry between {@code Given}/{@code When}/{@code Then}
 * steps without resorting to static or thread-unsafe state.
 */
public class TestContext {

    private final PageObjectManager pageObjectManager;

    public TestContext() {
        this.pageObjectManager = new PageObjectManager();
    }

    public PageObjectManager getPageObjectManager() {
        return pageObjectManager;
    }
}
