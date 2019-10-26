package com.ang.acb.personalpins.ui.pins;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.databinding.FragmentPinSelectBinding;
import com.ang.acb.personalpins.ui.boards.BoardsViewModel;
import com.ang.acb.personalpins.ui.common.MainActivity;
import com.ang.acb.personalpins.utils.GridMarginDecoration;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SelectPinsFragment extends Fragment {

    public static final String ARG_BOARD_ID = "ARG_BOARD_ID";

    private FragmentPinSelectBinding binding;
    private PinsViewModel pinsViewModel;
    private BoardsViewModel boardsViewModel;
    private SelectPinsAdapter selectPinsAdapter;
    private long boardId;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public SelectPinsFragment() {}

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
        binding = FragmentPinSelectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModels();
        initAdapter();
        populateUi();
    }

    private void initViewModels() {
        pinsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PinsViewModel.class);
        boardsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(BoardsViewModel.class);
        boardsViewModel.setBoardId(boardId);
    }

    private void initAdapter() {
        selectPinsAdapter = new SelectPinsAdapter(new SelectPinsAdapter.PinSelectCallback() {
            @Override
            public void onPinChecked(Pin pin) {
                // Save the result into the database and show a snackbar message.
                pinsViewModel.onPinChecked(boardId, pin.getId());
            }

            @Override
            public void onPinUnchecked(Pin pin) {
                // Delete item from the database and show a snackbar message.
                pinsViewModel.onPinUnchecked(boardId, pin.getId());
            }
        });

        binding.rvAllPins.setLayoutManager(new GridLayoutManager(
                getHostActivity(), getResources().getInteger(R.integer.columns_3)));
        binding.rvAllPins.addItemDecoration(new GridMarginDecoration(
                getHostActivity(), R.dimen.item_offset));
        binding.rvAllPins.setAdapter(selectPinsAdapter);
    }

    private void populateUi() {
        // Get all existing pins from the database.
        pinsViewModel.getAllPins().observe(getViewLifecycleOwner(), allPins -> {
            int allPinsCount = (allPins == null) ? 0 : allPins.size();
            binding.setAllPinsCount(allPinsCount);

            if(allPins == null) {
                binding.allPinsEmptyState.setText(R.string.no_pins);
            } else {
                // Get pins associated with this particular board.
                boardsViewModel.getPinsForBoard().observe(getViewLifecycleOwner(), boardPins -> {
                    if (boardPins != null) {
                        selectPinsAdapter.updateData(allPins, getPinStates(allPins, boardPins));
                        // FIXME: IndexOutOfBoundsException: Index: 0, Size: 0
                        // boardsViewModel.updateBoardCover(boardPins.get(0).getPhotoUri(), boardId);
                        binding.executePendingBindings();
                    }
                });
            }
        });

        // Observe the Snackbar messages displayed when adding/removing pin to/from board.
        pinsViewModel.getSnackbarMessage().observe(getViewLifecycleOwner(), (Observer<Integer>) message ->
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show());
    }

    private LongSparseArray<Boolean> getPinStates(final List<Pin> allPins, final List<Pin> boardPins) {
        // Maps longs (the topic IDs) to booleans (are topics checked or unchecked).
        // It's more memory efficient than a HashMap<Long,Boolean>.
        LongSparseArray<Boolean> pinStates = new LongSparseArray<>();
        for (Pin pin: allPins) {
            boolean isAssignedToBoard = false;
            for (Pin boardPin: boardPins) {
                if(pin.getId() == boardPin.getId()){
                    isAssignedToBoard = true;
                    break;
                }
            }
            pinStates.put(pin.getId(), isAssignedToBoard);
        }

        return  pinStates;
    }

    private MainActivity getHostActivity(){
        return  (MainActivity) getActivity();
    }

}
