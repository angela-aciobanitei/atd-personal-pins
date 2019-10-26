package com.ang.acb.personalpins.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;


import com.ang.acb.personalpins.data.entity.Board;

import java.util.List;

/**
 * Interface for database access on {@link Board} related operations.
 */
@Dao
public interface BoardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBoard(Board board);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBoards(List<Board> boards);

    /**
     * Updating only photo cover uri, by board id.
     * https://stackoverflow.com/questions/45789325/update-some-specific-field-of-an-entity-in-android-room
     */
    @Query("UPDATE board SET photo_cover_uri = :photoUri WHERE id = :id")
    void updateBoardCover(String photoUri, long id);

    @Delete
    void delete(Board board);

    @Query("DELETE FROM board WHERE id = :id")
    void deleteById(long id);

    @Transaction
    @Query("SELECT * FROM board WHERE id = :id")
    LiveData<Board> getBoardById(long id);

    @Transaction
    @Query("SELECT * FROM board")
    LiveData<List<Board>> getAllBoards();
}
