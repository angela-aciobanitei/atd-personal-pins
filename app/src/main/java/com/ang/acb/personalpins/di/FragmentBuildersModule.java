package com.ang.acb.personalpins.di;

import com.ang.acb.personalpins.ui.boards.BoardsFragment;
import com.ang.acb.personalpins.ui.common.PhotoFragment;
import com.ang.acb.personalpins.ui.common.VideoFragment;
import com.ang.acb.personalpins.ui.pins.PinsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract BoardsFragment contributeBoardsFragment();

    @ContributesAndroidInjector
    abstract PinsFragment contributePinsFragment();

    @ContributesAndroidInjector
    abstract PhotoFragment contributePhotoFragment();

    @ContributesAndroidInjector
    abstract VideoFragment contributeVideoFragment();
}
