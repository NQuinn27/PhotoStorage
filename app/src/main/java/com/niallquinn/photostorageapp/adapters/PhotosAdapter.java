package com.niallquinn.photostorageapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.niallquinn.photostorageapp.R;
import com.niallquinn.photostorageapp.model.Album;
import com.niallquinn.photostorageapp.model.Photo;
import com.niallquinn.photostorageapp.realm.AlbumService;
import com.niallquinn.photostorageapp.realm.PhotoService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by niall on 20/12/2016.
 */

public class PhotosAdapter extends BaseAdapter {
    private Context mContext;
    private String albumId;
    private ArrayList<Integer> selectedItems;

    public PhotosAdapter(Context c, String albumId, ArrayList<Integer> selectedItems) {
        this.albumId = albumId;
        this.selectedItems = selectedItems;
        mContext = c;
    }

    public void updateSelectedItems(ArrayList<Integer> selectedItems) {
        this.selectedItems = selectedItems;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        ImageView checkImageView;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            grid = inflater.inflate(R.layout.photo_item, null);
            imageView = (ImageView) grid.findViewById(R.id.photo_image);

//            imageView = new ImageView(mContext);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = metrics.widthPixels / 2;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width-16, width-16));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            grid = (View) convertView;
            checkImageView = (ImageView) grid.findViewById(R.id.checkImageView);
            imageView = (ImageView) grid.findViewById(R.id.photo_image);
        }

        RealmResults<Photo> photos = new PhotoService(mContext).photosForAlbumId(albumId);
        Bitmap bmp;
        if (photos.isEmpty()) {
            Drawable d = mContext.getDrawable(R.drawable.default_image);
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            bmp = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        } else {
            Photo photo = photos.get(position);
            bmp = BitmapFactory.decodeByteArray(photo.getImageData(), 0, photo.getImageData().length);
        }
        checkImageView = (ImageView)grid.findViewById(R.id.photo_checkImageView);
        imageView.setImageBitmap(bmp);
        if (selectedItems == null) {
            checkImageView.setVisibility(View.INVISIBLE);
        } else if (selectedItems.contains(position)) {
            checkImageView.setVisibility(View.VISIBLE);
            checkImageView.setImageDrawable(mContext.getDrawable(android.R.drawable.checkbox_on_background));
        } else {
            checkImageView.setVisibility(View.VISIBLE);
            checkImageView.setImageDrawable(mContext.getDrawable(android.R.drawable.checkbox_off_background));
        }
        return grid;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public int getCount() {
        PhotoService service = new PhotoService(mContext);
        return service.photosForAlbumId(albumId).size();
    }
}
