package com.courseapp.activities.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.courseapp.R;
import com.courseapp.activities.DashboardActivity;
import com.courseapp.models.Role;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.courseapp.AppCredentials.APP_DB;
import static com.courseapp.AppCredentials.AUTHENTICATED;
import static com.courseapp.AppCredentials.ROLE;
import static com.courseapp.AppCredentials.USERNAME;

public class LoginActivity extends AppCompatActivity {

    private EditText userName;
    private EditText userPassword;
    private int mode;
    private TextView studentOp;
    private TextView instructorOp;
    private TextView adminOp;
    private TextView[] Ops;
    private Role role;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = (EditText)findViewById(R.id.login_username);
        userPassword = (EditText)findViewById(R.id.login_password);
        mode = 0;
        studentOp = (TextView)findViewById(R.id.option_student);
        adminOp = (TextView)findViewById(R.id.option_admin);
        instructorOp = (TextView)findViewById(R.id.option_instructor);
        role = Role.STUDENT;
        Ops = new TextView[]{studentOp, instructorOp, adminOp};
        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setTitle("Signing in");
        mDialog.setMessage("Please wait");
        mDialog.setCanceledOnTouchOutside(false);
    }

    public void selectOption(View view){
        switch (view.getId()){
            case R.id.option_student:
                mode = 0;
                role = Role.STUDENT;
                break;

            case R.id.option_instructor:
                mode = 1;
                role = Role.INSTRUCTOR;
                break;

            case R.id.option_admin:
                mode = 2;
                role = Role.ADMIN;
                break;
        }

        TextView selected = Ops[mode];
        for (TextView textView : Ops){
            textView.setBackground(getResources().getDrawable(R.drawable.option_default));
            textView.setTextColor(getColor(R.color.colorAccent));
        }
        selected.setTextColor(getColor(android.R.color.black));
        selected.setBackground(getResources().getDrawable(R.drawable.option_selected));
    }

    public void goToSignUp(View view){
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }

    public void SignIn(View view){
        String username = userName.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if (username.length() < 4 || password.length() < 4){
            Toast.makeText(this, "Credentials should be atleast 4 characters long", Toast.LENGTH_SHORT).show();
        }
        else{
            mDialog.show();
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(role.name());
            mRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() == null){
                        mDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                    }
                    else if (!snapshot.child("password").getValue().toString().equals(password)){
                        mDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SharedPreferences preferences = getSharedPreferences(APP_DB, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(USERNAME, username);
                        editor.putString(ROLE, role.name());
                        editor.putBoolean(AUTHENTICATED, true);
                        editor.apply();
                        editor.commit();
                        mDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login failed! Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}