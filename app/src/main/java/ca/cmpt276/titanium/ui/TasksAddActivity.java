package ca.cmpt276.titanium.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.Tasks;

public class TasksAddActivity extends AppCompatActivity {

    private Children children;
    private Button saveTaskButton;
    private EditText userTaskInput;
    private Tasks taskManager;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TasksAddActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_add);
        taskManager = Tasks.getInstance();

        this.children = Children.getInstance(this);
        userTaskInput = findViewById(R.id.userTaskName);
        setUpButton();
        displayChildren();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setUpButton(){
        saveTaskButton = findViewById(R.id.saveTaskNoChildren);
        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userTaskInput.getText().toString().isEmpty()){
                    Toast.makeText(TasksAddActivity.this, "Cannot leave task name blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                String task = userTaskInput.getText().toString();
                taskManager.addTask(task);
                taskManager.addChild("Nobody");
                finish();
            }
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

        ListView childrenListView = (ListView) findViewById(R.id.childrenList);
        MenuChildrenListAdapter adapter = new MenuChildrenListAdapter(this, children.getChildren());
        childrenListView.setAdapter(adapter);
        childrenListView.setClickable(true);

        childrenListView.setOnItemClickListener((parent, view, position, id) -> {
            String child =  children.getChildren().get(position).getName();

            if(userTaskInput.getText().toString().isEmpty()){
                Toast.makeText(TasksAddActivity.this, "Cannot leave task name blank", Toast.LENGTH_SHORT).show();
                return;
            }

            String task = userTaskInput.getText().toString();
            taskManager.addTask(task);
            taskManager.addChild(child);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}