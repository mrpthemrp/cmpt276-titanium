package ca.cmpt276.titanium.ui.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.Task;

/**
 * Displays each Task object from a list of Task objects.
 *
 * @author Titanium
 */
public class TaskAdapter extends ArrayAdapter<Task> {
  public TaskAdapter(Context context, ArrayList<Task> tasks) {
    super(context, 0, tasks);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
    }

    Task task = getItem(position);

    TextView taskName = convertView.findViewById(R.id.TextView_item_task_task_name);
    taskName.setText(getContext().getString(R.string.item_task_task_name, task.getTaskName()));

    ChildManager childManager = ChildManager.getInstance(getContext());

    TextView childName = convertView.findViewById(R.id.TextView_item_task_child_name);
    childName.setText(
        task.getChildUniqueID() == null
            ? getContext().getString(R.string.empty_state_tasks_no_children)
            : getContext().getString(R.string.tasks_next_child_name,
            childManager.getChild(task.getChildUniqueID()).getName()));

    return convertView;
  }
}
