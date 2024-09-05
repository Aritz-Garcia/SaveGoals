package com.savegoals.savegoals.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Objetivos {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    @NonNull
    public int categoria;
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
    @NonNull
    public boolean archivado;

    public Objetivos() {}

    public Objetivos(int categoria, String nombre, String fecha, float cantidad, float ahorrado, boolean completado) {
        this.categoria = categoria;
        this.nombre = nombre;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.ahorrado = ahorrado;
        this.completado = completado;
        this.archivado = false;
    }

    public Objetivos(int categoria, String nombre, String fecha, float cantidad, float ahorrado, boolean completado, boolean archivado) {
        this.categoria = categoria;
        this.nombre = nombre;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.ahorrado = ahorrado;
        this.completado = completado;
        this.archivado = archivado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
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

    public boolean getArchivado() {
        return archivado;
    }

    public void setArchivado(boolean archivado) {
        this.archivado = archivado;
    }
}
