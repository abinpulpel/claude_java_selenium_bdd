package com.claude.bdd.context;

import com.claude.framework.driver.DriverManager;
import com.claude.framework.pages.HomePage;
import com.claude.framework.pages.LoginPage;

import java.util.HashMap;
import java.util.Map;

/**
 * Lazily creates and caches page objects for the current scenario's thread.
 * PicoContainer gives each scenario its own {@link TestContext} (and
 * therefore its own {@code PageObjectManager}), so step definition classes
 * never construct page objects directly — they ask this factory instead
 * (Factory pattern), which keeps page-object wiring in one place.
 */
public class PageObjectManager {

    private final Map<Class<?>, Object> pageCache = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <T> T getOrCreate(Class<T> pageClass, java.util.function.Supplier<T> factory) {
        return (T) pageCache.computeIfAbsent(pageClass, key -> factory.get());
    }

    public LoginPage getLoginPage() {
        return getOrCreate(LoginPage.class, () -> new LoginPage(DriverManager.getDriver()));
    }

    public HomePage getHomePage() {
        return getOrCreate(HomePage.class, () -> new HomePage(DriverManager.getDriver()));
    }
}
