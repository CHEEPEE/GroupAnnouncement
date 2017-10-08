package com.announcement.schol.schoolannouncementgroupchatwithfilesharing;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupChatActivity extends AppCompatActivity {
    private DatabaseReference mDatabase,mChatDatabase;
    private static final int READ_REQUEST_CODE = 42;
    private StorageReference mStorageRef;



    private FirebaseAuth mAuth;
    private String firstLoad = "";
    long datSap = 0;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private messageListAdapter messageListAdapter;
    private ArrayList<messageChatModel> messageChatModels = new ArrayList<>();
    private ArrayList<String> messeges = new ArrayList<>();
    @BindView(R.id.cv_upload_progress)
    CardView uploadProgress;
    @BindView(R.id.rel_send)
    RelativeLayout send_button;
    @BindView(R.id.msg_field)
    EditText msgField;
    @BindView(R.id.lv_message_items)
    ListView lvMessages;
    @BindView(R.id.choose_file)
    ImageView fileChooser;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Announcement");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Announcements");
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mAuth.getCurrentUser().getDisplayName();
                String userId = mAuth.getCurrentUser().getUid();
                String uEmail = mAuth.getCurrentUser().getEmail();
                String msg = msgField.getText().toString();
                String timeStamp = Utils.formatTheDate(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                writeNewMsg(uEmail,name,msg,timeStamp,null,"msg");
                messageListAdapter.notifyDataSetChanged();


            }
        });

        mChatDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                msgField.setText("");
                MsgText msgText = dataSnapshot.getValue(MsgText.class);

                messageChatModel msgModel = new messageChatModel();
                msgModel.setUsername(msgText.username);
                msgModel.setMessage(msgText.message);
                msgModel.setTimestamp(msgText.timeStamp);
                msgModel.setDownloadURL(msgText.downloadURL);
                msgModel.setType(msgText.type);
                messageChatModels.add(msgModel);
                System.out.println(msgText.message +" "+msgText.username+" "+msgText.timeStamp+" "+msgText.downloadURL);
                messageListAdapter.notifyDataSetChanged();



            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        messageListAdapter = new messageListAdapter(GroupChatActivity.this,messageChatModels);
        lvMessages.setAdapter(messageListAdapter);

        lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(messageChatModels.get(position).getDownloadURL());
               // Uri download = messageChatModels.get(position).getDownloadURL();
                if (messageChatModels.get(position).getDownloadURL()!=null){
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(messageChatModels.get(position).getDownloadURL()));
                    startActivity(i);
                }
            }
        });

        fileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

          performFileSearch();




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








    private void writeNewMsg(String useremail, String username, String msg, String timestamp,String downloadURL,String type) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mChatDatabase.push().getKey();
        MsgText msgText = new MsgText(username,useremail,msg,timestamp,downloadURL,type);
        Map<String, Object> postValues = msgText.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);
       // childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mChatDatabase.updateChildren(childUpdates);
    }
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        //intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");


        startActivityForResult(Intent.createChooser(intent,"Choose File"), READ_REQUEST_CODE);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("TAG", "Uri: " + uri.getLastPathSegment());
                uploadFileFromInputStream(uri);
                uploadProgress.setVisibility(View.VISIBLE);

            }

        }

    }



public void uploadFileFromInputStream(final Uri uri){
    InputStream file = null;
    try {
        file = getContentResolver().openInputStream(uri);
    }catch (FileNotFoundException e){
        e.printStackTrace();
    }

    System.out.println(getFileName(uri));
    final String type;
    if (getFileName(uri).substring(getFileName(uri).length()-3,getFileName(uri).length()).toLowerCase().equals("jpg") || getFileName(uri).substring(getFileName(uri).length()-3,getFileName(uri).length()).toLowerCase().equals("png")){
        type = "image";
    }else {
        type="file";
    }

    final String msgTitle;

    if (msgField.getText().toString().trim().equals("") || msgField.getText().toString().equals(null)){
        msgTitle = getFileName(uri);
    }else {
        msgTitle = msgField.getText().toString();

    }


    StorageReference riversRef = mStorageRef.child("files/"+File.separator+getFileName(uri)+file.toString()+File.separator+getFileName(uri));
    final String downloadURL = "files/"+File.separator+getFileName(uri)+file.toString()+File.separator+getFileName(uri);
    riversRef.putStream(file)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    @SuppressWarnings("VisibleForTests")
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    /*Uri downloadUrl = taskSnapshot.getDownloadUrl();*/

                    System.out.println("Upload Success");
                    System.out.println(downloadUri);
                    String name = mAuth.getCurrentUser().getDisplayName();
                    String userId = mAuth.getCurrentUser().getUid();
                    String uEmail = mAuth.getCurrentUser().getEmail();
                    String msg = msgField.getText().toString();
                    String timeStamp = Utils.formatTheDate(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                    writeNewMsg(uEmail,name,msgTitle.toString(),timeStamp,downloadUri,type);
                    messageListAdapter.notifyDataSetChanged();
                    uploadProgress.setVisibility(View.INVISIBLE);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    // ...
                    System.out.println("Upload Failed");
                }
            });
}

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }





}
