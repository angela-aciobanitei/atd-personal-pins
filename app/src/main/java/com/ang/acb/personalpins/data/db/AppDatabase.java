package com.ang.acb.personalpins.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ang.acb.personalpins.data.dao.BoardDao;
import com.ang.acb.personalpins.data.dao.CommentDao;
import com.ang.acb.personalpins.data.dao.PinDao;
import com.ang.acb.personalpins.data.dao.TagDao;
import com.ang.acb.personalpins.data.entitiy.Board;
import com.ang.acb.personalpins.data.entitiy.Comment;
import com.ang.acb.personalpins.data.entitiy.Pin;
import com.ang.acb.personalpins.data.entitiy.Tag;

/**
 * The Room database for this app.
 *
 * See: https://medium.com/androiddevelopers/7-steps-to-room-27a5fe5f99b2
 * See: https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1
 */
@Database(entities = {Board.class, Pin.class, Tag.class, Comment.class},
          version = 1,
          exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BoardDao boardDao();
    public abstract PinDao pinDao();
    public abstract TagDao tagDao();
    public abstract CommentDao commentDao();
}
