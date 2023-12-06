package com.github.chrisgenti.vpl.velocity.configurations.language.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum LanguageType {
    ENGLISH("messages_en.toml"), ITALIAN("messages_it.toml");

    private final String value;
}
