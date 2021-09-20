package com.github.elic0de.spigotcommandlib.invocation;

public class CommandInvocationException extends Exception {
    public CommandInvocationException() {
    }

    public CommandInvocationException(String message) {
        super(message);
    }

    public CommandInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandInvocationException(Throwable cause) {
        super(cause);
    }
}
