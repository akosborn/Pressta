package me.andrewosborn.pressta.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import me.andrewosborn.pressta.model.Brew;

@Dao
public interface BrewDao
{
    @Query("SELECT * FROM brew")
    LiveData<List<Brew>> findAll();

    @Query("SELECT * FROM brew WHERE id = :id")
    LiveData<Brew> findById(int id);

    @Query("SELECT * FROM brew WHERE title LIKE :title LIMIT 1")
    LiveData<Brew> findByTitle(String title);

    @Insert
    void insertAll(List<Brew> brews);

    @Delete
    void delete(Brew brew);
}
