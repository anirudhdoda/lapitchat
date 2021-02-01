package com.example.android.lapitchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragments extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private  View mView;
    private RecyclerView mFriendlist;

    public FriendsFragments() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment friendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragments newInstance(String param1, String param2) {
        FriendsFragments fragment = new FriendsFragments();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth=FirebaseAuth.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();
        mView=inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendlist=(RecyclerView)mView.findViewById(R.id.friend_list);
        mFriendsDatabase= FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mFriendlist.setHasFixedSize(true);
        mFriendlist.setLayoutManager(new LinearLayoutManager(getContext()));
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Friends>options=new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mFriendsDatabase,Friends.class).build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Friends,FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int position, @NonNull Friends friends) {
                friendsViewHolder.setDate(friends.getDate());
                final String user_id=getRef(position).getKey();
                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String userName=snapshot.child("name").getValue().toString();
                        String image=snapshot.child("image").getValue().toString();
                        
                        friendsViewHolder.setImage(image);
                        friendsViewHolder.setName(userName);
                        if(snapshot.hasChild("online")) {
                            String online = snapshot.child("online").getValue().toString();
                            friendsViewHolder.setOnline(online);
                        }
                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence option[]=new CharSequence[]{"Open Profile","Send Message"};
                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options:");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which==0)
                                        {
                                            Intent profileIntent=new Intent(getActivity(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id",user_id);
                                            startActivity(profileIntent);
                                        }
                                        if(which==1)
                                        {
                                            Intent chatIntent=new Intent(getActivity(), ChatActivity.class);
                                            chatIntent.putExtra("user_id",user_id);
                                            chatIntent.putExtra("userName",userName);
                                            startActivity(chatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);

                return new FriendsFragments.FriendsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        mFriendlist.setAdapter(firebaseRecyclerAdapter);

    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setDate(String date)
        {
            TextView friendDate=(TextView)mView.findViewById(R.id.user_single_status);
            friendDate.setText(date);
        }
        public void setName(String name)
        {
            TextView friendName=(TextView)mView.findViewById(R.id.user_single_name);
            friendName.setText(name);
        }
        public  void setImage(String image)
        {
            CircleImageView frinedImage=(CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.get().load(image).placeholder(R.drawable.icon_avatar_default).into(frinedImage);
        }
        public void setOnline(String online)
        {
            ImageView onlineImage=(ImageView)mView.findViewById(R.id.user_singlr_online_icon);
            if(online.equals("true"))
                onlineImage.setVisibility(View.VISIBLE);
            else
                onlineImage.setVisibility(View.INVISIBLE);
        }
    }
}