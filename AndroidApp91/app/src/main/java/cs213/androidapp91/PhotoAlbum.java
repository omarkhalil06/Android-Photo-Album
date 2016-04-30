package cs213.androidapp91;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class PhotoAlbum extends AppCompatActivity {

    private MyAlbumList myList;
    private ListView listView;
    private ArrayAdapter<Album> albumArrayAdapter;

    public static final int ADD_ALBUM_CODE = 1;
    public static final int EDIT_ALBUM_CODE = 2;
    public static final int OPEN_ALBUM_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        /*
        myList = MyAlbumList.getInstance();
        String[] initAlbums = getResources().getStringArray(R.array.album_array);
        for (String album : initAlbums) {
            //myList.add(album);
        }
        */

        try {
            myList = MyAlbumList.getInstance(this);
            //myList.setContext(this);
            //myList.load();
        } catch (IOException e) {
            Toast.makeText(this, "Error loading songs", Toast.LENGTH_LONG).show();
        }
        albumArrayAdapter = new ArrayAdapter<Album>(this, R.layout.album, myList.getAlbums());
        listView = (ListView)findViewById(R.id.album_list);
        listView.setAdapter(albumArrayAdapter);

        // listen to item click
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        open(i);
                    }
                }
        );

        registerForContextMenu(listView);

        // for contextual action mode
/*        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(
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
                        mode.setTitle(listView.getCheckedItemCount() +
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

                });*/
    }

    // for floating contextual menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(menu, view, contextMenuInfo);
        getMenuInflater().inflate(R.menu.album_actions, menu);
    }

    // for floating contextual menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //System.out.println(info.position);

        switch(item.getItemId()){
            case R.id.action_edit:
                //Toast.makeText(this, "Edit Album", Toast.LENGTH_SHORT).show();
                showAlbum(info.position);
                albumArrayAdapter.notifyDataSetChanged();
                break;
            case R.id.action_delete:
                //Toast.makeText(this, "Delete Album", Toast.LENGTH_SHORT).show();
                myList.remove((Album) listView.getItemAtPosition(info.position));
                albumArrayAdapter.notifyDataSetChanged();
                break;
            case R.id.action_open:
                //Toast.makeText(this, "Open Album", Toast.LENGTH_SHORT).show();
                open(info.position);
                break;
            case R.id.cancel_action:
                //Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void addAlbum() {
        ArrayList<String> albumList = new ArrayList<String>();
        for (Album a : myList.getAlbums()) {
            albumList.add(a.getName());
        }
        //System.out.println(albumList);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(AddAlbum.MYLIST, albumList);
        Intent intent = new Intent(this, AddAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ADD_ALBUM_CODE);
    }

    // on return from activity
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK) {return;}
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }

        String name = bundle.getString(AddAlbum.ALBUM_NAME);
        String newName = bundle.getString(EditAlbum.ALBUM_NAME);
        String oldName = bundle.getString(EditAlbum.OLD_ALBUM_NAME);

        if (requestCode == ADD_ALBUM_CODE) {
                myList.add(name);
        } else if (requestCode == EDIT_ALBUM_CODE) {
            Album album = new Album(newName);
            if (bundle.getBoolean(EditAlbum.ALBUM_DELETE)) {
                myList.remove(album);
            } else {
                myList.update(oldName, album);
            }
        }
        albumArrayAdapter.notifyDataSetChanged();
    }

    public void open(int pos) {
        // open album at position specified
        // pass that album's details to next activity
        //Toast.makeText(this, "Opening Album: " + myList.getAlbums().get(pos).getName(), Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putInt("id", pos);
        Intent intent = new Intent(this, Gallery.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, OPEN_ALBUM_CODE);
    }

    public void showAlbum(int pos) {
        // prepare album list to pass to EditAlbum
        ArrayList<String> albumList = new ArrayList<String>();
        for (Album a : myList.getAlbums()) {
            albumList.add(a.getName());
        }
        Album album = myList.getAlbums().get(pos);

        Intent intent = new Intent(this, EditAlbum.class);
        Bundle bundle = new Bundle();
        bundle.putString(EditAlbum.ALBUM_NAME,album.getName());
        bundle.putStringArrayList(EditAlbum.MYLIST, albumList);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_ALBUM_CODE);
    }

/*    private void deleteSelectedItems() {
        SparseBooleanArray arr = listView.getCheckedItemPositions();
        //String str="";
        // gather songs in a to-delete list
        ArrayList<Album> deleteAlbums = new ArrayList<Album>();
        for (int i=0; i < arr.size(); i++) {
            if (arr.valueAt(i)) {
                Album album = (Album)listView.getItemAtPosition(
                        arr.keyAt(i));
                //str += song.id + ";";
                deleteAlbums.add(album);
            }
        }
        for (Album album: deleteAlbums) {
            myList.remove(album);
        }
        listView.setAdapter(new ArrayAdapter<Album>(this, R.layout.album, myList.getAlbums()));
        //Toast.makeText(SongLib.this,str,Toast.LENGTH_LONG).show();
    }*/

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add:
                addAlbum();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
