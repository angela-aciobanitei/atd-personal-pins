package com.ang.acb.personalpins.ui.boards;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Board;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.data.repository.BoardRepository;
import com.ang.acb.personalpins.utils.AbsentLiveData;
import com.ang.acb.personalpins.utils.UiUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * Stores and manages UI-related data in a lifecycle conscious way.
 *
 * See: https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
 * See: https://medium.com/androiddevelopers/livedata-beyond-the-viewmodel-reactive-patterns-using-transformations-and-mediatorlivedata-fda520ba00b7
 */
public class BoardsViewModel extends ViewModel {

    private BoardRepository boardRepository;
    private LiveData<List<Board>> allBoards;
    private final MutableLiveData<Long> boardId = new MutableLiveData<>();

    @Inject
    public BoardsViewModel(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public LiveData<List<Board>> getAllBoards() {
        if (allBoards == null) {
            allBoards = boardRepository.getAllBoards();
        }
        return allBoards;
    }

    public void setBoardId(long value) {
        boardId.setValue(value);
    }

    public LiveData<List<Pin>> getPinsForBoard() {
        return Transformations.switchMap(boardId, id -> {
            if (id == null) return AbsentLiveData.create();
            else return boardRepository.getPinsForBoard(id);
        });
    }

    public void createBoard(Context context, String title) {
        // Set default board image with fixed image.
        boardRepository.insertBoard(new Board(title, UiUtils.getImageResourceUri(
                context, R.drawable.board_cool).toString()));
    }

    public void updateBoardCover(String photoCoverUri, long boardId) {
        boardRepository.updateBoardCover(photoCoverUri, boardId);
    }


}
