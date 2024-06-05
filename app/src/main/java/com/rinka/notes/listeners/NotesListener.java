package com.rinka.notes.listeners;

import com.rinka.notes.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
