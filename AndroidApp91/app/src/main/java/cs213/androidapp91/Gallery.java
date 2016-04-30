package cs213.androidapp91;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Gallery extends AppCompatActivity {

    public static final int SELECT_PHOTO = 4;

    private MyAlbumList myList;
    private Album album;

    private ArrayList<Uri> uriList;
    private ArrayList<Bitmap> bitmapList;

    GridView gridView;
    ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // initializations
        bitmapList = new ArrayList<Bitmap>();
        uriList = new ArrayList<Uri>();

        try {
            myList = MyAlbumList.getInstance(this);
            System.out.println("Gallery: onCreate - got MyAlbumList instance");
            //myList.setContext(this);
            //myList.load();
        } catch (IOException e) {
            Toast.makeText(this, "Error loading songs", Toast.LENGTH_LONG).show();
            return;
        }

        // check if Bundle was passed, and populate fields
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            int current = bundle.getInt("id");
            System.out.println("Gallery: onCreate - attempting album URIs");
            uriList = (ArrayList<Uri>) myList.getAlbums().get(current).getUris();
            System.out.println("Gallery: onCreate - got album URIs");
            setAlbum(myList.getAlbums().get(current));

            // get list of photo URIs in album
            //ArrayList<String> temp = bundle.getStringArrayList(MYURILIST);
            //uriList = Utils.stringToUri(temp);

        }

        // convert URIs to bitmaps
        for (Uri u : uriList) {
            try {
                ContentResolver resolver = getContentResolver();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    resolver.takePersistableUriPermission(u, takeFlags);
                }
                InputStream imageStream = resolver.openInputStream(u);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                bitmapList.add(bitmap);
            } catch(IOException e) {

            }
        }
        gridView = (GridView) findViewById(R.id.gridview);

        imageAdapter = new ImageAdapter(this, bitmapList);
        gridView.setAdapter(imageAdapter);
        setClick();

        // for contextual action mode
        gridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(
                new AbsListView.MultiChoiceModeListener() {
                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        // Respond to clicks on the actions in the CAB
                        switch (menuItem.getItemId()) {
                            case R.id.action_delete:
                                deleteSelectedItems();
                                actionMode.finish(); // Action picked, so close the CAB
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        // Inflate the menu for the CAB
                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.delete_menu, menu);
                        return true;
                    }

                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                          long id, boolean checked) {
                        // Here you can do something when items are selected/de-selected,
                        // such as update the title in the CAB
                        mode.setTitle(gridView.getCheckedItemCount() +
                                " selected");
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        // Here you can make any necessary updates to the activity when
                        // the CAB is removed. By default, selected items are deselected/unchecked.
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        // Here you can perform updates to the CAB due to
                        // an invalidate() request
                        return false;
                    }

                });
    }

    private void setAlbum(Album album) {
        this.album = album;
    }

    private void setClick() {
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //System.out.println("Clicked on image " + i);
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(getApplicationContext(), DisplayPhoto.class);

/*                      sbitmapList.clear();
                        // bitmap to string
                        for (Bitmap bitmap : bitmapList) {
                            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                            byte [] b= byteArray.toByteArray();
                            String temp = Base64.encodeToString(b, Base64.DEFAULT);
                            System.out.println("Added something to string bitmaplist");
                            sbitmapList.add(temp);
                        }*/

                        // send album details to DisplayPhoto
                        //bundle.putStringArrayList(DisplayPhoto.MYURILIST, Utils.uriToString(uriList));
                        bundle.putString("album", album.getName());
                        bundle.putInt("id", i);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add:
                addPhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addPhoto() {
        // select image and once selected, return URI of image to onActivityResult

        Intent photoPickerIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        } else {
            photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,  SELECT_PHOTO);

/*        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image*//*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);*/
    }

    private boolean containsPhoto(Bitmap bitmap) {
        if (bitmapList == null) {
            return false;
        }
        for (Bitmap b : bitmapList) {
            if (b.sameAs(bitmap)) {
                return true;
            }
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, requestCode, intent);
        if (requestCode == SELECT_PHOTO) {
            if(resultCode == RESULT_OK){
                try {
                    Uri selected = intent.getData();
                    ContentResolver resolver = getContentResolver();

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        resolver.takePersistableUriPermission(selected, takeFlags);
                    }

                    InputStream imageStream = resolver.openInputStream(selected);
                    Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);

                    if (!containsPhoto(selectedBitmap)) {
                        System.out.println("Gallery: onActivityResult - adding photo URIs");

                        bitmapList.add(selectedBitmap);
                        uriList.add(selected);
                        album.addPhoto(selected);
                        imageAdapter.notifyDataSetChanged();
                        try {
                            System.out.println("Gallery: onActivityResult - attempting to store data");
                            myList.store();
                        } catch(IOException e) {
                            Toast.makeText(myList.context, "Could not store photos to file", Toast.LENGTH_LONG).show();
                        }
                        setClick();
                    } else {
                        Toast
                                .makeText(this, "Duplicate photo in album", Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (FileNotFoundException e) {

                }
            }
        }
    }

    private void deleteSelectedItems() {
        SparseBooleanArray arr = gridView.getCheckedItemPositions();
        //String str="";
        // gather songs in a to-delete list
        ArrayList<Bitmap> deleteBitmap = new ArrayList<Bitmap>();
        for (int i=arr.size(); i >= 0; i--) {
            if (arr.valueAt(i)) {
                // position of the photo to be deleted in the gallery
                //System.out.println(arr.keyAt(i));
                bitmapList.remove(arr.keyAt(i));
            }
        }
        /*for (Bitmap bitmap: deleteBitmap) {
            System.out.println("Removing something...?");
            System.out.println(bitmapList.remove(bitmap));
        }*/

        //gridView.setAdapter(new ArrayAdapter<Uri>(this, R.layout.album, myList.getAlbums()));
        //Toast.makeText(SongLib.this,str,Toast.LENGTH_LONG).show();
        imageAdapter.notifyDataSetChanged();

    }

}