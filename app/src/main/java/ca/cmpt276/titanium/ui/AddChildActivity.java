package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;


public class AddChildActivity extends AppCompatActivity {
    private Children instance = Children.getInstance(this);
    private EditText childName;
    private String newName;
    private Child selectedChild;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        //setup text watcher!
        newName="Bob the Builder";//dummy

        addChild(newName);
        setupScreenText();
        setupButton();
    }

    private void setupButton() {
        add.setOnClickListener(view -> {
            instance.saveData();
            finish();
        });
    }
    private void findSelectedChild() {
        for(int i =0; i< instance.getNumOfChildren();i++){
            if(Children.getChildren().get(i).isSelected()){
                this.selectedChild = instance.getChild(i);
            }
        }
    }
    private void addChild(String name) {
        instance.addChild(name,true);
        findSelectedChild();
        System.out.println("getName() "+selectedChild.getName());
//        this.childName.setText(selectedChild.getName());
    }

    private void setupScreenText() {
        this.childName = findViewById(R.id.childName);
        this.childName.setText(selectedChild.getName());

        this.add = findViewById(R.id.viewFunctionBtn);
        this.add.setText(getResources().getString(R.string.viewSave));
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, AddChildActivity.class);
    }
}