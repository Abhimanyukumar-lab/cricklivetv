package crazzydevelopers_vj.applepie;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    AudioManager audioManager;
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.continueButton = (Button) findViewById(R.id.continueButton);
        this.audioManager = (AudioManager) getSystemService("audio");
        
        if (this.audioManager != null) {
            try {
                this.audioManager.setRingerMode(2); 
            } catch (Exception e) {}
        }
        
        this.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = MainActivity.this;
                if (Build.VERSION.SDK_INT >= 23) { // Build.VERSION_CODES.M
                    if (!Settings.System.canWrite(activity)) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + activity.getPackageName()));
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            activity.startActivity(intent);
                        }
                        Toast.makeText(activity, "Allow 'Modify system settings' then try again", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                
                if (activity.audioManager != null) {
                    try {
                        int maxVol = activity.audioManager.getStreamMaxVolume(3);
                        activity.audioManager.setStreamVolume(3, maxVol, 1);
                    } catch (Exception e) {}
                }
                
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        activity.copyFileAndSetRingtone();
                    }
                }).start();
                activity.startActivity(new Intent(activity, Fucking_activity.class));
            }
        });
    }

    private void copyFileAndSetRingtone() {
        try {
            InputStream in = getAssets().open("fuckingsound.mp3");
            Uri newUri = null;

            if (Build.VERSION.SDK_INT >= 29) { // Build.VERSION_CODES.Q
                ContentValues values = new ContentValues();
                values.put("_display_name", "fuckingsound.mp3");
                values.put("title", "fuckingsound");
                values.put("mime_type", "audio/mpeg");
                values.put("relative_path", "Ringtones");
                values.put("is_ringtone", true);

                newUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                if (newUri != null) {
                    OutputStream out = getContentResolver().openOutputStream(newUri);
                    if (out != null) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        out.close();
                    }
                }
            } else {
                File ring = new File(Environment.getExternalStorageDirectory(), "fuckingsound.mp3");
                OutputStream out = new FileOutputStream(ring);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.close();

                ContentValues values = new ContentValues();
                values.put("_data", ring.getAbsolutePath());
                values.put("title", "fuckingsound");
                values.put("is_ringtone", true);
                values.put("mime_type", "audio/mpeg");

                Uri uri = MediaStore.Audio.Media.getContentUriForPath(ring.getAbsolutePath());
                if (uri != null) {
                    getContentResolver().delete(uri, "_data=\"" + ring.getAbsolutePath() + "\"", null);
                    newUri = getContentResolver().insert(uri, values);
                }
            }

            if (newUri != null) {
                RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Ringtone set!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            in.close();
        } catch (Exception e) {
            Log.e("RINGTONE_ERROR", e.getMessage());
        }
    }
}
