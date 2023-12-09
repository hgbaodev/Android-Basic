package com.programiner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.programiner.pdfreaderbts.FileUtils;
import com.programiner.pdfreaderbts.MainActivity;
import com.programiner.pdfreaderbts.R;
import com.programiner.pdfreaderbts.Viewer;

import java.io.File;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    MainActivity activity;
    List<File> list;

    public Adapter(MainActivity activity, List<File> list) {
        this.activity = activity;
        this.list = list;
    }
    public void filterlist(List<File> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        File file = list.get(position);
        holder.name.setText(file.getName());
//        holder.path.setText(file.getPath());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, Viewer.class);
                intent.putExtra("name", file.getName());
                intent.putExtra("path", file.getPath());
                activity.startActivity(intent);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ShareCompat.IntentBuilder.from(activity).setType("application/pdf")
                        .setStream(Uri.parse(file.getAbsolutePath())).
                        setChooserTitle("Choose app")
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Bạn có chắc chắn?")
                        .setContentText("Xoá file: " + file.getName())
                        .setConfirmText("Vâng, Tôi chắc chắn");

                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (position != RecyclerView.NO_POSITION) {
                            FileUtils.deletePdfFile(activity, file.getPath());
                            list.remove(position);
                            notifyDataSetChanged();
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    }
                });

                sweetAlertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,path;
        ImageView share, delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.file_name);
//            path = itemView.findViewById(R.id.file_path);
            share = itemView.findViewById(R.id.share);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
