package com.orgware.cloudnarisample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.orgware.cloudnarisample.R;
import com.orgware.cloudnarisample.dataitem.ImageItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nandagopal on 12/8/16.
 */
public class UploadedImagesAdapter
    extends RecyclerView.Adapter<UploadedImagesAdapter.UploadImagesViewHolder> {

  private Context context;
  private LayoutInflater inflater;
  private List<ImageItem> imageItemList;
  private ClickManager clickManager;

  public UploadedImagesAdapter(Context context, ClickManager clickManager) {
    this.context = context;
    this.clickManager = clickManager;
    imageItemList = new ArrayList<>();
    inflater = LayoutInflater.from(context);
  }

  public void setImageItemList(List<ImageItem> imageItemList) {
    if (imageItemList == null) {
      return;
    }

    this.imageItemList.clear();
    this.imageItemList.addAll(imageItemList);
    notifyDataSetChanged();
  }

  public interface ClickManager {
    void onItemClicked(ImageItem item);
  }

  @Override public UploadImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_uploaded, parent, false);
    return new UploadImagesViewHolder(context, view);
  }

  @Override public void onBindViewHolder(UploadImagesViewHolder holder, int position) {
    ImageItem item = imageItemList.get(position);

    holder.setDataToHolder(item);
  }

  @Override public int getItemCount() {
    return imageItemList.size();
  }

  public void setThumbNail(Context context, String url, ImageView imageView) {
    Glide.with(context).load(url).asBitmap().error(R.drawable.ic_error).into(imageView);
  }

  class UploadImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.description) TextView tvTitle;
    //@BindView(R.id.createdAt) TextView tvCreatedAt;
    @BindView(R.id.thumbnail) ImageView thumbnail;

    private Context context;

    public UploadImagesViewHolder(Context context, View itemView) {
      super(itemView);
      this.context = context;
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    public void setDataToHolder(ImageItem item) {

      tvTitle.setText(item.getPublicId());
      //tvCreatedAt.setText(DateTimeUtils.splitFromString(item.getCreatedAt()));

      setThumbNail(context, item.getSecureUrl(), thumbnail);
    }

    @Override public void onClick(View view) {

      int position = getAdapterPosition();

      if (position < 0) return;
      if (clickManager != null) {
        ImageItem imageItem = imageItemList.get(position);
        clickManager.onItemClicked(imageItem);
      }
    }
  }
}
