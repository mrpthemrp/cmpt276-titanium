package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

public class ViewChildActivity extends AppCompatActivity {
    private Children instance = Children.getInstance(this);
    private TextView childName;
    private Child selectedChild;
    private Button view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        findSelectedChild();
        setupScreenText();
    }
    @Override
    protected void onStop() {
        selectedChild.setSelected(false);
        this.childName.setEnabled(true);
        super.onStop();
    }

    private void findSelectedChild() {
        for(int i =0; i< instance.getNumOfChildren();i++){
            if(Children.getChildren().get(i).isSelected()){
                this.selectedChild = instance.getChild(i);
            }
        }
    }

    private void setupScreenText() {
        this.childName = findViewById(R.id.childName);
        this.childName.setText(selectedChild.getName());
        this.childName.setEnabled(false);

        this.view = findViewById(R.id.viewFunctionBtn);
        this.view.setVisibility(View.INVISIBLE);
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, ViewChildActivity.class);
    }

}