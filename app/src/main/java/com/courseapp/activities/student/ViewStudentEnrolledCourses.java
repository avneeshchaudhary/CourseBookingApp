package com.courseapp.activities.student;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.courseapp.R;
import com.courseapp.models.Course;
import com.courseapp.viewholder.CourseViewHolderS;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.courseapp.AppCredentials.APP_DB;
import static com.courseapp.AppCredentials.USERNAME;

public class ViewStudentEnrolledCourses extends AppCompatActivity {

    private RecyclerView courseList;
    private DatabaseReference mRef;
    private ArrayList<String> courseIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_enrolled_courses);
        courseList = (RecyclerView)findViewById(R.id.courseList);
        SharedPreferences preferences = getSharedPreferences(APP_DB, MODE_PRIVATE);
        String username = preferences.getString(USERNAME, "admin");
        mRef = FirebaseDatabase.getInstance().getReference().child("student_enrollments").child(username);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(ViewStudentEnrolledCourses.this, RecyclerView.VERTICAL, false);
        courseList.setLayoutManager(manager);
        fetchCourses();
    }

    private void fetchCourses() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseIds = new ArrayList<>();
                if (snapshot.getValue() != null){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        courseIds.add(dataSnapshot.getKey());
                    }
                    RecyclerView.Adapter<CourseViewHolderS> adapter = new RecyclerView.Adapter<CourseViewHolderS>() {
                        @NonNull
                        @Override
                        public CourseViewHolderS onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return new CourseViewHolderS(getLayoutInflater().inflate(R.layout.course_card, parent, false));
                        }

                        @Override
                        public void onBindViewHolder(@NonNull CourseViewHolderS holder, int position) {
                            FirebaseDatabase.getInstance().getReference().child("courses").child(courseIds.get(position)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue()!= null){
                                        Course course = snapshot.getValue(Course.class);
                                        holder.setLayout(course.getName(), course.getCode());
                                        holder.setOnclickListner(ViewStudentEnrolledCourses.this, courseIds.get(position));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public int getItemCount() {
                            return courseIds.size();
                        }
                    };
                    courseList.setAdapter(adapter);
                }
                else {
                    Toast.makeText(ViewStudentEnrolledCourses.this, "You have not enrolled for any course yet!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}