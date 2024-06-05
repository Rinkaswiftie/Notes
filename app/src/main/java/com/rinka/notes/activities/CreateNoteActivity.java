package com.rinka.notes.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.rinka.notes.NotesApp;
import com.rinka.notes.R;
import com.rinka.notes.database.NotesDatabase;
import com.rinka.notes.entities.Note;
import com.rinka.notes.tasks.TaskRunner;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;

public class CreateNoteActivity extends AppCompatActivity {

    ImageView goBack;
    ImageView saveNote;
    TextView noteTitle;
    ImageView imageForNote;
    TextView noteText;
    TextView createdAt;
    Executor executor;
    View noteLayout;
    String selectedColor;
    String selectedImagePath;
    Note selectedNote;

    public static final int REQUEST_CODE_FOR_STORAGE_PERMISSION = 1;
    public static final int REQUEST_CODE_FOR_SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //base setup
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_note);
        executor = NotesApp.getExecutorService();

        //get required views
        goBack = findViewById(R.id.goBack);
        saveNote = findViewById(R.id.save_note);
        noteTitle = findViewById(R.id.note_title);
        noteText = findViewById(R.id.note_content);
        createdAt = findViewById(R.id.created_at);
        noteLayout = findViewById(R.id.single_note);
        imageForNote = findViewById(R.id.note_image);
        selectedColor = "#333333";
        selectedImagePath = "";

        //load existing data
        if(getIntent().getBooleanExtra("isViewOrUpdate", false)){
            selectedNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        //initialize operations on views
        goBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        saveNote.setOnClickListener(v -> saveNote());
        createdAt.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));
        initMiscDrawer();
    }

    private void setViewOrUpdateNote(){
        noteTitle.setText(selectedNote.getTitle());
        createdAt.setText(selectedNote.getCreatedAt());
        noteText.setText(selectedNote.getNote_text());

        selectedColor = selectedNote.getColor();

        setupNoteBackground();

        selectedImagePath = selectedNote.getImagePath();
        if(selectedImagePath != null){
            imageForNote.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        }
    }


    private void saveNote() {
        if (noteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (noteText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        //create a note
        Note newNote = new Note();
        newNote.setTitle(noteTitle.getText().toString().trim());
        newNote.setNote_text(noteText.getText().toString().trim());
        newNote.setCreatedAt(createdAt.getText().toString().trim());
        newNote.setColor(selectedColor);
        newNote.setImagePath(selectedImagePath);
        if(selectedNote != null) {
            newNote.setId(selectedNote.getId());
        }

        new TaskRunner(executor).executeAsync(() -> {
            try {
                NotesDatabase.getDatabase(getApplicationContext()).notesDAO().insertNote(newNote);
                return true;
            } catch (Exception e) {
                Log.e("CreateNoteActivity", "Error", e);
                return false;
            }
        }, result -> {
            setResult(RESULT_OK, new Intent());
            finish();
        });
    }

    private void initMiscDrawer() {
        final LinearLayout drawer = findViewById(R.id.miscellaneous_drawer);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(drawer);
        drawer.findViewById(R.id.misc_title).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        final ImageView noteColorOptionDefault = drawer.findViewById(R.id.view_color_default);
        final ImageView noteColorOption1 = drawer.findViewById(R.id.view_color_1);
        final ImageView noteColorOption2 = drawer.findViewById(R.id.view_color_2);
        final ImageView noteColorOption3 = drawer.findViewById(R.id.view_color_3);
        final ImageView noteColorOption4 = drawer.findViewById(R.id.view_color_4);
        final ImageView noteColorOption5 = drawer.findViewById(R.id.view_color_5);
        final ImageView noteColorOption6 = drawer.findViewById(R.id.view_color_6);
        ArrayList<ImageView> optionList = new ArrayList<ImageView>() {{
            add(noteColorOptionDefault);
            add(noteColorOption1);
            add(noteColorOption2);
            add(noteColorOption3);
            add(noteColorOption4);
            add(noteColorOption5);
            add(noteColorOption6);
        }};

        if(selectedColor != null) {
            switch (selectedColor.trim()){
                case "#ffe666":
                    noteColorOption1.setImageResource(R.drawable.check);
                    break;
                case "#f5c27d":
                    break;
                case "#d5a6bd":
                    noteColorOption3.setImageResource(R.drawable.check);
                    break;
                case "#b4a6d6":
                    noteColorOption4.setImageResource(R.drawable.check);
                    break;
                case "#a4c2f4":
                    noteColorOption5.setImageResource(R.drawable.check);
                    break;
                case "#afdb88":
                    noteColorOption6.setImageResource(R.drawable.check);
                    break;
                case "#333333":
                    noteColorOptionDefault.setImageResource(R.drawable.check);
                    break;
            }
        }

        for (ImageView option : optionList) {
            option.setOnClickListener(v -> {
                for (ImageView color : optionList) {
                    if (v.equals(color)) {
                        color.setImageResource(R.drawable.check);
                    } else {
                        color.setImageResource(0);
                    }
                }
                int viewId = v.getId();
                if (viewId == R.id.view_color_1) {
                    selectedColor = "#ffe666";
                } else if (viewId == R.id.view_color_2) {
                    selectedColor = "#f5c27d";
                } else if (viewId == R.id.view_color_3) {
                    selectedColor = "#d5a6bd";
                } else if (viewId == R.id.view_color_4) {
                    selectedColor = "#b4a6d6";
                } else if (viewId == R.id.view_color_5) {
                    selectedColor = "#a4c2f4";
                } else if (viewId == R.id.view_color_6) {
                    selectedColor = "#afdb88";
                } else if (viewId == R.id.view_color_default) {
                    selectedColor = "#333333";
                }
                setupNoteBackground();
            });
        }

        LinearLayout addImage = findViewById(R.id.add_note_image);
        addImage.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_FOR_STORAGE_PERMISSION);
            } else {
                selectImage();
            }
        });
    }

    private void setupNoteBackground() {
        noteLayout.setBackgroundColor(Color.parseColor(selectedColor));
        if (Objects.equals(selectedColor, "#333333")) {
            noteTitle.setTextColor(Color.parseColor("#B5B5B5"));
            noteTitle.setHintTextColor(Color.parseColor("#B5B5B5"));
            noteText.setTextColor(Color.parseColor("#B5B5B5"));
            noteText.setHintTextColor(Color.parseColor("#B5B5B5"));
            createdAt.setTextColor(Color.parseColor("#B5B5B5"));
            createdAt.setHintTextColor(Color.parseColor("#B5B5B5"));
            goBack.setColorFilter(Color.parseColor("#B5B5B5"));
            saveNote.setColorFilter(Color.parseColor("#B5B5B5"));
        } else {
            noteTitle.setTextColor(Color.parseColor("#212121"));
            noteTitle.setHintTextColor(Color.parseColor("#212121"));
            noteText.setTextColor(Color.parseColor("#212121"));
            noteText.setHintTextColor(Color.parseColor("#212121"));
            createdAt.setTextColor(Color.parseColor("#212121"));
            createdAt.setHintTextColor(Color.parseColor("#212121"));
            goBack.setColorFilter(Color.parseColor("#212121"));
            saveNote.setColorFilter(Color.parseColor("#212121"));
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_FOR_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_FOR_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FOR_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageForNote.setImageBitmap(bitmap);
                        imageForNote.setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    }catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentURI){
        String filePath;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if(cursor == null){
            filePath = contentURI.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
}

