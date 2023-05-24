package com.savegoals.savegoals.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"id"})
public class Objetivos {
    @NonNull
    public Integer id;
    @NonNull
    public String categoria;
    @NonNull
    public String nombre;
    @NonNull
    public String fecha;
    @NonNull
    public float cantidad;
    @NonNull
    public float ahorrado;
    @NonNull
    public boolean completado;

    public Objetivos() {}

    public Objetivos(int id, String categoria, String nombre, String fecha, float cantidad, float ahorrado, boolean completado) {
        this.id = id;
        this.categoria = categoria;
        this.nombre = nombre;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.ahorrado = ahorrado;
        this.completado = completado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

     public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

     public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

     public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

     public float getAhorrado() {
        return ahorrado;
    }

    public void setAhorrado(float ahorrado) {
        this.ahorrado = ahorrado;
    }

     public boolean getCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}
