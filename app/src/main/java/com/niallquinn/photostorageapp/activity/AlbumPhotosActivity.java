package com.niallquinn.photostorageapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.niallquinn.photostorageapp.R;
import com.niallquinn.photostorageapp.adapters.AlbumAdapter;
import com.niallquinn.photostorageapp.adapters.PhotosAdapter;
import com.niallquinn.photostorageapp.model.Album;
import com.niallquinn.photostorageapp.model.Photo;
import com.niallquinn.photostorageapp.realm.AlbumService;
import com.niallquinn.photostorageapp.realm.PhotoService;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import io.realm.RealmResults;

public class AlbumPhotosActivity extends AppCompatActivity {

    public static final int CAMERA_RESULT = 100;
    public static final int CAMERA_REQUEST = 200;
    public static final int GALLERY_RESULT = 300;
    public static final int GALLERY_REQUEST = 400;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 500;

    private GridView gridView;
    private PhotosAdapter adapter;
    private String albumId;
    private Album album;
    private ArrayList<Integer> selectedItems;
    private boolean selectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photos);
        FloatingActionButton camera_fab = (FloatingActionButton) findViewById(R.id.camera_fab);
        camera_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraClick();
            }
        });

        FloatingActionButton gallery_fab = (FloatingActionButton) findViewById(R.id.gallery_fab);
        gallery_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                galleryClick();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            albumId = extras.getString("albumId");
            album = new AlbumService(getApplicationContext()).albumForId(albumId);
        }
        if (album != null) {
            setTitle(album.getName());
        }
        gridView = (GridView) findViewById(R.id.photos_gridview);
        selectMode = false;
        adapter = new PhotosAdapter(this, albumId, null);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (selectMode) {
                    if (selectedItems.contains(position)) {
                        selectedItems.remove((Integer)position);
                    } else {
                        selectedItems.add(position);
                    }
                    adapter.updateSelectedItems(selectedItems);
                    adapter.notifyDataSetChanged();
                    return;
                }
                Photo selected = new PhotoService(getApplicationContext()).photosForAlbumId(albumId).get(position);
                Intent i = new Intent(getApplicationContext(), SingleImageActivity.class);
                i.putExtra("imageData", selected.getImageData());
                startActivity(i);
            }
        });
    }

    private void cameraClick() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager())!= null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }

    private void galleryClick() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap picture = (Bitmap) data.getExtras().get("data");//this is your bitmap image and now you can do whatever you want with this
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            PhotoService service = new PhotoService(getApplicationContext());
            service.addPhoto(byteArray, albumId);
            adapter.notifyDataSetChanged();
        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            PhotoService service = new PhotoService(getApplicationContext());
            service.addPhoto(byteArray, albumId);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_select) {
            if (selectMode) {
                selectMode = false;
                adapter.updateSelectedItems(null);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"Select Mode Disabled", Toast.LENGTH_LONG).show();
                setTitle("Albums");
            } else {
                selectMode = true;
                selectedItems = new ArrayList<>();
                adapter.updateSelectedItems(selectedItems);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),
                        "Select Mode Enabled", Toast.LENGTH_LONG).show();
                setTitle("Albums (Select Mode)");
            }
        } else if (id == R.id.action_delete) {
            if (!selectMode) {
                Toast.makeText(getApplicationContext(),
                        "Not in Select Mode", Toast.LENGTH_LONG).show();
                return true;
            }
            ArrayList<String> toDelete = new ArrayList<>();
            RealmResults<Photo> photos = new PhotoService(getApplicationContext()).photosForAlbumId(albumId);
            for (Integer i : selectedItems) {
                toDelete.add(photos.get(i).getId());
            }
            PhotoService as = new PhotoService(getApplicationContext());
            for (String s : toDelete) {
                as.deletePhoto(s);
            }
            selectedItems = null;
        }
        adapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, GALLERY_REQUEST);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
