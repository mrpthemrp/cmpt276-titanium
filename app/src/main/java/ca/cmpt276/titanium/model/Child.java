package ca.cmpt276.titanium.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.UUID;

import ca.cmpt276.titanium.R;

/**
 * This class is used to get and set the attributes of a single child.
 */
public class Child {
    private final UUID uniqueID;
    private String name;
    private String portraitPath;

    public Child(String name, String portraitPath) {
        this.uniqueID = UUID.randomUUID();
        setName(name);
        this.portraitPath = portraitPath;
    }

    public static RoundedBitmapDrawable getSpecifiedPortrait(Resources resources, String portraitPath) {
        RoundedBitmapDrawable portrait;
        Bitmap portraitBitmap;

        if (portraitPath == null) {
            portraitBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_default_portrait_green);
        } else {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(portraitPath, bitmapOptions);

            bitmapOptions.inJustDecodeBounds = false;
            bitmapOptions.inSampleSize = Math.max(1, Math.min(bitmapOptions.outWidth / 500, bitmapOptions.outHeight / 500));

            portraitBitmap = BitmapFactory.decodeFile(portraitPath, bitmapOptions);
        }

        portrait = RoundedBitmapDrawableFactory.create(resources, portraitBitmap);
        portrait.setCircular(true);

        return portrait;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoundedBitmapDrawable getPortrait(Resources resources) {
        return getSpecifiedPortrait(resources, portraitPath);
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }
}
