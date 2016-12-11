package com.orgware.cloudnarisample.network;

import android.content.Context;
import android.os.AsyncTask;
import com.cloudinary.Api;
import com.orgware.cloudnarisample.app.AppConstants;
import java.io.IOException;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nandagopal on 12/8/16.
 */
public class NetworkCall extends AsyncTask<String, Void, String> {

  //private ProgressDialog progressDialog;
  private Context context;
  private String url;
  private NetworkListener networkListener;
  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private Api api;

  public NetworkCall(Context context, String url, Api api, NetworkListener networkListener) {
    this.context = context;
    this.url = url;
    this.api = api;
    this.networkListener = networkListener;
  }

  @Override protected void onPreExecute() {
    super.onPreExecute();
    //progressDialog = new ProgressDialog(context);
    //progressDialog.setMessage("Getting Images From Server...");
    //progressDialog.setCancelable(true);
    //progressDialog.show();
  }

  @Override protected String doInBackground(String... strings) {

    OkHttpClient client = new OkHttpClient();

    String credential = Credentials.basic(AppConstants.CLOUD_API_KEY, AppConstants.CLOUD_SECRET);

    Request request = new Request.Builder().url(url).
        addHeader("Authorization", credential).build();

    Response response = null;
    try {
      response = client.newCall(request).execute();
      return response.body().string();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override protected void onPostExecute(String result) {
    super.onPostExecute(result);

    //if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

    networkListener.onRequestCompleted(result);
  }
}
