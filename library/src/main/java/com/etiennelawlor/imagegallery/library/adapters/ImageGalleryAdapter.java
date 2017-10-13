package com.etiennelawlor.imagegallery.library.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.util.ImageGalleryUtils;

import java.util.List;

/**
 * Created by etiennelawlor on 8/20/15.
 */
public class ImageGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Member Variables
    private final List<String> mImages;
    private final List<String> mFolders;
    private int mGridItemWidth;
    private int mGridItemHeight;
    private OnImageClickListener mOnImageClickListener;
    private ImageThumbnailLoader mImageThumbnailLoader;
    // endregion

    // region Interfaces
    public interface OnImageClickListener {
        void onImageClick(int position);
        void onFolderClick(String folderId);
    }

    public interface ImageThumbnailLoader {
        void loadImageThumbnail(ImageView iv, String imageUrl, int dimension);
        void loadFolderThumbnail(ImageView iv, Button bt, int pos, int dimension);
    }
    // endregio

    // region Constructors
    public ImageGalleryAdapter(List<String> images) { this(images, null); }
    public ImageGalleryAdapter(List<String> images, List<String> folders) {
        mImages = images;
        mFolders = folders;
    }
    // endregion

    @Override
    public int getItemViewType(int position) {
        if (mFolders != null) {
            if (position < mFolders.size()) {
                return 0;
            }
        }
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_thumbnail, viewGroup, false);
        v.setLayoutParams(getGridItemLayoutParams(v));

        switch(viewType) {
            case 0:
                return new FolderViewHolder(v);
            default:
                return new ImageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ImageViewHolder holder = (ImageViewHolder) viewHolder;

        int folderSize = 0;
        if (mFolders != null) {
            if (position < mFolders.size()) {
                onBindViewHolderFolder(holder, position);
                return;
            }
            folderSize = mFolders.size();
        }
        final int offset = folderSize;
        position = position - offset;

        String image = mImages.get(position);

        mImageThumbnailLoader.loadImageThumbnail(holder.mImageView, image, mGridItemWidth);
        holder.mButton.setVisibility(View.GONE);

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = holder.getAdapterPosition();
                if(adapterPos != RecyclerView.NO_POSITION){
                    if (mOnImageClickListener != null) {
                        adapterPos = adapterPos - offset;
                        mOnImageClickListener.onImageClick(adapterPos);
                    }
                }
            }
        });
    }

    public void onBindViewHolderFolder(final ImageViewHolder holder, int position) {
        final String folder = mFolders.get(position);
        mImageThumbnailLoader.loadFolderThumbnail(holder.mImageView,
                holder.mButton,
                position,
                mGridItemWidth);
        holder.mButton.setVisibility(View.VISIBLE);

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    if (mOnImageClickListener != null) {
                        mOnImageClickListener.onFolderClick(folder);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (mFolders != null) {
            size += mFolders.size();
        }
        if (mImages != null) {
            size += mImages.size();
        }
        return size;
    }

    // region Helper Methods
    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mOnImageClickListener = listener;
    }

    public void setImageThumbnailLoader(ImageThumbnailLoader loader) {
        this.mImageThumbnailLoader = loader;
    }

    private ViewGroup.LayoutParams getGridItemLayoutParams(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int screenWidth = ImageGalleryUtils.getScreenWidth(view.getContext());
        int numOfColumns;
        if (ImageGalleryUtils.isInLandscapeMode(view.getContext())) {
            numOfColumns = 4;
        } else {
            numOfColumns = 3;
        }

        mGridItemWidth = screenWidth / numOfColumns;
        mGridItemHeight = screenWidth / numOfColumns;

        layoutParams.width = mGridItemWidth;
        layoutParams.height = mGridItemHeight;

        return layoutParams;
    }
    // endregion

    // region Inner Classes

    /**
     * Separate types because Glide will overwrite the mImageView.
     */
    public static class FolderViewHolder extends ImageViewHolder {

        public FolderViewHolder(final View view) {
            super(view);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        // region Member Variables
        private final ImageView mImageView;
        private final LinearLayout mLinearLayout;
        private final Button mButton;
        // endregion

        // region Constructors
        public ImageViewHolder(final View view) {
            super(view);

            mImageView = (ImageView) view.findViewById(R.id.iv);
            mLinearLayout = (LinearLayout) view.findViewById(R.id.ll);
            mButton = (Button) view.findViewById(R.id.bt);
        }
        // endregion
    }

    // endregion
}
