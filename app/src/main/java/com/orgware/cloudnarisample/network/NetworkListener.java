package com.orgware.cloudnarisample.network;

/**
 * Created by nandagopal on 12/8/16.
 */

public interface NetworkListener {

  void onRequestCompleted(String result);

  void onUploadCompletedListener(String result);

  void onUploadFailedListener(String result);
}
