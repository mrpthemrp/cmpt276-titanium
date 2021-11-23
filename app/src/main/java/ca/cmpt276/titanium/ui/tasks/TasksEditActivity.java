package ca.cmpt276.titanium.ui.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Tasks;

/**
 * This class displays the details for a single task and allows the user to edit task data.
 */
public class TasksEditActivity extends AppCompatActivity {

    private static final String INDEX = "EditClicked";
    private int index;
    private TextView userTaskName;
    private Tasks taskManager;

    public static Intent makeIntent(Context context, int index) {
        Intent intent = new Intent(context, TasksEditActivity.class);
        intent.putExtra(INDEX, index);
        return intent;
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        index = intent.getIntExtra(INDEX, 0);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_add);

        Toolbar myToolbar = findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        setTitle("Edit Task");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        extractIntentData();

        taskManager = Tasks.getInstance();
        TextView titleAddText = findViewById(R.id.titleAddText);
        titleAddText.setText("Edit Name of Task:");
        userTaskName = findViewById(R.id.userTaskName);
        userTaskName.setText(taskManager.getTask(index));

        setUpButton();
    }

    private void setUpButton() {
        Button saveTaskButton = findViewById(R.id.saveTask);
        saveTaskButton.setText(getResources().getString(R.string.edit_task_button));
        saveTaskButton.setOnClickListener(view -> {
            if (userTaskName.getText().toString().isEmpty()) {
                Toast.makeText(TasksEditActivity.this, "Cannot leave task name blank", Toast.LENGTH_SHORT).show();
                return;
            }
            String task = userTaskName.getText().toString();
            taskManager.editTask(index, task);
            finish();
        });
    }


    @Override
    public void onBackPressed() {
        launchDiscardChangesPrompt();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            launchDiscardChangesPrompt();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchDiscardChangesPrompt() {

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_warning_black_24)
                .setTitle(R.string.prompt_discard_changes_title)
                .setMessage(R.string.prompt_discard_changes_message)
                .setPositiveButton(R.string.prompt_discard_changes_positive, (dialog, which) -> {
                    Toast.makeText(TasksEditActivity.this, R.string.toast_changes_discarded, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.prompt_discard_changes_negative, null)
                .show();

    }
}
