package com.orgware.cloudnarisample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.orgware.cloudnarisample.dataitem.ImageItem;

/**
 * Created by nandagopal on 12/9/16.
 */
public class DetailActivity extends AppCompatActivity {

  @BindView(R.id.detail_image) ImageView imageView;

  private ImageItem imageItem;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      imageItem = getIntent().getExtras().getParcelable("image_item");
    }

    Glide.with(this)
        .load(imageItem.getDetailUrl())
        .asBitmap()
        .error(R.drawable.ic_error)
        .into(imageView);
  }
}
