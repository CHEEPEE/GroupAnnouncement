package com.announcement.schol.schoolannouncementgroupchatwithfilesharing;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @BindView(R.id.input_email)
    EditText emailfield;
    @BindView(R.id.input_password)
    EditText passwordField;
    @BindView(R.id.input_fname)
    EditText fnameField;
    @BindView(R.id.input_lname)
    EditText lnameField;
    @BindView(R.id.signup)
    Button signup;
    @BindView(R.id.signupprogress)
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_uo);
        getSupportActionBar().setTitle("Sign Up");
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(emailfield.getText().toString(),passwordField.getText().toString(),fnameField.getText().toString()+" "+lnameField.getText().toString());
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void createAccount(String email, String password, final String displayName) {
        // [START create_user_with_email]
        if (validateForm()){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName)
                                        .setPhotoUri(Uri.EMPTY)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("User Profile Update", "User profile updated.");
                                                    writeNewUser(mAuth.getCurrentUser().getUid().toString(),"test",mAuth.getCurrentUser().getEmail().toString());


                                                }
                                            }
                                        });
                                mAuth.signOut();
                                Intent i = new Intent(SignUpActivity.this,LogInActivty.class);
                                startActivity(i);


                            } else {
                                Log.d("Authentication Status","Aunthentication failed");
                                Toast.makeText(SignUpActivity.this,"Account Creation Failed Please check your Internet",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }
    @IgnoreExtraProperties
    public class User {

        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).child("name").setValue(name);
        mDatabase.child("users").child(userId).child("email").setValue(email);
        mDatabase.child("users").child(userId).child("userid").setValue(userId);

    }

    private boolean validateForm() {
        boolean valid = true;

        String emailString = emailfield.getText().toString();
        if (TextUtils.isEmpty(emailString)) {
            emailfield.setError("Required.");
            valid = false;
        } else {
            emailfield.setError(null);
        }

        String passwordString = emailfield.getText().toString();
        if (TextUtils.isEmpty(passwordString)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        String fname = fnameField.getText().toString();
        if (TextUtils.isEmpty(fname)) {
            fnameField.setError("Required.");
            valid = false;
        } else {
            fnameField.setError(null);
        }
        String lname = lnameField.getText().toString();
        if (TextUtils.isEmpty(lname)) {
            lnameField.setError("Required.");
            valid = false;
        } else {
            lnameField.setError(null);
        }

        return valid;
    }

}
