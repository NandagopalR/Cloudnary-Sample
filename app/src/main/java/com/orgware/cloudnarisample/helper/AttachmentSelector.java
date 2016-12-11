package com.orgware.cloudnarisample.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;
import com.orgware.cloudnarisample.BuildConfig;
import com.orgware.cloudnarisample.app.AppConstants;
import com.orgware.cloudnarisample.utils.CommonUtils;
import com.orgware.cloudnarisample.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by nandagopal on 12/9/16.
 */
public class AttachmentSelector {

  private Activity activity;
  private Fragment fragment;
  private AttachmentSelectionListener listener;
  private Uri mCameraUri;
  private File mAudioFile;
  private String uniqueIdPerWorkOrder;

  private AttachmentSelector(String uniqueIdPerWorkOrder, AttachmentSelectionListener listener) {
    this.uniqueIdPerWorkOrder = uniqueIdPerWorkOrder;
    this.listener = listener;
  }

  public AttachmentSelector(String uniqueIdPerWorkOrder, Activity activity,
      AttachmentSelectionListener listener) {
    this(uniqueIdPerWorkOrder, listener);
    this.activity = activity;
  }

  public AttachmentSelector(String uniqueIdPerWorkOrder, Fragment fragment,
      AttachmentSelectionListener listener) {
    this(uniqueIdPerWorkOrder, listener);
    this.fragment = fragment;
  }

  private Uri createUriForCameraIntent(Context context) throws IOException {
    File parent = new File(context.getFilesDir(), BuildConfig.UPLOAD_FILES_FOLDER);
    parent.mkdirs();
    File file = new File(parent, uniqueIdPerWorkOrder + "_" + System.currentTimeMillis() + ".jpg");
    if (!file.exists()) file.createNewFile();

    return FileProvider.getUriForFile(context, BuildConfig.FILE_PROVIDER_AUTHORITY, file);
  }

  public Context getContext() {
    if (activity != null) {
      return activity;
    } else {
      return fragment.getContext();
    }
  }

  public void selectCameraAttachment() throws IOException {
    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    boolean isCameraAvailable = CommonUtils.isAvailable(getContext(), cameraIntent);
    if (isCameraAvailable) {
      mCameraUri = createUriForCameraIntent(getContext());
      cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
      cameraIntent.setFlags(
          Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
      // Workaround for Android bug.
      // grantUriPermission also needed for KITKAT,
      // see https://code.google.com/p/android/issues/detail?id=76683
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
        List<ResolveInfo> resInfoList = getContext().getPackageManager()
            .queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
          String packageName = resolveInfo.activityInfo.packageName;
          getContext().grantUriPermission(packageName, mCameraUri,
              Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
      }
      if (activity != null) {
        activity.startActivityForResult(cameraIntent, AppConstants.REQUEST_CAMERA_PICK);
      } else {
        fragment.startActivityForResult(cameraIntent, AppConstants.REQUEST_CAMERA_PICK);
      }
    } else {
      Toast.makeText(getContext(), "No app available to perform Camera action", Toast.LENGTH_SHORT)
          .show();
    }
  }

  public void selectGalleryAttachment() {
    Intent pickIntent =
        new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    pickIntent.setType("image/*");
    boolean isPickAvailable = CommonUtils.isAvailable(getContext(), pickIntent);

    if (isPickAvailable) {
      if (activity != null) {
        activity.startActivityForResult(pickIntent, AppConstants.REQUEST_GALLERY_PICK);
      } else {
        fragment.startActivityForResult(pickIntent, AppConstants.REQUEST_GALLERY_PICK);
      }
    } else {
      Toast.makeText(getContext(), "No app available to perform Gallery action", Toast.LENGTH_SHORT)
          .show();
    }
  }

  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) return false;

    if (requestCode == AppConstants.REQUEST_CAMERA_PICK
        || requestCode == AppConstants.REQUEST_GALLERY_PICK) {

      Uri attachmentUri;
      try {
        if (requestCode == AppConstants.REQUEST_CAMERA_PICK) {
          if (listener != null) {
            listener.onAttachmentSelected(new File(mCameraUri.getPath()));
          }
        } else {
          attachmentUri = data.getData();

          File dstFile = FileUtils.createAttachmentFile(getContext(),
              uniqueIdPerWorkOrder + "_" + FileUtils.getFileName(getContext(), attachmentUri));

          if (FileUtils.copyFile(getContext(), attachmentUri, dstFile)) {
            File file = new File(getContext().getFilesDir(),
                BuildConfig.UPLOAD_FILES_FOLDER + "/" + dstFile.getName());
            //Uri convertedUri =
            //    FileProvider.getUriForFile(getContext(), BuildConfig.FILE_PROVIDER_AUTHORITY, file);

            if (listener != null) {
              listener.onAttachmentSelected(file);
            }
          } else {
            Toast.makeText(getContext(), "Failed to attach file", Toast.LENGTH_SHORT).show();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(getContext(), "Failed to attach file", Toast.LENGTH_SHORT).show();
      }
      return true;
    } else {
      return false;
    }
  }

  public interface AttachmentSelectionListener {
    void onAttachmentSelected(File attachmentUri);
  }
}
