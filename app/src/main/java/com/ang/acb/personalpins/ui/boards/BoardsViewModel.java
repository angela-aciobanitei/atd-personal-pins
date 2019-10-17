package com.ang.acb.personalpins.ui.boards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ang.acb.personalpins.data.entitiy.Board;
import com.ang.acb.personalpins.data.entitiy.Pin;
import com.ang.acb.personalpins.data.repository.BoardRepository;
import com.ang.acb.personalpins.utils.AbsentLiveData;

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
    private final MutableLiveData<Long> boardId = new MutableLiveData<>();
    private LiveData<List<Board>> allBoards;
    private LiveData<Board> board;
    private LiveData<List<Pin>> pins;

    @Inject
    public BoardsViewModel(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public void setBoardId(long id) {
        boardId.setValue(id);
    }

    public LiveData<List<Board>> getAllBoards() {
        if (allBoards == null) {
            allBoards = boardRepository.getAllBoards();
        }
        return allBoards;
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

    public void createBoard(String title) {
        boardRepository.insertBoard(new Board(title, " "));
    }

}
