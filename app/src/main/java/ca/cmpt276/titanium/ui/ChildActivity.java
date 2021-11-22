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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.BuildConfig;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;

/**
 * This activity represents the viewing, adding, and editing of a single child.
 */
public class ChildActivity extends AppCompatActivity {
    private static final String INTENT_TYPE_KEY = "intentType";
    private static final String EDIT_CHILD_INTENT = "Edit Child";
    private static final String VIEW_CHILD_INTENT = "View Child";
    private static final String CHILD_UNIQUE_ID_KEY = "childUniqueID";

    private String intentType;
    private Children children;
    private Toast toast; // prevents toast stacking
    private UUID focusedChildUniqueID;
    private boolean changesAccepted = true;
    private ImageView portraitView;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> startTakePhoto;
    private ActivityResultLauncher<Intent> startSelectFromGallery;

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

        if (intentType.equals(EDIT_CHILD_INTENT) || intentType.equals(VIEW_CHILD_INTENT)) {
            this.focusedChildUniqueID = (UUID) getIntent().getSerializableExtra(CHILD_UNIQUE_ID_KEY);
        }

        setupActivityResults();
        setupInputFields();
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

    @Override
    protected void onResume() {
        super.onResume();
        setupInputFields();
    }

    @Override
    public void onBackPressed() {
        launchPrompt(getString(R.string.prompt_discard_changes_title),
                getString(R.string.prompt_discard_changes_message),
                getString(R.string.toast_changes_discarded));
    }

    private void setupActionBar(String intentType) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(intentType);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupActivityResults() {
        this.startTakePhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        setPic();
                        galleryAddPic();

                        if (intentType.equals(EDIT_CHILD_INTENT)) {
                            children.getChild(focusedChildUniqueID).setPortraitPath(currentPhotoPath);
                        }
                    }
                });

        this.startSelectFromGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Uri selectedImage = Objects.requireNonNull(result.getData()).getData();
                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage, projection, null, null, null);

                        if (cursor != null) {
                            int columnIndex = cursor.getColumnIndex(projection[0]);

                            cursor.moveToFirst();
                            this.currentPhotoPath = cursor.getString(columnIndex);
                            cursor.close();

                            setPic();

                            if (intentType.equals(EDIT_CHILD_INTENT)) {
                                children.getChild(focusedChildUniqueID).setPortraitPath(currentPhotoPath);
                            }
                        }
                    }
                });
    }

    private void setupInputFields() {
        EditText childName = findViewById(R.id.childName);
        childName.setEnabled(true);
        childName.setCursorVisible(true);

        if (intentType.equals(EDIT_CHILD_INTENT) || intentType.equals(VIEW_CHILD_INTENT)) {
            childName.setText(children.getChild(focusedChildUniqueID).getName());
        }

        RoundedBitmapDrawable portrait = null;

        if (intentType.equals(EDIT_CHILD_INTENT) || intentType.equals(VIEW_CHILD_INTENT)) {
            portrait = children.getChild(focusedChildUniqueID).getPortrait(getResources());
        }

        if (portrait == null) {
            Drawable defaultPortraitDrawable = Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.ic_baseline_circle_green_200));
            Bitmap defaultPortraitBitmap = Bitmap.createBitmap(defaultPortraitDrawable.getIntrinsicWidth(), defaultPortraitDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas defaultPortraitCanvas = new Canvas(defaultPortraitBitmap);

            defaultPortraitDrawable.setBounds(0, 0, defaultPortraitCanvas.getWidth(), defaultPortraitCanvas.getHeight());
            defaultPortraitDrawable.draw(defaultPortraitCanvas);

            portrait = RoundedBitmapDrawableFactory.create(getResources(), defaultPortraitBitmap);
            portrait.setCircular(true);
        }

        this.portraitView = findViewById(R.id.addProfilePic);
        portraitView.setImageDrawable(portrait);

        if (!intentType.equals(VIEW_CHILD_INTENT)) {
            ImageView editIcon = findViewById(R.id.editIcon);
            editIcon.setVisibility(View.VISIBLE);

            portraitView.setAlpha(0.75f);
            portraitView.setClickable(true);
            portraitView.setOnClickListener(view -> selectImage());
        }
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

        EditText childName = findViewById(R.id.childName);
        saveButton.setOnClickListener(view -> {
            if (childName.getText().toString().equals("")) {
                updateToast(getString(R.string.toast_no_name));
            } else if (nameContainsOnlyLetters(childName.getText().toString())) {
                if (intentType.equals(EDIT_CHILD_INTENT)) {
                    children.setChildName(focusedChildUniqueID, childName.getText().toString());
                } else {
                    children.addChild(childName.getText().toString(), currentPhotoPath);
                }

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
                if (intentType.equals(EDIT_CHILD_INTENT)) {
                    changesAccepted = childName.getText().toString().equals(children.getChild(focusedChildUniqueID).getName());
                } else {
                    changesAccepted = childName.getText().toString().equals("");
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(new File(currentPhotoPath)));
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date().getTime());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // TODO: Delete photo from device if child deleted
        // TODO: Go back to default image if user deletes portrait photo for a given child via files app (i.e. if getPortrait returns null)
        // TODO: Consider cropping + saving portraits in separate location so things don't need to be recropped repeatedly and
        // TODO:    to make it more difficult for user to manually delete image

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int scaleFactor = Math.max(1, Math.min(bmOptions.outWidth / portraitView.getWidth(), bmOptions.outHeight / portraitView.getHeight()));

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        RoundedBitmapDrawable portraitDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        portraitDrawable.setCircular(true);
        portraitView.setImageDrawable(portraitDrawable);
    }

    private void selectImage() {
        final String[] dialogOptions = {getString(R.string.prompt_select_image_option1), getString(R.string.prompt_select_image_option2), getString(R.string.prompt_select_image_option3)};

        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.prompt_select_image_title)
                .setItems(dialogOptions, (dialog, item) -> {
                    switch (dialogOptions[item]) {
                        case "Take Photo":
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // TODO: Way for user to crop images?

                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException e) {
                                e.getStackTrace();
                            }

                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(this,
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startTakePhoto.launch(takePictureIntent);
                            }
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
