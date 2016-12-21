package com.niallquinn.photostorageapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.niallquinn.photostorageapp.R;

import com.niallquinn.photostorageapp.adapters.AlbumAdapter;
import com.niallquinn.photostorageapp.model.Album;
import com.niallquinn.photostorageapp.realm.AlbumService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import io.realm.RealmResults;

public class AlbumListActivity extends AppCompatActivity {

    GridView gridView;
    private String dialogText = "";
    private AlbumAdapter adapter;
    private ArrayList<Integer> selectedItems;
    private boolean selectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewAlbumDialog();
            }
        });
        selectedItems = new ArrayList<Integer>();
        gridView = (GridView) findViewById(R.id.gridview);
        adapter = new AlbumAdapter(this, null);
        gridView.setAdapter(adapter);
        gridView.setItemChecked(1, true);

        setTitle("Albums");


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                if (selectMode) {
                    if (selectedItems.contains(position)) {
                        selectedItems.remove((Integer)position);
                    } else {
                        selectedItems.add(position);
                    }
                    adapter.updateSelectedItems(selectedItems);
                    adapter.notifyDataSetChanged();
                    Log.w("myApp", selectedItems.toString());
                    return;
                }
                Album selected = new AlbumService(getApplicationContext()).getAlbums().get(position);
                Intent i = new Intent(getApplicationContext(), AlbumPhotosActivity.class);
                i.putExtra("albumId", selected.getId());
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_list, menu);
        return true;
    }

    private void showNewAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Album Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogText = input.getText().toString();
                //Create New Album here
                AlbumService service = new AlbumService(getApplicationContext());
                service.createAlbum(dialogText);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
            RealmResults<Album> albums = new AlbumService(getApplicationContext()).getAlbums();
            for (Integer i : selectedItems) {
                toDelete.add(albums.get(i).getId());
            }
            AlbumService as = new AlbumService(getApplicationContext());
            for (String s : toDelete) {
                as.deleteAlbum(s);
            }
            selectedItems = null;
        }
        adapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }
}
