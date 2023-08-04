package com.pemesananlapanganfutsal.model;

public class Lapangan {
    private int idLapangan;
    private String namaLapangan;
    private String imgUrl;
    private int harga;

    public Lapangan(int idLapangan, String namaLapangan, String imgUrl, int harga) {
        this.idLapangan = idLapangan;
        this.namaLapangan = namaLapangan;
        this.imgUrl = imgUrl;
        this.harga = harga;
    }

    public int getIdLapangan() {
        return idLapangan;
    }

    public String getNamaLapangan() {
        return namaLapangan;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getHarga() {
        return harga;
    }
}
