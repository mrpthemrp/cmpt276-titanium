package ca.cmpt276.titanium.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.Tasks;

public class TasksViewActivity extends AppCompatActivity {

    private static final String INDEX = "UserClicked";
    private static final String BOOLEAN_VALUE = "isClicked";
    private int index;
    private boolean edit;
    private Children children;
    private Tasks taskManager;

    public static Intent makeIntent(Context context, int index, boolean edit){
        Intent intent = new Intent(context, TasksViewActivity.class);
        intent.putExtra(INDEX, index);
        intent.putExtra(BOOLEAN_VALUE, edit);
        return intent;
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        index = intent.getIntExtra(INDEX, 0);
        edit = intent.getBooleanExtra(BOOLEAN_VALUE,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_view);
        this.children = Children.getInstance(this);
        taskManager = Tasks.getInstance();

        extractIntentData();
        displayData();
        setUpButtons();
    }

    private void displayData(){
        TextView childName = findViewById(R.id.childNameText);
        TextView taskName = findViewById(R.id.taskNameText);

        String name = taskManager.getChild(index);
        String task = taskManager.getTask(index);

        childName.setText(name);
        taskName.setText(task);
    }

    private void setUpButtons(){
        Button completeTask = findViewById(R.id.completeTaskButton);
        completeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Button cancel = findViewById(R.id.cancelTaskButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        else if(item.getItemId() == R.id.taskRemove){

        }

        else if(item.getItemId() == R.id.taskEdit){

        }
        return super.onOptionsItemSelected(item);
    }
}