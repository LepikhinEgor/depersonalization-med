package com.lepikhina;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import lombok.SneakyThrows;

public class ScriptsExecutor {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static  <T> List<T> executeScript(String scriptPath, HashMap<String, Object> variables) {
        Binding binding = new Binding();

        variables.forEach(binding::setVariable );

        GroovyShell shell = new GroovyShell(binding);

        return (List<T>)shell.evaluate(new File(scriptPath));
    }
}
