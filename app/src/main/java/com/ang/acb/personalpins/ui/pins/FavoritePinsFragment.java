package com.ang.acb.personalpins.ui.pins;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.databinding.FragmentFavoritePinsBinding;
import com.ang.acb.personalpins.databinding.FragmentPinListBinding;
import com.ang.acb.personalpins.utils.GridMarginDecoration;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_ID;

public class FavoritePinsFragment extends Fragment {

    private FragmentFavoritePinsBinding binding;
    private PinsViewModel pinsViewModel;
    private PinsAdapter pinsAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public FavoritePinsFragment() {}

    @Override
    public void onAttach(@NotNull Context context) {
        // When using Dagger for injecting Fragments, inject as early as possible.
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and get an instance of the binding class.
        binding = FragmentFavoritePinsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModel();
        initAdapter();
        populateUi();
    }

    private void initViewModel() {
        pinsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PinsViewModel.class);
    }

    private void initAdapter() {
        pinsAdapter = new PinsAdapter(this::onPinClick);
        binding.rvFavorites.setLayoutManager(new GridLayoutManager(
                getContext(), getResources().getInteger(R.integer.columns_3)));
        binding.rvFavorites.addItemDecoration(new GridMarginDecoration(
                getContext(), R.dimen.item_offset));
        binding.rvFavorites.setAdapter(pinsAdapter);
    }

    private void onPinClick(Pin pin) {
        // On item click navigate to pin details fragment
        Bundle args = new Bundle();
        args.putLong(ARG_PIN_ID, pin.getId());
        NavHostFragment.findNavController(FavoritePinsFragment.this)
                .navigate(R.id.action_favorite_pins_to_pin_details, args);
    }

    private void populateUi() {
        pinsViewModel.getAllFavoritePins().observe(getViewLifecycleOwner(), pins -> {
            int favoritesCount = (pins == null) ? 0 : pins.size();
            binding.setFavoritesCount(favoritesCount);

            if(favoritesCount != 0) pinsAdapter.submitList(pins);
            else binding.favoritesEmptyState.setText(R.string.no_favorites);

            binding.executePendingBindings();
        });
    }

}
