package com.etiennelawlor.imagegallery.library.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.adapters.ImageGalleryAdapter;
import com.etiennelawlor.imagegallery.library.util.ImageGalleryUtils;
import com.etiennelawlor.imagegallery.library.view.GridSpacesItemDecoration;

import java.util.ArrayList;

public class ImageGalleryActivity extends AppCompatActivity implements ImageGalleryAdapter.OnImageClickListener, ImageGalleryAdapter.ImageThumbnailLoader {

    public static int CAMERA = 1;
    public static int GALLERY = 2;


        // region Member Variables
    private ArrayList<String> mImages;
    private ArrayList<String> mFolders;
    private ArrayList<String> mComments;
    private String mTitle;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private static ImageGalleryAdapter.ImageThumbnailLoader sImageThumbnailLoader;

    private boolean isReadOnly = false;
    public static String IS_READ_ONLY = "isReadOnly";

    public static void setAddHandler(ImageGalleryAdd addHandler) {
        ImageGalleryActivity.addHandler = addHandler;
    }

    private static ImageGalleryAdd addHandler;
    // endregion

    public interface ImageGalleryAdd {
        public void menuItemPressed(int itemId, Activity activity);
        public void onFolderClick(String folderId, Activity activity);
    }

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mImages = extras.getStringArrayList("images");
                mFolders = extras.getStringArrayList("folders");
                mComments = extras.getStringArrayList("comments");
                mTitle = extras.getString("title");
                isReadOnly = extras.getBoolean(IS_READ_ONLY);
            }
        }

        setContentView(R.layout.activity_image_gallery);

        bindViews();

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mTitle);
        }

        setUpRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_gallery_activity_actions, menu);
        menu.findItem(R.id.action_select_picture).setVisible(!isReadOnly);
        menu.findItem(R.id.action_new_picture).setVisible(!isReadOnly);
        return super.onCreateOptionsMenu(menu);
    }

    // endregion

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else {
            if (itemId == R.id.action_new_picture) {
                addHandler.menuItemPressed(CAMERA, this);
                return true;
            } else if (itemId == R.id.action_select_picture) {
                addHandler.menuItemPressed(GALLERY, this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUpRecyclerView();
    }

    // region ImageGalleryAdapter.OnImageClickListener Methods
    @Override
    public void onImageClick(int position) {
        Intent intent = new Intent(ImageGalleryActivity.this, FullScreenImageGalleryActivity.class);

        intent.putStringArrayListExtra("images", mImages);
        intent.putStringArrayListExtra("comments", mComments);
        intent.putExtra("position", position);

        startActivity(intent);
    }

    @Override
    public void onFolderClick(String folderId) {
        addHandler.onFolderClick(folderId, this);
    }
    // endregion

    // region ImageGalleryAdapter.ImageThumbnailLoader Methods
    @Override
    public void loadImageThumbnail(ImageView iv, String imageUrl, int dimension) {
        sImageThumbnailLoader.loadImageThumbnail(iv, imageUrl, dimension);
    }

    @Override
    public void loadFolderThumbnail(ImageView iv, Button bt, int pos, int dimension) {
        sImageThumbnailLoader.loadFolderThumbnail(iv, bt, pos, dimension);
    }
    // endregion

    // region Helper Methods
    private void bindViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setUpRecyclerView() {
        int numOfColumns;
        if (ImageGalleryUtils.isInLandscapeMode(this)) {
            numOfColumns = 4;
        } else {
            numOfColumns = 3;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(ImageGalleryActivity.this, numOfColumns));
        mRecyclerView.addItemDecoration(new GridSpacesItemDecoration(ImageGalleryUtils.dp2px(this, 2), numOfColumns));
        ImageGalleryAdapter imageGalleryAdapter = new ImageGalleryAdapter(mImages, mFolders);
        imageGalleryAdapter.setOnImageClickListener(this);
        imageGalleryAdapter.setImageThumbnailLoader(this);

        mRecyclerView.setAdapter(imageGalleryAdapter);
    }

    public static void setImageThumbnailLoader(ImageGalleryAdapter.ImageThumbnailLoader loader) {
        sImageThumbnailLoader = loader;
    }
    // endregion
}
