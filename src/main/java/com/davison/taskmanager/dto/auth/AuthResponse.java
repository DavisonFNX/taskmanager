package com.davison.taskmanager.dto.auth;

public class AuthResponse {
    private String token;
    private String nome;

    public AuthResponse(String token, String nome) {
        this.token = token;
        this.nome = nome;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}