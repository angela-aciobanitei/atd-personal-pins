package com.ang.acb.personalpins.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;


import com.ang.acb.personalpins.data.entitiy.Board;

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

    @Delete
    void delete(Board board);

    @Query("DELETE FROM board WHERE id = :id")
    void deleteById(long id);

    @Transaction
    @Query("SELECT * FROM board WHERE id = :id")
    LiveData<Board> getBoardById(long id);
}
