package com.android.homescool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ViewDiscussion extends AppCompatActivity {

    ImageView imageDiscussion, upvote, downvote;
    TextView tagsDiscussion, subjectDiscussion, titleDiscussion, bodyDiscussion, upvoteNumberView, downvoteNumberView;
    boolean hasImage;
    boolean hasUpvoted = false;
    boolean hasDownvoted = false;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref = mDatabase.child("discussion");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_discussion);





        Intent intent = getIntent();
        final String imageBitmap = intent.getStringExtra("imageBitmap");
        final String id = intent.getStringExtra("id");
        String tags = intent.getStringExtra("tags");
        String subject = intent.getStringExtra("subject");
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        final String upvoteNumber = intent.getStringExtra("upvoteNumber");
        final String downVoteNumber = intent.getStringExtra("downvoteNumber");

        if (imageBitmap == null) {
             hasImage = false;
        }

        else{
            hasImage = true;
        }


        byte[] decodedByteArray = android.util.Base64.decode(imageBitmap, Base64.DEFAULT);
        final Bitmap imageBit = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);

        imageDiscussion = findViewById(R.id.image_discussion);
        tagsDiscussion =findViewById(R.id.tags_discussion);
        subjectDiscussion = findViewById(R.id.subject_discussion);
        titleDiscussion = findViewById(R.id.discussion_title);
        bodyDiscussion = findViewById(R.id.discussion_body);
        upvote = findViewById(R.id.discussion_upvote);
        downvote = findViewById(R.id.discussion_downvote);
        upvoteNumberView = findViewById(R.id.discussion_upvoteNumber);
        downvoteNumberView = findViewById(R.id.discussion_downvoteNumber);

        if (hasImage == false) {
            imageDiscussion.setVisibility(View.GONE);
        }

        else{
            imageDiscussion.setImageBitmap(imageBit);
        }

        titleDiscussion.setText(title);
        subjectDiscussion.setText(subject + ": " + tags);
        bodyDiscussion.setText(body);
        upvoteNumberView.setText(upvoteNumber);
        downvoteNumberView.setText(downVoteNumber);



        imageDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ViewImageInDiscussion.class);
                intent.putExtra("image", imageBitmap);
                startActivity(intent);

            }
        });

        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasDownvoted) {
                    int initial = Integer.parseInt(upvoteNumber);
                    int newinitial = initial + 1;
                    String newUpvote = Integer.toString(newinitial);
                    ref.child(id).child("upvote").setValue(newUpvote);
                    upvoteNumberView.setText(newUpvote);
                    hasUpvoted = true;
                    upvote.setEnabled(false);
                    downvote.setEnabled(true);
                    int inititalDownvote = Integer.parseInt(downvoteNumberView.getText().toString());
                    String newDownvote = Integer.toString(inititalDownvote-1);
                    downvoteNumberView.setText(newDownvote);

                }

                else{
                    int initial = Integer.parseInt(upvoteNumber);
                    int newinitial = initial + 1;
                    String newUpvote = Integer.toString(newinitial);
                    ref.child(id).child("upvote").setValue(newUpvote);
                    upvoteNumberView.setText(newUpvote);
                    hasUpvoted = true;
                    upvote.setEnabled(false);
                    downvote.setEnabled(true);
                }
            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasUpvoted){
                int initial = Integer.parseInt(downVoteNumber);
                int newinitial = initial+1;
                String newDownvote = Integer.toString(newinitial);
                ref.child(id).child("downvote").setValue(newDownvote);
                downvoteNumberView.setText(newDownvote);
                int inititalUpvote = Integer.parseInt(upvoteNumberView.getText().toString());
                String newUpvote = Integer.toString(inititalUpvote-1);
                upvoteNumberView.setText(newUpvote);
                downvote.setEnabled(false);
                upvote.setEnabled(true);
                hasDownvoted = true;
                downvote.setEnabled(false);


                }

                else{
                    int initial = Integer.parseInt(downVoteNumber);
                    int newinitial = initial+1;
                    String newDownvote = Integer.toString(newinitial);
                    ref.child(id).child("downvote").setValue(newDownvote);
                    downvoteNumberView.setText(newDownvote);
                    downvote.setEnabled(false);
                    hasDownvoted = true;
                    downvote.setEnabled(false);
                    upvote.setEnabled(true);
                }
            }
        });
//        Glide.with(getApplicationContext()).load(img)
//                .thumbnail(0.5f)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imageDiscussion);

    }
}
