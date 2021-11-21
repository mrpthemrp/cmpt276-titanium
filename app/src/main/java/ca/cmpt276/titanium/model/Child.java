package ca.cmpt276.titanium.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This class is used to get and set the attributes of a single child.
 */
public class Child {
    private final UUID uniqueID;
    private String name;
    private final String portraitName;

    public Child(Context context, Activity activity, String name, Bitmap portrait) {
        this.uniqueID = UUID.randomUUID();
        setName(name);
        this.portraitName = "portrait_" + uniqueID.toString();

        try {
            setPortrait(context, activity, portrait);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        char[] nameChars = name.toCharArray();

        for (char nameChar : nameChars) {
            if (!Character.isLetter(nameChar)) {
                throw new IllegalArgumentException("Names must contain only letters");
            }
        }

        this.name = name;
    }

    public RoundedBitmapDrawable getPortrait(Context context, Activity activity) throws IOException {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        String cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
        File file = new File(cameraDirectory, portraitName + ".png");

        Bitmap savedPortraitBitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        RoundedBitmapDrawable portrait = RoundedBitmapDrawableFactory.create(context.getResources(), savedPortraitBitmap);
        portrait.setCircular(true);

        return portrait;
    }

    public void setPortrait(Context context, Activity activity, Bitmap bitmap) throws IOException {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        String cameraDirectoryName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
        File cameraDirectory = new File(cameraDirectoryName);

        if (cameraDirectory.exists() || cameraDirectory.mkdirs()) {
            File portrait = new File(cameraDirectory, portraitName + ".png");
            OutputStream outputStream = new FileOutputStream(portrait);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.close();
        } else {
            assert false;
        }
    }
}
