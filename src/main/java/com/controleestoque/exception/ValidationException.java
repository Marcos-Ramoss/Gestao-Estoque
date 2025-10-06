package com.controleestoque.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    
    private final List<String> errors;
    
    public ValidationException(String message) {
        super(message);
        this.errors = List.of(message);
    }
    
    public ValidationException(List<String> errors) {
        super("Erro de validação");
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
}




