package com.ang.acb.personalpins.ui.pins;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


public class PinDetailsFragment extends Fragment {

    public static final String ARG_PIN_ID = "ARG_PIN_ID";

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
                binding.executePendingBindings();
            }
        });
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
        MainActivity activity = getHostActivity();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        View dialogView = activity.getLayoutInflater()
                .inflate(R.layout.create_new_dialog, null);
        dialogBuilder.setView(dialogView);

        // Set title
        TextView title = dialogView.findViewById(R.id.dialog_title);
        title.setText(R.string.new_tag);

        // Setup dialog buttons
        final EditText editText = dialogView.findViewById(R.id.dialog_edit_text);
        editText.setHint(R.string.tag_name);
        dialogBuilder.setPositiveButton(R.string.dialog_pos_button, (dialog, whichButton) -> {
            String input = editText.getText().toString();
            if (input.trim().length() != 0) pinDetailsViewModel.createTag(pinId, input);
            else dialog.dismiss();
        });
        dialogBuilder.setNegativeButton(R.string.dialog_neg_button, (dialog, whichButton) ->
                dialog.cancel()
        );

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // Customize dialog buttons
        Button posBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        posBtn.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
        posBtn.setTextColor(ContextCompat.getColor(activity,R.color.colorAccent));
        posBtn.setPadding(16, 0, 16, 0);
        Button negBtn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negBtn.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
        negBtn.setTextColor(ContextCompat.getColor(activity,R.color.colorAccent));
        negBtn.setPadding(16, 0, 16, 0);
    }

    private void createNewCommentDialog() {
        MainActivity activity = getHostActivity();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        View dialogView = activity.getLayoutInflater()
                .inflate(R.layout.create_new_dialog, null);
        dialogBuilder.setView(dialogView);

        // Set title
        TextView title = dialogView.findViewById(R.id.dialog_title);
        title.setText(R.string.new_comment);

        // Setup dialog buttons
        final EditText editText = dialogView.findViewById(R.id.dialog_edit_text);
        editText.setHint(R.string.comment_text);
        dialogBuilder.setPositiveButton(R.string.dialog_pos_button, (dialog, whichButton) -> {
            String input = editText.getText().toString();
            if (input.trim().length() != 0) pinDetailsViewModel.createComment(pinId, input);
            else dialog.dismiss();
        });
        dialogBuilder.setNegativeButton(R.string.dialog_neg_button, (dialog, whichButton) ->
                dialog.cancel()
        );

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // Customize dialog buttons
        Button posBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        posBtn.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
        posBtn.setTextColor(ContextCompat.getColor(activity,R.color.colorAccent));
        posBtn.setPadding(16, 0, 16, 0);
        Button negBtn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negBtn.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
        negBtn.setTextColor(ContextCompat.getColor(activity,R.color.colorAccent));
        negBtn.setPadding(16, 0, 16, 0);
    }

    private MainActivity getHostActivity(){
        return  (MainActivity) getActivity();
    }
}
