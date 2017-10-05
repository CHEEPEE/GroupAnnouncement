package com.announcement.schol.schoolannouncementgroupchatwithfilesharing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupChatActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @BindView(R.id.rel_send)
    RelativeLayout send_button;
    @BindView(R.id.msg_field)
    EditText msgField;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Announcement");
        mAuth = FirebaseAuth.getInstance();
        String name = mAuth.getCurrentUser().getEmail();
        System.out.println(mAuth.getCurrentUser().getDisplayName());
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mssg = msgField.getText().toString();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                String key = mDatabase.child("Announcement").push().getKey();
                mDatabase.child("Announcements").child(key).child("user").setValue(mAuth.getCurrentUser().getDisplayName());
                mDatabase.child("Announcements").child(key).child("message").setValue(mssg);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.announcement_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.signout:
             mAuth.signOut();
                Intent i = new Intent(GroupChatActivity.this,LogInActivty.class);
                startActivity(i);
            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
