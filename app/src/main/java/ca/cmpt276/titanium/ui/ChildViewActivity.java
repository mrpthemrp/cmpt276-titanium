package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

/**
 * This activity represents the viewing of a single child.
 */
public class ChildViewActivity extends AppCompatActivity {
    private static final String CHILD_UNIQUE_ID_INTENT = "childUniqueID";

    private final Children children = Children.getInstance(this);
    private Toast toast; // prevents toast stacking
    private UUID childUniqueId;
    private Child childBeingViewed;


    public static Intent makeIntent(Context context, UUID childUniqueId) {
        Intent viewChildIntent = new Intent(context, ChildViewActivity.class);
        viewChildIntent.putExtra(CHILD_UNIQUE_ID_INTENT, childUniqueId);

        return viewChildIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        setupActionBar();

        this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        this.childUniqueId = (UUID) getIntent().getSerializableExtra(CHILD_UNIQUE_ID_INTENT);
        this.childBeingViewed = children.getChild(childUniqueId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.childBeingViewed = children.getChild(childUniqueId);
        displayChildInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.optionsEdit) {
            Intent editChildIntent = ChildEditActivity.makeIntent(this, childUniqueId);
            startActivity(editChildIntent);
            return true;
        } else if (item.getItemId() == R.id.optionsRemove) {
            launchDeleteChildPrompt();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.menuViewChild);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void displayChildInfo() {
        TextView childName = findViewById(R.id.childName);
        childName.setText(childBeingViewed.getName());
    }

    private void updateToast(String toastText) {
        toast.cancel();
        toast.setText(toastText);
        toast.show();
    }

    private void launchDeleteChildPrompt() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_delete_black_24)
                .setTitle(getString(R.string.delete_child_prompt_title) + childBeingViewed.getName())
                .setMessage(R.string.delete_child_prompt_message)
                .setPositiveButton(R.string.prompt_positive, (dialog, which) -> {
                    children.removeChild(childBeingViewed.getUniqueID());
                    updateToast(getString(R.string.delete_child_prompt_toast));
                    finish();
                })
                .setNegativeButton(R.string.prompt_negative, null)
                .show();
    }
}
