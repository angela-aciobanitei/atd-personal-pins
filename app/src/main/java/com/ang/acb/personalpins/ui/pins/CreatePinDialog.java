package com.ang.acb.personalpins.ui.pins;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.ang.acb.personalpins.R;
import com.ang.acb.personalpins.ui.common.MainActivity;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_IS_VIDEO;
import static com.ang.acb.personalpins.ui.pins.PinsFragment.ARG_PIN_URI;


public class CreatePinDialog extends DialogFragment {

    public static final int REQUEST_TAKE_PHOTO = 103;
    public static final int REQUEST_TAKE_VIDEO = 104;
    public static final int REQUEST_PICK_PHOTO = 106;
    public static final int REQUEST_PICK_VIDEO = 107;
    public static final int MEDIA_TYPE_IMAGE = 200;
    public static final int MEDIA_TYPE_VIDEO = 201;

    private Uri pinUri;
    private boolean isVideo;
    private Button takePhotoBtn;
    private Button pickPhotoBtn;
    private Button recordVideoBtn;
    private Button pickVideoBtn;

    // Required empty public constructor
    public CreatePinDialog() {}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picker_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        takePhotoBtn = view.findViewById(R.id.take_photo_btn);
        pickPhotoBtn = view.findViewById(R.id.pick_photo_btn);
        recordVideoBtn = view.findViewById(R.id.record_video_btn);
        pickVideoBtn = view.findViewById(R.id.pick_video_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        takePhotoBtn.setOnClickListener(takePhoto -> {
            takePhoto();
        });

        pickPhotoBtn.setOnClickListener(pickPhoto -> {
            pickPhoto();
        });

        pickVideoBtn.setOnClickListener(pickVideo -> {
            pickVideo();
        });

        recordVideoBtn.setOnClickListener(recordVideo -> {
            recordVideo();
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See: https://stackoverflow.com/questions/6147884/onactivityresult-is-not-being-called-in-fragment
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    isVideo = false;
                    navigateToPinEditFragment();
                    break;
                case REQUEST_PICK_PHOTO:
                    isVideo = false;
                    if (data != null) pinUri = data.getData();
                    navigateToPinEditFragment();
                    break;
                case REQUEST_TAKE_VIDEO:
                    isVideo = true;
                    navigateToPinEditFragment();
                    break;
                case REQUEST_PICK_VIDEO:
                    isVideo = true;
                    if (data != null) pinUri = data.getData();
                    navigateToPinEditFragment();
                    break;
            }
        }
    }

    private void navigateToPinEditFragment() {
        String pinUriString = pinUri.toString();
        Bundle args = new Bundle();
        args.putString(ARG_PIN_URI, pinUriString);
        args.putBoolean(ARG_PIN_IS_VIDEO, isVideo);
        NavHostFragment.findNavController(CreatePinDialog.this)
                .navigate(R.id.action_create_pin_dialog_to_pin_edit, args);
    }

    private MainActivity getHostActivity(){
        return  (MainActivity) getActivity();
    }
}
