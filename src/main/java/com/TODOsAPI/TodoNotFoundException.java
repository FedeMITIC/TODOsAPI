package com.TODOsAPI;

public class TodoNotFoundException extends Exception {

    public TodoNotFoundException(Long id) {
        super(String.format("Cannot find the todo note with the ID (%s) supplied.", id));
    }

}
