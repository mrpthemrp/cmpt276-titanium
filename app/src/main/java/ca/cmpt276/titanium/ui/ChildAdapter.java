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

import java.util.List;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;

/**
 * This is an adapter for the children list.
 */
public class ChildAdapter extends ArrayAdapter<Child> {
  public ChildAdapter(Context context, List<Child> children) {
    super(context, 0, children);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_child, parent, false);
    }

    Child child = getItem(position);

    ImageView childIcon = convertView.findViewById(R.id.ImageView_item_child_portrait);
    childIcon.setImageDrawable(child.getPortrait(getContext().getResources()));

    TextView childTextView = convertView.findViewById(R.id.TextView_item_child_name);
    childTextView.setText(child.getName());

    return convertView;
  }
}
