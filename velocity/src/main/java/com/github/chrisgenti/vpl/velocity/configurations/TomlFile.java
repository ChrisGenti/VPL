package com.github.chrisgenti.vpl.velocity.configurations;

import com.moandjiezana.toml.Toml;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public abstract class TomlFile {
    private final File directory;
    private final String name;
    protected Toml tomlRoot;

    public TomlFile(File directory, String name) {
        this.directory = directory; this.name = name; this.ensure();
    }

    @SneakyThrows @SuppressWarnings("ResultOfMethodCallIgnored")
    private void ensure() {
        File file = new File(directory, name);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();


        if (!file.exists()) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(name);
            if (inputStream == null)
                throw new NullPointerException(name + " not found in resources");
            Files.copy(inputStream, file.toPath());
        }
        tomlRoot = new Toml().read(file);
    }
}
