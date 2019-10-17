package com.ang.acb.personalpins.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ang.acb.personalpins.ui.boards.BoardsViewModel;
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
    @ViewModelKey(PinsViewModel.class)
    abstract ViewModel bindPinsViewModel(PinsViewModel pinsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
