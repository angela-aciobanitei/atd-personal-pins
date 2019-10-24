package com.ang.acb.personalpins.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ang.acb.personalpins.data.dao.BoardDao;
import com.ang.acb.personalpins.data.dao.BoardPinDao;
import com.ang.acb.personalpins.data.dao.CommentDao;
import com.ang.acb.personalpins.data.dao.PinDao;
import com.ang.acb.personalpins.data.dao.TagDao;
import com.ang.acb.personalpins.data.entity.Board;
import com.ang.acb.personalpins.data.entity.BoardPin;
import com.ang.acb.personalpins.data.entity.Comment;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.data.entity.Tag;

/**
 * The Room database for this app.
 *
 * See: https://medium.com/androiddevelopers/7-steps-to-room-27a5fe5f99b2
 * See: https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1
 */
@Database(entities = {Board.class, Pin.class, Tag.class, Comment.class, BoardPin.class},
          version = 2,
          exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BoardDao boardDao();
    public abstract PinDao pinDao();
    public abstract TagDao tagDao();
    public abstract CommentDao commentDao();
    public abstract BoardPinDao boardPinDao();
}
