package com.ang.acb.personalpins.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;


import com.ang.acb.personalpins.data.entity.Pin;

import java.util.List;

/**
 * Interface for database access on {@link Pin} related operations.
 */
@Dao
public interface PinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPin(Pin pin);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPins(List<Pin> pins);

    @Delete
    void delete(Pin pin);

    @Query("UPDATE pin SET is_favorite = 1 WHERE id = :pinId")
    void markAsFavorite(long pinId);

    @Query("UPDATE pin SET is_favorite = 0 WHERE id = :pinId")
    void markAsNotFavorite(long pinId);

    @Query("DELETE FROM pin WHERE id = :id")
    void deleteById(long id);

    @Transaction
    @Query("SELECT * FROM pin WHERE id = :id")
    LiveData<Pin> getPinById(long id);

    @Transaction
    @Query("SELECT * FROM pin")
    LiveData<List<Pin>> getAllPins();

    @Transaction
    @Query("SELECT * FROM pin WHERE is_favorite = 1")
    LiveData<List<Pin>> getAllFavoritePins();
}
