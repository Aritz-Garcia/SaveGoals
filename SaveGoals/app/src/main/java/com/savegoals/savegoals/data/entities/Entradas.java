package com.savegoals.savegoals.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"idObjetivos", "idEntrada"}, foreignKeys = @ForeignKey(entity = Objetivos.class,
                                                                                parentColumns = "id",
                                                                                childColumns = "idObjetivos",
                                                                                onDelete = ForeignKey.CASCADE))
public class Entradas {
    @NonNull
    public Integer idObjetivos;
    @NonNull
    public Integer idEntrada;
    @NonNull
    public String categoria;
    @NonNull
    public String fecha;
    @NonNull
    public String nombre;
    @NonNull
    public float cantidad;


    public Entradas() {}

    public Entradas(int idObjetivos, int idEntrada, String categoria, String fecha, String nombre, float cantidad) {
        this.idObjetivos = idObjetivos;
        this.idEntrada = idEntrada;
        this.categoria = categoria;
        this.fecha = fecha;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public int getIdObjetivos() {
        return idObjetivos;
    }

    public void setIdObjetivos(int idObjetivos) {
        this.idObjetivos = idObjetivos;
    }

     public int getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(int idEntrada) {
        this.idEntrada = idEntrada;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

     public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

     public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

     public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }
}
