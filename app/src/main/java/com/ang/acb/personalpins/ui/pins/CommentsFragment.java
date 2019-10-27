package com.ang.acb.personalpins.ui.pins;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.databinding.FragmentCommentsBinding;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_ID;

public class CommentsFragment extends Fragment {

    private FragmentCommentsBinding binding;
    private PinDetailsViewModel pinDetailsViewModel;
    private CommentsAdapter commentsAdapter;
    private long pinId;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public CommentsFragment() {}

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
        binding = FragmentCommentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModel();
        initAdapter();
        observeComments();
    }

    private void initViewModel() {
        pinDetailsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PinDetailsViewModel.class);
        pinDetailsViewModel.setPinId(pinId);
    }

    private void initAdapter() {
        binding.rvComments.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
        commentsAdapter = new CommentsAdapter();
        binding.rvComments.setAdapter(commentsAdapter);
    }

    private void observeComments() {
        pinDetailsViewModel.getPinComments().observe(getViewLifecycleOwner(), comments -> {
            int commentsCount = (comments == null) ? 0 : comments.size();
            binding.setCommentsCount(commentsCount);
            if(commentsCount != 0) commentsAdapter.submitList(comments);
            else binding.commentsEmptyState.setText(R.string.no_comments);
            binding.executePendingBindings();
        });
    }
}
