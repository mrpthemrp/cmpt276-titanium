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
public class ChangeChildAdapter extends ArrayAdapter<Child> {

    public ChangeChildAdapter(Context context, List<Child> children) {
        super(context, 0, children);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_change_child_list, parent, false);
        }

        Child child = getItem(position);

        // TODO: Retrieve image of child
        ImageView changeChildIcon = convertView.findViewById(R.id.changeChildIconList);
        changeChildIcon.setImageResource(R.drawable.ic_baseline_circle_green_200);

        TextView childQueueNameText = convertView.findViewById(R.id.changeChildNameList);
        String changeChildName = child.getName();
        childQueueNameText.setText(changeChildName);

        return convertView;
    }
}