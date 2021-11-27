package ca.cmpt276.titanium.ui.coinflip;

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
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.CoinFlip;

/**
 * This is an adapter for the coin flip history.
 */
public class CoinFlipAdapter extends ArrayAdapter<CoinFlip> {
  public CoinFlipAdapter(Context context, ArrayList<CoinFlip> coinFlipHistory) {
    super(context, 0, coinFlipHistory);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      convertView =
          LayoutInflater.from(getContext()).inflate(R.layout.item_coin_flip, parent, false);
    }

    CoinFlip coinFlip = getItem(position);

    ImageView childIcon = convertView.findViewById(R.id.ImageView_item_coin_flip_portrait);
    ImageView resultImageView = convertView.findViewById(R.id.ImageView_item_coin_flip_result);
    TextView childTextView = convertView.findViewById(R.id.TextView_item_coin_flip_child_name);

    ChildManager childManager = ChildManager.getInstance(getContext());
    Child child = childManager.getChild(coinFlip.getPickerUniqueID());
    childIcon.setImageDrawable(child.getPortrait(getContext().getResources()));

    String childChoice = child.getName()
        + getContext().getString(R.string.coin_flip_history_child_choice)
        + coinFlip.getChosenSide();
    childTextView.setText(childChoice);

    resultImageView.setImageResource(
        coinFlip.getResult() == coinFlip.getChosenSide()
            ? R.drawable.ic_checkmark_green
            : R.drawable.ic_xmark_red);

    TextView coinFlipDate = convertView.findViewById(R.id.TextView_item_coin_flip_date);
    coinFlipDate.setText(coinFlip.getTimeOfFlip());

    TextView resultTextView = convertView.findViewById(R.id.TextView_item_coin_flip_result);
    String coinFlipResult =
        getContext().getString(R.string.coin_flip_history_result) + coinFlip.getResult().toString();
    resultTextView.setText(coinFlipResult);

    return convertView;
  }
}
