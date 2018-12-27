package com.example.joovictor.designtry_v02;

/**
 * Created by Gustavo on 25/11/2017.
 */

public class Pessoa {
    int id_pessoa;
    int Id_casa ;
    String Nome;
    String BluetoothAddress;
    public Pessoa()
    {}

    public Pessoa(int id_pessoa, int id_casa, String nome, String bluetoothAddress) {
        this.id_pessoa = id_pessoa;
        Id_casa = id_casa;
        Nome = nome;
        BluetoothAddress = bluetoothAddress;
    }

    public int getId_pessoa() {
        return id_pessoa;
    }

    public void setId_pessoa(int id_pessoa) {
        this.id_pessoa = id_pessoa;
    }

    public int getId_casa() {
        return Id_casa;
    }

    public void setId_casa(int id_casa) {
        Id_casa = id_casa;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getBluetoothAddress() {
        return BluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        BluetoothAddress = bluetoothAddress;
    }
}
