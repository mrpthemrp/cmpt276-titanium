package ca.cmpt276.titanium.ui.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Tasks;

public class TasksEditActivity extends AppCompatActivity {

    private static final String INDEX = "EditClicked";
    private int index;
    private EditText userTaskInput;
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

        taskManager = Tasks.getInstance();
        TextView titleAddText = findViewById(R.id.titleAddText);
        titleAddText.setText("Edit Name of Task:");
        userTaskInput = findViewById(R.id.userTaskName);
        userTaskInput.setText(taskManager.getTask(index));

        extractIntentData();
        setUpButton();
    }

    private void setUpButton() {
        Button saveTaskButton = findViewById(R.id.saveTask);
        saveTaskButton.setOnClickListener(view -> {
            if (userTaskInput.getText().toString().isEmpty()) {
                Toast.makeText(TasksEditActivity.this, "Cannot leave task name blank", Toast.LENGTH_SHORT).show();
                return;
            }
            String task = userTaskInput.getText().toString();
            taskManager.editTask(index, task);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        launchDiscardChangesPrompt();
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
