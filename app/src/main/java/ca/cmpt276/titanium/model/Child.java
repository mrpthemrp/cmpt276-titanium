package ca.cmpt276.titanium.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.UUID;

import ca.cmpt276.titanium.R;

/**
 * Represents a child.
 *
 * @author Titanium
 */
public class Child {
  private final UUID uniqueID;
  private String name;
  private String portraitPath;

  public Child(String name, String portraitPath) {
    this.uniqueID = UUID.randomUUID();
    this.name = name;
    this.portraitPath = portraitPath;
  }

  public static RoundedBitmapDrawable getThisPortrait(Resources resources, String portraitPath) {
    Bitmap portraitBitmap =
        portraitPath == null
            ? BitmapFactory.decodeResource(resources, R.drawable.ic_default_portrait_green)
            : BitmapFactory.decodeFile(portraitPath);

    RoundedBitmapDrawable portrait = RoundedBitmapDrawableFactory.create(resources, portraitBitmap);
    portrait.setCircular(true);

    return portrait;
  }

  public RoundedBitmapDrawable getPortrait(Resources resources) {
    return getThisPortrait(resources, portraitPath);
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

  public void setPortraitPath(String portraitPath) {
    this.portraitPath = portraitPath;
  }
}
