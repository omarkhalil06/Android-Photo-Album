package cs213.androidapp91;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by m95ch on 4/26/2016.
 */
public class Utils {

    // convert URI ArrayList to String ArrayList
    public static ArrayList<String> uriToString(ArrayList<Uri> uriList) {
        ArrayList<String> stringList = new ArrayList<String>();
        if (uriList != null) {
            for (Uri uri : uriList) {
               stringList.add(uri.toString());
            }
        }
        return stringList;
    }

    // convert String ArrayList to URI ArrayList
    public static ArrayList<Uri> stringToUri(ArrayList<String> stringList) {
        ArrayList<Uri> uriList = new ArrayList<Uri>();
        //int count = 0;
        if (stringList != null) {
            for (String s : stringList) {
                uriList.add(Uri.parse(s));
                //count++;
            }
        }
        //System.out.println("Util: String count " + count);
        return uriList;
    }

}
