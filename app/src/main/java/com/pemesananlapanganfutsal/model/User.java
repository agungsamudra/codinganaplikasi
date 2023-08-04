package com.pemesananlapanganfutsal.model;

public class User {
    private String idPemesan;
    private String namaPemesan;

    public User(String idPemesan, String namaPemesan) {
        this.idPemesan = idPemesan;
        this.namaPemesan = namaPemesan;
    }

    public String getIdPemesan() {
        return idPemesan;
    }

    public String getNamaPemesan() {
        return namaPemesan;
    }
}
