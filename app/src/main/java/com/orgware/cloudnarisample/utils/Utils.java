package com.orgware.cloudnarisample.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.orgware.cloudnarisample.R;
import com.orgware.cloudnarisample.app.AppConstants;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {

  public static String cloudinaryUrlFromContext(Context context) {
    String url = "";
    try {
      PackageManager packageManager = context.getPackageManager();
      ApplicationInfo info =
          packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      if (info != null && info.metaData != null) {
        url = (String) info.metaData.get("CLOUDINARY_URL");
      }
    } catch (NameNotFoundException e) {
      // No metadata found
    }
    return url;
  }

  public static String generateUniqueKey() {
    return UUID.randomUUID().toString();
  }

  public static Map getCloudinaryConfig() {
    Map config = new HashMap();
    config.put("cloud_name", AppConstants.CLOUD_NAME);
    config.put("api_key", AppConstants.CLOUD_API_KEY);
    config.put("api_secret", AppConstants.CLOUD_SECRET);

    return config;
  }

  public static AlertDialog createImageChooserDialog(Context context,
      View.OnClickListener cameraClickListener, View.OnClickListener galleryClickListener) {
    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    View view = View.inflate(context, R.layout.dialog_image_chooser, null);
    alertDialogBuilder.setView(view);
    final AlertDialog alertDialog = alertDialogBuilder.create();
    view.findViewById(R.id.img_camera).setOnClickListener(cameraClickListener);
    view.findViewById(R.id.img_gallery).setOnClickListener(galleryClickListener);
    return alertDialog;
  }

  public static Uri selectCamera(Activity activity) {
    //Camera permission required for Marshmallow version

    // permission has been granted, continue as usual
    Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    File file = new File(Environment.getExternalStorageDirectory(), generateUniqueKey() + ".jpg");
    Uri outputFileUri = Uri.fromFile(file);
    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
    activity.startActivityForResult(captureIntent, AppConstants.REQUEST_CAMERA_PICK);
    return outputFileUri;
  }

  public static void selectGallery(Activity activity) {
    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    // Start the Intent
    activity.startActivityForResult(galleryIntent, AppConstants.REQUEST_GALLERY_PICK);
  }
}
