package com.septech.centauri.ui.user.cart;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.septech.centauri.R;
import com.septech.centauri.domain.models.Item;

import java.util.List;
import java.util.Map;

import static com.septech.centauri.persistent.CentauriApp.getAppContext;

class CartItemAdapter extends
        RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private List<Item> mItems;

    private Map<Integer, Uri> images;

    private OnItemListener onItemListener;

    public CartItemAdapter(OnItemListener onItemListener, List<Item> mItems, Map<Integer, Uri> images) {
        this.onItemListener = onItemListener;
        this.mItems = mItems;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.user_cart_item_fragment, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(itemView, onItemListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = mItems.get(position);

        ImageView imageView = holder.itemImageView;

        imageView.setImageURI(images.get(item.getId()));

        TextView nameTextView = holder.nameTextView;
        nameTextView.setText(item.getName());

        TextView priceTextView = holder.priceTextView;

        ImageView checkMarkImage = holder.checkMarkImageView;
        checkMarkImage.setVisibility(View.GONE);

        Resources res = getAppContext().getResources();
        priceTextView.setText(res.getString(R.string.placeholder, item.getBuyoutPrice()));
    }

    public void setItems(List<Item> mItems) {
        this.mItems = mItems;
    }

    public void setImages(Map<Integer, Uri> images) {
        this.images = images;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView nameTextView;
        public ImageView itemImageView;
        public TextView priceTextView;

        public ImageView checkMarkImageView;

        private OnItemListener onItemListener;

        public ViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);

            this.onItemListener = onItemListener;

            itemImageView = itemView.findViewById(R.id.itemImage);
            nameTextView = itemView.findViewById(R.id.itemName);
            priceTextView = itemView.findViewById(R.id.priceTextView);

            checkMarkImageView = itemView.findViewById(R.id.checkMarkImageView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getBindingAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onItemListener.onItemLongClick(getBindingAdapterPosition());
            return true;
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
}