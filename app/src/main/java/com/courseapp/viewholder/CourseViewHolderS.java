package com.courseapp.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.courseapp.R;
import com.courseapp.activities.instructor.InstructorCourseEdit;
import com.courseapp.activities.student.ViewStudentCourseDetail;

import static com.courseapp.AppCredentials.COURSE_CODE;

public class CourseViewHolderS extends RecyclerView.ViewHolder {

    private View mView;
    private TextView courseName;
    private TextView courseCode;
    private ImageButton editButton;
    private ImageButton deleteButton;

    public CourseViewHolderS(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        courseCode = mView.findViewById(R.id.course_code);
        courseName = mView.findViewById(R.id.course_name);
        editButton = mView.findViewById(R.id.edit_button);
        deleteButton = mView.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);
    }

    public void setLayout(String name, String code){
        courseName.setText(name);
        courseCode.setText(code);
    }

    public void setOnclickListner(Context context, String code){
        editButton.setImageDrawable(context.getDrawable(android.R.drawable.ic_menu_view));
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewStudentCourseDetail.class);
            intent.putExtra(COURSE_CODE, code);
            context.startActivity(intent);
        });
    }
}
