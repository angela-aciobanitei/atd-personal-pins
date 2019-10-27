package com.ang.acb.personalpins.data.repository;

import androidx.lifecycle.LiveData;

import com.ang.acb.personalpins.data.db.AppDatabase;
import com.ang.acb.personalpins.data.entity.Board;
import com.ang.acb.personalpins.data.entity.BoardPin;
import com.ang.acb.personalpins.data.entity.Comment;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.data.entity.Tag;
import com.ang.acb.personalpins.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

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

    public LiveData<List<Pin>> getAllFavoritePins() {
        return database.pinDao().getAllFavoritePins();
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

    public void deletePin(long id) {
        executors.diskIO().execute(()
                -> database.pinDao().deleteById(id));
    }

    public void markAsFavorite(final Pin pin) {
        executors.diskIO().execute(() -> {
            Timber.d("Adding pin to favorites");
            database.pinDao().markAsFavorite(pin.getId());
        });
    }

    public void markAsNotFavorite(final Pin pin) {
        executors.diskIO().execute(() -> {
            Timber.d("Removing pin from favorites");
            database.pinDao().markAsNotFavorite(pin.getId());
        });
    }

    public void insertTag(Tag tag) {
        executors.diskIO().execute(()
                -> database.tagDao().insertTag(tag));
    }

    public void deleteTag(long id) {
        executors.diskIO().execute(()
                -> database.tagDao().deleteById(id));
    }

    public void insertComment(Comment comment) {
        executors.diskIO().execute(()
                -> database.commentDao().insertComment(comment));
    }

    public void deleteComment(long id) {
        executors.diskIO().execute(()
                -> database.commentDao().deleteById(id));
    }

    public void insertBoardPin(long boardId, long pinId) {
        executors.diskIO().execute(() ->
                database.boardPinDao().insert(new BoardPin(boardId, pinId)));
    }

    public void deleteBoardPin(long boardId, long pinId){
        executors.diskIO().execute(() ->
                database.boardPinDao().deleteByIds(boardId,pinId));
    }

    public LiveData<List<Board>> getBoardsForPin(long pinId) {
        return database.boardPinDao().getBoardsForPin(pinId);
    }

    public LiveData<List<Pin>> getPinsForBoard(long boardId) {
        return database.boardPinDao().getPinsForBoard(boardId);
    }
}
