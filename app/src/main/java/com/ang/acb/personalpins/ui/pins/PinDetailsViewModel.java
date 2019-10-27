package com.ang.acb.personalpins.ui.pins;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Comment;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.data.entity.Tag;
import com.ang.acb.personalpins.data.repository.PinRepository;
import com.ang.acb.personalpins.utils.AbsentLiveData;
import com.ang.acb.personalpins.utils.SnackbarMessage;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * Stores and manages UI-related data in a lifecycle conscious way.
 *
 * See: https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
 * See: https://medium.com/androiddevelopers/livedata-beyond-the-viewmodel-reactive-patterns-using-transformations-and-mediatorlivedata-fda520ba00b7
 */
public class PinDetailsViewModel extends ViewModel {

    private PinRepository pinRepository;
    private final MutableLiveData<Long> pinId = new MutableLiveData<>();
    private LiveData<List<Tag>> pinTags;
    private LiveData<List<Comment>> pinComments;
    private LiveData<Pin> pin;
    private final SnackbarMessage snackbarMessage = new SnackbarMessage();
    private boolean isFavorite;

    @Inject
    public PinDetailsViewModel(PinRepository pinRepository) {
        this.pinRepository = pinRepository;
    }

    public void setPinId(long id) {
        pinId.setValue(id);
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public LiveData<Pin> getPin() {
        if (pin == null) {
            pin = Transformations.switchMap(pinId, id -> {
                if (id == null) return AbsentLiveData.create();
                else return pinRepository.getPinById(id);
            });
        }
        return  pin;
    }

    public LiveData<List<Tag>> getPinTags() {
        if (pinTags == null) {
            pinTags = Transformations.switchMap(pinId, id -> {
                if (id == null) return AbsentLiveData.create();
                else return pinRepository.getTagsForPin(id);
            });
        }
        return  pinTags;
    }

    public LiveData<List<Comment>> getPinComments() {
        if (pinComments == null) {
            pinComments = Transformations.switchMap(pinId, id -> {
                if (id == null) return AbsentLiveData.create();
                else return pinRepository.getCommentsForPin(id);
            });
        }
        return  pinComments;
    }

    public SnackbarMessage getSnackbarMessage() {
        return snackbarMessage;
    }


    public void onFavoriteClicked() {
        Pin markedPin = Objects.requireNonNull(pin.getValue());
        if (!isFavorite) {
            pinRepository.markAsFavorite(Objects.requireNonNull(markedPin));
            snackbarMessage.setValue(R.string.pin_added_to_favorites);
            isFavorite = true;
        } else {
            pinRepository.markAsNotFavorite(Objects.requireNonNull(markedPin));
            snackbarMessage.setValue(R.string.pin_removed_from_favorites);
            isFavorite = false;
        }
    }

    public void createTag(long pinId, String tag) {
        pinRepository.insertTag(new Tag(pinId, tag));
    }

    public void createComment(long pinId, String comment) {
        pinRepository.insertComment(new Comment(pinId, comment));
    }
}
