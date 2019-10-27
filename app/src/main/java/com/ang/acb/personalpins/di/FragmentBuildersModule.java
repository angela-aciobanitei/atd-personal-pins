package com.ang.acb.personalpins.di;

import com.ang.acb.personalpins.ui.boards.BoardDetailsFragment;
import com.ang.acb.personalpins.ui.boards.BoardsFragment;
import com.ang.acb.personalpins.ui.pins.CommentsFragment;
import com.ang.acb.personalpins.ui.pins.FavoritePinsFragment;
import com.ang.acb.personalpins.ui.pins.PinDetailsFragment;
import com.ang.acb.personalpins.ui.pins.PinEditFragment;
import com.ang.acb.personalpins.ui.pins.SelectPinsFragment;
import com.ang.acb.personalpins.ui.pins.PinsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract BoardsFragment contributeBoardsFragment();

    @ContributesAndroidInjector
    abstract BoardDetailsFragment contributeBoardDetailsFragment();

    @ContributesAndroidInjector
    abstract PinsFragment contributePinsFragment();

    @ContributesAndroidInjector
    abstract SelectPinsFragment contributePinSelectFragment();

    @ContributesAndroidInjector
    abstract PinDetailsFragment contributePinDetailsFragment();

    @ContributesAndroidInjector
    abstract CommentsFragment contributeCommentsFragment();

    @ContributesAndroidInjector
    abstract PinEditFragment contributePinEditFragment();

    @ContributesAndroidInjector
    abstract FavoritePinsFragment contributeFavoritePinsFragment();
}
