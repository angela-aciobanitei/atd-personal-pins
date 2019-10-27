package com.ang.acb.personalpins.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ang.acb.personalpins.data.entity.Board;
import com.ang.acb.personalpins.data.entity.BoardPin;
import com.ang.acb.personalpins.data.entity.Pin;

import java.util.List;

/**
 * Interface for database access on {@link BoardPin} related operations.
 */
@Dao
public interface BoardPinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BoardPin boardPin);

    @Delete
    void delete(BoardPin boardPin);

    @Query("DELETE FROM boardPin " +
            "WHERE boardPin.boardId = :boardId " +
            "AND boardPin.pinId = :pinId")
    void deleteByIds(long boardId, long pinId);

    @Query("SELECT id, pin_title, photo_uri, video_uri, is_favorite FROM pin " +
            "INNER JOIN boardPin " +
            "ON pin.id = boardPin.pinId " +
            "WHERE boardPin.boardId = :boardId")
    LiveData<List<Pin>> getPinsForBoard(final long boardId);

    @Query("SELECT id, board_title, photo_cover_uri FROM board " +
            "INNER JOIN boardPin " +
            "ON board.id = boardPin.boardId " +
            "WHERE boardPin.pinId = :pinId")
    LiveData<List<Board>> getBoardsForPin(final long pinId);
}
