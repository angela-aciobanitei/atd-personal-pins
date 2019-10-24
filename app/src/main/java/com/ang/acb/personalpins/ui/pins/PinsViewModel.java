package com.ang.acb.personalpins.ui.pins;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ang.acb.personalpins.data.entity.Comment;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.data.repository.PinRepository;

import java.util.List;

import javax.inject.Inject;

public class PinsViewModel extends ViewModel {

    private PinRepository pinRepository;
    private LiveData<List<Pin>> allPins;

    @Inject
    public PinsViewModel(PinRepository pinRepository) {
        this.pinRepository = pinRepository;
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
}
