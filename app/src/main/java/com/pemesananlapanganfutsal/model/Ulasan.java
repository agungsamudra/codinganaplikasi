package com.pemesananlapanganfutsal.model;

public class Ulasan {
    private String namaPemesan;
    private String tanggal;
    private String rating;
    private String ulasan;

    public Ulasan(String namaPemesan, String tanggal, String rating, String ulasan) {
        this.namaPemesan = namaPemesan;
        this.tanggal = tanggal;
        this.rating = rating;
        this.ulasan = ulasan;
    }

    public String getNamaPemesan() {
        return namaPemesan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getRating() {
        return rating;
    }

    public String getUlasan() {
        return ulasan;
    }
}
