package ca.cmpt276.titanium.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.Tasks;

public class TasksEditActivity extends AppCompatActivity {

    private static final String INDEX = "EditClicked";
    private int index;
    private boolean changesAccepted = true;
    private Children children;
    private Button saveTaskButton;
    private EditText userTaskInput;
    private Tasks taskManager;

    public static Intent makeIntent(Context context, int index){
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

        this.children = Children.getInstance(this);
        setUpButton();
        displayChildren();

    }

    private void setUpButton(){
        saveTaskButton = findViewById(R.id.saveTaskNoChildren);
        saveTaskButton.setOnClickListener(view -> {
            if(userTaskInput.getText().toString().isEmpty()){
                Toast.makeText(TasksEditActivity.this, "Cannot leave task name blank", Toast.LENGTH_SHORT).show();
                return;
            }
            String task = userTaskInput.getText().toString();
            taskManager.addTask(task);
            taskManager.addChild("Nobody");
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayChildren();
    }

    private void displayChildren() {
        if (children.getChildren().size() == 0) {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.VISIBLE);
            saveTaskButton.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.INVISIBLE);
            saveTaskButton.setVisibility(View.INVISIBLE);
        }

        ListView childrenListView = findViewById(R.id.childrenList);
        MenuChildrenListAdapter adapter = new MenuChildrenListAdapter(this, children.getChildren());
        childrenListView.setAdapter(adapter);
        childrenListView.setClickable(true);

        childrenListView.setOnItemClickListener((parent, view, position, id) -> {
            String child =  children.getChildren().get(position).getName();

            if(userTaskInput.getText().toString().isEmpty()){
                Toast.makeText(TasksEditActivity.this, "Cannot leave task name blank", Toast.LENGTH_SHORT).show();
                return;
            }

            String task = userTaskInput.getText().toString();
            taskManager.editTask(index, task);
            // TODO: need to fix edit for child (as it replaces the index, so it will be there twice and the other child is gone)
            taskManager.editChild(index, child);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        launchDiscardChangesPrompt();
    }

    private void launchDiscardChangesPrompt() {
        if (!changesAccepted) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_warning_black_24)
                    .setTitle(R.string.discard_changes_title)
                    .setMessage(R.string.discard_changes_message)
                    .setPositiveButton(R.string.prompt_positive, (dialog, which) -> {
                        Toast.makeText(TasksEditActivity.this, R.string.changes_discarded_toast, Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton(R.string.prompt_negative, null)
                    .show();
        } else {
            finish();
        }
    }

}
