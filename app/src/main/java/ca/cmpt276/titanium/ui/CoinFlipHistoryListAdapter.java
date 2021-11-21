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
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.CoinFlip;

/**
 * This is an adapter for the coin flip history.
 */
public class CoinFlipHistoryListAdapter extends ArrayAdapter<CoinFlip> {
    private final Children children;

    public CoinFlipHistoryListAdapter(Context context, ArrayList<CoinFlip> coinFlipHistory) {
        super(context, 0, coinFlipHistory);
        children = Children.getInstance(context);
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

        // TODO: Add child icon when finished
        childIcon.setImageResource(R.drawable.ic_baseline_circle_green_200);

        String childChoice = children.getChild(coinFlip.getPickerUniqueID()).getName() + getContext().getString(R.string.coin_flip_child_choice_text) + coinFlip.getChosenSide();
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
        String coinFlipResult = getContext().getString(R.string.coin_flip_result_initial_text) + coinFlip.getResult().toString();
        resultTextView.setText(coinFlipResult);

        return convertView;
    }
}