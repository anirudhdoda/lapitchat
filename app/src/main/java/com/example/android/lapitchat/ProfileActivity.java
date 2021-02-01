package com.example.android.lapitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView mDisplayName;
    private TextView mDisplayStatus;
    private ImageView mDisplayImage;
    private TextView mProfileFriendsCount;
    private Button mProfileSendRequestButton,mDeclineButton;
    private DatabaseReference mUserDataBase;
    private ProgressDialog mProgressBar;
    private String mCurrent_state;
    private DatabaseReference mFriendRequestDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id=getIntent().getStringExtra("user_id");
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendsDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        mDisplayName=(TextView)findViewById(R.id.profile_display_name);
        mDisplayStatus=(TextView)findViewById(R.id.profile_display_status);
        mDisplayImage= (ImageView)findViewById(R.id.profile_display_image);
        mProfileSendRequestButton=(Button) findViewById(R.id.profile_request_button);
        mDeclineButton=(Button)findViewById(R.id.delete_request_button);

        mCurrent_state="not_friends";



        mProgressBar=new ProgressDialog(this);
        mProgressBar.setTitle("Loading User Data");
        mProgressBar.setMessage("Please wait until we load user's data");
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.show();




        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=snapshot.child("name").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                String image=snapshot.child("image").getValue().toString();
                mDisplayName.setText(name);
                mDisplayStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.icon_avatar_default).into(mDisplayImage);

                //----friends list/Request Feature-----
                mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(user_id)) {
                            String request_type = snapshot.child(user_id).child("request_type").getValue().toString();
                            if (request_type.equals("received")) {
                                mProfileSendRequestButton.setEnabled(true);
                                mCurrent_state = "request_received";
                                mProfileSendRequestButton.setText("Accept Friend Request");
                                mDeclineButton.setEnabled(true);
                            } else if (request_type.equals("sent")) {
                                mProfileSendRequestButton.setEnabled(true);
                                mCurrent_state = "request_sent";
                                mProfileSendRequestButton.setText("Cancel Friend Request");
                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);
                            }
                        }
                        else{
                            mFriendsDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(user_id))
                                    {
                                        mProfileSendRequestButton.setEnabled(true);
                                        mCurrent_state = "friends";
                                        mProfileSendRequestButton.setText("Unfriend this person");
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        if(mCurrent_state=="not_friends")
                        {
                            mDeclineButton.setVisibility(View.INVISIBLE);
                            mDeclineButton.setEnabled(false);
                        }

                        mProgressBar.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mProfileSendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileSendRequestButton.setEnabled(false);

                //----Not Friend State----
                if (mCurrent_state.equals("not_friends"))
                {

                    if (!mCurrentUser.getUid().equals(user_id)) {
                        Map requestmap=new HashMap();
                        requestmap.put("Friend_req/"+mCurrentUser.getUid()+"/"+user_id+"/request_type","sent");
                        requestmap.put("Friend_req/"+user_id+"/"+mCurrentUser.getUid()+"/request_type","received");
                        mRootRef.updateChildren(requestmap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        mProfileSendRequestButton.setEnabled(true);
                                        mCurrent_state="request_sent";
                                        mProfileSendRequestButton.setText("Cancel Friend Request");
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);
                                        Toast.makeText(ProfileActivity.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();


                                    }
                                });

                    }
                    else{
                        Toast.makeText(ProfileActivity.this, "You cannot send request to your own account", Toast.LENGTH_SHORT).show();
                    }
                }

                //------cancel request state  -------
                if(mCurrent_state.equals("request_sent"))
                {
                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProfileSendRequestButton.setEnabled(true);
                                            mCurrent_state="not_friends";
                                            mProfileSendRequestButton.setText("Send Friend Request");
                                            mDeclineButton.setVisibility(View.INVISIBLE);
                                            mDeclineButton.setEnabled(false);
                                            Toast.makeText(ProfileActivity.this, "Friend Request Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                }
                //-----request received state------
                if(mCurrent_state.equals("request_received"))
                {
                    final String currentDate= DateFormat.getDateInstance().format(new Date());
                    Map friendsMap=new HashMap();
                    friendsMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id+"/date",currentDate);
                    friendsMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid()+"/date",currentDate);
                    friendsMap.put("Friend_req/"+mCurrentUser.getUid()+"/"+user_id,null);
                    friendsMap.put("Friend_req/"+user_id+"/"+mCurrentUser.getUid(),null);
                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            mProfileSendRequestButton.setEnabled(true);
                            mCurrent_state="friends";
                            mProfileSendRequestButton.setText("Unfriend this person");
                            mDeclineButton.setVisibility(View.INVISIBLE);
                            mDeclineButton.setEnabled(false);
                        }
                    });
                }
                if (mCurrent_state.equals("friends"))
                {
                    Map unfriendMap=new HashMap();
                    unfriendMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id,null);
                    unfriendMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid(),null);
                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            mProfileSendRequestButton.setEnabled(true);
                            mCurrent_state="not_friends";
                            mProfileSendRequestButton.setText("Send Friend Request");
                        }
                    });

                }

            }
        });

    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//                mRootRef.child("Users").child(mCurrentUser.getUid()).child("online").setValue(true);
//
//    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(mCurrentUser!=null)
//            mRootRef.child("Users").child(mCurrentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
//    }



}