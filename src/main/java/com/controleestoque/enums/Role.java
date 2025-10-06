package com.controleestoque.enums;

/**
 * Enum para definir os papéis dos usuários no sistema
 */
public enum Role {
    ADMIN("Administrador"),
    VENDEDOR("Vendedor"),
    VISUALIZADOR("Visualizador");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

