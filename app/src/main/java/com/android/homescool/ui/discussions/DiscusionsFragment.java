package com.android.homescool.ui.discussions;

import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.homescool.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class DiscusionsFragment extends Fragment {

    private FloatingActionButton fab;
    private DatabaseReference mDatabase;

    private DiscussionsViewModel discussionsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        discussionsViewModel = ViewModelProviders.of(this).get(DiscussionsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_discussions, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                writeNewDiscussion(user.getUid(), "test", ts);
            }
        });
        discussionsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

            }

        });


        return root;






    }

    @IgnoreExtraProperties
    public class Discussion {
        public String from;
        public String text;
        public String timestamp;

        public Discussion() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Discussion(String from, String text, String timestamp) {
            this.from = from;
            this.text = text;
            this.timestamp = timestamp;
        }



    }

    private void writeNewDiscussion(String userId, String text, String timestamp) {
        Discussion discussion = new Discussion(userId, text, timestamp);
        Map<String, Object> post = new HashMap<>();
        post.put("from", userId);
        post.put("text", text);
        post.put("timestamp", timestamp);


        mDatabase.child("discussion").push().setValue(discussion);
    }



}