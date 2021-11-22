package ca.cmpt276.titanium.model;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.UUID;

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
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(portraitPath, bitmapOptions);

        int scaleFactor = Math.max(1, Math.min(bitmapOptions.outWidth / 500, bitmapOptions.outHeight / 500)); // TODO: Scale according to device pixel density

        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;

        RoundedBitmapDrawable portrait = RoundedBitmapDrawableFactory.create(resources, BitmapFactory.decodeFile(portraitPath, bitmapOptions));
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
