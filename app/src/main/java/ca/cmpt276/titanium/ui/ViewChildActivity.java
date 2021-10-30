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
import com.google.gson.Gson;
import java.util.Objects;

public class ViewChildActivity extends AppCompatActivity {
    private static final Gson GSON = new Gson();

    private final Children children = Children.getInstance(this);
    private Child childBeingViewed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);
        setupActionBar();

        String childBeingViewedJson = getIntent().getStringExtra("child_json");
        this.childBeingViewed = GSON.fromJson(childBeingViewedJson, Child.class);
        setChildInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_child, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.optionsEdit) {
            Intent editChildIntent = EditChildActivity.makeIntent(this, childBeingViewed);
            startActivity(editChildIntent);
            return true;
        } else if (item.getItemId() == R.id.optionsRemove) {
            removeChild();
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

    private void setChildInfo() {
        TextView childName = findViewById(R.id.childName);
        childName.setText(childBeingViewed.getName());
    }

    private void removeChild() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_delete_24_black)
                .setTitle("Delete " + childBeingViewed.getName())
                .setMessage("Are you sure? This cannot be reversed.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    children.removeChild(childBeingViewed.getUniqueId());
                    Toast.makeText(this, "Child deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    public static Intent makeIntent(Context context, Child child) {
        String childJson = GSON.toJson(child);

        Intent viewChildIntent = new Intent(context, ViewChildActivity.class);
        viewChildIntent.putExtra("child_json", childJson);

        return viewChildIntent;
    }
}
