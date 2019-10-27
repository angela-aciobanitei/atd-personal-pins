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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_ID;


public class PinDetailsFragment extends Fragment {

    private FragmentPinDetailsBinding binding;
    private PinDetailsViewModel pinDetailsViewModel;
    private TagsAdapter tagsAdapter;
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
        createNewTag();
        createNewComment();
        viewComments();
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
        binding.rvTags.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        tagsAdapter =  new TagsAdapter();
        binding.rvTags.setAdapter(tagsAdapter);

        pinDetailsViewModel.getPinTags().observe(getViewLifecycleOwner(), tags -> {
            if(tags != null) tagsAdapter.submitList(tags);
            binding.executePendingBindings();
        });
    }

    private void createNewTag() {
        binding.icAddTag.setOnClickListener(view -> {
            createNewTagDialog();
        });
    }

    private void createNewComment() {
        binding.icAddComment.setOnClickListener(view -> {
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
            if (input.trim().length() != 0) {
                pinDetailsViewModel.createTag(pinId, input);
            }
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
            if (input.trim().length() != 0) {
                pinDetailsViewModel.createComment(pinId, input);
            }
            dialog.dismiss();
        });

        Button cancelButton = dialogView.findViewById(R.id.dialog_new_cancel_btn);
        cancelButton.setOnClickListener(view -> {
            dialog.cancel();
        });

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
