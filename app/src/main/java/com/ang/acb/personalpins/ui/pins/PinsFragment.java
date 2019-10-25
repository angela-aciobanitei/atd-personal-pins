package com.ang.acb.personalpins.ui.pins;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.databinding.FragmentPinListBinding;
import com.ang.acb.personalpins.ui.common.MainActivity;
import com.ang.acb.personalpins.utils.GridMarginDecoration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.ang.acb.personalpins.ui.pins.PinDetailsFragment.ARG_PIN_ID;;

public class PinsFragment extends Fragment {

    public static final String ARG_PIN_URI = "ARG_PIN_URI";
    public static final String ARG_PIN_IS_VIDEO = "ARG_PIN_IS_VIDEO";

    private FragmentPinListBinding binding;
    private PinsViewModel pinsViewModel;
    private PinsAdapter pinsAdapter;
    private Uri pinUri;
    private boolean isVideo;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public PinsFragment() {}

    @Override
    public void onAttach(@NotNull Context context) {
        // When using Dagger for injecting Fragments, inject as early as possible.
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and get an instance of the binding class.
        binding = FragmentPinListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModel();
        initAdapter();
        populateUi();
        onCreateNewPin();
    }

    private void initViewModel() {
        pinsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PinsViewModel.class);
    }

    private void initAdapter() {
        pinsAdapter = new PinsAdapter(this::onPinClick);
        binding.rvAllPins.setAdapter(pinsAdapter);
        binding.rvAllPins.setLayoutManager(new GridLayoutManager(
                getHostActivity(), getResources().getInteger(R.integer.span_count)));
        binding.rvAllPins.addItemDecoration(new GridMarginDecoration(
                getHostActivity(), R.dimen.item_offset));
    }

    private void onPinClick(Pin pin) {
        // On item click navigate to pin details fragment
        Bundle args = new Bundle();
        args.putLong(ARG_PIN_ID, pin.getId());
        NavHostFragment.findNavController(PinsFragment.this)
                .navigate(R.id.action_pin_list_to_pin_details, args);
    }

    private void populateUi() {
        pinsViewModel.getAllPins().observe(getViewLifecycleOwner(), pins -> {
            int allPinsCount = (pins == null) ? 0 : pins.size();
            binding.setAllPinsCount(allPinsCount);

            if(allPinsCount != 0) pinsAdapter.submitList(pins);
            else binding.allPinsEmptyState.setText(R.string.no_pins);

            binding.executePendingBindings();
        });
    }

    private void onCreateNewPin() {
        binding.newPinButton.setOnClickListener(view -> {
            // Navigate to picker dialog fragment
            // See: https://stackoverflow.com/questions/50311637/navigation-architecture-component-dialog-fragments
            Navigation.findNavController(view).navigate(R.id.picker_dialog);
        });
    }

    private MainActivity getHostActivity(){
        return  (MainActivity) getActivity();
    }
}
