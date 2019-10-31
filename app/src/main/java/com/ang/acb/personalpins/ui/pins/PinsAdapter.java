package com.ang.acb.personalpins.ui.pins;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.databinding.PinItemBinding;
import com.ang.acb.personalpins.utils.GlideApp;

import java.util.List;

public class PinsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Pin> pins;
    private PinClickCallback clickCallback;

    public PinsAdapter(PinClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout and get an instance of the binding class.
        PinItemBinding itemBinding = PinItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),  parent, false);
        return new PinViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PinViewHolder) holder).bindTo(pins.get(position));
    }

    @Override
    public int getItemCount() {
        return pins == null ? 0 :  pins.size();
    }

    public void submitList(List<Pin> pins) {
        this.pins = pins;
        // Notify any registered observers that the data set has changed.
        notifyDataSetChanged();
    }

    public interface PinClickCallback {
        void onClick(Pin pin, ImageView sharedImageView);
    }

    class PinViewHolder extends RecyclerView.ViewHolder {

        private PinItemBinding binding;

        // Required constructor matching super
        PinViewHolder(@NonNull PinItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Pin pin) {
            // Bind data for this item.
            binding.setPin(pin);

            // Set the string value of the pin ID as the unique transition name
            // for the image view that will be used in the shared element transition.
            ViewCompat.setTransitionName(binding.pinItemCover, String.valueOf(pin.getId()));

            // Handle pin cover
            if (pin.getPhotoUri() != null) {
                GlideApp.with(binding.pinItemCover.getContext())
                        .load(Uri.parse(pin.getPhotoUri()))
                        .into(binding.pinItemCover);
            } else if (pin.getVideoUri() != null) {
                binding.pinItemVideoView.setVideoURI(Uri.parse(pin.getVideoUri()));
                // See: https://stackoverflow.com/questions/17079593/how-to-set-the-preview-image-in-videoview-before-playing
                binding.pinItemVideoView.pause();
                binding.pinItemVideoView.seekTo(100);
            }

            // Handle item click events
            binding.getRoot().setOnClickListener(view -> {
                if (clickCallback != null) {
                    clickCallback.onClick(pin, binding.pinItemCover);
                }
            });

            // Binding must be executed immediately.
            binding.executePendingBindings();
        }
    }
}
