package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;

/**
 * Displays each Child object from a list of Child objects.
 *
 * @author Titanium
 */
public class ChildAdapter extends ArrayAdapter<Child> {
  public ChildAdapter(Context context, ArrayList<Child> children) {
    super(context, 0, children);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_child, parent, false);
    }

    Child child = getItem(position);

    ImageView childPortrait = convertView.findViewById(R.id.ImageView_item_child_portrait);
    childPortrait.setImageDrawable(child.getPortrait(getContext().getResources()));

    TextView childName = convertView.findViewById(R.id.TextView_item_child_name);
    childName.setText(child.getName());

    return convertView;
  }
}
