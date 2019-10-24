package com.ang.acb.personalpins.ui.pins;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.databinding.FragmentPinListBinding;
import com.ang.acb.personalpins.ui.common.MainActivity;
import com.ang.acb.personalpins.utils.GridMarginDecoration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.ang.acb.personalpins.ui.pins.PinDetailsFragment.ARG_PIN_ID;;

public class PinsFragment extends Fragment {

    public static final String ARG_PIN_URI = "ARG_PIN_URI";
    public static final String ARG_PIN_IS_VIDEO = "ARG_PIN_IS_VIDEO";

    public static final int REQUEST_TAKE_PHOTO = 103;
    public static final int REQUEST_TAKE_VIDEO = 104;
    public static final int REQUEST_PICK_PHOTO = 106;
    public static final int REQUEST_PICK_VIDEO = 107;
    public static final int MEDIA_TYPE_IMAGE = 200;
    public static final int MEDIA_TYPE_VIDEO = 201;

    private FragmentPinListBinding binding;
    private PinsViewModel pinsViewModel;
    private PinsAdapter pinsAdapter;
    private Uri pinUri;
    private boolean isVideo;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public PinsFragment() {}

    @Override
    public void onAttach(@NotNull Context context) {
        // When using Dagger for injecting Fragments, inject as early as possible.
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and get an instance of the binding class.
        binding = FragmentPinListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModel();
        initAdapter();
        populateUi();
    }

    private void initViewModel() {
        pinsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PinsViewModel.class);
    }

    private void initAdapter() {
        pinsAdapter = new PinsAdapter(this::onPinClick);
        binding.rvAllPins.setAdapter(pinsAdapter);
        binding.rvAllPins.setLayoutManager(new GridLayoutManager(
                getHostActivity(), getResources().getInteger(R.integer.span_count)));
        binding.rvAllPins.addItemDecoration(new GridMarginDecoration(
                getHostActivity(), R.dimen.item_offset));
    }

    private void onPinClick(Pin pin) {
        // On item click navigate to pin details fragment
        Bundle args = new Bundle();
        args.putLong(ARG_PIN_ID, pin.getId());
        NavHostFragment.findNavController(PinsFragment.this)
                .navigate(R.id.action_pin_list_to_pin_details, args);
    }

    private void populateUi() {
        pinsViewModel.getAllPins().observe(getViewLifecycleOwner(), pins -> {
            int allPinsCount = (pins == null) ? 0 : pins.size();
            binding.setAllPinsCount(allPinsCount);

            if(allPinsCount != 0) pinsAdapter.submitList(pins);
            else binding.allPinsEmptyState.setText(R.string.no_pins);

            binding.executePendingBindings();
        });
    }

//    private void createNewPin() {
//        binding.newPinButton.setOnClickListener(view -> {
//            // Navigate to picker dialog fragment
//            NavHostFragment.findNavController(PinsFragment.this)
//                    .navigate(R.id.action_pin_list_to_picker_dialog);
//        });
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See: https://stackoverflow.com/questions/6147884/onactivityresult-is-not-being-called-in-fragment
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    isVideo = false;
                    navigateToPinDetailsFragment();
                    break;
                case REQUEST_PICK_PHOTO:
                    isVideo = false;
                    if (data != null) pinUri = data.getData();
                    navigateToPinDetailsFragment();
                    break;
                case REQUEST_TAKE_VIDEO:
                    isVideo = true;
                    navigateToPinDetailsFragment();
                    break;
                case REQUEST_PICK_VIDEO:
                    isVideo = true;
                    if (data != null) pinUri = data.getData();
                    navigateToPinDetailsFragment();
                    break;
            }
        }
    }

    private void navigateToPinDetailsFragment() {
        String pinUriString = pinUri.toString();
        Bundle args = new Bundle();
        args.putString(ARG_PIN_URI, pinUriString);
        args.putBoolean(ARG_PIN_IS_VIDEO, isVideo);
        NavHostFragment.findNavController(PinsFragment.this)
                .navigate(R.id.action_pin_list_to_pin_edit, args);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.camera_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.take_photo:
                takePhoto();
                return true;
            case R.id.pick_photo:
                pickPhoto();
                return true;
            case R.id.record_video:
                recordVideo();
                return true;
            case R.id.pick_video:
                pickVideo();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pickPhoto() {
        // https://developer.android.com/reference/android/content/Intent#ACTION_OPEN_DOCUMENT
        Intent pickPhotoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickPhotoIntent.setType("image/*");
        if (pickPhotoIntent.resolveActivity(getHostActivity().getPackageManager()) != null) {
            startActivityForResult(pickPhotoIntent, REQUEST_PICK_PHOTO);
        }
    }

    private void pickVideo() {
        Intent pickVideoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickVideoIntent.setType("video/*");
        // See: https://stackoverflow.com/questions/6147884/onactivityresult-is-not-being-called-in-fragment
        if (pickVideoIntent.resolveActivity(getHostActivity().getPackageManager()) != null) {
            startActivityForResult(pickVideoIntent, REQUEST_PICK_VIDEO);
        }
    }

    private void takePhoto() {
        // See: https://developer.android.com/training/camera/photobasics
        pinUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, pinUri);
        startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
    }

    private void recordVideo() {
        // See:  https://developer.android.com/training/camera/videobasics
        pinUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, pinUri);
        startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
    }

    private Uri getOutputMediaFileUri(int mediaType) {
        Uri uri = null;
        if (isExternalStorageAvailable()) {
            // Directory: /storage/emulated/0/Android/data/com.ang.acb.personalpins/files/Pictures
            File mediaStorageDir = getHostActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Timber.d("Media Storage Directory: %s" , mediaStorageDir);

            // Create the prefix and suffix for the file.
            String filePrefix = "";
            String fileSuffix = "";
            if (mediaType == MEDIA_TYPE_IMAGE) {
                filePrefix = "IMG_"+ System.currentTimeMillis();
                fileSuffix = ".jpg";
            } else if(mediaType == MEDIA_TYPE_VIDEO) {
                filePrefix = "VID_"+ System.currentTimeMillis();
                fileSuffix = ".mp4";
            } else {
                return null;
            }

            // Create the media file
            File mediaFile = null;
            try {
                // See: https://developer.android.com/training/data-storage/files/internal#WriteCacheFileInternal
                mediaFile = File.createTempFile(filePrefix, fileSuffix, mediaStorageDir);
                Timber.d( "Media File: %s" , Uri.fromFile(mediaFile));
            } catch (IOException e){
                Timber.d("Error creating file: " +
                        mediaStorageDir.getAbsolutePath() + filePrefix + fileSuffix);
            }

            // Generate the Content URI for a File
            // See: https://developer.android.com/reference/androidx/core/content/FileProvider#GetUri
            uri = FileProvider.getUriForFile(getHostActivity(),
                    "com.ang.acb.personalpins.fileprovider" , mediaFile);
            Timber.d("File content Uri: %s", uri);
        }

        return uri;
    }

    private boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private MainActivity getHostActivity(){
        return  (MainActivity) getActivity();
    }
}
