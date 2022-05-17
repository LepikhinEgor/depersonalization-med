package com.lepikhina;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import lombok.SneakyThrows;
import org.codehaus.groovy.control.CompilerConfiguration;

public class ScriptsExecutor {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static  <T> List<T> executeScript(String scriptPath, HashMap<String, Object> variables) {
        Binding binding = new Binding();

        variables.forEach(binding::setVariable );

        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding(StandardCharsets.UTF_8.name());
        GroovyShell shell = new GroovyShell(binding, config);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(scriptPath), StandardCharsets.UTF_8));
        return (List<T>)shell.evaluate(br);
    }
}
