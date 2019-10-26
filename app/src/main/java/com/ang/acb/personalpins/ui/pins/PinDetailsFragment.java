package com.ang.acb.personalpins.ui.pins;


import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.databinding.FragmentPinDetailsBinding;
import com.ang.acb.personalpins.ui.common.MainActivity;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_ID;


public class PinDetailsFragment extends Fragment {

    private FragmentPinDetailsBinding binding;
    private PinDetailsViewModel pinDetailsViewModel;
    private PinTagsAdapter tagsAdapter;
    private PinCommentsAdapter commentsAdapter;
    private long pinId;

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
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and get an instance of the binding class.
        binding = FragmentPinDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModel();
        observePin();
        observeTags();
        observeComments();
        createNewTag();
        createNewComment();
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

                if(pin.getPhotoUri() != null) displayPhoto(pin.getPhotoUri());
                else if(pin.getVideoUri() != null) playVideo(pin.getVideoUri());

                binding.executePendingBindings();
            }
        });
    }

    private void displayPhoto(String photoUriString) {
        binding.pinDetailsPhoto.setImageURI(Uri.parse(photoUriString));
    }

    private void playVideo(String videoUriString) {
        binding.pinDetailsVideoView.setVideoURI(Uri.parse(videoUriString));
        binding.pinDetailsVideoPlayBtn.setOnClickListener(view -> {
            if (binding.pinDetailsVideoView.isPlaying()){
                binding.pinDetailsVideoView.pause();
                binding.pinDetailsVideoPlayBtn.setVisibility(View.VISIBLE);
            } else {
                binding.pinDetailsVideoView.start();
                binding.pinDetailsVideoPlayBtn.setVisibility(View.INVISIBLE);
            }
        });

        binding.pinDetailsVideoView.setOnCompletionListener(mediaPlayer ->
                binding.pinDetailsVideoPlayBtn.setVisibility(View.VISIBLE));
    }

    private void observeTags() {
        tagsAdapter =  new PinTagsAdapter();
        binding.pinPartialInfo.rvTags.setAdapter(tagsAdapter);
        binding.pinPartialInfo.rvTags.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));

        pinDetailsViewModel.getPinTags().observe(getViewLifecycleOwner(), tags -> {
            binding.setTagsCount((tags == null) ? 0 : tags.size());
            if(tags != null) {
                tagsAdapter.submitList(tags);
            }

            binding.executePendingBindings();
        });
    }

    private void observeComments() {
        commentsAdapter = new PinCommentsAdapter();
        binding.pinPartialInfo.rvComments.setAdapter(commentsAdapter);
        binding.pinPartialInfo.rvComments.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));

        pinDetailsViewModel.getPinComments().observe(getViewLifecycleOwner(), comments -> {
            int commentsCount = (comments == null) ? 0 : comments.size();
            binding.setCommentsCount(commentsCount);
            if(commentsCount != 0) commentsAdapter.submitList(comments);

            binding.executePendingBindings();
        });
    }

    private void createNewTag() {
        binding.pinPartialInfo.newTagButton.setOnClickListener(view -> {
            createNewTagDialog();
        });
    }

    private void createNewComment() {
        binding.pinPartialInfo.newCommentButton.setOnClickListener(view -> {
            createNewCommentDialog();
        });
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
            if (input.trim().length() != 0) pinDetailsViewModel.createTag(pinId, input);
            dialog.dismiss();
        });

        Button cancelButton = dialogView.findViewById(R.id.dialog_new_cancel_btn);
        cancelButton.setOnClickListener(view -> {
            dialog.cancel();
        });

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
            if (input.trim().length() != 0) pinDetailsViewModel.createComment(pinId, input);
            dialog.dismiss();
        });

        Button cancelButton = dialogView.findViewById(R.id.dialog_new_cancel_btn);
        cancelButton.setOnClickListener(view -> {
            dialog.cancel();
        });

        dialog.show();
    }

    private MainActivity getHostActivity(){
        return  (MainActivity) getActivity();
    }
}
