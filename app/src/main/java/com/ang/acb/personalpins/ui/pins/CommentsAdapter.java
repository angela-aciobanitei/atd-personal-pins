package com.ang.acb.personalpins.ui.pins;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.personalpins.data.entity.Comment;
import com.ang.acb.personalpins.databinding.CommentItemBinding;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Comment> comments;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout and get an instance of the binding class.
        CommentItemBinding itemBinding = CommentItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),  parent, false);
        return new CommentViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CommentViewHolder) holder).bindTo(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 :  comments.size();
    }

    public void submitList(List<Comment> comments) {
        this.comments = comments;
        // Notify any registered observers that the data set has changed.
        notifyDataSetChanged();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private CommentItemBinding binding;

        // Required constructor matching super.
        CommentViewHolder(@NonNull CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Comment comment) {
            // Bind data for this item.
            binding.setComment(comment);

            // Binding must be executed immediately.
            binding.executePendingBindings();
        }
    }
}
