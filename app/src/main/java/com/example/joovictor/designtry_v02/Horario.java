package com.example.joovictor.designtry_v02;

/**
 * Created by Gustavo on 25/11/2017.
 */

public class Horario {
    int id_horario;
    int id_pessoa;
    int id_tomada;
    String horario;
    double seg;

    public Horario() {
    }

    public Horario(int id_horario, int id_pessoa, int id_tomada, String horario, double seg) {
        this.id_horario = id_horario;
        this.id_pessoa = id_pessoa;
        this.id_tomada = id_tomada;
        this.horario = horario;
        this.seg = seg;
    }

    public int getId_horario() {
        return id_horario;
    }

    public void setId_horario(int id_horario) {
        this.id_horario = id_horario;
    }

    public int getId_pessoa() {
        return id_pessoa;
    }

    public void setId_pessoa(int id_pessoa) {
        this.id_pessoa = id_pessoa;
    }

    public int getId_tomada() {
        return id_tomada;
    }

    public void setId_tomada(int id_tomada) {
        this.id_tomada = id_tomada;
    }


    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public double getSeg() {
        return seg;
    }

    public void setSeg(double seg) {
        this.seg = seg;
    }
}




