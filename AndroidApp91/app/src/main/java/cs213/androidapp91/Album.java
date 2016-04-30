package cs213.androidapp91;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private String name;
    private List<Photo> photos;

    public Album() {

    }

    public Album(String name) {
        this.name = name;
        photos = new ArrayList<Photo>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return this.photos;
    }

    public List<Uri> getUris() {
        List<Uri> uriList = new ArrayList<Uri>();
        for (Photo p : this.getPhotos()) {
            uriList.add(p.getUri());
        }
        return uriList;
    }

    public boolean addPhoto(Uri uri) {
        System.out.println("Album: addPhoto - adding photo " + uri);
        Photo photo = new Photo(uri);
        photos.add(photo);
        return true;
    }

    public void deletePhoto(Photo photo) {
        photos.remove(photo);
    }

    public String toString() {
        return name;
    }

    public String getString() {
        return name;
    }

    public static Album parseAlbums(String albumInfo) {
        if (albumInfo.compareTo("=====") != 0) {
            Album album = new Album(albumInfo);
            return album;
        }
        return null;
    }
}