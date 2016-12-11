package com.orgware.cloudnarisample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import com.orgware.cloudnarisample.app.AppConstants;

/**
 * Created by nandagopal on 12/10/16.
 */
public class SplashActivity extends AppCompatActivity {

  private Handler handler = new Handler();
  String[] mPermission = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      boolean allPermissionsGranted = true;
      for (int i = 0, mPermissionLength = mPermission.length; i < mPermissionLength; i++) {
        String permission = mPermission[i];
        if (ActivityCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED) {
          allPermissionsGranted = false;
          break;
        }
      }
      if (!allPermissionsGranted) {
        ActivityCompat.requestPermissions(this, mPermission, AppConstants.REQUEST_PERMISSION_CODE);
      } else {
        navigateToNextActivity();
      }
    } else {
      navigateToNextActivity();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == AppConstants.REQUEST_PERMISSION_CODE) {
      boolean allPermissionGranted = true;
      if (grantResults.length == permissions.length) {
        for (int i = 0; i < permissions.length; i++) {
          if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
            allPermissionGranted = false;
            break;
          }
        }
      } else {
        allPermissionGranted = false;
      }
      if (allPermissionGranted) {
        navigateToNextActivity();
      } else {
        navigateToNextActivity();
      }
    }
  }

  private void navigateToNextActivity() {
    handler.postDelayed(new Runnable() {
      @Override public void run() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
      }
    }, 1000);
  }
}
