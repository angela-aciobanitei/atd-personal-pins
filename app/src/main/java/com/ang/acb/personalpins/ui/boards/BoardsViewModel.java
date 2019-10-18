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

/**
 * Stores and manages UI-related data in a lifecycle conscious way.
 *
 * See: https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
 * See: https://medium.com/androiddevelopers/livedata-beyond-the-viewmodel-reactive-patterns-using-transformations-and-mediatorlivedata-fda520ba00b7
 */
public class BoardsViewModel extends ViewModel {

    private BoardRepository boardRepository;
    private LiveData<List<Board>> allBoards;

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

    public void createBoard(String title) {
        // Set default board image with fixed image.
        // https://stackoverflow.com/questions/4896223/how-to-get-an-uri-of-an-image-resource-in-android/38340580
        boardRepository.insertBoard(new Board(title, ("android.resource://com.ang.acb.personalpins/R.drawable.ic_dashboard_24dp")));
    }
}
