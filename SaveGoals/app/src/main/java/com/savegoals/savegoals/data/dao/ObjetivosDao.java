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

    @Query("SELECT * FROM objetivos WHERE archivado = 0")
    List<Objetivos> getAllSinArchivados();

    @Query("SELECT * FROM objetivos WHERE completado = 0 AND archivado = 0")
    List<Objetivos> getAllNotCompleted();

    @Query("SELECT * FROM objetivos WHERE completado = 1 AND archivado = 0")
    List<Objetivos> getAllCompleted();

    @Query("SELECT * FROM objetivos WHERE archivado = 1")
    List<Objetivos> getAllArchived();

    @Query("SELECT * FROM objetivos WHERE id LIKE :id LIMIT 1")
    Objetivos findById(int id);

    @Query("SELECT * FROM objetivos WHERE fecha LIKE :fecha")
    List<Objetivos> findByFecha(String fecha);

    @Query("SELECT * FROM objetivos WHERE fecha LIKE :fecha AND archivado = 0")
    List<Objetivos> findByFechaSinArchivar(String fecha);

    @Query("UPDATE objetivos SET categoria = :categoria, nombre = :nombre, fecha = :fecha, cantidad = :cantidad, ahorrado = :ahorrado, completado = :completado, archivado = :archivado WHERE id = :id")
    void update(int id, int categoria, String nombre, String fecha, float cantidad, float ahorrado, boolean completado, boolean archivado);

    @Query("UPDATE objetivos SET ahorrado = :ahorrado WHERE id = :id")
    void updateAhorrado(int id, float ahorrado);

    @Query("UPDATE objetivos SET completado = :completado WHERE id = :id")
    void updateCompletado(int id, boolean completado);

    @Query("UPDATE objetivos SET archivado = :archivado WHERE id = :id")
    void updateArchivado(int id, boolean archivado);

    @Insert
    void insertAll(Objetivos... objetivos);

    @Query("DELETE FROM objetivos WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM objetivos")
    void deleteAll();

    @Delete
    void delete(Objetivos objetivos);
}
