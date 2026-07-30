package crazzydevelopers_vj.applepie;

import android.app.WallpaperManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import java.io.IOException;

public class Fucking_activity extends AppCompatActivity {

    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fucking_activity);
        
        setMyWallpaper();
        
        try {
            AssetFileDescriptor afd = getAssets().openFd("fuckingsound.mp3");
            player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.setLooping(true);
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void setMyWallpaper() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Fucking_activity activity = Fucking_activity.this;
                WallpaperManager wm = WallpaperManager.getInstance(activity);
                try {
                    // Try to find the image resource
                    int resId = activity.getResources().getIdentifier("fucking_image", "drawable", activity.getPackageName());
                    if (resId != 0) {
                        Drawable d = activity.getResources().getDrawable(resId);
                        if (d instanceof BitmapDrawable) {
                            Bitmap b = ((BitmapDrawable) d).getBitmap();
                            wm.setBitmap(b);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Wallpaper Set!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
