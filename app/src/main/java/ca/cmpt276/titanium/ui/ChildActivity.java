package ca.cmpt276.titanium.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.BuildConfig;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.ChildManager;

/**
 * Allows a user to view, add, and edit a Child object's data.
 *
 * @author Titanium
 */
public class ChildActivity extends AppCompatActivity {
  private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0;
  private static final float PORTRAIT_ALPHA_WHILE_EDITED = 0.75f;
  private static final String INTENT_TYPE_KEY = "intentType";
  private static final String CHILD_UNIQUE_ID_KEY = "childUniqueID";
  private static final String ADD_CHILD_INTENT = "Add Child";
  private static final String EDIT_CHILD_INTENT = "Edit Child";
  private static final String VIEW_CHILD_INTENT = "Manage Child";

  private String intentType;
  private ChildManager childManager;
  private Toast toast; // prevents toast stacking
  private ImageView portraitView;
  private EditText childNameInput;

  private UUID focusedChildUniqueID;
  private Child focusedChild;
  private boolean changesAccepted = true;
  private ActivityResultLauncher<Intent> takePhoto;
  private ActivityResultLauncher<Intent> selectFromGallery;
  private String portraitPath;

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

    this.childManager = ChildManager.getInstance(this);
    this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    this.portraitView = findViewById(R.id.ImageView_child_portrait);
    this.childNameInput = findViewById(R.id.EditText_child_name);

    if (intentType.equals(EDIT_CHILD_INTENT) || intentType.equals(VIEW_CHILD_INTENT)) {
      this.focusedChildUniqueID = (UUID) getIntent().getSerializableExtra(CHILD_UNIQUE_ID_KEY);
      this.focusedChild = childManager.getChild(focusedChildUniqueID);
    }

