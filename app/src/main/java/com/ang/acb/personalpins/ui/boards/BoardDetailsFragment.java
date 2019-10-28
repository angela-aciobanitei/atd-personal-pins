package com.ang.acb.personalpins.ui.boards;


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
import com.ang.acb.personalpins.databinding.FragmentBoardDetailsBinding;
import com.ang.acb.personalpins.ui.common.MainActivity;
import com.ang.acb.personalpins.ui.pins.PinsAdapter;
import com.ang.acb.personalpins.utils.GridMarginDecoration;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static com.ang.acb.personalpins.ui.boards.BoardsFragment.ARG_BOARD_ID;
import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_ID;

public class BoardDetailsFragment extends Fragment {

    private FragmentBoardDetailsBinding binding;
    private BoardDetailsViewModel boardDetailsViewModel;
    private PinsAdapter pinsAdapter;
    private long boardId;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public BoardDetailsFragment() {}

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
            boardId = getArguments().getLong(ARG_BOARD_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and get an instance of the binding class.
        binding = FragmentBoardDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModel();
        initAdapter();
        observeBoard();
        observePins();
        onAddPins();
    }

    private void initViewModel() {
        boardDetailsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(BoardDetailsViewModel.class);
        boardDetailsViewModel.setBoardId(boardId);
    }

    private void initAdapter() {
        pinsAdapter = new PinsAdapter(this::onPinClick);
        binding.rvBoardPins.setAdapter(pinsAdapter);
        binding.rvBoardPins.setLayoutManager(new GridLayoutManager(
                getHostActivity(), getResources().getInteger(R.integer.columns_3)));
        binding.rvBoardPins.addItemDecoration(new GridMarginDecoration(
                getHostActivity(), R.dimen.grid_item_spacing));
    }

    private void onPinClick(Pin pin) {
        // On item click navigate to pin details fragment
        Bundle args = new Bundle();
        args.putLong(ARG_PIN_ID, pin.getId());
        NavHostFragment.findNavController(BoardDetailsFragment.this)
                .navigate(R.id.action_board_details_to_pin_details, args);
    }

    private void observeBoard() {
        boardDetailsViewModel.getBoard().observe(getViewLifecycleOwner(), board -> {
            if (board != null) {
                if (getHostActivity().getSupportActionBar() != null) {
                    getHostActivity().getSupportActionBar().setTitle(board.getTitle());
                }
            }
        });
    }

    private void observePins() {
        boardDetailsViewModel.getPinsForBoard().observe(getViewLifecycleOwner(), pins -> {
            int boardPinsCount = (pins == null) ? 0 : pins.size();
            binding.setBoardPinsCount(boardPinsCount);

            if(boardPinsCount != 0) pinsAdapter.submitList(pins);
            else binding.boardDetailsEmptyState.setText(R.string.no_board_pins);

            binding.executePendingBindings();
        });
    }

    private void onAddPins() {
        binding.addPinsButton.setOnClickListener(view -> {
            // Navigate to pin select fragment and pass the board ID as bundle arg.
            Bundle args = new Bundle();
            args.putLong(ARG_BOARD_ID, boardId);
            NavHostFragment.findNavController(BoardDetailsFragment.this)
                    .navigate(R.id.action_board_details_to_pin_select, args);
        });
    }

    private MainActivity getHostActivity(){
        return  (MainActivity) getActivity();
    }
}
