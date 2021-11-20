package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Tasks;

public class TasksActivity extends AppCompatActivity {

    private ArrayList<String> taskList = new ArrayList<>();
    private ArrayList<String> childList = new ArrayList<>();
    private Tasks taskManager;
    private TextView noTasks;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TasksActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        setTitle(R.string.whoseTurn);
        noTasks = findViewById(R.id.noTasksText);

        taskManager = Tasks.getInstance();
        checkTaskList();
        populate();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        registerClickCallback();
    }

    private void checkTaskList(){
        if(taskManager.numberOfTasks() == 0){
            noTasks.setVisibility(View.VISIBLE);
        }
        else{
            noTasks.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        populate();
        checkTaskList();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        else if(item.getItemId() == R.id.taskAdd){
           startActivity(TasksAddActivity.makeIntent(this));
        }
        return super.onOptionsItemSelected(item);
    }

    public void set(){
        taskList = taskManager.getListOfTasks();
        childList = taskManager.getListOfChildren();
    }

    private void populate(){
        set();
        ArrayAdapter<String> adapter = new adapter();
        ListView list = findViewById(R.id.taskListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public class adapter extends ArrayAdapter<String>{
        public adapter(){
            super(TasksActivity.this, R.layout.list_item, taskList);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View taskItemView = convertView;

            if(taskItemView == null){
                taskItemView = getLayoutInflater().inflate(R.layout.list_item, parent, false );
            }

            String task = taskList.get(position);
            String name = childList.get(position);

            TextView taskName = taskItemView.findViewById(R.id.taskNameInList);
            taskName.setText(task);

            TextView childName = taskItemView.findViewById(R.id.childNameForTaskInList);
            childName.setText(name);

            return taskItemView;
        }
    }

    private void registerClickCallback(){
        ListView list = findViewById(R.id.taskListView);
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = TasksViewActivity.makeIntent(TasksActivity.this, i, true);
            startActivity(intent);
        });
    }
}