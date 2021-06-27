package com.courseapp.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.courseapp.R;
import com.courseapp.activities.admin.coursemanagement.EditCourse;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.courseapp.AppCredentials.COURSE_CODE;

public class CourseViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    private TextView courseName;
    private TextView courseCode;
    private ImageButton editButton;
    private ImageButton deleteButton;

    public CourseViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        courseCode = mView.findViewById(R.id.course_code);
        courseName = mView.findViewById(R.id.course_name);
        editButton = mView.findViewById(R.id.edit_button);
        deleteButton = mView.findViewById(R.id.delete_button);
    }

    public void setLayout(String name, String code){
        courseName.setText(name);
        courseCode.setText(code);
    }

    public void setOnclickListner(Context context, String courseId){

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCourse.class);
            intent.putExtra(COURSE_CODE, courseId);
            context.startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("courses").child(courseId).setValue(null));
    }
}
