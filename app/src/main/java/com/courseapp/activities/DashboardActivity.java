package com.courseapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.courseapp.R;
import com.courseapp.activities.admin.coursemanagement.CourseManagement;
import com.courseapp.activities.admin.instructormanagement.InstructorManagement;
import com.courseapp.activities.admin.studentmanagement.StudentManagementActivity;
import com.courseapp.activities.authentication.LoginActivity;
import com.courseapp.activities.instructor.CourseList;
import com.courseapp.activities.student.ViewStudentCourses;
import com.courseapp.activities.student.ViewStudentEnrolledCourses;
import com.courseapp.models.Role;

import static com.courseapp.AppCredentials.APP_DB;
import static com.courseapp.AppCredentials.AUTHENTICATED;
import static com.courseapp.AppCredentials.ROLE;
import static com.courseapp.AppCredentials.USERNAME;

public class DashboardActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        preferences = getSharedPreferences(APP_DB, MODE_PRIVATE);
        getUserDetails();
    }

    private void getUserDetails(){
        String username = preferences.getString(USERNAME, "Username");
        String userRole = preferences.getString(ROLE, "STUDENT");

        ((TextView)findViewById(R.id.dashboard_username)).setText(username);
        ((TextView)findViewById(R.id.dashboard_role)).setText(userRole);

        switch (Role.valueOf(userRole)){
            case ADMIN:
                ((GridLayout)findViewById(R.id.admin_dashboard)).setVisibility(View.VISIBLE);
                ((GridLayout)findViewById(R.id.student_dashboard)).setVisibility(View.GONE);
                ((GridLayout)findViewById(R.id.instructor_dashboard)).setVisibility(View.GONE);
                break;

            case STUDENT:
                ((GridLayout)findViewById(R.id.student_dashboard)).setVisibility(View.VISIBLE);
                ((GridLayout)findViewById(R.id.admin_dashboard)).setVisibility(View.GONE);
                ((GridLayout)findViewById(R.id.instructor_dashboard)).setVisibility(View.GONE);
                break;

            case INSTRUCTOR:
                ((GridLayout)findViewById(R.id.instructor_dashboard)).setVisibility(View.VISIBLE);
                ((GridLayout)findViewById(R.id.student_dashboard)).setVisibility(View.GONE);
                ((GridLayout)findViewById(R.id.admin_dashboard)).setVisibility(View.GONE);
                break;
        }
    }

    public void viewMyCourses(View view){
        startActivity(new Intent(DashboardActivity.this, ViewStudentEnrolledCourses.class));
    }

    public void InstructorManagement(View view){
        startActivity(new Intent(DashboardActivity.this, InstructorManagement.class));
    }

    public void StudentManagement(View view){
        startActivity(new Intent(DashboardActivity.this, StudentManagementActivity.class));
    }

    public void courseManager(View view){
        startActivity(new Intent(DashboardActivity.this, CourseManagement.class));
    }

    public void InstructorCourseManager(View view){
        startActivity(new Intent(DashboardActivity.this, CourseList.class));
    }

    public void StudentCourseManager(View view){
        startActivity(new Intent(DashboardActivity.this, ViewStudentCourses.class));
    }

    public void logOut(View view){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AUTHENTICATED, false);
        editor.apply();
        editor.commit();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        finish();
    }
}