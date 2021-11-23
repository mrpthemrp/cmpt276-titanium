package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.cmpt276.titanium.R;

/**
 * This class handles the help screen.
 */
public class HelpActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setTitle(getString(R.string.title_help));

        Toolbar myToolbar = findViewById(R.id.toolbar_help);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setUpHyperLinks();
    }

    private void setUpHyperLinks() {
        TextView coinLink = findViewById(R.id.helpCoinFlipLink);
        coinLink.setMovementMethod(LinkMovementMethod.getInstance());

        TextView calmImageLink = findViewById(R.id.helpCalmImageLink);
        calmImageLink.setMovementMethod(LinkMovementMethod.getInstance());

        TextView alarmLink = findViewById(R.id.helpAlarmSoundLink);
        alarmLink.setMovementMethod(LinkMovementMethod.getInstance());

        TextView checkmarkImageLink = findViewById(R.id.helpCheckMarkLink);
        checkmarkImageLink.setMovementMethod(LinkMovementMethod.getInstance());

        TextView xMarkImageLink = findViewById(R.id.helpXLink);
        xMarkImageLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
