package com.niallquinn.photostorageapp.model;

import android.media.Image;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by niall on 17/12/2016.
 */

public class Photo extends RealmObject {


    @PrimaryKey
    private String id;
    private String albumId;
    private byte[] imageData;

    public Photo(){}
    public Photo(byte[] bytes, String albumId) {
        this.imageData = bytes;
        this.id = UUID.randomUUID().toString();
        this.albumId = albumId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
