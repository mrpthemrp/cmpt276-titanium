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
public class MenuChildrenListAdapter extends ArrayAdapter<Child> {
    private Context context;

    public MenuChildrenListAdapter(Context context, List<Child> children) {
        super(context, 0, children);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_menu_children_list, parent, false);
        }

        Child child = getItem(position);

        ImageView childIcon = convertView.findViewById(R.id.childIcon);
        childIcon.setImageDrawable(child.getPortrait(context.getResources()));

        TextView childTextView = convertView.findViewById(R.id.childNameList);
        String childName = child.getName();
        childTextView.setText(childName);

        return convertView;
    }
}