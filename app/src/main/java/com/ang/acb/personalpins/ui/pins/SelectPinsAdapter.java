package com.ang.acb.personalpins.ui.pins;

import android.net.Uri;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.databinding.PinSelectItemBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SelectPinsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Keeps track of all existing pins.
    private List<Pin> pins = new ArrayList<>();

    // Keeps track of which pins are checked/unchecked.
    private LongSparseArray<Boolean> pinStates = new LongSparseArray<>();

    private PinSelectCallback checkedCallback;

    SelectPinsAdapter(PinSelectCallback checkedCallback) {
        this.checkedCallback = checkedCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout and get an instance of the binding class.
        PinSelectItemBinding itemBinding = PinSelectItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PinSelectViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PinSelectViewHolder) holder).bindTo(pins.get(position));
    }

    @Override
    public int getItemCount() {
        return pins == null ? 0 :  pins.size();
    }

    public void updateData(List<Pin> pins, LongSparseArray<Boolean> pinStates) {
        this.pins = pins;
        this.pinStates = pinStates;
        // Notify any registered observers that the data set has changed.
        notifyDataSetChanged();
    }

    class PinSelectViewHolder extends RecyclerView.ViewHolder {

        private PinSelectItemBinding binding;

        // Required constructor matching super.
        PinSelectViewHolder(@NonNull PinSelectItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Pin pin) {
            // Bind data for this item.
            binding.setPin(pin);

            // Handle pin cover
            if (pin.getPhotoUri() != null) {
                Glide.with(itemView.getContext())
                        .load(Uri.parse(pin.getPhotoUri()))
                        .into(binding.pinSelectItemCover);
            } else if (pin.getVideoUri() != null) {
                binding.pinSelectItemVideoView.setVideoURI(Uri.parse(pin.getVideoUri()));
                binding.pinSelectItemVideoView.pause();
                binding.pinSelectItemVideoView.seekTo(100);
            }

            // Remove the previous OnCheckedChangeListener.
            binding.selectPinCheckbox.setOnCheckedChangeListener(null);

            // Get checkbox state from LongSparseArray pinStates.
            binding.selectPinCheckbox.setChecked(pinStates.get(pin.getId()));

            // Register a callback to be invoked when the checked state of this button changes.
            binding.selectPinCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (checkedCallback != null) {
                    if (isChecked) {
                        checkedCallback.onPinChecked(pin);
                    } else {
                        checkedCallback.onPinUnchecked(pin);
                    }
                }
            });

            // Binding must be executed immediately.
            binding.executePendingBindings();
        }
    }

    public interface PinSelectCallback {
        void onPinChecked(Pin pin);
        void onPinUnchecked(Pin pin);
    }
}