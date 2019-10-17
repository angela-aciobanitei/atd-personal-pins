package com.ang.acb.personalpins.di;

import android.app.Application;

import androidx.room.Room;

import com.ang.acb.personalpins.data.dao.BoardDao;
import com.ang.acb.personalpins.data.dao.CommentDao;
import com.ang.acb.personalpins.data.dao.PinDao;
import com.ang.acb.personalpins.data.dao.TagDao;
import com.ang.acb.personalpins.data.db.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton
    @Provides
    AppDatabase provideDatabase(Application app) {
        return Room.databaseBuilder(app, AppDatabase.class, "youtube.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    BoardDao provideBoardDao(AppDatabase database) {
        return database.boardDao();
    }

    @Singleton
    @Provides
    PinDao providePinDao(AppDatabase database) {
        return database.pinDao();
    }

    @Singleton
    @Provides
    TagDao provideTagDao(AppDatabase database) {
        return database.tagDao();
    }

    @Singleton
    @Provides
    CommentDao provideCommentDao(AppDatabase database) {
        return database.commentDao();
    }
}
