package ca.cmpt276.titanium.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private ImageView imageView;
    private ActivityResultLauncher<Intent> startTakePicture;
    private ActivityResultLauncher<Intent> startChooseFromGallery;

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

        ImageView editIcon = findViewById(R.id.editIcon);
        editIcon.setVisibility(View.VISIBLE);

        this.imageView = findViewById(R.id.addProfilePic);
        imageView.setAlpha(0.75f);
        imageView.setClickable(true);

        imageView.setOnClickListener(view -> selectImage());

        RoundedBitmapDrawable image = null;
        try {
            image = children.getChild(childUniqueId).getPortrait(this, ChildEditActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageDrawable(image);

        displayChildInfo();
        setupButtons();

        this.startChooseFromGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImage =  Objects.requireNonNull(data).getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);

                                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

                                RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                mDrawable.setCircular(true);

                                imageView.setImageDrawable(mDrawable);
                                cursor.close();

                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }

                                try {
                                    children.getChild(childUniqueId).setPortrait(this, ChildEditActivity.this, bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

        this.startTakePicture = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        String cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
                        File file = new File(cameraDirectory, "temp" + ".png");

                        Bitmap savedPortraitBitmap = null;
                        try {
                            savedPortraitBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        RoundedBitmapDrawable portrait = RoundedBitmapDrawableFactory.create(this.getResources(), savedPortraitBitmap);
                        portrait.setCircular(true);

                        imageView.setImageDrawable(portrait);

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }

                        try {
                            children.getChild(childUniqueId).setPortrait(this, ChildEditActivity.this, savedPortraitBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        file.delete();

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
        final String[] dialogOptions = {"Take Photo", "Select from Gallery", "Cancel"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Add Portrait")
                .setItems(dialogOptions, (dialog, item) -> {
                    switch (dialogOptions[item]) {
                        case "Take Photo":
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/temp.png";

                            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(cameraDirectory));
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                            startTakePicture.launch(takePicture);
                            break;
                        case "Choose from Gallery":
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startChooseFromGallery.launch(pickPhoto);
                            break;
                        case "Cancel":
                            dialog.dismiss();
                            break;
                    }
        }).show();
    }
}
