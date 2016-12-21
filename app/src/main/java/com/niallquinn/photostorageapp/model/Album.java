package com.niallquinn.photostorageapp.model;

import android.media.Image;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by niall on 17/12/2016.
 */

public class Album extends RealmObject {

    @PrimaryKey
    private String id;
    private RealmList<Photo> photos;
    private String name;

    public Album(){}
    public Album(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.photos = new RealmList<Photo>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(RealmList<Photo> photos) {
        this.photos = photos;
    }

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }
}
