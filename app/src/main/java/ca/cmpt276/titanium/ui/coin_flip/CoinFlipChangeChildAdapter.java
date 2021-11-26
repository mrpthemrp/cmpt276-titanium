package ca.cmpt276.titanium.ui.coin_flip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;

import java.util.List;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;

/**
 * This is an adapter for the children list.
 */
public class CoinFlipChangeChildAdapter extends ArrayAdapter<Child> {
    private final Context context;

    public CoinFlipChangeChildAdapter(Context context, List<Child> children) {
        super(context, 0, children);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_menu_children, parent, false);
        }

        Child child = getItem(position);

        ImageView changeChildIcon = convertView.findViewById(R.id.childIcon);
        RoundedBitmapDrawable drawable = child.getPortrait(context.getResources());
        changeChildIcon.setImageDrawable(drawable);

        TextView childQueueNameText = convertView.findViewById(R.id.childNameList);
        String changeChildName = child.getName();
        childQueueNameText.setText(changeChildName);

        return convertView;
    }
}