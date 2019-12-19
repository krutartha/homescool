package com.android.homescool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ViewDiscussion extends AppCompatActivity {

    ImageView imageDiscussion, upvote, downvote;
    TextView tagsDiscussion, subjectDiscussion, titleDiscussion, bodyDiscussion, upvoteNumberView, downvoteNumberView;
    EditText commentText;
    Button commentSend;
    LinearLayout showCommentsLayout;

    boolean hasImage;
    boolean hasUpvoted = false;
    boolean hasDownvoted = false;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref = mDatabase.child("discussion");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_discussion);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final Uri usrImage = user.getPhotoUrl();
        final String userName = user.getDisplayName();


        Intent intent = getIntent();
        final String imageBitmap = intent.getStringExtra("imageBitmap");
        final String id = intent.getStringExtra("id");
        String tags = intent.getStringExtra("tags");
        String subject = intent.getStringExtra("subject");
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        final String upvoteNumber = intent.getStringExtra("upvoteNumber");
        final String downVoteNumber = intent.getStringExtra("downvoteNumber");
        final String name = intent.getStringExtra("name");




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
        commentText = findViewById(R.id.comment_editText);
        commentSend = findViewById(R.id.comment_send);
        showCommentsLayout = findViewById(R.id.view_comments_layout);


        commentText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if(commentText.getText().toString().matches("")){
                    commentSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
               commentSend.setVisibility(View.VISIBLE);

            }
        });

        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment = commentText.getText().toString();
                DatabaseReference newRef = ref.child(id).child("comments").push();
                DatabaseReference finalRef = newRef.push();
                finalRef.child("displayName").setValue(userName);
                finalRef.child("comment").setValue(comment);
                finalRef.child("userImage").setValue(usrImage.toString());
                commentText.setText("");
            }
        });


        if (imageBitmap.length()<=0){
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
                    ref.child(id).child("downvote").setValue(newDownvote);

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
                ref.child(id).child("upvote").setValue(newUpvote);
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

        DatabaseReference commentRef = mDatabase.child("discussion").child(id).child("comments");

        commentRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<DataSnapshot> items = new ArrayList<DataSnapshot>();
            ArrayList<DataSnapshot> reverseItems = new ArrayList<DataSnapshot>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    final String commentId = snapshot.getKey().toString();

                    for(DataSnapshot newSnapshot : snapshot.getChildren()){


                        String userImg = newSnapshot.child("userImage").getValue().toString();
                        String name = newSnapshot.child("displayName").getValue().toString();
                        String comment = newSnapshot.child("comment").getValue().toString();
                        displayComments(id, commentId, name, comment, userImg);

                    }




                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }



    public void displayComments(final String discussionId, final String commentId, final String name, final String comment, final String userPhoto){

        LinearLayout horizontalLayout = new LinearLayout(getApplicationContext());
        horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout userLayout = new LinearLayout(getApplicationContext());
        userLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        userLayout.setOrientation(LinearLayout.VERTICAL);

        horizontalLayout.addView(userLayout);

        final LinearLayout commentLayout = new LinearLayout(getApplicationContext());
        commentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        commentLayout.setOrientation(LinearLayout.VERTICAL);

        horizontalLayout.addView(commentLayout);

        final CardView cardView = new CardView(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        cardView.setLayoutParams(params);
        cardView.setElevation(5);
        cardView.setRadius(7);

        cardView.addView(horizontalLayout);

        ImageView userImage = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams userImageParams = new LinearLayout.LayoutParams(100, 100);
        userImageParams.setMargins(20, 20, 20, 20);
        userImage.setLayoutParams(userImageParams);

        userImage.setId(R.id.userImage);
        userImage.setScaleType(ImageView.ScaleType.FIT_XY);

        Glide.with(getApplication().getApplicationContext()).load(userPhoto)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.circleCropTransform())
                .into(userImage);

        userLayout.addView(userImage);

        TextView userName = new TextView(getApplicationContext());
        LinearLayout.LayoutParams userNameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        userName.setLayoutParams(userNameParams);
        userName.setTextSize(10);
        userNameParams.setMargins(40,10,20,5);
        userName.setText(name);
        userLayout.addView(userName);

        TextView bodyText = new TextView(getApplicationContext());
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bodyText.setLayoutParams(bodyParams);
        bodyParams.setMargins(20, 20, 20, 20);
        bodyText.setTextSize(16);
        bodyText.setMaxLines(4);
        bodyText.setTextColor(Color.parseColor("#5e5e5e"));
        bodyText.setText(comment);
        commentLayout.addView(bodyText);

        final Button replyButton = new Button(getApplicationContext());
        LinearLayout.LayoutParams replyButtonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        replyButton.setLayoutParams(replyButtonParams);
        replyButtonParams.setMargins(10, 10, 10, 10);
        replyButtonParams.gravity = Gravity.END;
        replyButton.setBackgroundColor(Color.TRANSPARENT);
        replyButton.setText("Reply");

        commentLayout.addView(replyButton);

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout replyLayout = new LinearLayout(getApplicationContext());
                replyLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                replyLayout.setOrientation(LinearLayout.VERTICAL);

                commentLayout.addView(replyLayout);

                replyButton.setVisibility(View.GONE);
                final EditText replyEditText = new EditText(getApplicationContext());
                LinearLayout.LayoutParams replyTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                replyEditText.setHint("Enter your reply");
                replyEditText.setTextSize(15);
                replyEditText.setPadding(3, 20, 20, 20);
                replyEditText.setMinimumHeight(100);
                replyEditText.setBackgroundColor(Color.TRANSPARENT);
                replyEditText.setLayoutParams(replyTextParams);

                replyLayout.addView(replyEditText);

                final Button sendReply = new Button(getApplicationContext());
                LinearLayout.LayoutParams sendReplyParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                sendReply.setLayoutParams(sendReplyParams);
                sendReply.setText("Send Reply");
                sendReplyParams.gravity = Gravity.END;
                sendReply.setVisibility(View.GONE);

                replyLayout.addView(sendReply);
                replyEditText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {

                        if(replyEditText.getText().toString().matches("")){
                            sendReply.setVisibility(View.GONE);
                            replyEditText.setVisibility(View.GONE);
                            replyButton.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        sendReply.setVisibility(View.VISIBLE);

                    }
                });

                sendReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final Uri usrImage = user.getPhotoUrl();
                        final String userName = user.getDisplayName();

                        String reply = replyEditText.getText().toString();
                        DatabaseReference newRef = ref.child(discussionId).child("comments").child(commentId);
                        DatabaseReference finalRef = newRef.push();
                        finalRef.child("displayName").setValue(userName);
                        finalRef.child("comment").setValue(reply);
                        finalRef.child("userImage").setValue(usrImage.toString());
                        sendReply.setVisibility(View.GONE);
                        replyEditText.setVisibility(View.GONE);
                        replyButton.setVisibility(View.VISIBLE);

                    }
                });
            }
        });

        showCommentsLayout.addView(cardView);




    }
}
