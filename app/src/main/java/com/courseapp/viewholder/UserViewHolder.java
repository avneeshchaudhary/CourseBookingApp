package com.courseapp.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.courseapp.R;
import com.google.firebase.database.DatabaseReference;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private ImageButton deleteButton;
    private TextView studentName;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        deleteButton = (ImageButton) itemView.findViewById(R.id.student_delete_button);
        studentName = (TextView) itemView.findViewById(R.id.student_name);
    }

    public void setView(String name, DatabaseReference ref){
        studentName.setText(name);
        deleteButton.setOnClickListener(v -> ref.removeValue());
    }
}
