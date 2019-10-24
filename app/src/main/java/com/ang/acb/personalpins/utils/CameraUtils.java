package com.ang.acb.personalpins.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.ang.acb.personalpins.ui.common.MainActivity;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

public class CameraUtils {

    public static final int REQUEST_TAKE_PHOTO = 103;
    public static final int REQUEST_TAKE_VIDEO = 104;
    public static final int REQUEST_PICK_PHOTO = 106;
    public static final int REQUEST_PICK_VIDEO = 107;
    public static final int MEDIA_TYPE_IMAGE = 200;
    public static final int MEDIA_TYPE_VIDEO = 201;

    public static void pickPhoto(Fragment fragment) {
        // https://developer.android.com/reference/android/content/Intent#ACTION_OPEN_DOCUMENT
        Intent pickPhotoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickPhotoIntent.setType("image/*");
        fragment.startActivityForResult(pickPhotoIntent, REQUEST_PICK_PHOTO);
    }

    public static void pickVideo(Fragment fragment) {
        Intent pickVideoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickVideoIntent.setType("video/*");
        // See: https://stackoverflow.com/questions/6147884/onactivityresult-is-not-being-called-in-fragment
        fragment.startActivityForResult(pickVideoIntent, REQUEST_PICK_VIDEO);
    }

    public static Uri takePhoto(MainActivity activity, Fragment fragment) {
        // See: https://developer.android.com/training/camera/photobasics
        Uri uri = getOutputMediaFileUri(activity, MEDIA_TYPE_IMAGE);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        fragment.startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);

        return uri;
    }

    public static Uri recordVideo(MainActivity activity, Fragment fragment) {
        // See:  https://developer.android.com/training/camera/videobasics
        Uri uri = getOutputMediaFileUri(activity, MEDIA_TYPE_VIDEO);
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);

        return uri;
    }

    private static Uri getOutputMediaFileUri(MainActivity activity, int mediaType) {
        Uri uri = null;
        if (isExternalStorageAvailable()) {
            // Directory: /storage/emulated/0/Android/data/com.ang.acb.personalpins/files/Pictures
            File mediaStorageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
            uri = FileProvider.getUriForFile(activity,
                    "com.ang.acb.personalpins.fileprovider" , mediaFile);
            Timber.d("File content Uri: %s", uri);
        }

        return uri;
    }

    private static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
