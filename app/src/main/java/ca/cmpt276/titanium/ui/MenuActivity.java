package ca.cmpt276.titanium.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.ui.coin_flip.CoinFlipActivity;
import ca.cmpt276.titanium.ui.tasks.TasksActivity;
import ca.cmpt276.titanium.ui.timer.TimerActivity;

/**
 * This activity represents the main menu.
 * Shows children and buttons to the timer and coin flip.
 */
public class MenuActivity extends AppCompatActivity {
    private Children children;
    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.children = Children.getInstance(this);

        Button addChildButton = findViewById(R.id.menuGoToAddChild);
        addChildButton.setOnClickListener(view -> startActivity(ChildActivity.makeIntent(this, getString(R.string.title_child_add), null)));

        Button coinFlipButton = findViewById(R.id.menuGoToFlipCoin);
        coinFlipButton.setOnClickListener(view -> startActivity(CoinFlipActivity.makeIntent(this)));

        Button timerButton = findViewById(R.id.menuGoToTimer);
        timerButton.setOnClickListener(view -> startActivity(TimerActivity.makeIntent(this)));

        Button whoseTurnButton = findViewById(R.id.menuWhoseTurn);
        whoseTurnButton.setOnClickListener(view -> startActivity(TasksActivity.makeIntent(this)));

        MaterialButton helpScreen = findViewById(R.id.helpButton);
        helpScreen.setOnClickListener(view -> startActivity(HelpActivity.makeIntent(this)));

        welcomeMessage = findViewById(R.id.mainMenuWelcomeMessage);
        setMessage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayChildren();
        setMessage();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.optionsEdit) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void displayChildren() {
        if (children.getChildren().size() == 0) {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.INVISIBLE);
        }

        ListView childrenListView = (ListView) findViewById(R.id.childrenList);
        MenuChildrenListAdapter adapter = new MenuChildrenListAdapter(this, children.getChildren());
        childrenListView.setAdapter(adapter);
        childrenListView.setClickable(true);

        childrenListView.setOnItemClickListener((parent, view, position, id) -> {
            UUID childUUID = children.getChildren().get(position).getUniqueID();
            Intent viewChildIntent = ChildActivity.makeIntent(this, getString(R.string.title_child_view), childUUID);

            startActivity(viewChildIntent);
        });
    }

    // setMessage function written from the following resource:
    // https://stackoverflow.com/questions/27589701/showing-morning-afternoon-evening-night-message-based-on-time-in-java
    private void setMessage() {
        Calendar currentCalendar = Calendar.getInstance();
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);

        if (currentHour < 12) {
            welcomeMessage.setText(getString(R.string.menuMorning));
        } else if (currentHour < 16) {
            welcomeMessage.setText(getString(R.string.menuAfternoon));
        } else if (currentHour >= 17) {
            welcomeMessage.setText(getString(R.string.menuEvening));
        }
    }
}
