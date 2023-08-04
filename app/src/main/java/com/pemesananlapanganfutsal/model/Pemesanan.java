package com.pemesananlapanganfutsal.model;

public class Pemesanan {
    private String idPemesanan;
    private String kodePemesanan;
    private String tanggal;
    private int total;
    private String status;

    public Pemesanan(String idPemesanan, String kodePemesanan, String tanggal, int total, String status) {
        this.idPemesanan = idPemesanan;
        this.kodePemesanan = kodePemesanan;
        this.tanggal = tanggal;
        this.total = total;
        this.status = status;
    }

    public String getIdPemesanan() {
        return idPemesanan;
    }

    public String getKodePemesanan() {
        return kodePemesanan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public int getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }
}
