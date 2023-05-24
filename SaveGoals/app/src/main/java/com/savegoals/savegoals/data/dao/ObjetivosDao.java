package com.savegoals.savegoals.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.savegoals.savegoals.data.entities.Objetivos;

import java.util.List;

@Dao
public interface ObjetivosDao {

    @Query("SELECT * FROM objetivos")
    List<Objetivos> getAll();

    @Query("SELECT * FROM objetivos WHERE id LIKE :id LIMIT 1")
    Objetivos findById(int id);

    @Query("UPDATE objetivos SET categoria = :categoria, nombre = :nombre, fecha = :fecha, cantidad = :cantidad, ahorrado = :ahorrado, completado = :completado WHERE id = :id")
    void update(int id, String categoria, String nombre, String fecha, float cantidad, float ahorrado, boolean completado);

    @Query("UPDATE objetivos SET ahorrado = :ahorrado WHERE id = :id")
    void updateAhorrado(int id, float ahorrado);

    @Query("UPDATE objetivos SET completado = :completado WHERE id = :id")
    void updateCompletado(int id, boolean completado);

    @Insert
    void insertAll(Objetivos... objetivos);

    @Delete
    void delete(Objetivos objetivos);
}