    setupActivityResults();
    setupGUI();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (intentType.equals(VIEW_CHILD_INTENT)) {
      getMenuInflater().inflate(R.menu.menu_child, menu);
    }

    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (intentType.equals(VIEW_CHILD_INTENT)) {
      this.focusedChild = childManager.getChild(focusedChildUniqueID);
      portraitView.setImageDrawable(focusedChild.getPortrait(getResources()));
      childNameInput.setText(focusedChild.getName());
    }
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      launchPrompt(getString(R.string.prompt_title_child_discard_changes),
          getString(R.string.prompt_message_child_discard_changes),
          getString(R.string.toast_child_changes_discarded),
          false);
      return true;
    } else if (item.getItemId() == R.id.menu_item_child_edit) {
      startActivity(ChildActivity.makeIntent(
          this, getString(R.string.title_child_edit), focusedChildUniqueID));
      return true;
    } else if (item.getItemId() == R.id.menu_item_child_delete) {
      this.focusedChild = childManager.getChild(focusedChildUniqueID);
      launchPrompt(getString(R.string.prompt_title_child_delete, focusedChild.getName()),
          getString(R.string.prompt_message_child_delete),
          getString(R.string.toast_child_deleted),
          true);
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onBackPressed() {
    launchPrompt(getString(R.string.prompt_title_child_discard_changes),
        getString(R.string.prompt_message_child_discard_changes),
        getString(R.string.toast_child_changes_discarded),
        false);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        selectFromGallery.launch(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
      } else {
        updateToast(getString(R.string.toast_child_permission_denied));
      }
    }
  }

  private void setupActionBar(String intentType) {
    setSupportActionBar(findViewById(R.id.ToolBar_child));
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    setTitle(intentType);
  }

  private void setupActivityResults() {
    this.takePhoto = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
          if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
            portraitView.setImageDrawable(Child.getThisPortrait(getResources(), portraitPath));

            if (intentType.equals(EDIT_CHILD_INTENT)) {
              childManager.setPortraitPath(focusedChildUniqueID, portraitPath);
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
              this.portraitPath = cursor.getString(columnIndex);
            }

            cursor.close();
            portraitView.setImageDrawable(Child.getThisPortrait(getResources(), portraitPath));

            if (intentType.equals(EDIT_CHILD_INTENT)) {
              childManager.setPortraitPath(focusedChildUniqueID, portraitPath);
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

    Button saveButton = findViewById(R.id.Button_child_save);
    saveButton.setVisibility(View.VISIBLE);

    saveButton.setOnClickListener(view -> {
      String childName = childNameInput.getText().toString();
      if (childName.equals("")) {
        updateToast(getString(R.string.toast_child_name_field_empty));
      } else {
        if (intentType.equals(ADD_CHILD_INTENT)) {
          childManager.addChild(childName, portraitPath);
        } else if (intentType.equals(EDIT_CHILD_INTENT)) {
          childManager.setName(focusedChildUniqueID, childName);
        }

        updateToast(getString(R.string.toast_child_saved));
        finish();
      }
    });
  }

  private void setupPortrait() {
    RoundedBitmapDrawable portrait;

    if (intentType.equals(ADD_CHILD_INTENT)) {
      portrait = Child.getThisPortrait(getResources(), null);
    } else {
      portrait = childManager.getChild(focusedChildUniqueID).getPortrait(getResources());
      childNameInput.setText(childManager.getChild(focusedChildUniqueID).getName());
    }

    portraitView.setImageDrawable(portrait);
    portraitView.setAlpha(PORTRAIT_ALPHA_WHILE_EDITED);
    portraitView.setClickable(true);
    portraitView.setOnClickListener(view -> selectImage());

    ImageView editIcon = findViewById(R.id.ImageView_child_portrait_edit_icon);
    editIcon.setVisibility(View.VISIBLE);
  }

  private void setupChildName() {
    childNameInput.setEnabled(true);
    childNameInput.setCursorVisible(true);

    childNameInput.setOnKeyListener((view, keyCode, keyEvent) -> {
      if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
        InputMethodManager inputMethodManager =
            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(childNameInput.getWindowToken(), 0);

        childNameInput.clearFocus();
        childNameInput.setText(childNameInput.getText());
      }

      return false;
    });

    childNameInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String childName = childNameInput.getText().toString();

        if (intentType.equals(ADD_CHILD_INTENT)) {
          changesAccepted = childName.equals("");
        } else if (intentType.equals(EDIT_CHILD_INTENT)) {
          changesAccepted = childName.equals(childManager.getChild(focusedChildUniqueID).getName());
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

  private void selectImage() {
    final String[] dialogOptions = {
        getString(R.string.prompt_option1_child_select_image),
        getString(R.string.prompt_option2_child_select_image),
        getString(R.string.prompt_option3_child_select_image)
    };

    new android.app.AlertDialog.Builder(this)
        .setTitle(R.string.prompt_title_child_select_image)
        .setItems(dialogOptions, (dialog, item) -> {
          switch (dialogOptions[item]) {
            case "Take Photo":
              File portraitFile = null;

              try {
                portraitFile = File.createTempFile(
                    "portrait_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                this.portraitPath = portraitFile.getAbsolutePath();
              } catch (IOException e) {
                e.getStackTrace();
              }

              if (portraitFile != null) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    portraitFile));
                takePhoto.launch(photoIntent);
              }
              break;
            case "Select from Gallery":
              if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                  != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
              } else {
                selectFromGallery.launch(
                    new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
              }
              break;
            case "Cancel":
              dialog.dismiss();
              break;
          }
        }).show();
  }

  private void launchPrompt(
      String title, String message, String positiveToast, boolean isDeletePrompt) {
    if (!changesAccepted || isDeletePrompt) {
      new AlertDialog.Builder(this)
          .setIcon(R.drawable.ic_baseline_delete_black_24)
          .setTitle(title)
          .setMessage(message)
          .setPositiveButton(R.string.prompt_positive_child, (dialog, which) -> {
            if (isDeletePrompt) {
              childManager.removeChild(focusedChildUniqueID);
            }

            updateToast(positiveToast);
            finish();
          })
          .setNegativeButton(R.string.prompt_negative_child, null)
          .show();
    } else {
      finish();
    }
  }
}
