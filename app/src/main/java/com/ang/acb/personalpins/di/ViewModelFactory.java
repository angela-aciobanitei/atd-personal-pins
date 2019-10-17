package com.ang.acb.personalpins.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {

    // Maps a Class that extends ViewModel as key, and a Provider of ViewModel (a
    // Dagger 2-specific class that let us provide — and so instantiate — a
    // dependency-injected class) as value.
    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
        this.creators = creators;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
        // Get the possible Provider for the given ViewModel from the Map.
        Provider<? extends ViewModel> creator = creators.get(modelClass);
        if (creator == null) {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : creators.entrySet()) {
                // If our Providers map doesn't have that specific key, check
                // if there is a subclass of the ViewModel we must instantiate.
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }
        if (creator == null) {
            // If all previous attempts of getting a valid Provider from the map
            // fail, throw an exception.
            throw new IllegalArgumentException("Unknown model class: " + modelClass);
        }
        try {
            // Finally, let Dagger create our ViewModel by invoking the get() method
            // on the Provider object and casting it to our final type.
            return (T) creator.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
