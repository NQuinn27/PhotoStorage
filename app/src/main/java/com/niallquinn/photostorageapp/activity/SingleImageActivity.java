package com.niallquinn.photostorageapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.niallquinn.photostorageapp.R;
import com.niallquinn.photostorageapp.realm.AlbumService;

public class SingleImageActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 500;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] imageData = extras.getByteArray("imageData");
            ImageView singleImageView = (ImageView) findViewById(R.id.singleImageView);
            bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            singleImageView.setImageBitmap(bitmap);
        }

        FloatingActionButton sms_fab = (FloatingActionButton) findViewById(R.id.sms_fab);
        sms_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsClick();
            }
        });
    }
    private void smsClick() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }
       String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Photoz" , "Photo taken with" +
               " the Photoz " +
                "App");
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra("sms_body", "Check out my photo!");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
        sendIntent.setType("image/png");
        startActivity(sendIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    smsClick();

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
