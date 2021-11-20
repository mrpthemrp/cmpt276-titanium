package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.cmpt276.titanium.R;

public class TasksEditActivity extends AppCompatActivity {

    private Toast toast;
    private boolean changesAccepted = true;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TasksEditActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_view);
        setTitle("Edit Task");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onBackPressed() {
        launchDiscardChangesPrompt();
    }

    private void updateToast(String toastText) {
        toast.cancel();
        toast.setText(toastText);
        toast.show();
    }

    private void launchDiscardChangesPrompt() {
        if (!changesAccepted) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_warning_black_24)
                    .setTitle(R.string.discard_changes_title)
                    .setMessage(R.string.discard_changes_message)
                    .setPositiveButton(R.string.prompt_positive, (dialog, which) -> {
                        updateToast(getString(R.string.changes_discarded_toast));
                        finish();
                    })
                    .setNegativeButton(R.string.prompt_negative, null)
                    .show();
        } else {
            finish();
        }
    }

}
