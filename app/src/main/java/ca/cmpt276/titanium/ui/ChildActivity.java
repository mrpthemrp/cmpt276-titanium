package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.BuildConfig;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

// TODO: Fix select from gallery option
// TODO: Replace current default portrait with PNG and move associated code into Child
// TODO: Save cropped versions of photos rather than originals
// TODO: Add ability to manually crop photos in-app

/**
 * This activity represents the viewing, adding, and editing of a single child.
 */
public class ChildActivity extends AppCompatActivity {
    private static final String INTENT_TYPE_KEY = "intentType";
    private static final String CHILD_UNIQUE_ID_KEY = "childUniqueID";
    private static final String ADD_CHILD_INTENT = "Add Child";
    private static final String EDIT_CHILD_INTENT = "Edit Child";
    private static final String VIEW_CHILD_INTENT = "View Child";

    private String intentType;
    private Children children;
    private Toast toast; // prevents toast stacking
    EditText childName;
    private ImageView portraitView;
    private UUID focusedChildUniqueID;
    private boolean changesAccepted = true;
    private ActivityResultLauncher<Intent> takePhoto;
    private ActivityResultLauncher<Intent> selectFromGallery;
    private String currentPhotoPath;

    public static Intent makeIntent(Context context, String intentType, UUID childUniqueID) {
        Intent intent = new Intent(context, ChildActivity.class);
        intent.putExtra(INTENT_TYPE_KEY, intentType);
        intent.putExtra(CHILD_UNIQUE_ID_KEY, childUniqueID);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        this.intentType = getIntent().getStringExtra(INTENT_TYPE_KEY);
        setupActionBar(intentType);

        this.children = Children.getInstance(this);
        this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        this.portraitView = findViewById(R.id.addProfilePic);
        this.childName = findViewById(R.id.childName);

        if (intentType.equals(EDIT_CHILD_INTENT) || intentType.equals(VIEW_CHILD_INTENT)) {
            this.focusedChildUniqueID = (UUID) getIntent().getSerializableExtra(CHILD_UNIQUE_ID_KEY);
        }

        setupActivityResults();
        setupGUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (intentType.equals(VIEW_CHILD_INTENT)) {
            getMenuInflater().inflate(R.menu.menu_child_view, menu);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (intentType.equals(VIEW_CHILD_INTENT)) {
            portraitView.setImageDrawable(children.getChild(focusedChildUniqueID).getPortrait(getResources()));
            childName.setText(children.getChild(focusedChildUniqueID).getName());
        }
    }

    @Override
    public void onBackPressed() {
        launchPrompt(getString(R.string.prompt_discard_changes_title),
                getString(R.string.prompt_discard_changes_message),
                getString(R.string.toast_changes_discarded));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.optionsHelp) {
            Intent editChildIntent = ChildActivity.makeIntent(this, getString(R.string.menuEdit), focusedChildUniqueID);
            startActivity(editChildIntent);
            return true;
        } else if (item.getItemId() == R.id.optionsRemove) {
            launchPrompt(getString(R.string.prompt_delete_child_title, children.getChild(focusedChildUniqueID).getName()),
                    getString(R.string.prompt_delete_child_message),
                    getString(R.string.toast_child_deleted));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar(String intentType) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(intentType);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupActivityResults() {
        this.takePhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        portraitView.setImageDrawable(Child.getOtherPortrait(getResources(), currentPhotoPath));

                        if (intentType.equals(EDIT_CHILD_INTENT)) {
                            children.getChild(focusedChildUniqueID).setPortraitPath(currentPhotoPath);
                        }
                    }
                });

