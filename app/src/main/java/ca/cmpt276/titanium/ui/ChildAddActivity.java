package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

import ca.cmpt276.titanium.BuildConfig;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;

/**
 * This activity represents the adding of a single child.
 */
public class ChildAddActivity extends AppCompatActivity {
    private Children children;
    private Toast toast; // prevents toast stacking
    private EditText childName;
    private boolean changesAccepted = true;
    private Bitmap portrait;
    private ImageView portraitView;
    private ActivityResultLauncher<Intent> startTakePhoto;
    private ActivityResultLauncher<Intent> startSelectFromGallery;

    public static Intent makeIntent(Context context) {
        return new Intent(context, ChildAddActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        setupActionBar();

        this.children = Children.getInstance(this);
        this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        setupInputFields();
        setupGUI();

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
                                this.portrait = portrait;

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
                        File cameraDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera");

                        if (cameraDirectory.exists() || cameraDirectory.mkdirs()) {
                            File portrait = new File(cameraDirectory, "temp.png");

                            Bitmap savedPortraitBitmap = null;
                            try {
                                savedPortraitBitmap = BitmapFactory.decodeStream(new FileInputStream(portrait));
                                this.portrait = savedPortraitBitmap;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            RoundedBitmapDrawable portraitDrawable = RoundedBitmapDrawableFactory.create(this.getResources(), savedPortraitBitmap);
                            portraitDrawable.setCircular(true);
                            portraitView.setImageDrawable(portraitDrawable);

                            // TODO: Delete photo from device if child deleted
                            // TODO: Store photos in separate location from D C I M/Camera?
                            // TODO: like when taking photo, store a default photo as if it was from camera app
                            // TODO: Then copy that into practical parent directory for use/deletion as required?
                            // TODO: Go back to default image if user deletes portrait photo for a given child via files app or something
                        }
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
        setTitle(R.string.title_child_add);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupInputFields() {
        childName = findViewById(R.id.childName);
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

    private void setupGUI() {
        Button saveButton = findViewById(R.id.viewFunctionBtn);
        saveButton.setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(view -> {
            if (childName.getText().toString().equals("")) {
                updateToast(getString(R.string.toast_no_name));
            } else if (nameContainsOnlyLetters(childName.getText().toString())) {
                children.addChild(childName.getText().toString(), portrait);
                updateToast(getString(R.string.toast_child_saved));
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
                changesAccepted = childName.getText().toString().equals("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Drawable defaultPortraitDrawable = Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.ic_baseline_circle_green_150));
        Bitmap defaultPortraitBitmap = Bitmap.createBitmap(defaultPortraitDrawable.getIntrinsicWidth(), defaultPortraitDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas defaultPortraitCanvas = new Canvas(defaultPortraitBitmap);
        this.portrait = defaultPortraitBitmap;

        defaultPortraitDrawable.setBounds(0, 0, defaultPortraitCanvas.getWidth(), defaultPortraitCanvas.getHeight());
        defaultPortraitDrawable.draw(defaultPortraitCanvas);

        //Bitmap newBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_black_150); //Source: http://www.onlinewebfonts.com

        ImageView editIcon = findViewById(R.id.editIcon);
        editIcon.setVisibility(View.VISIBLE);

        this.portraitView = findViewById(R.id.addProfilePic);
        portraitView.setAlpha(0.75f);
        portraitView.setClickable(true);
        portraitView.setOnClickListener(view -> selectImage());

        RoundedBitmapDrawable portrait = RoundedBitmapDrawableFactory.create(getResources(), defaultPortraitBitmap);
        portrait.setCircular(true);
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
