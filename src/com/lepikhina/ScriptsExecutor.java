package com.lepikhina;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;

import lombok.SneakyThrows;

public class ScriptsExecutor {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static  <T> T executeScript(String scriptPath, T oldValue) {
        Binding binding = new Binding();
        binding.setVariable("oldValue", oldValue);
        GroovyShell shell = new GroovyShell(binding);

        return (T)shell.evaluate(new File(scriptPath));
    }
}
