package com.rinka.notes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.rinka.notes.NotesApp;
import com.rinka.notes.R;
import com.rinka.notes.adapters.NotesAdapter;
import com.rinka.notes.database.NotesDatabase;
import com.rinka.notes.entities.Note;
import com.rinka.notes.tasks.TaskRunner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView createNoteButton;
    private RecyclerView recyclerView;
    private List<Note> notes;
    private NotesAdapter notesAdapter;

    public static final int REQUEST_CODE_ADD_NOTE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //base setup
        setContentView(R.layout.activity_main);

        //get required views
        createNoteButton = findViewById(R.id.create_note);
        recyclerView = findViewById(R.id.note_list);

        //initialize operations on views
        createNoteButton.setOnClickListener(v -> startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE ));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notes = new ArrayList<Note>();
        notesAdapter = new NotesAdapter(notes);
        recyclerView.setAdapter(notesAdapter);

        //call methods
        getNotes();
    }

    public void getNotes() {
        new TaskRunner(NotesApp.getExecutorService()).executeAsync(() -> NotesDatabase.getDatabase(getApplicationContext()).notesDAO().getAllNotes(), new TaskRunner.Callback<List<Note>>() {
            @Override
            public void onComplete(List<Note> result) {
                if(notes.isEmpty()){
                    notes.addAll(result);
                    notesAdapter.notifyDataSetChanged();
                } else {
                   notes.add(0, result.get(0));
                   notesAdapter.notifyItemInserted(0);
                   recyclerView.scrollToPosition(0);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes();
        }
    }
}