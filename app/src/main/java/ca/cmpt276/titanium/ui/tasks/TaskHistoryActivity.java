package ca.cmpt276.titanium.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.TaskHistory;
import ca.cmpt276.titanium.model.TaskManager;

public class TaskHistoryActivity extends AppCompatActivity {

    private static final String TASK_INDEX_KEY = "indexTask";
    private static final int INVALID_TASK_INDEX = -1;
    private ArrayList<TaskHistory> historyForTaskList = new ArrayList<>();

    public static Intent makeIntent(Context context, int taskIndex) {
        Intent intent = new Intent(context, TaskHistoryActivity.class);
        intent.putExtra(TASK_INDEX_KEY, taskIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);


        Toolbar toolbar = findViewById(R.id.ToolBar_task_history_manage);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        TaskManager taskManager = TaskManager.getInstance(this);
        int taskIndex = getIntent().getIntExtra(TASK_INDEX_KEY, INVALID_TASK_INDEX);

        if (taskIndex == INVALID_TASK_INDEX) {
            finish();
        }

        historyForTaskList = taskManager.createListForSpecificTask(taskManager.getTasks().get(taskIndex).getTaskName());
        populateTaskList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void populateTaskList() {
        adapter taskAdapter = new adapter(this, historyForTaskList);

        ListView taskListView = findViewById(R.id.ListView_tasks_history);
        taskListView.setAdapter(taskAdapter);

        TextView noTasksMessage = findViewById(R.id.TextView_history_empty_state_message);
        noTasksMessage.setVisibility(
                historyForTaskList.size() == 0
                        ? View.VISIBLE
                        : View.INVISIBLE);
    }

    public class adapter extends ArrayAdapter<TaskHistory> {
        public adapter(Context context, ArrayList<TaskHistory> tasks) {
            super(context, 0, tasks);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView =
                        LayoutInflater.from(getContext()).inflate(R.layout.item_history_task, parent, false);
            }

            ChildManager childManager = ChildManager.getInstance(getContext());

            TaskHistory task = getItem(position);

            TextView childName = convertView.findViewById(R.id.TextView_item_task_child);
            childName.setText(getContext().getString(R.string.item_task_child_name,
                    childManager.getChild(task.getChildUniqueID()).getName()));

            TextView taskDate = convertView.findViewById(R.id.TextView_item_task_date);
            taskDate.setText(getContext().getString(R.string.item_task_date,task.getDate()));

            ImageView iconItem = convertView.findViewById(R.id.ImageView_item_history_child_image);
            Child child = childManager.getChild(task.getChildUniqueID());
            iconItem.setImageDrawable(child.getPortrait(getContext().getResources()));

            return convertView;
        }
    }
}