        this.selectFromGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Uri selectedImage = Objects.requireNonNull(result.getData()).getData();
                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage, projection, null, null, null);

                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            this.currentPhotoPath = cursor.getString(columnIndex);
                        }

                        cursor.close();

                        portraitView.setImageDrawable(Child.getOtherPortrait(getResources(), selectedImage.getPath()));

                        if (intentType.equals(EDIT_CHILD_INTENT)) {
                            children.getChild(focusedChildUniqueID).setPortraitPath(currentPhotoPath);
                        }
                    }
                });
    }

    private void setupGUI() {
        if (intentType.equals(VIEW_CHILD_INTENT)) {
            return;
        }

        setupPortrait();
        setupChildName();

        Button saveButton = findViewById(R.id.viewFunctionBtn);
        saveButton.setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(view -> {
            if (childName.getText().toString().equals("")) {
                updateToast(getString(R.string.toast_no_name));
            } else {
                if (intentType.equals(ADD_CHILD_INTENT)) {
                    children.addChild(childName.getText().toString(), currentPhotoPath);
                } else if (intentType.equals(EDIT_CHILD_INTENT)) {
                    children.setChildName(focusedChildUniqueID, childName.getText().toString());
                }

                updateToast(getString(R.string.toast_child_saved));
                finish();
            }
        });
    }

    private void setupPortrait() {
        RoundedBitmapDrawable portrait;

        if (intentType.equals(ADD_CHILD_INTENT)) {
            Drawable defaultPortraitDrawable = Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.ic_baseline_circle_green_200));
            Bitmap defaultPortraitBitmap = Bitmap.createBitmap(defaultPortraitDrawable.getIntrinsicWidth(), defaultPortraitDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas defaultPortraitCanvas = new Canvas(defaultPortraitBitmap);

            defaultPortraitDrawable.setBounds(0, 0, defaultPortraitCanvas.getWidth(), defaultPortraitCanvas.getHeight());
            defaultPortraitDrawable.draw(defaultPortraitCanvas);

            portrait = RoundedBitmapDrawableFactory.create(getResources(), defaultPortraitBitmap);
            portrait.setCircular(true);
        } else {
            portrait = children.getChild(focusedChildUniqueID).getPortrait(getResources());
            childName.setText(children.getChild(focusedChildUniqueID).getName());
        }

        portraitView.setImageDrawable(portrait);
        portraitView.setAlpha(0.75f);
        portraitView.setClickable(true);
        portraitView.setOnClickListener(view -> selectImage());

        ImageView editIcon = findViewById(R.id.editIcon);
        editIcon.setVisibility(View.VISIBLE);
    }

    private void setupChildName() {
        childName.setEnabled(true);
        childName.setCursorVisible(true);

        childName.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(childName.getWindowToken(), 0);

                childName.clearFocus();
                childName.setText(childName.getText());
            }

            return false;
        });

        childName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (intentType.equals(ADD_CHILD_INTENT)) {
                    changesAccepted = childName.getText().toString().equals("");
                } else if (intentType.equals(EDIT_CHILD_INTENT)) {
                    changesAccepted = childName.getText().toString().equals(children.getChild(focusedChildUniqueID).getName());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void updateToast(String toastText) {
        toast.cancel();
        toast.setText(toastText);
        toast.show();
    }

    private void launchPrompt(String title, String message, String positiveToast) {
        if (!changesAccepted || title.equals(getString(R.string.prompt_delete_child_title, children.getChild(focusedChildUniqueID).getName()))) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_delete_black_24)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.prompt_discard_changes_positive, (dialog, which) -> {
                        if (title.equals(getString(R.string.prompt_delete_child_title, children.getChild(focusedChildUniqueID).getName()))) {
                            children.removeChild(children.getChild(focusedChildUniqueID).getUniqueID());
                        }

                        updateToast(positiveToast);
                        finish();
                    })
                    .setNegativeButton(R.string.prompt_discard_changes_negative, null)
                    .show();
        }
    }

    private void selectImage() {
        final String[] dialogOptions = {getString(R.string.prompt_select_image_option1), getString(R.string.prompt_select_image_option2), getString(R.string.prompt_select_image_option3)};

        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.prompt_select_image_title)
                .setItems(dialogOptions, (dialog, item) -> {
                    switch (dialogOptions[item]) {
                        case "Take Photo":
                            File portraitFile = null;

                            try {
                                portraitFile = File.createTempFile("portrait_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                                this.currentPhotoPath = portraitFile.getAbsolutePath();
                            } catch (IOException e) {
                                e.getStackTrace();
                            }

                            if (portraitFile != null) {
                                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", portraitFile));
                                takePhoto.launch(takePhotoIntent);
                            }

                            break;
                        case "Select from Gallery":
                            selectFromGallery.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                            break;
                        case "Cancel":
                            dialog.dismiss();
                            break;
                    }
                }).show();
    }
}
