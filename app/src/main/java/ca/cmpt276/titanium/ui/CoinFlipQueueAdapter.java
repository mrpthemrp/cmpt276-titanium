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
public class CoinFlipQueueAdapter extends ArrayAdapter<Child> {

    public CoinFlipQueueAdapter(Context context, List<Child> children) {
        super(context, 0, children);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_coin_flip_children_queue, parent, false);
        }

        Child child = getItem(position);

        // TODO: Retrieve image of child
        ImageView childIcon = convertView.findViewById(R.id.childIcon);
        childIcon.setImageResource(R.drawable.ic_baseline_circle_green_24);

        TextView childTextView = convertView.findViewById(R.id.childNameList);
        String childName = child.getName();
        childTextView.setText(childName);

        return convertView;
    }
}