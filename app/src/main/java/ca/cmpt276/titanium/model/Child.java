package ca.cmpt276.titanium.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

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
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(portraitPath, bmOptions);

        int scaleFactor = Math.max(1, Math.min(bmOptions.outWidth / 500, bmOptions.outHeight / 500)); // TODO: Scale according to device pixel density

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(portraitPath, bmOptions);

        RoundedBitmapDrawable portraitDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap);
        portraitDrawable.setCircular(true);

        return portraitDrawable;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }
}
