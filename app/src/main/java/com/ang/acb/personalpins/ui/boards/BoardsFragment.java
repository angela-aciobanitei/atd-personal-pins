package com.ang.acb.personalpins.ui.boards;


import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Board;
import com.ang.acb.personalpins.databinding.FragmentBoardsBinding;
import com.ang.acb.personalpins.ui.common.MainActivity;
import com.ang.acb.personalpins.utils.GridMarginDecoration;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class BoardsFragment extends Fragment {

    public static final String ARG_BOARD_ID = "ARG_BOARD_ID";

    private FragmentBoardsBinding binding;
    private BoardsAdapter boardsAdapter;
    private BoardsViewModel boardsViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public BoardsFragment() {}

    @Override
    public void onAttach(@NotNull Context context) {
        // Note: when using Dagger for injecting Fragment objects, inject as early
        // as possible. For this reason, call AndroidInjection.inject() in onAttach().
        // This also prevents inconsistencies if the Fragment is reattached.
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        binding = FragmentBoardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModel();
        initAdapter();
        handleNewBoardCreation();
        populateUi();
    }

    private void initViewModel() {
        boardsViewModel = ViewModelProviders.of(getHostActivity(), viewModelFactory)
                .get(BoardsViewModel.class);
    }

    private void initAdapter() {
        boardsAdapter = new BoardsAdapter(this::onBoardClick);
        binding.allBoards.rv.setAdapter(boardsAdapter);
        binding.allBoards.rv.setLayoutManager(new GridLayoutManager(
                getHostActivity(), getResources().getInteger(R.integer.columns_3)));
        binding.allBoards.rv.addItemDecoration(new GridMarginDecoration(
                getHostActivity(), R.dimen.grid_item_spacing));

    }

    private void onBoardClick(Board board) {
        // On item click navigate to board details fragment
        Bundle args = new Bundle();
        args.putLong(ARG_BOARD_ID, board.getId());
        NavHostFragment.findNavController(BoardsFragment.this)
                .navigate(R.id.action_board_list_to_board_details, args);
    }

    private void populateUi() {
        boardsViewModel.getAllBoards().observe(getViewLifecycleOwner(), boards -> {
            int boardsCount = (boards == null) ? 0 : boards.size();
            binding.setBoardsCount(boardsCount);

            if(boardsCount != 0) boardsAdapter.submitList(boards);
            else binding.allBoards.tv.setText(R.string.no_boards);

            binding.executePendingBindings();
        });
    }

    private void handleNewBoardCreation() {
        binding.newBoardButton.setOnClickListener(view -> {
            showNewBoardDialog();
        });
    }

    private void showNewBoardDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getHostActivity().getLayoutInflater()
                .inflate(R.layout.create_new_dialog, null);
        dialogBuilder.setView(dialogView);

        AlertDialog dialog = dialogBuilder.create();

        // Set title
        TextView title = dialogView.findViewById(R.id.dialog_new_title);
        title.setText(R.string.new_board);

        // Set edit text hint
        final EditText editText = dialogView.findViewById(R.id.dialog_new_edit_text);
        editText.setHint(R.string.board_name);

        Button saveButton = dialogView.findViewById(R.id.dialog_new_save_btn);
        saveButton.setOnClickListener(view -> {
            String input = editText.getText().toString();
            if (input.trim().length() != 0) boardsViewModel.createBoard(getContext(), input);
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
