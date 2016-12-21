package com.niallquinn.photostorageapp.realm;

import android.content.Context;

import com.niallquinn.photostorageapp.model.Album;
import com.niallquinn.photostorageapp.model.Photo;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by niall on 20/12/2016.
 */

public class PhotoService {
    private Context mContext;
    public PhotoService(Context context){
        Realm.init(context);
        mContext = context;
    }

    public RealmResults<Photo> photosForAlbumId(String albumId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Photo> photos = realm.where(Photo.class).equalTo("albumId", albumId).findAll();
        return photos;
    }

    public void addPhoto(byte[] data, String albumId) {
        Photo photo = new Photo(data, albumId);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(photo);
        realm.commitTransaction();
    }

    public void deletePhoto(String id) {
        Realm realm = Realm.getDefaultInstance();

        final RealmResults<Photo> results = realm.where(Photo.class).equalTo("id",id).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Delete all matches
                results.deleteAllFromRealm();

            }
        });

    }
}
