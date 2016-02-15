package risinglabs.com.trackme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by venkata on 2/14/16.
 */
public class GoogleMapInstance {
    private static GoogleMapInstance ourInstance = new GoogleMapInstance();
    private GoogleMap mMap;
    private Context appContext;

    public static GoogleMapInstance getInstance() {
        if(ourInstance == null){
            ourInstance = new GoogleMapInstance();
        }else{
            return ourInstance;
        }

        return ourInstance;
    }

    private GoogleMapInstance() {
    }

    public void saveGoogleMapObj(GoogleMap map, Context context){
        mMap = map;
        appContext = context;
    }

    void takeSnapshot() {
        if (mMap == null) {
            return;
        }

        //final ImageView snapshotHolder = (ImageView) findViewById(R.id.snapshot_holder);

        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // Callback is called from the main thread, so we can modify the ImageView safely.
                //snapshotHolder.setImageBitmap(snapshot);
                // save bitmap to cache directory
                try {

                    File cachePath = new File(appContext.getCacheDir(), "images");
                    cachePath.mkdirs(); // don't forget to make the directory
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                    snapshot.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File imagePath = new File(appContext.getCacheDir(), "images");
                File newFile = new File(imagePath, "image.png");
                Uri contentUri = FileProvider.getUriForFile(appContext, "risinglabs.com.trackme.fileprovider", newFile);

                if (contentUri != null) {

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, appContext.getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    appContext.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                    //mShareActionProvider.setShareIntent(shareIntent);

                }
            }
        };

        mMap.snapshot(callback);

    }
}
