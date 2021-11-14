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
import ca.cmpt276.titanium.model.CoinFlip;

/**
 * This is an adapter for the coin flip history.
 */
public class CoinFlipHistoryAdapter extends ArrayAdapter<CoinFlip> {
    public CoinFlipHistoryAdapter(Context context, List<CoinFlip> coinFlipHistory) {
        super(context, 0, coinFlipHistory);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.coin_flip_history_list_item, parent, false);
        }

        CoinFlip coinFlip = getItem(position);

        ImageView resultImageView = convertView.findViewById(R.id.coinFlipIcon);
        TextView childTextView = convertView.findViewById(R.id.coinFlipChild);

        String childChoice = coinFlip.getChildWhoPicksSide().getName() + " chose " + coinFlip.getSideThatChildPicks();
        childTextView.setText(childChoice);

        if (coinFlip.getCoinSideLandedOn() == coinFlip.getSideThatChildPicks()) {
            // image retrieved from https://www.vhv.rs/dpng/f/406-4067045_checkmark-png.png
            resultImageView.setImageResource(R.drawable.checkmark);
        } else {
            // image retrieved from https://www.nicepng.com/png/full/910-9107823_circle-cross-png.png
            resultImageView.setImageResource(R.drawable.xmark);
        }

        TextView coinFlipDate = convertView.findViewById(R.id.coinFlipDate);
        coinFlipDate.setText(coinFlip.getTimeOfFlip());

        TextView resultTextView = convertView.findViewById(R.id.coinFlipResult2);
        String coinFlipResult = "Landed on " + coinFlip.getCoinSideLandedOn().toString();
        resultTextView.setText(coinFlipResult);

        return convertView;
    }
}