package com.orgware.cloudnarisample;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import com.orgware.cloudnarisample.adapter.UploadedImagesAdapter;
import com.orgware.cloudnarisample.app.AppConstants;
import com.orgware.cloudnarisample.dataitem.ImageItem;
import com.orgware.cloudnarisample.helper.AttachmentSelector;
import com.orgware.cloudnarisample.network.NetworkCall;
import com.orgware.cloudnarisample.network.NetworkHelper;
import com.orgware.cloudnarisample.network.NetworkListener;
import com.orgware.cloudnarisample.upload.UploadFile;
import com.orgware.cloudnarisample.utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
    implements NetworkListener, UploadedImagesAdapter.ClickManager,
    AttachmentSelector.AttachmentSelectionListener, SwipeRefreshLayout.OnRefreshListener {

  private static final String directory = "Cloudinary/upload/img_upload_1.jpg";
  private static final String TAG = "MainActivity";
  private static String filePath =
      Environment.getExternalStorageDirectory() + File.separator + directory;
  private Cloudinary cloudinary;
  private Api cloudinaryApi;
  private AlertDialog alertDialog;
  private Uri outputUri = null;

  private List<ImageItem> imageItemList = new ArrayList<>();
  private String url =
      "https://api.cloudinary.com/v1_1/orgware-technologies-pvt-ltd/resources/image/upload";

  @BindView(R.id.recyclerview) RecyclerView recyclerView;
  @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
  private UploadedImagesAdapter adapter;
  private AttachmentSelector attachmentSelector;
  private int[] swipeLayoutColors = new int[] {
      R.color.colorPrimary, R.color.orange_light, R.color.olive_light, R.color.purple_light,
      R.color.colorAccent
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    attachmentSelector = new AttachmentSelector(Utils.generateUniqueKey(), MainActivity.this, this);

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    adapter = new UploadedImagesAdapter(this, this);
    recyclerView.setAdapter(adapter);

    swipeRefreshLayout.setOnRefreshListener(this);
    swipeRefreshLayout.setColorSchemeResources(swipeLayoutColors);

    cloudinary = new Cloudinary(Utils.getCloudinaryConfig());
    cloudinaryApi = cloudinary.api();

    onRefresh();
  }

  private void updateRecyclerView(List<ImageItem> imageItemList) {
    adapter.setImageItemList(imageItemList);
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void onAttachmentSelected(File attachmentUri) {
    new UploadFile(this, cloudinary, attachmentUri).execute();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode != RESULT_OK) {
      return;
    }
    switch (requestCode) {
      case AppConstants.REQUEST_CAMERA_PICK:

        if (outputUri == null) {
          return;
        }
        File file = new File(outputUri.getPath());
        new UploadFile(MainActivity.this, cloudinary, file.getPath(), this).execute();
        break;
      case AppConstants.REQUEST_GALLERY_PICK:
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        // Get the cursor
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

        if (cursor == null) {
          return;
        }
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        new UploadFile(MainActivity.this, cloudinary, imgDecodableString, this).execute();
        break;
    }
  }

  @Override public void onRequestCompleted(String result) {

    try {
      JSONArray jsonArray = new JSONObject(result).optJSONArray("resources");
      imageItemList.clear();
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.optJSONObject(i);

        ImageItem imageItem = new ImageItem();
        imageItem.setPublicId(jsonObject.optString("public_id"));
        imageItem.setCreatedAt(jsonObject.optString("created_at"));
        imageItem.setSecureUrl(jsonObject.optString("secure_url"));

        imageItemList.add(imageItem);
      }

      updateRecyclerView(imageItemList);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override public void onUploadCompletedListener(String result) {
    if (result != null) onRefresh();
  }

  @Override public void onUploadFailedListener(String result) {
    Toast.makeText(this, "Failed to upload image to Cloudinary Server", Toast.LENGTH_SHORT).show();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_upload_item, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.action_upload) {
      if (alertDialog == null) {
        alertDialog = Utils.createImageChooserDialog(this, new View.OnClickListener() {
          @Override public void onClick(View view) {

            outputUri = Utils.selectCamera(MainActivity.this);
            alertDialog.dismiss();
          }
        }, new View.OnClickListener() {
          @Override public void onClick(View view) {
            Utils.selectGallery(MainActivity.this);
            alertDialog.dismiss();
          }
        });
      }
      alertDialog.show();
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onItemClicked(ImageItem item) {
    Bundle bundle = new Bundle();
    bundle.putParcelable("image_item", item);
    startActivity(new Intent(MainActivity.this, DetailActivity.class).putExtras(bundle));
  }

  @Override public void onRefresh() {
    swipeRefreshLayout.setRefreshing(true);
    callServerForImageList();
  }

  private void callServerForImageList() {
    if (NetworkHelper.isNetworkAvailable(this)) {
      new NetworkCall(this, url, cloudinaryApi, this).execute();
    } else {
      swipeRefreshLayout.setRefreshing(false);
      Toast.makeText(this, "Please enable internet connection", Toast.LENGTH_LONG).show();
    }
  }
}
