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
 * Displays each CoinFlip object from a list of CoinFlip objects.
 *
 * @author Titanium
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

    ImageView resultIcon = convertView.findViewById(R.id.ImageView_item_coin_flip_result);
    resultIcon.setImageResource(
        coinFlip.getResult() == coinFlip.getChildChosenSide()
            ? R.drawable.ic_checkmark_green
            : R.drawable.ic_xmark_red);

    ChildManager childManager = ChildManager.getInstance(getContext());
    Child child = childManager.getChild(coinFlip.getChildUniqueID());

    TextView childChoice = convertView.findViewById(R.id.TextView_item_coin_flip_child_choice);
    String childChoiceMessage = child.getName()
        + getContext().getString(R.string.item_coin_flip_child_choice)
        + coinFlip.getChildChosenSide();
    childChoice.setText(childChoiceMessage);

    TextView coinFlipDate = convertView.findViewById(R.id.TextView_item_coin_flip_date);
    coinFlipDate.setText(coinFlip.getDate());

    TextView coinFlipResult = convertView.findViewById(R.id.TextView_item_coin_flip_result);
    String coinFlipResultMessage =
        getContext().getString(R.string.item_coin_flip_result) + coinFlip.getResult().toString();
    coinFlipResult.setText(coinFlipResultMessage);

    ImageView childPortrait = convertView.findViewById(R.id.ImageView_item_coin_flip_portrait);
    childPortrait.setImageDrawable(child.getPortrait(getContext().getResources()));

    return convertView;
  }
}
