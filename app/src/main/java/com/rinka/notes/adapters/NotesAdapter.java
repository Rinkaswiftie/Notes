package com.rinka.notes.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rinka.notes.R;
import com.rinka.notes.entities.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    List<Note> notes;

    public NotesAdapter(List<Note> note) {
        this.notes = note;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        LinearLayout noteContainer;
        TextView title, time, content;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_preview_title);
            time = itemView.findViewById(R.id.note_preview_time);
            content = itemView.findViewById(R.id.note_preview_content);
            noteContainer = itemView.findViewById(R.id.note_item);
        }

        @SuppressLint("SetTextI18n")
        void setNote(@NonNull Note note) {
            GradientDrawable gradientDrawable = (GradientDrawable) noteContainer.getBackground();
            gradientDrawable.setColor(Color.parseColor(note.getColor()));

            title.setText(note.getTitle());
            title.setTextColor(Color.parseColor(note.getTextColor()));
            time.setText(note.getCreatedAt());
            time.setTextColor(Color.parseColor(note.getTextColor()));
            String noteText = note.getNote_text();
            int noteLength = noteText.length();
            content.setText(noteText.substring(0, Math.min(noteLength, 100)) + (noteLength > 100 ? "..." : ""));
            content.setTextColor(Color.parseColor(note.getTextColor()));
        }
    }
}
