package com.lepikhina;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.util.List;

import lombok.SneakyThrows;

public class ScriptsExecutor {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static  <T> T executeScript(String scriptPath, List<T> oldValues) {
        Binding binding = new Binding();
        binding.setVariable("oldValues", oldValues);
        GroovyShell shell = new GroovyShell(binding);

        return (T)shell.evaluate(new File(scriptPath));
    }
}
