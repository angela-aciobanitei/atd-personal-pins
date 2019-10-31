package com.ang.acb.personalpins.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.ang.acb.personalpins.data.entity.Tag;

import java.util.List;

/**
 * Interface for database access on {@link Tag} related operations.
 */
@Dao
public interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTag(Tag tag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTags(List<Tag> tags);

    @Delete
    void delete(Tag tag);

    @Query("DELETE FROM tag WHERE id = :id")
    void deleteById(long id);

    @Transaction
    @Query("SELECT * FROM tag WHERE id = :id")
    LiveData<Tag> getTagById(long id);

    @Transaction
    @Query("SELECT * FROM tag WHERE tag.pin_id = :pinId")
    LiveData<List<Tag>> getTagsForPin(long pinId);
}
