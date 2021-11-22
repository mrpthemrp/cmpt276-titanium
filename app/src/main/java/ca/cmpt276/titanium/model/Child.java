package ca.cmpt276.titanium.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This class is used to get and set the attributes of a single child.
 */
public class Child {
    private final UUID uniqueID;
    private final String portraitName;
    private String name;

    public Child(String name, Bitmap portrait) {
        this.uniqueID = UUID.randomUUID();
        setName(name);
        this.portraitName = "portrait_" + uniqueID.toString();
        setPortrait(portrait);
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

    public RoundedBitmapDrawable getPortrait(Resources resources) {
        File cameraDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera");
        RoundedBitmapDrawable portrait = null;

        try {
            if (cameraDirectory.exists() || cameraDirectory.mkdirs()) {
                File portraitFile = new File(cameraDirectory, portraitName + ".png");

                portrait = RoundedBitmapDrawableFactory.create(resources, BitmapFactory.decodeStream(new FileInputStream(portraitFile)));
                portrait.setCircular(true);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return portrait;
    }

    public void setPortrait(Bitmap bitmap) {
        File cameraDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera");

        try {
            if (cameraDirectory.exists() || cameraDirectory.mkdirs()) {
                File portrait = new File(cameraDirectory, portraitName + ".png");

                OutputStream outputStream = new FileOutputStream(portrait);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
