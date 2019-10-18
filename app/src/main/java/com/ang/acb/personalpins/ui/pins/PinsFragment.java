package com.ang.acb.personalpins.ui.pins;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.data.entity.Pin;
import com.ang.acb.personalpins.databinding.FragmentPinListBinding;
import com.ang.acb.personalpins.ui.common.MainActivity;
import com.ang.acb.personalpins.utils.ErrorDialog;
import com.ang.acb.personalpins.utils.GridMarginDecoration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.ang.acb.personalpins.ui.pins.PinDetailsFragment.ARG_PIN_ID;

public class PinsFragment extends Fragment {

    private static final String FRAGMENT_DIALOG = "FRAGMENT_DIALOG";

    private static final int REQUEST_APP_PERMISSIONS = 102;
    private static final String[] APP_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};

    public static final int REQUEST_TAKE_PHOTO = 103;
    public static final int REQUEST_TAKE_VIDEO = 104;
    public static final int REQUEST_PICK_BOARD_PHOTO = 105;
    public static final int REQUEST_PICK_PIN_PHOTO = 106;
    public static final int REQUEST_PICK_PIN_VIDEO = 107;
    public static final int MEDIA_TYPE_IMAGE = 200;
    public static final int MEDIA_TYPE_VIDEO = 201;

    private FragmentPinListBinding binding;
    private PinsViewModel pinsViewModel;
    private PinsAdapter pinsAdapter;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and get an instance of the binding class.
        binding = FragmentPinListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModels();
        initAdapter();
        populateUi();
        takePhoto();
        recordVideo();
    }

    private void initViewModels() {
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

    private void createNewPin() {

    }

    private void takePhoto() {
        if (!permissionsGranted(APP_PERMISSIONS)) {
            ActivityCompat.requestPermissions(getHostActivity(),
                    APP_PERMISSIONS, REQUEST_APP_PERMISSIONS);
        } else {
            binding.takePictureButton.setOnClickListener(view -> {
                // See: https://developer.android.com/training/camera/photobasics
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);

            });
        }
    }

    private void recordVideo() {
        if (!permissionsGranted(APP_PERMISSIONS)) {
            ActivityCompat.requestPermissions(getHostActivity(),
                    APP_PERMISSIONS, REQUEST_APP_PERMISSIONS);
        } else {
            binding.recordVideoButton.setOnClickListener(view -> {
                // See:  https://developer.android.com/training/camera/videobasics
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Uri uri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
            });
        }
    }

    private Uri getOutputMediaFileUri(int mediaType) {
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
            mediaFile = File.createTempFile(filePrefix, fileSuffix, mediaStorageDir);
            Timber.d( "Media File: %s" , Uri.fromFile(mediaFile));


        } catch (IOException e){
            Timber.d("Error creating file: " + mediaStorageDir.getAbsolutePath() + filePrefix + fileSuffix);
        }

        // Generate the Content URI for a File
        // See: https://developer.android.com/reference/androidx/core/content/FileProvider#GetUri
        Uri uri = FileProvider.getUriForFile(
                                getHostActivity(),
                                "com.ang.acb.personalpins.fileprovider" ,
                                mediaFile);
        // Expected Uri: content://com.ang.acb.personalpins.fileprovider/external_files/Android/data/com.ang.acb
        Timber.d("Uri: %s", uri);

        return uri;
    }

    private boolean permissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getHostActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
       if (requestCode == REQUEST_APP_PERMISSIONS) {
            if (grantResults.length == APP_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        ErrorDialog.newInstance(getString(R.string.video_request_rationale))
                                .show(getChildFragmentManager(), FRAGMENT_DIALOG);
                        break;
                    }
                }
            } else {
                ErrorDialog.newInstance(getString(R.string.video_request_rationale))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_PICK_PIN_PHOTO){
                Uri pinImageUri = data.getData();
                // TODO Start PinEditFragment and pass the Uri

            } else if(requestCode == REQUEST_PICK_PIN_VIDEO){
                Uri pinVideoUri = data.getData();
                // TODO Start PinEditFragment and pass the Uri
            }
        }
    }

    private MainActivity getHostActivity(){
        return  (MainActivity) getActivity();
    }
}
