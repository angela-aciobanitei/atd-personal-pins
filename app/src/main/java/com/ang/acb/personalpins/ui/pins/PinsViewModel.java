package com.ang.acb.personalpins.ui.pins;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Board;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.data.repository.PinRepository;
import com.ang.acb.personalpins.utils.AbsentLiveData;
import com.ang.acb.personalpins.utils.SnackbarMessage;

import java.util.List;

import javax.inject.Inject;

public class PinsViewModel extends ViewModel {

    private PinRepository pinRepository;
    private LiveData<List<Pin>> allPins;
    private final MutableLiveData<Long> pinId = new MutableLiveData<>();
    private final SnackbarMessage snackbarMessage = new SnackbarMessage();

    @Inject
    public PinsViewModel(PinRepository pinRepository) {
        this.pinRepository = pinRepository;
    }

    public void setPinId(long value) {
        pinId.setValue(value);
    }

    public LiveData<List<Board>> getBoardsForPin() {
        return Transformations.switchMap(pinId, id -> {
            if (id == null) return AbsentLiveData.create();
            else return pinRepository.getBoardsForPin(id);
        });
    }

    public SnackbarMessage getSnackbarMessage() {
        return snackbarMessage;
    }

    public LiveData<List<Pin>> getAllPins() {
        if (allPins == null) {
            allPins = pinRepository.getAllPins();
        }
        return allPins;
    }

    public void createPin(Pin pin) {
        pinRepository.insertPin(pin);
    }

    public void onPinChecked(long boardId, long pinId) {
        pinRepository.insertBoardPin(boardId, pinId);
        snackbarMessage.setValue(R.string.pin_added_to_board);
    }

    public void onPinUnchecked(long boardId, long pinId) {
        pinRepository.deleteBoardPin(boardId, pinId);
        snackbarMessage.setValue(R.string.pin_removed_from_board);
    }
}
