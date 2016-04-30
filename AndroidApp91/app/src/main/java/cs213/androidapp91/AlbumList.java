package cs213.androidapp91;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public interface AlbumList {
    void setContext(Context ctx);
    void load() throws IOException;
    void store() throws IOException;
    ArrayList<Album> getAlbums();
    Album add(String name);
    ArrayList<Album> update(String oldName, Album album) throws NoSuchElementException;
    ArrayList<Album> remove(Album album) throws NoSuchElementException;
    int getPos(Album album);
}
