package com.ang.acb.personalpins.ui.pins;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.databinding.FragmentPinEditBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_IS_VIDEO;
import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_URI;

public class PinEditFragment extends Fragment {

    private FragmentPinEditBinding binding;
    private PinsViewModel pinsViewModel;
    private String pinTitle;
    private Uri pinUri;
    private boolean isVideo;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public PinEditFragment() {}

    @Override
    public void onAttach(@NotNull Context context) {
        // When using Dagger for injecting Fragments, inject as early as possible.
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            pinUri = Uri.parse(getArguments().getString(ARG_PIN_URI));
            isVideo = getArguments().getBoolean(ARG_PIN_IS_VIDEO);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPinEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setIsVideo(isVideo);

        if (!isVideo) {
            displayPhoto(view);
        } else {
            playVideo();
        }

        initNameInputListener();
        initViewModel();
        handleSaveButton();
        handleCancelButton();
    }

    private void displayPhoto(View view) {
        Glide.with(view.getContext()).load(pinUri).into(binding.pinEditPhoto);
    }

    private void playVideo() {
        binding.pinEditVideoView.setVideoURI(pinUri);
        // Set the preview image in videoview before playing
        binding.pinEditVideoView.pause();
        binding.pinEditVideoView.seekTo(100);
        binding.pinEditVideoPlayBtn.setOnClickListener(view -> {
            if (binding.pinEditVideoView.isPlaying()){
                binding.pinEditVideoView.pause();
                binding.pinEditVideoPlayBtn.setVisibility(View.VISIBLE);
            } else {
                binding.pinEditVideoView.start();
                binding.pinEditVideoPlayBtn.setVisibility(View.INVISIBLE);
            }
        });

        binding.pinEditVideoView.setOnCompletionListener(mediaPlayer ->
                binding.pinEditVideoPlayBtn.setVisibility(View.VISIBLE));
    }

    private void initNameInputListener() {
        binding.pinNameEditText.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                pinTitle = binding.pinNameEditText.getText().toString();
                dismissKeyboard(view.getWindowToken());
                return true;
            }
            return false;
        });

        binding.pinNameEditText.setOnKeyListener((view, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN)
                    && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                pinTitle = binding.pinNameEditText.getText().toString();
                dismissKeyboard(view.getWindowToken());
                return true;
            }
            return false;
        });
    }

    private void dismissKeyboard(IBinder windowToken) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    private void initViewModel() {
        pinsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PinsViewModel.class);
    }

    private void handleSaveButton() {
        binding.pinEditSaveBtn.setOnClickListener(view -> {
            if (pinTitle != null && !pinTitle.isEmpty()) {
                // Save result into the database.
                if(isVideo) pinsViewModel.createPin(new Pin(pinTitle, null, pinUri.toString()));
                else pinsViewModel.createPin(new Pin(pinTitle, pinUri.toString(), null));
                // Navigate back to pin list fragment.
                Navigation.findNavController(view).popBackStack(R.id.pins, false);
            } else {
                Toast.makeText(getActivity(), getString(R.string.enter_pin_title),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCancelButton() {
        binding.pinEditCancelBtn.setOnClickListener(view -> {
            // Navigate back to pin list fragment.
            Navigation.findNavController(view).popBackStack(R.id.pins, false);
        });
    }

}
