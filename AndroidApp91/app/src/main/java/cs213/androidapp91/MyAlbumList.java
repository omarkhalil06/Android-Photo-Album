package cs213.androidapp91;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Created by m95ch on 4/22/2016.
 */
public class MyAlbumList implements AlbumList {

    // single instance
    private static MyAlbumList albumList = null;

    // holds songs in a sorted array list
    private ArrayList<Album> albums;

    // context
    Context context;

    // songs file
    public static final String ALBUMS_FILE = "albums.dat";

    // make constructor private for single instance control
    private MyAlbumList() {
        albums = new ArrayList<Album>();
    }

    // deal out the singleton
    public static MyAlbumList getInstance(Context ctx) throws IOException {
        if (albumList == null) {
            albumList = new MyAlbumList();
            albumList.context = ctx;
            albumList.load();
        }
        return albumList;
    }

    public Album add(String name) {
        // name and artist are mandatory
        if (name == null) {
            throw new IllegalArgumentException("Album name is mandatory");
        }

        // create Album object
        Album album = new Album(name);

        // if this is the first add, it's easy
        if (albums.size() == 0) {
            albums.add(album);
            try {
                store();
            } catch (IOException e) {
                Toast.makeText(context, "Could not store albums to file", Toast.LENGTH_LONG).show();
            }
            return album;
        }

        albums.add(album);
        // write through
        try {
            store();
            return album;
        } catch (IOException e) {
            return null;
        }

    }

    public int getPos(Album album) {
        if (albums.size() == 0) {
            return -1;
        }

        int i = 0;
        for (Album a : albums) {
            if (a.getName().equals(album.getName())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void store() throws IOException {
        FileOutputStream fos = context.openFileOutput(ALBUMS_FILE, Context.MODE_PRIVATE);
        PrintWriter pw = new PrintWriter(fos);
        for (Album album: albums) {
            pw.println("=====");
            pw.println(album.getString());
            for (Photo photo : album.getPhotos()) {
                pw.println(photo.getString());
            }
        }
        pw.close();
    }

    public void load() throws IOException {
        try {
            FileInputStream fis = context.openFileInput(ALBUMS_FILE);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String albumInfo = br.readLine();
            while (albumInfo != null) {
                if (albumInfo.compareTo("=====") == 0) {
                    String albumName = br.readLine();
                    System.out.println("Loading " + albumName);
                    Album album = new Album(albumName);
                    albums.add(album);

                    while ((albumInfo = br.readLine()) != null) {
                        if (albumInfo.compareTo("=====") == 0) {
                            break;
                        } else {
                            System.out.println("Photo: " + albumInfo);
                            album.addPhoto(Uri.parse(albumInfo));
                        }
                    }
                }
            }
            fis.close();

        } catch (FileNotFoundException e) {  // default to initial set
            String[] initAlbums = context.getResources().getStringArray(R.array.album_array);
            for (String album: initAlbums) {
                add(album);
            }
        }
    }

    public ArrayList<Album> remove(Album album) throws NoSuchElementException {
        int pos = getPos(album);
        if (pos == -1) {
            throw new NoSuchElementException();
        }
        albums.remove(pos);
        try {
            store();
        } catch (IOException e) {
            Toast.makeText(context, "Could not store songs to file", Toast.LENGTH_LONG).show();
        }
        return albums;
    }

    public void setContext(Context ctx) {
        this.context = ctx;
    }

    public ArrayList<Album> update(String oldName, Album album) throws NoSuchElementException {
        if (album == null) {
            throw new NoSuchElementException();
        }
        //System.out.println("MyAlbumList oldName: " + oldName);
        String newName = album.getName();
        for (Album a : albums) {
            if (a.getName().equals(oldName)) {
                a.setName(newName);
            }
        }

        try {
            store();
        } catch (IOException e) {
            Toast.makeText(context, "Could not store songs to file", Toast.LENGTH_LONG).show();
        }

        // don't forget to save changes
        return albums;
    }

}
