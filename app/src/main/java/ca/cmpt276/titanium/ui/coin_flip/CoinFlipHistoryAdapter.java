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

import java.util.ArrayList;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.CoinFlip;

/**
 * This is an adapter for the coin flip history.
 */
public class CoinFlipHistoryAdapter extends ArrayAdapter<CoinFlip> {
    private final ChildManager childManager;
    private final Context context;

    public CoinFlipHistoryAdapter(Context context, ArrayList<CoinFlip> coinFlipHistory) {
        super(context, 0, coinFlipHistory);
        childManager = ChildManager.getInstance(context);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_coin_flip, parent, false);
        }

        CoinFlip coinFlip = getItem(position);

        ImageView childIcon = convertView.findViewById(R.id.ImageView_item_coin_flip_portrait);
        ImageView resultImageView = convertView.findViewById(R.id.ImageView_item_coin_flip_result);
        TextView childTextView = convertView.findViewById(R.id.TextView_item_coin_flip_child_name);

        RoundedBitmapDrawable drawable = childManager.getChild(coinFlip.getPickerUniqueID()).getPortrait(context.getResources());
        childIcon.setImageDrawable(drawable);

        String childChoice = childManager.getChild(coinFlip.getPickerUniqueID()).getName() +
                getContext().getString(R.string.coin_flip_history_child_choice) + coinFlip.getChosenSide();
        childTextView.setText(childChoice);

        if (coinFlip.getResult() == coinFlip.getChosenSide()) {
            // image retrieved from https://www.vhv.rs/dpng/f/406-4067045_checkmark-png.png
            resultImageView.setImageResource(R.drawable.ic_checkmark_green);
        } else {
            // image retrieved from https://www.nicepng.com/png/full/910-9107823_circle-cross-png.png
            resultImageView.setImageResource(R.drawable.ic_xmark_red);
        }

        TextView coinFlipDate = convertView.findViewById(R.id.TextView_item_coin_flip_date);
        coinFlipDate.setText(coinFlip.getTimeOfFlip());

        TextView resultTextView = convertView.findViewById(R.id.TextView_item_coin_flip_result);
        String coinFlipResult = getContext().getString(R.string.coin_flip_history_result) + coinFlip.getResult().toString();
        resultTextView.setText(coinFlipResult);

        return convertView;
    }
}