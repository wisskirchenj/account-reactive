package de.cofinpro.account;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.NonNull;

public class ResourcesRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
        hints.resources()
                .registerPattern("keystore/*.p12")
                .registerPattern("*.sql");
    }
}