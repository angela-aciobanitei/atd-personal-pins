package com.ang.acb.personalpins.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.ang.acb.personalpins.data.entitiy.Comment;

import java.util.List;

/**
 * Interface for database access on {@link Comment} related operations.
 */
@Dao
public interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComment(Comment comment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComments(List<Comment> comments);

    @Delete
    void delete(Comment comment);

    @Query("DELETE FROM comment WHERE id = :id")
    void deleteById(long id);

    @Transaction
    @Query("SELECT * FROM comment WHERE id = :id")
    LiveData<Comment> getCommentById(long id);

    @Transaction
    @Query("SELECT * FROM comment WHERE comment.pin_id = :pinId")
    LiveData<List<Comment>> getCommentsForPin(long pinId);
}
