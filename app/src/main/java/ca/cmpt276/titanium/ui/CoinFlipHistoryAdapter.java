package ca.cmpt276.titanium.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.CoinFlip;

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

        ImageView resultImageView = (ImageView) convertView.findViewById(R.id.coinFlipIcon);
        TextView childTextView = (TextView) convertView.findViewById(R.id.coinFlipChild);

        // coinFlip.getChildWhoPicksSide().getName() instead of "name"
        childTextView.setText("Name " + "chose " + coinFlip.getSideThatChildPicks());

        if (coinFlip.getCoinSideLandedOn() == coinFlip.getSideThatChildPicks()) {
            resultImageView.setImageResource(R.drawable.checkmark);
        } else {
            resultImageView.setImageResource(R.drawable.xmark);
        }

        TextView coinFlipDate = (TextView) convertView.findViewById(R.id.coinFlipDate);
        coinFlipDate.setText(coinFlip.getTimeOfFlip());

        TextView resultTextView = (TextView) convertView.findViewById(R.id.coinFlipResult2);
        resultTextView.setText("Landed on " + coinFlip.getCoinSideLandedOn().toString());

        return convertView;
    }
}