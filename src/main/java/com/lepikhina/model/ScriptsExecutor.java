package com.lepikhina.model;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.SneakyThrows;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class ScriptsExecutor {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> List<T> executeScript(String scriptPath, HashMap<String, Object> variables) {
        Binding binding = new Binding();

        variables.forEach(binding::setVariable);

        BufferedReader scriptReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(scriptPath), StandardCharsets.UTF_8));

        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding(StandardCharsets.UTF_8.name());
        GroovyShell shell = new GroovyShell(binding, config);

        return (List<T>) shell.evaluate(scriptReader);
    }
}
