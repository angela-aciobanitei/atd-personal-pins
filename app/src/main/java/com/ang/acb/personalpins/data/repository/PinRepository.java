package com.ang.acb.personalpins.data.repository;

import androidx.lifecycle.LiveData;

import com.ang.acb.personalpins.data.db.AppDatabase;
import com.ang.acb.personalpins.data.entitiy.Board;
import com.ang.acb.personalpins.data.entitiy.Comment;
import com.ang.acb.personalpins.data.entitiy.Pin;
import com.ang.acb.personalpins.data.entitiy.Tag;
import com.ang.acb.personalpins.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository module for handling data operations.
 *
 * See: https://developer.android.com/jetpack/docs/guide#truth
 * See: https://github.com/android/sunflower
 */
@Singleton
public class PinRepository {

    private AppDatabase database;
    private AppExecutors executors;

    @Inject
    public PinRepository(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    public LiveData<List<Pin>> getAllPins(){
        return database.pinDao().getAllPins();
    }

    public LiveData<Pin> getPinById(long id){
        return database.pinDao().getPinById(id);
    }

    public LiveData<List<Tag>> getTagsForPin(long pinId) {
        return database.tagDao().getTagsForPin(pinId);
    }

    public LiveData<List<Comment>> getCommentsForPin(long pinId) {
        return database.commentDao().getCommentsForPin(pinId);
    }

    public void insertPin(Pin pin) {
        executors.diskIO().execute(()
                -> database.pinDao().insertPin(pin));
    }

    public void insertTag(Tag tag) {
        executors.diskIO().execute(()
                -> database.tagDao().insertTag(tag));
    }

    public void insertComment(Comment comment) {
        executors.diskIO().execute(()
                -> database.commentDao().insertComment(comment));
    }

    public void deletePin(long id) {
        executors.diskIO().execute(()
                -> database.pinDao().deleteById(id));
    }

    public void deleteTag(long id) {
        executors.diskIO().execute(()
                -> database.tagDao().deleteById(id));
    }

    public void deleteComment(long id) {
        executors.diskIO().execute(()
                -> database.commentDao().deleteById(id));
    }

    public LiveData<List<Board>> getBoardsForPin(long pinId) {
        return database.boardPinDao().getBoardsForPin(pinId);
    }
}