package cs213.androidapp91;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Deletes and edits selected album. Returns updated information
 * to PhotoAlbum.
 *
 * @author Omar Khalil
 * @author Michelle Hwang
 */
public class EditAlbum extends AppCompatActivity {

    public static final String ALBUM_NAME = "albumName";
    public static final String OLD_ALBUM_NAME = "oldAlbumName";

    public static final String ALBUM_DELETE = "albumDelete";
    public static final int OPEN_ALBUM_CODE = 3;

    // from PhotoAlbum
    public static final String MYLIST = "albumList";

    // processed data from PhotoAlbum
    private String oldName;
    private ArrayList<String> albumList;

    EditText albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_album);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        albumName = (EditText) findViewById(R.id.album_name);

        // check if Bundle was passed, and populate fields
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            oldName = bundle.getString(ALBUM_NAME);
            albumName.setText(oldName);
            albumList = bundle.getStringArrayList(MYLIST);
        }
    }

/*    public void open(View view) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, Gallery.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, OPEN_ALBUM_CODE);
    }*/

    // called when the user taps the Save button
    public void save(View view) {
        // gather all data
        String name = albumName.getText().toString();

        // name and artist are mandatory
        if (name == null || name.length() == 0) {
            Toast
                    .makeText(this, "Album name is required", Toast.LENGTH_SHORT)
                    .show();
            return;   // does not quit activity, just returns from method
        }

        if (!oldName.equals(name)) {
            for (String albumName : albumList) {
                if (albumName.equals(name)) {
                    Toast
                            .makeText(this, "Duplicate album name", Toast.LENGTH_SHORT)
                            .show();
                    return;   // does not quit activity, just returns from method
                }
            }
        }

        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_NAME, name);
        bundle.putString(OLD_ALBUM_NAME, oldName);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish(); // pops the activity from the call stack, returns to parent
    }

    // called when the user taps the Cancel button
    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteAlbum();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteAlbum() {
        if (oldName == null) {
            Toast.makeText(this, "Cannot delete on add request", Toast.LENGTH_LONG).show();
            return;
        }

        Bundle bundle = new Bundle();

        bundle.putString(ALBUM_NAME, albumName.getText().toString());
        bundle.putBoolean(ALBUM_DELETE, true);

        Intent intent = new Intent();
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();
    }
}


