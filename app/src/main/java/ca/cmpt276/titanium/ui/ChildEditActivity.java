package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.BuildConfig;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

/**
 * This activity represents the editing of a single child.
 */
public class ChildEditActivity extends AppCompatActivity {
    private static final String CHILD_UNIQUE_ID_INTENT = "childUniqueID";

    private final Children children = Children.getInstance(this);
    private Toast toast; // prevents toast stacking
    private UUID childUniqueId;
    private Child childBeingEdited;
    private EditText childName;
    private boolean changesAccepted = true;
    private ImageView portraitView;
    private ActivityResultLauncher<Intent> startTakePhoto;
    private ActivityResultLauncher<Intent> startSelectFromGallery;

    public static Intent makeIntent(Context context, UUID childUniqueId) {
        Intent editChildIntent = new Intent(context, ChildEditActivity.class);
        editChildIntent.putExtra(CHILD_UNIQUE_ID_INTENT, childUniqueId);

        return editChildIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        setupActionBar();

        this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        this.childUniqueId = (UUID) getIntent().getSerializableExtra(CHILD_UNIQUE_ID_INTENT);
        this.childBeingEdited = children.getChild(childUniqueId);

        displayChildInfo();
        setupButtons();

        this.startSelectFromGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImage = Objects.requireNonNull(data).getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);

                                Bitmap portrait = BitmapFactory.decodeFile(picturePath);
                                children.getChild(childUniqueId).setPortrait(portrait);

                                RoundedBitmapDrawable portraitDrawable = RoundedBitmapDrawableFactory.create(getResources(), portrait);
                                portraitDrawable.setCircular(true);

                                portraitView.setImageDrawable(portraitDrawable);
                                cursor.close();
                            }
                        }
                    }
                });

        this.startTakePhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        String cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
                        File file = new File(cameraDirectory, "temp.png");

                        Bitmap savedPortraitBitmap = null;
                        try {
                            savedPortraitBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        RoundedBitmapDrawable portrait = RoundedBitmapDrawableFactory.create(this.getResources(), savedPortraitBitmap);
                        portrait.setCircular(true);

                        portraitView.setImageDrawable(portrait);
                        children.getChild(childUniqueId).setPortrait(savedPortraitBitmap);

                        assert file.delete();

                        // TODO: Delete photo from device if child deleted
                        // TODO: Store photos in separate location from D C I M/Camera?
                        // TODO: like when taking photo, store a default photo as if it was from camera app
                        // TODO: Then copy that into practical parent directory for use/deletion as required?
                        // TODO: Go back to default image if user deletes portrait photo for a given child via files app or something
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            launchDiscardChangesPrompt();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        launchDiscardChangesPrompt();
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.menuEdit);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void displayChildInfo() {
        this.childName = findViewById(R.id.childName);
        childName.setText(childBeingEdited.getName());
        childName.setEnabled(true);
        childName.setCursorVisible(true);
    }

    private boolean nameContainsOnlyLetters(String name) {
        char[] nameChars = name.toCharArray();

        for (char nameChar : nameChars) {
            if (!Character.isLetter(nameChar)) {
                return false;
            }
        }

        return true;
    }

    private void setupButtons() {
        Button saveButton = findViewById(R.id.viewFunctionBtn);
        saveButton.setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(view -> {
            if (childName.getText().toString().equals("")) {
                updateToast(getString(R.string.toast_no_name));
            } else if (nameContainsOnlyLetters(childName.getText().toString())) {
                children.setChildName(childUniqueId, childName.getText().toString());
                updateToast(getString(R.string.toast_child_updated));
                finish();
            } else {
                updateToast(getString(R.string.toast_invalid_name));
            }
        });

        childName.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // minimize keyboard
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
                changesAccepted = childName.getText().toString().equals(childBeingEdited.getName());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ImageView editIcon = findViewById(R.id.editIcon);
        editIcon.setVisibility(View.VISIBLE);

        this.portraitView = findViewById(R.id.addProfilePic);
        portraitView.setAlpha(0.75f);
        portraitView.setClickable(true);
        portraitView.setOnClickListener(view -> selectImage());

        RoundedBitmapDrawable portrait = children.getChild(childUniqueId).getPortrait(getResources());
        portraitView.setImageDrawable(portrait);
    }

    private void updateToast(String toastText) {
        toast.cancel();
        toast.setText(toastText);
        toast.show();
    }

    private void launchDiscardChangesPrompt() {
        if (!changesAccepted) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_warning_black_24)
                    .setTitle(R.string.prompt_discard_changes_title)
                    .setMessage(R.string.prompt_discard_changes_message)
                    .setPositiveButton(R.string.prompt_discard_changes_positive, (dialog, which) -> {
                        updateToast(getString(R.string.toast_changes_discarded));
                        finish();
                    })
                    .setNegativeButton(R.string.prompt_discard_changes_negative, null)
                    .show();
        } else {
            finish();
        }
    }

    private void selectImage() {
        final String[] dialogOptions = {getString(R.string.prompt_select_image_option1), getString(R.string.prompt_select_image_option2), getString(R.string.prompt_select_image_option3)};

        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.prompt_select_image_title)
                .setItems(dialogOptions, (dialog, item) -> {
                    switch (dialogOptions[item]) {
                        case "Take Photo":
                            File cameraDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/temp.png");
                            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", cameraDirectory);

                            Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startTakePhoto.launch(takePhoto);
                            break;
                        case "Select from Gallery":
                            startSelectFromGallery.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                            break;
                        case "Cancel":
                            dialog.dismiss();
                            break;
                    }
        }).show();
    }
}
