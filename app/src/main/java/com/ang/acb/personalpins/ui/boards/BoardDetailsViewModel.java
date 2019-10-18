package com.ang.acb.personalpins.ui.boards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ang.acb.personalpins.data.entity.Board;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.data.repository.BoardRepository;
import com.ang.acb.personalpins.utils.AbsentLiveData;

import java.util.List;

import javax.inject.Inject;

public class BoardDetailsViewModel extends ViewModel {

    private BoardRepository boardRepository;
    private final MutableLiveData<Long> boardId = new MutableLiveData<>();
    private LiveData<Board> board;
    private LiveData<List<Pin>> pins;

    @Inject
    public BoardDetailsViewModel(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public void setBoardId(long id) {
        boardId.setValue(id);
    }

    public LiveData<Board> getBoard() {
        if (board == null) {
            board = Transformations.switchMap(boardId, id -> {
                if (id == null) return AbsentLiveData.create();
                else return boardRepository.getBoardById(id);
            });
        }
        return  board;
    }

    public LiveData<List<Pin>> getPinsForBoard() {
        if (pins == null) {
            pins = Transformations.switchMap(boardId, id -> {
                if (id == null) return AbsentLiveData.create();
                else return boardRepository.getPinsForBoard(id);
            });
        }
        return  pins;
    }
}
