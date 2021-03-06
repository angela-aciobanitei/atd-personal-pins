package com.ang.acb.personalpins.ui.pins;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.databinding.FragmentPinDetailsBinding;
import com.ang.acb.personalpins.ui.common.MainActivity;
import com.ang.acb.personalpins.utils.GlideApp;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


public class PinDetailsFragment extends Fragment {

    public static final String ARG_PIN_ID = "ARG_PIN_ID";
    public static final String ARG_PIN_IS_PHOTO = "ARG_PIN_IS_PHOTO";

    private static final String EXTRA_PLAYBACK_POSITION = "EXTRA_PLAYBACK_POSITION";
    private static final String EXTRA_SHOULD_PLAY = "EXTRA_SHOULD_PLAY";

    private FragmentPinDetailsBinding binding;
    private PinDetailsViewModel pinDetailsViewModel;
    private TagsAdapter tagsAdapter;
    private long pinId;
    private boolean isPhoto;

    private SimpleExoPlayer simpleExoPlayer;
    private boolean shouldPlayWhenReady;
    private long currentPlaybackPosition;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public PinDetailsFragment() {}

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
            pinId = getArguments().getLong(ARG_PIN_ID);
            isPhoto = getArguments().getBoolean(ARG_PIN_IS_PHOTO);
        }

        if (isPhoto) {
            postponeEnterTransition();
            setSharedElementEnterTransition(TransitionInflater.from(getContext())
                    .inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and get an instance of the binding class.
        binding = FragmentPinDetailsBinding.inflate(inflater, container, false);

        // Set the string value of the pin ID as the unique transition name
        // for the image view that will be used in the shared element transition.
        ViewCompat.setTransitionName(binding.pinDetailsPhoto, String.valueOf(pinId));
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        restoreInstanceState(savedInstanceState);
        initViewModel();
        observePin();
        observeTags();
        handleFavorite();
        handleNewTag();
        handleNewComment();
        viewComments();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_PLAYBACK_POSITION, currentPlaybackPosition);
        outState.putBoolean(EXTRA_SHOULD_PLAY, shouldPlayWhenReady);
    }

    private void restoreInstanceState(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_PLAYBACK_POSITION)) {
                currentPlaybackPosition = savedInstanceState.getLong(EXTRA_PLAYBACK_POSITION);
            }
            if (savedInstanceState.containsKey(EXTRA_SHOULD_PLAY)) {
                shouldPlayWhenReady = savedInstanceState.getBoolean(EXTRA_SHOULD_PLAY);
            }
        }
    }

    private void initViewModel() {
        pinDetailsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PinDetailsViewModel.class);
        pinDetailsViewModel.setPinId(pinId);
    }

    private void observePin() {
        pinDetailsViewModel.getPin().observe(getViewLifecycleOwner(), pin -> {
            if (pin != null) {
                binding.setPin(pin);
                if(pin.getPhotoUri() != null && !pin.getPhotoUri().isEmpty()) {
                    loadPhoto(Uri.parse(pin.getPhotoUri()));
                }
                else if(pin.getVideoUri() != null && !pin.getVideoUri().isEmpty()) {
                    initPlayer(Uri.parse(pin.getVideoUri()));
                }

                // Binding needs to be executed immediately.
                binding.executePendingBindings();
            }
        });
    }

    private void loadPhoto(Uri photoUri) {
        GlideApp.with(this)
                // Calling GlideApp.with() returns a RequestBuilder.
                // By default you get a Drawable RequestBuilder, but
                // you can change the requested type using as... methods.
                // For example, asBitmap() returns a Bitmap RequestBuilder.
                .asBitmap()
                .load(photoUri)
                // Tell Glide not to use its standard crossfade animation.
                .dontAnimate()
                // Display a placeholder until the image is loaded and processed.
                .placeholder(R.color.imagePlaceholder)
                // Keep track of errors and successful image loading.
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                   com.bumptech.glide.load.DataSource dataSource,
                                                   boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(binding.pinDetailsPhoto);
    }

    private void initPlayer(Uri videoUri) {
        // See: https://exoplayer.dev/hello-world
        if (simpleExoPlayer == null) {
            // Create the player using the ExoPlayerFactory.
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    Objects.requireNonNull(getContext()));

            // Attach the payer to the view.
            binding.pinDetailsExoplayerView.setPlayer(simpleExoPlayer);
        }

        // Create a media source representing the media to be played.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                Objects.requireNonNull(getContext()),
                Util.getUserAgent(getContext(), getString(R.string.app_name)));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);

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

    private void observeTags() {
        binding.rvTags.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        tagsAdapter =  new TagsAdapter();
        binding.rvTags.setAdapter(tagsAdapter);

        pinDetailsViewModel.getPinTags().observe(getViewLifecycleOwner(), tags -> {
            if(tags != null) tagsAdapter.submitList(tags);
            binding.executePendingBindings();
        });
    }

    private void handleFavorite() {
        binding.icAddToFavorite.setOnClickListener(view ->
                pinDetailsViewModel.onFavoriteClicked());

        // Observe the Snackbar messages displayed when adding/removing pin to/from favorites.
        pinDetailsViewModel.getSnackbarMessage().observe(
                getViewLifecycleOwner(), (Observer<Integer>) message ->
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show());
    }

    private void handleNewTag() {
        binding.icAddTag.setOnClickListener(view -> createNewTagDialog());
    }

    private void handleNewComment() {
        binding.icAddComment.setOnClickListener(view -> createNewCommentDialog());
    }

    private void createNewTagDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getHostActivity().getLayoutInflater()
                .inflate(R.layout.create_new_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();

        // Set title
        TextView title = dialogView.findViewById(R.id.dialog_new_title);
        title.setText(R.string.new_tag);

        // Set edit text hint
        final EditText editText = dialogView.findViewById(R.id.dialog_new_edit_text);
        editText.setHint(R.string.tag_name);

        Button saveButton = dialogView.findViewById(R.id.dialog_new_save_btn);
        saveButton.setOnClickListener(view -> {
            String input = editText.getText().toString();
            if (input.trim().length() != 0) {
                pinDetailsViewModel.createTag(pinId, input);
            }
            dialog.dismiss();
        });

        Button cancelButton = dialogView.findViewById(R.id.dialog_new_cancel_btn);
        cancelButton.setOnClickListener(view -> dialog.cancel());

        dialog.show();
    }

    private void createNewCommentDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getHostActivity().getLayoutInflater()
                .inflate(R.layout.create_new_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();

        // Set title
        TextView title = dialogView.findViewById(R.id.dialog_new_title);
        title.setText(R.string.new_comment);

        // Set edit text hint
        final EditText editText = dialogView.findViewById(R.id.dialog_new_edit_text);
        editText.setHint(R.string.comment_text);

        Button saveButton = dialogView.findViewById(R.id.dialog_new_save_btn);
        saveButton.setOnClickListener(view -> {
            String input = editText.getText().toString();
            if (input.trim().length() != 0) {
                pinDetailsViewModel.createComment(pinId, input);
            }
            dialog.dismiss();
        });

        Button cancelButton = dialogView.findViewById(R.id.dialog_new_cancel_btn);
        cancelButton.setOnClickListener(view -> dialog.cancel());

        dialog.show();
    }

    private void viewComments() {
        binding.viewCommentsTv.setOnClickListener(view -> {
            // Navigate to comments fragment and pass the pin ID as bundle arg.
            Bundle args = new Bundle();
            args.putLong(ARG_PIN_ID, pinId);
            NavHostFragment.findNavController(PinDetailsFragment.this)
                    .navigate(R.id.action_pin_details_to_comments, args);
        });
    }

    private MainActivity getHostActivity(){
        return  (MainActivity) getActivity();
    }
}
