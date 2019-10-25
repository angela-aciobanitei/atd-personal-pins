package com.ang.acb.personalpins.ui.boards;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.personalpins.data.entity.Board;
import com.ang.acb.personalpins.databinding.BoardItemBinding;

import java.util.List;

public class BoardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Board> boards;
    private BoardClickCallback clickCallback;

    public BoardsAdapter(BoardClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout and get an instance of the binding class.
        BoardItemBinding itemBinding = BoardItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),  parent, false);
        return new BoardViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Bind item data
        Board board = boards.get(position);
        ((BoardViewHolder) holder).bindTo(board);

        // Handle item click events
        holder.itemView.setOnClickListener(v -> {
            if (board != null && clickCallback != null) {
                clickCallback.onClick(board);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boards == null ? 0 :  boards.size();
    }

    public void submitList(List<Board> boards) {
        this.boards = boards;
        // Notify any registered observers that the data set has changed.
        notifyDataSetChanged();
    }

    public interface BoardClickCallback {
        void onClick(Board board);
    }

    class BoardViewHolder extends RecyclerView.ViewHolder {

        private BoardItemBinding binding;

        // Required constructor matching super
        BoardViewHolder(@NonNull BoardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        void bindTo(Board board) {
            // Bind data for this item.
            binding.setBoard(board);

            binding.boardCover.setImageURI(Uri.parse(board.getPhotoCoverUri()));

            // Binding must be executed immediately.
            binding.executePendingBindings();
        }
    }
}
