package com.niallquinn.photostorageapp.realm;

import com.niallquinn.photostorageapp.model.Album;

import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;
import android.content.Context;
/**
 * Created by niall on 17/12/2016.
 */

public class AlbumService {
    private Context mContext;
    public AlbumService(Context context){
        Realm.init(context);
        mContext = context;
    }

    public void createAlbum(String albumName) {
        Realm realm = Realm.getDefaultInstance();
        Album album = new Album(albumName);
        realm.beginTransaction();
        realm.insertOrUpdate(album);
        realm.commitTransaction();
    }

    public RealmResults<Album> getAlbums() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Album> albums = realm.where(Album.class).findAll();
        if (albums.isEmpty()) {
            RealmInitialData initializer = new RealmInitialData(mContext);
            initializer.execute(realm);
            albums = realm.where(Album.class).findAll();
        }
        return albums;
    }

    public void deleteAlbum(String id) {
        Realm realm = Realm.getDefaultInstance();

        final RealmResults<Album> results = realm.where(Album.class).equalTo("id",id).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Delete all matches
                results.deleteAllFromRealm();

            }
        });

    }

    public Album albumForId(String id) {
        Realm realm = Realm.getDefaultInstance();
        Album album = realm.where(Album.class).equalTo("id", id).findFirst();
        return album;
    }
}
