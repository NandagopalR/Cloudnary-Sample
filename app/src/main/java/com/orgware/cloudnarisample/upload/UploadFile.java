package com.orgware.cloudnarisample.upload;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.orgware.cloudnarisample.network.NetworkListener;
import com.orgware.cloudnarisample.utils.Utils;
import java.io.File;
import java.io.IOException;

/**
 * Created by nandagopal on 12/8/16.
 */
public class UploadFile extends AsyncTask<String, Void, String> {

  public static final String TAG = "UploadFile";

  private ProgressDialog progressDialog;
  private Context context;
  private Cloudinary cloudinary;
  private String filePath;
  private Uri uri;
  private File file;
  private NetworkListener networkListener;

  public UploadFile(Context context, Cloudinary cloudinary, String filePath,
      NetworkListener networkListener) {
    this.context = context;
    this.cloudinary = cloudinary;
    this.filePath = filePath;
    this.networkListener = networkListener;
  }

  public UploadFile(Context context, Cloudinary cloudinary, File file) {
    this.context = context;
    this.cloudinary = cloudinary;
    this.file = file;
  }

  @Override protected void onPreExecute() {
    super.onPreExecute();
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(context);
      progressDialog.setMessage("Uploading Image");
      progressDialog.setCancelable(false);
    }
    progressDialog.show();
  }

  @Override protected void onCancelled() {
    super.onCancelled();

    if (progressDialog == null) {
      progressDialog = new ProgressDialog(context);
      progressDialog.setMessage("Loading");
      progressDialog.setCancelable(true);
    }

    if (progressDialog.isShowing()) progressDialog.dismiss();
  }

  @Override protected String doInBackground(String... strings) {
    String status = "success";
    try {
      cloudinary.uploader()
          .upload(new File(filePath),
              ObjectUtils.asMap("public_id", Utils.generateUniqueKey() + ".jpg"));
    } catch (IOException e) {
      status = "fail";
      e.printStackTrace();
    }
    return status;
  }

  @Override protected void onPostExecute(String result) {
    super.onPostExecute(result);

    if (progressDialog == null) {
      progressDialog = new ProgressDialog(context);
      progressDialog.setMessage("Loading");
      progressDialog.setCancelable(true);
    }

    if (progressDialog.isShowing()) progressDialog.dismiss();

    if (networkListener != null) {
      networkListener.onUploadCompletedListener(result);
    }
  }
}
