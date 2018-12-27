package com.example.joovictor.designtry_v02;

/**
 * Created by Gustavo on 25/11/2017.
 */

public class Casa {
    private    int id_casa;
    private    int num_pessoas;
    private   int num_tomadas;
    public Casa(){

    }

    public Casa(int id_casa, int num_pessoas, int num_tomadas) {
        this.id_casa = id_casa;
        this.num_pessoas = num_pessoas;
        this.num_tomadas = num_tomadas;
    }

    public int getId_casa() {
        return id_casa;
    }

    public void setId_casa(int id_casa) {
        this.id_casa = id_casa;
    }

    public int getNum_pessoas() {
        return num_pessoas;
    }

    public void setNum_pessoas(int num_pessoas) {
        this.num_pessoas = num_pessoas;
    }

    public int getNum_tomadas() {
        return num_tomadas;
    }

    public void setNum_tomadas(int num_tomadas) {
        this.num_tomadas = num_tomadas;
    }
}
