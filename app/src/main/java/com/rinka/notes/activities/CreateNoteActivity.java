package com.rinka.notes.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.rinka.notes.NotesApp;
import com.rinka.notes.R;
import com.rinka.notes.database.NotesDatabase;
import com.rinka.notes.entities.Note;
import com.rinka.notes.tasks.TaskRunner;

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
    TextView noteText;
    TextView createdAt;
    Executor executor;
    View noteLayout;
    String selectedColor;

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

        //initialize operations on views
        goBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        saveNote.setOnClickListener(v -> saveNote());
        createdAt.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));
        initMiscDrawer();
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

        for (ImageView option : optionList) {
            option.setOnClickListener(v -> {
                for (ImageView color : optionList) {
                    if(v.equals(color)){
                        color.setImageResource(R.drawable.check);
                    }else{
                        color.setImageResource(0);
                    }
                }
                int viewId = v.getId();
                selectedColor = "#333333";
                if (viewId == R.id.view_color_1){
                    selectedColor = "#ffe666";
                } else if (viewId == R.id.view_color_2){
                    selectedColor = "#f5c27d";
                } else if (viewId == R.id.view_color_3){
                    selectedColor = "#d5a6bd";
                } else if (viewId == R.id.view_color_4){
                    selectedColor = "#b4a6d6";
                } else if (viewId == R.id.view_color_5){
                    selectedColor = "#a4c2f4";
                } else if (viewId == R.id.view_color_6){
                    selectedColor = "#afdb88";
                } else if (viewId == R.id.view_color_default){
                    selectedColor = "#333333";
                }
                setupNoteBackground();
            });
        }
    }

    private void setupNoteBackground() {
        noteLayout.setBackgroundColor(Color.parseColor(selectedColor));
        if (Objects.equals(selectedColor, "#333333")){
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

}

