package com.ang.acb.personalpins.data.repository;

import androidx.lifecycle.LiveData;

import com.ang.acb.personalpins.data.db.AppDatabase;
import com.ang.acb.personalpins.data.entity.Board;
import com.ang.acb.personalpins.data.entity.Pin;
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
public class BoardRepository {

    private AppDatabase database;
    private AppExecutors executors;

    @Inject
    public BoardRepository(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    public LiveData<List<Board>> getAllBoards(){
        return database.boardDao().getAllBoards();
    }

    public LiveData<List<Pin>> getPinsForBoard(long boardId) {
        return database.boardPinDao().getPinsForBoard(boardId);
    }

    public LiveData<Board> getBoardById(long id){
        return database.boardDao().getBoardById(id);
    }

    public void insertBoard(Board board) {
        executors.diskIO().execute(()
                -> database.boardDao().insertBoard(board));
    }

    public void updateBoardCover(String photoCoverUri, long boardId) {
        executors.diskIO().execute(()
                -> database.boardDao().updateBoardCover(photoCoverUri, boardId));
    }

    public void deleteBoard(long id) {
        executors.diskIO().execute(()
                -> database.boardDao().deleteById(id));
    }
}
