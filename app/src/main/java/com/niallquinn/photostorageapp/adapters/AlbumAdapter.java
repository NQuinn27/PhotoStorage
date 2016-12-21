package com.niallquinn.photostorageapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.niallquinn.photostorageapp.R;
import com.niallquinn.photostorageapp.model.Album;
import com.niallquinn.photostorageapp.model.Photo;
import com.niallquinn.photostorageapp.realm.AlbumService;
import com.niallquinn.photostorageapp.realm.RealmInitialData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by niall on 17/12/2016.
 */

public class AlbumAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Integer> selectedItems;

    public AlbumAdapter(Context c, ArrayList<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        mContext = c;
    }

    public void updateSelectedItems(ArrayList<Integer> selectedItems) {
        this.selectedItems = selectedItems;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView nameView;
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.album_item, null);
            nameView = (TextView) grid.findViewById(R.id.album_title);
            imageView = (ImageView) grid.findViewById(R.id.album_image);

//            imageView = new ImageView(mContext);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = metrics.widthPixels / 2;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width-16, width-16));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            grid = (View) convertView;
            nameView = (TextView) grid.findViewById(R.id.album_title);
            imageView = (ImageView) grid.findViewById(R.id.album_image);
        }

        Album album = new AlbumService(mContext).getAlbums().get(position);
        Realm realm = Realm.getDefaultInstance();
        Photo photo = realm.where(Photo.class).equalTo("albumId", album.getId()).findFirst();
        Bitmap bmp;
        if (photo == null) {
            Drawable d = mContext.getDrawable(R.drawable.default_image);
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            bmp = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        } else {
            bmp = BitmapFactory.decodeByteArray(photo.getImageData(), 0, photo.getImageData().length);
        }
        imageView.setImageBitmap(bmp);
        ImageView checkImage = (ImageView) grid.findViewById(R.id.checkImageView);

        if (selectedItems == null) {
            checkImage.setVisibility(View.INVISIBLE);
        } else if (selectedItems.contains(position)) {
            checkImage.setVisibility(View.VISIBLE);
            checkImage.setImageDrawable(mContext.getDrawable(android.R.drawable.checkbox_on_background));
        } else {
            checkImage.setVisibility(View.VISIBLE);
            checkImage.setImageDrawable(mContext.getDrawable(android.R.drawable.checkbox_off_background));
        }
        String name = album.getName();
        if (name == null) {
            nameView.setText("No Name");
        } else {
            nameView.setText(name);
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
        AlbumService service = new AlbumService(mContext);
        return service.getAlbums().size();
    }
}

    // references to our images

