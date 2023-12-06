package com.github.chrisgenti.vpl.velocity.data.mysql.credentials;

public record Credentials(String hostname, int port, String database, String username, String password) {}
