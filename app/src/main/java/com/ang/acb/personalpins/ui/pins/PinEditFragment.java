package com.ang.acb.personalpins.ui.pins;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
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
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_IS_VIDEO;
import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_URI;

public class PinEditFragment extends Fragment {

    private static final String CURRENT_PLAYBACK_POSITION_KEY = "CURRENT_PLAYBACK_POSITION_KEY";
    private static final String SHOULD_PLAY_WHEN_READY_KEY = "SHOULD_PLAY_WHEN_READY_KEY";

    private FragmentPinEditBinding binding;
    private PinsViewModel pinsViewModel;
    private String pinTitle;
    private Uri pinUri;
    private boolean isVideo;

    private SimpleExoPlayer simpleExoPlayer;
    private boolean shouldPlayWhenReady;
    private long currentPlaybackPosition;

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_PLAYBACK_POSITION_KEY, currentPlaybackPosition);
        outState.putBoolean(SHOULD_PLAY_WHEN_READY_KEY, shouldPlayWhenReady);
    }

    private void restoreInstanceState(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_PLAYBACK_POSITION_KEY)) {
                currentPlaybackPosition = savedInstanceState.getLong(CURRENT_PLAYBACK_POSITION_KEY);
            }
            if (savedInstanceState.containsKey(SHOULD_PLAY_WHEN_READY_KEY)) {
                shouldPlayWhenReady = savedInstanceState.getBoolean(SHOULD_PLAY_WHEN_READY_KEY);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restoreInstanceState(savedInstanceState);
        displayPhotoOrVideo();
        initNameInputListener();
        initViewModel();
        handleSaveButton();
        handleCancelButton();
    }

    private void displayPhotoOrVideo() {
        binding.setIsVideo(isVideo);

        if (!isVideo) {
            binding.pinEditPhoto.setImageURI(pinUri);
        } else {
            initPlayer(pinUri);
        }
    }

    private void initPlayer(Uri pinUri) {
        // See: https://exoplayer.dev/hello-world
        if (simpleExoPlayer == null) {
            // Create the player using the ExoPlayerFactory.
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    Objects.requireNonNull(getContext()));

            // Attach the payer to the view.
            binding.pinEditExoplayerView.setPlayer(simpleExoPlayer);
        }

        // Create a media source representing the media to be played.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                Objects.requireNonNull(getContext()),
                Util.getUserAgent(getContext(), getString(R.string.app_name)));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(pinUri);

        // Prepare the player.
        simpleExoPlayer.prepare(mediaSource);

        // Control the player.
        simpleExoPlayer.seekTo(currentPlaybackPosition);
        simpleExoPlayer.setPlayWhenReady(shouldPlayWhenReady);
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            // Returns the playback position in the current content window
            // or ad, in milliseconds.
            currentPlaybackPosition = simpleExoPlayer.getCurrentPosition();
            // Returns whether playback will proceed when ready (i.e. when
            // Player.getPlaybackState() == Player.STATE_READY.)
            shouldPlayWhenReady = simpleExoPlayer.getPlayWhenReady();

            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // See: https://www.raywenderlich.com/5573-media-playback-on-android-with-exoplayer-getting-started
        // Release the player in onPause() if on Android Marshmallow and below.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Release the player in onStop() if on Android Nougat and above
        // because of the multi window support that was added in Android N.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            releasePlayer();
        }
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
                if(isVideo) {
                    pinsViewModel.createPin(new Pin(
                            pinTitle, null, pinUri.toString(), false));
                }
                else {
                    pinsViewModel.createPin(new Pin(
                            pinTitle, pinUri.toString(), null, false));
                }

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
