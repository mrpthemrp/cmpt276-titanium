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
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.CoinFlip;

/**
 * This is an adapter for the coin flip history.
 */
public class CoinFlipHistoryListAdapter extends ArrayAdapter<CoinFlip> {
    private final Children children;
    private final Context context;

    public CoinFlipHistoryListAdapter(Context context, ArrayList<CoinFlip> coinFlipHistory) {
        super(context, 0, coinFlipHistory);
        children = Children.getInstance(context);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_coin_flip_history_list, parent, false);
        }

        CoinFlip coinFlip = getItem(position);

        ImageView childIcon = convertView.findViewById(R.id.coinFlipHistoryChildIcon);
        ImageView resultImageView = convertView.findViewById(R.id.childIcon);
        TextView childTextView = convertView.findViewById(R.id.childNameList);

        RoundedBitmapDrawable drawable = children.getChild(coinFlip.getPickerUniqueID()).getPortrait(context.getResources());
        childIcon.setImageDrawable(drawable);

        String childChoice = children.getChild(coinFlip.getPickerUniqueID()).getName() +
                getContext().getString(R.string.coin_flip_history_child_choice) + coinFlip.getChosenSide();
        childTextView.setText(childChoice);

        if (coinFlip.getResult() == coinFlip.getChosenSide()) {
            // image retrieved from https://www.vhv.rs/dpng/f/406-4067045_checkmark-png.png
            resultImageView.setImageResource(R.drawable.ic_checkmark_green);
        } else {
            // image retrieved from https://www.nicepng.com/png/full/910-9107823_circle-cross-png.png
            resultImageView.setImageResource(R.drawable.ic_xmark_red);
        }

        TextView coinFlipDate = convertView.findViewById(R.id.coinFlipDate);
        coinFlipDate.setText(coinFlip.getTimeOfFlip());

        TextView resultTextView = convertView.findViewById(R.id.coinFlipResult2);
        String coinFlipResult = getContext().getString(R.string.coin_flip_history_result) + coinFlip.getResult().toString();
        resultTextView.setText(coinFlipResult);

        return convertView;
    }
}