package com.niallquinn.photostorageapp.realm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.niallquinn.photostorageapp.model.Album;
import com.niallquinn.photostorageapp.model.Photo;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import com.niallquinn.photostorageapp.R;

/**
 * Created by niall on 17/12/2016.
 */

public class RealmInitialData implements Realm.Transaction {
    private Context mContext;
    public RealmInitialData(Context c) {
        this.mContext = c;
    }

    @Override
    public void execute(Realm realm) {
        Album defaultAlbum = new Album("Default");

        Photo defaultPhoto = new Photo();
        Drawable d = ContextCompat.getDrawable(mContext, R.drawable.sample_0);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        defaultPhoto.setImageData(bitmapdata);
        defaultPhoto.setAlbumId(defaultAlbum.getId());

        defaultAlbum.addPhoto(defaultPhoto);
        realm.beginTransaction();
        realm.insertOrUpdate(defaultAlbum);
        realm.insertOrUpdate(defaultPhoto);
        realm.commitTransaction();
    }

    @Override
    public int hashCode() {
        return RealmInitialData.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof RealmInitialData;
    }

}
