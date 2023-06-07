package com.savegoals.savegoals.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.savegoals.savegoals.data.entities.Entradas;

import java.util.List;

@Dao
public interface EntradasDao {

    @Query("SELECT * FROM entradas")
    List<Entradas> getAll();

    @Query("SELECT * FROM entradas WHERE idObjetivos LIKE :idObjetivos")
    List<Entradas> findByIdObj(int idObjetivos);

    @Query("SELECT * FROM entradas WHERE idObjetivos LIKE :idObjetivos AND idEntrada LIKE :idEntrada")
    Entradas findByIds(int idObjetivos, int idEntrada);

    @Query("UPDATE entradas SET categoria = :categoria, fecha = :fecha, nombre = :nombre, cantidad = :cantidad WHERE idObjetivos = :idObjetivos AND idEntrada = :idEntrada")
    void update(int idObjetivos, int idEntrada, int categoria, String fecha, String nombre, float cantidad);

    @Query("UPDATE entradas SET cantidad = :cantidad WHERE idObjetivos = :idObjetivos AND idEntrada = :idEntrada")
    void updateCantidad(int idObjetivos, int idEntrada, float cantidad);

    @Insert
    void insertAll(Entradas... entradas);

    @Query("DELETE FROM entradas WHERE idObjetivos = :idObjetivos AND idEntrada = :idEntrada")
    void deleteByIds(int idObjetivos, int idEntrada);

    @Delete
    void delete(Entradas entradas);
}
