package com.ang.acb.personalpins.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ang.acb.personalpins.ui.boards.BoardDetailsViewModel;
import com.ang.acb.personalpins.ui.boards.BoardsViewModel;
import com.ang.acb.personalpins.ui.pins.PinDetailsViewModel;
import com.ang.acb.personalpins.ui.pins.PinsViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(BoardsViewModel.class)
    abstract ViewModel bindBoardsViewModel(BoardsViewModel boardsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BoardDetailsViewModel.class)
    abstract ViewModel bindBoardDetailsViewModel(BoardDetailsViewModel boardDetailsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PinsViewModel.class)
    abstract ViewModel bindPinsViewModel(PinsViewModel pinsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PinDetailsViewModel.class)
    abstract ViewModel bindPinDetailsViewModel(PinDetailsViewModel pinDetailsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
