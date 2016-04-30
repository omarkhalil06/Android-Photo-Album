package cs213.androidapp91;


import android.net.Uri;

public class Photo {
    private Uri uri;

    public Photo(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return this.uri;
    }

    public String getString() {
        return this.uri + "";
    }


}
