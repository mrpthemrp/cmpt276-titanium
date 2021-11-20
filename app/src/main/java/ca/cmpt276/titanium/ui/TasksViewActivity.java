package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ca.cmpt276.titanium.R;

public class TasksViewActivity extends AppCompatActivity {

    private static final String INDEX = "UserClicked";
    private static final String BOOLEAN_VALUE = "isClicked";

    public static Intent makeIntent(Context context, int index, boolean edit){
        Intent intent = new Intent(context, TasksViewActivity.class);
        intent.putExtra(INDEX, index);
        intent.putExtra(BOOLEAN_VALUE, edit);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_view);
    }
}