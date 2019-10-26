package com.ang.acb.personalpins.ui.pins;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.personalpins.data.entity.Tag;
import com.ang.acb.personalpins.databinding.TagItemBinding;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tag> tags;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout and get an instance of the binding class.
        TagItemBinding itemBinding = TagItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),  parent, false);
        return new TagViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Bind item data
        Tag tag = tags.get(position);
        ((TagViewHolder) holder).bindTo(tag);
    }

    @Override
    public int getItemCount() {
        return tags == null ? 0 :  tags.size();
    }

    public void submitList(List<Tag> tags) {
        this.tags = tags;
        // Notify any registered observers that the data set has changed.
        notifyDataSetChanged();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        private TagItemBinding binding;

        // Required constructor matching super.
        TagViewHolder(@NonNull TagItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Tag tag) {
            // Bind data for this item.
            binding.setTag(tag);

            // Binding must be executed immediately.
            binding.executePendingBindings();
        }
    }
}
