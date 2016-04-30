package cs213.androidapp91;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DisplayPhoto extends AppCompatActivity {

    public static final int DISPLAY_PHOTO = 5;
    public static final String MYURILIST = "uriList";
    private ArrayList<Uri> uriList;
    private ArrayList<Bitmap> bitmapList;
    private int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);
        //ImageView imageView = (ImageView) findViewById(R.id.photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Get intent data
        Intent intent = getIntent();
        current = intent.getExtras().getInt("id");

        //System.out.println("Current: " + current);

        // initializations
        uriList = new ArrayList<Uri>();
        bitmapList = new ArrayList<Bitmap>();

        // check if Bundle was passed, and populate fields
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // convert bundle String ArrayList to URI ArrayList
            ArrayList<String> temp = bundle.getStringArrayList(MYURILIST);
            uriList = Utils.stringToUri(temp);

            // convert to list of bitmaps
            // convert URIs to bitmaps
            for (Uri u : uriList) {
                try {
                    InputStream imageStream = getContentResolver().openInputStream(u);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    bitmapList.add(bitmap);
                } catch(IOException e) {

                }
            }

            // display photo
            //ImageAdapter imageAdapter = new ImageAdapter(this, bitmapList);
            //imageView.setImageBitmap(imageAdapter.getBitmap().get(current));
            //imageView.setImageBitmap(bitmapList.get(current));

            ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
            vImageAdapter adapter = new vImageAdapter(this, bitmapList);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(current);
        }
    }
}