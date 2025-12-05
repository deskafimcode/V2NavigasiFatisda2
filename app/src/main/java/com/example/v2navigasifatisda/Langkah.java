package com.example.v2navigasifatisda;

import java.io.Serializable;

public class Langkah implements Serializable {
    String teks;
    int gambarResId;

    public Langkah(String teks, int gambarResId) {
        this.teks = teks;
        this.gambarResId = gambarResId;
    }
}
