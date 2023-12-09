package com.example.noteapp.GUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.BUS.NoteBUS;
import com.example.noteapp.DTO.Note;
import com.example.noteapp.Provider.MyContentProvider;
import com.example.noteapp.R;
import com.example.noteapp.Util.ItemClickListener;

import java.util.List;
import java.util.Random;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> noteList;
    private Context context;
    public static String colorList[] = {"#DC3545", "#28A745", "#007BFF", "#17A2B8", "#FD7E14", "#6F42C1"};


    public NoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    public void setList(List<Note> list) {
        this.noteList = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.note_item,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = noteList.get(position);

        holder.titleText.setText(note.getTitle().equals("") ? "Không có tiêu đề" : note.getTitle());
        holder.contentText.setText(note.getContent());
        if(!note.getUpdatedAt().equalsIgnoreCase(""))
            holder.dateText.setText(note.getUpdatedAt());
        else
            holder.dateText.setText(note.getCreatedAt());

        if(!note.getReminderTime().equalsIgnoreCase("")) {
            holder.reminderText.setText(note.getReminderTime());
        } else {
            holder.reminderText.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.dateText.getLayoutParams();
            layoutParams.bottomMargin = 10;
            holder.dateText.setLayoutParams(layoutParams);
        }
        holder.color.setBackgroundColor(Color.parseColor(colorList[new Random().nextInt(colorList.length)]));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    processDeleteItem(position);
                } else {
                    processClickItem(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private void processClickItem(int position) {
        Note note = noteList.get(position);

        Intent intent = new Intent(context, NewNote.class);
        intent.putExtra("id", note.getId());
        intent.putExtra("action","Edit");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void processDeleteItem(int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Deleting Note");
        alert.setMessage("Do you want to delete this note?");

        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            int noteId = noteList.get(position).getId();
            public void onClick(DialogInterface dialog, int which) {
                boolean result = NoteBUS.delete(noteId);

                if (result) {
                    Toast.makeText(context, "Ghi chú đã được xóa", Toast.LENGTH_SHORT).show();
                    // Update UI
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, noteList.size());
                } else {
                    Toast.makeText(context, "Không thể xóa ghi chú", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView titleText, contentText, dateText, reminderText;
        private View color;
        private ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            contentText = itemView.findViewById(R.id.contentText);
            dateText = itemView.findViewById(R.id.dateText);
            color = itemView.findViewById(R.id.color);
            reminderText = itemView.findViewById(R.id.reminderText);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition(), true);
            return true;
        }

    }
}
