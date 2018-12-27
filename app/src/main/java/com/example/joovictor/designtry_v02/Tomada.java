package com.example.joovictor.designtry_v02;

/**
 * Created by Gustavo on 25/11/2017.
 */

public class Tomada {
   private int id_tomada ;
   private int id_casa ;
   private  int num_tomada;
   private   int potencia;
   public Tomada(){

   }
    public Tomada(int id_tomada, int id_casa, int num_tomada, int potencia)
    {
        this.id_tomada= id_tomada;
        this.id_casa= id_casa;
        this.num_tomada= num_tomada;
        this.potencia= potencia;
    }

    public int getId_tomada() {
        return id_tomada;
    }

    public void setId_tomada(int id_tomada) {
        this.id_tomada = id_tomada;
    }

    public int getId_casa() {
        return id_casa;
    }

    public void setId_casa(int id_casa) {
        this.id_casa = id_casa;
    }

    public int getNum_tomada() {
        return num_tomada;
    }

    public void setNum_tomada(int num_tomada) {
        this.num_tomada = num_tomada;
    }

    public int getpotencia() {
        return potencia;
    }

    public void setpotencia(int potencia) {
        this.potencia = potencia;
    }


}
