package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        childName = findViewById(R.id.childName);
        childName.addTextChangedListener(childWatch);

        setupScreenText();
        setupButton();
    }

    private void setupButton() {
        add.setOnClickListener(view -> {
            addChild(newName);
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
        //this.childName.setText(selectedChild.getName());
    }

    private void setupScreenText() {
        this.childName = findViewById(R.id.childName);
        if(selectedChild != null){
            this.childName.setText(selectedChild.getName());
        }

        this.add = findViewById(R.id.viewFunctionBtn);
        this.add.setText(getResources().getString(R.string.viewSave));
    }

    public TextWatcher childWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            newName = childName.getText().toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    public static Intent makeIntent(Context c){
        return new Intent(c, AddChildActivity.class);
    }
}