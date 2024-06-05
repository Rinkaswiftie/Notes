package com.rinka.notes.adapters;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.rinka.notes.R;
import com.rinka.notes.entities.Note;
import com.rinka.notes.listeners.NotesListener;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    List<Note> notes;
    NotesListener notesListener;

    public NotesAdapter(List<Note> note,  NotesListener notesListener) {
        this.notes = note;
        this.notesListener = notesListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.noteContainer.setOnClickListener(v -> notesListener.onNoteClicked(notes.get(position), position));
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
        private RoundedImageView previewImage;
        TextView title, time, content;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_preview_title);
            time = itemView.findViewById(R.id.note_preview_time);
            content = itemView.findViewById(R.id.note_preview_content);
            noteContainer = itemView.findViewById(R.id.note_item);
            previewImage = itemView.findViewById(R.id.preview_image);
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
            if (note.getImagePath() != null) {
                previewImage.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                previewImage.setVisibility(View.VISIBLE);
            } else {
                previewImage.setVisibility(View.GONE);
            }
        }
    }
}
