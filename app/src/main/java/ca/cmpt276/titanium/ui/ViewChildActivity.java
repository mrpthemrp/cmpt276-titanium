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
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;
import java.util.Objects;

public class ViewChildActivity extends AppCompatActivity {
    private static final int INVALID_UNIQUE_ID = -1;

    private final Children children = Children.getInstance(this);
    private int childUniqueId;
    private Child childBeingViewed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        setupActionBar();

        this.childUniqueId = getIntent().getIntExtra("child_unique_id", INVALID_UNIQUE_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.childBeingViewed = children.getChild(childUniqueId);
        displayChildInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        } else if (item.getItemId() == R.id.optionsEdit) {
            Intent editChildIntent = EditChildActivity.makeIntent(this, childUniqueId);
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

    private void launchDeleteChildPrompt() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_delete_black_24)
                .setTitle(getString(R.string.delete_child_prompt_title) + childBeingViewed.getName())
                .setMessage(R.string.delete_child_prompt_message)
                .setPositiveButton(R.string.prompt_positive, (dialog, which) -> {
                    children.removeChild(childBeingViewed.getUniqueId());
                    Toast.makeText(this, R.string.delete_child_prompt_toast, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.prompt_negative, null)
                .show();
    }

    public static Intent makeIntent(Context context, int childUniqueId) {
        Intent viewChildIntent = new Intent(context, ViewChildActivity.class);
        viewChildIntent.putExtra("child_unique_id", childUniqueId);

        return viewChildIntent;
    }
}
