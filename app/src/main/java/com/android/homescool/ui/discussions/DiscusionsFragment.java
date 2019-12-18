package com.android.homescool.ui.discussions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.homescool.R;
import com.android.homescool.UploadDiscussion;
import com.android.homescool.ViewDiscussion;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiscusionsFragment extends Fragment {

    private FloatingActionButton fabAddDiscussion;
    private DatabaseReference mDatabase;
    LinearLayout linearLayout;
    NestedScrollView scrollView;
    CardView searchCard;
    EditText searchEditText;
    boolean hasImage = true;

    private DiscussionsViewModel discussionsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mDatabase = FirebaseDatabase.getInstance().getReference();
        discussionsViewModel = ViewModelProviders.of(this).get(DiscussionsViewModel.class);
//        final View root = inflater.inflate(R.layout.fragment_discussions, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        fab = root.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                Long tsLong = System.currentTimeMillis()/1000;
//                String ts = tsLong.toString();
//                writeNewDiscussion(user.getUid(), "test", ts);
//            }
//        });
//        discussionsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//
//            }
//
//        });

        View root = inflater.inflate(R.layout.fragment_discussions, container, false);
        setHasOptionsMenu(true);

        fabAddDiscussion = root.findViewById(R.id.fab_add_discussion);
        linearLayout = (LinearLayout) root.findViewById(R.id.linear_layout_discussion);
        scrollView = root.findViewById(R.id.scroll_view_discussion);
        searchCard = root.findViewById(R.id.cardview_search_discussion);
        searchEditText = root.findViewById(R.id.edit_text_search_discussion);


        fabAddDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UploadDiscussion.class));
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("discussion");


        ref.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<DataSnapshot> items = new ArrayList<DataSnapshot>();
            ArrayList<DataSnapshot> reverseItems = new ArrayList<DataSnapshot>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        items.add(snapshot);
                }

                for(int i = items.size() - 1; i>=0; i--){
                    reverseItems.add(items.get(i));
                }

                for(int i =0; i < items.size(); i++){
                    String id = reverseItems.get(i).getKey().toString();
                    String imageEncoded= reverseItems.get(i).child("imageEncoded").getValue().toString();
                    String subject = reverseItems.get(i).child("subject").getValue().toString();
                    String tags = reverseItems.get(i).child("tags").getValue().toString();
                    String title= reverseItems.get(i).child("title").getValue().toString();
                    String userImg = reverseItems.get(i).child("userImg").getValue().toString();
                    String name = reverseItems.get(i).child("displayName").getValue().toString();
                    String body = reverseItems.get(i).child("body").getValue().toString();
                    String upvotes = reverseItems.get(i).child("upvote").getValue().toString();
                    String downvotes = reverseItems.get(i).child("downvote").getValue().toString();
                    displayDiscussion(id, imageEncoded, subject,tags, title, userImg, name, body, upvotes, downvotes);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });





        return root;

    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.discussions_itemdetail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.search_discussion:
                displaySearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void displayDiscussion(final String id, final String imageEncoded, final String subject, final String tags, final String title, final String userImg, final String name, final String body, final String upvotes, final String downvotes){

        if (imageEncoded == ""){
            hasImage = false;
        }
        final byte[] decodedByteArray = Base64.decode(imageEncoded, Base64.DEFAULT);
        final Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);

        LinearLayout horizontalLayout = new LinearLayout(getContext());
        horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout userLayout = new LinearLayout(getContext());
        userLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        userLayout.setOrientation(LinearLayout.VERTICAL);

        horizontalLayout.addView(userLayout);

        LinearLayout discussionLayout = new LinearLayout(getContext());
        discussionLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        discussionLayout.setOrientation(LinearLayout.VERTICAL);

        horizontalLayout.addView(discussionLayout);


        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,25,10,10);
        cardView.setLayoutParams(params);
        cardView.setElevation(5);
        cardView.setRadius(7);

        cardView.addView(horizontalLayout);

        ImageView userImage = new ImageView(getContext());
        LinearLayout.LayoutParams userImageParams = new LinearLayout.LayoutParams(100, 100);
        userImageParams.setMargins(20, 20, 20, 20);
        userImage.setLayoutParams(userImageParams);

        userImage.setId(R.id.userImage);
        userImage.setScaleType(ImageView.ScaleType.FIT_XY);

        Glide.with(getActivity().getApplicationContext()).load(userImg)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.circleCropTransform())
                .into(userImage);

        userLayout.addView(userImage);

        TextView userName = new TextView(getContext());
        LinearLayout.LayoutParams userNameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        userName.setLayoutParams(userNameParams);
        userName.setTextSize(10);
        userNameParams.setMargins(40,10,20,5);
        userName.setText(name);
        userLayout.addView(userName);

        TextView titleText = new TextView(getContext());
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleText.setLayoutParams(titleParams);
        titleParams.setMargins(20, 20, 20, 20);
        titleText.setTextSize(16);
        titleText.setMaxLines(2);
        titleText.setTextColor(Color.parseColor("#1c1c1c"));
        titleText.setText(title);
        discussionLayout.addView(titleText);

        TextView bodyText = new TextView(getContext());
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bodyText.setLayoutParams(bodyParams);
        bodyParams.setMargins(20, 20, 20, 20);
        bodyText.setTextSize(16);
        bodyText.setMaxLines(4);
        bodyText.setTextColor(Color.parseColor("#5e5e5e"));
        bodyText.setText(body);
        discussionLayout.addView(bodyText);

        TextView subjectText = new TextView(getContext());
        LinearLayout.LayoutParams subjectParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subjectText.setLayoutParams(subjectParams);
        subjectParams.setMargins(20, 20, 20, 20);
        subjectText.setTextSize(14);
        subjectText.setMaxLines(4);
        subjectText.setTextColor(Color.parseColor("#5e5e5e"));
        subjectText.setText(subject + ": " + tags);
        discussionLayout.addView(subjectText);





        

//        newImage.setImageBitmap(imageBitmap);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bitmap emptyBitmap = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), imageBitmap.getConfig());
                Intent intent = new Intent(getActivity(), ViewDiscussion.class);
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("subject", subject);
                intent.putExtra("tags", tags);
                intent.putExtra("body", body);
                intent.putExtra("upvoteNumber", upvotes);
                intent.putExtra("downvoteNumber", downvotes);
                if(hasImage == true){
                    intent.putExtra("imageBitmap", imageEncoded);
                }



                startActivity(intent);
            }
        });

        linearLayout.addView(cardView);



    }


    public void displaySearch(){
        if (searchCard.getVisibility() == View.VISIBLE){
            searchCard.setVisibility(View.GONE);
            searchForText("");
        } else {
            searchCard.setVisibility(View.VISIBLE);
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    searchForText(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }

    public void searchForText(String queryText){
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            final View child = linearLayout.getChildAt(i);
            child.setVisibility(View.GONE);
            if (child instanceof RelativeLayout) {
                LinearLayout linearlayout2 = (LinearLayout) ((RelativeLayout) child).getChildAt(0);
                LinearLayout linearLayout3 = (LinearLayout) linearlayout2.getChildAt(1);
                TextView textView1 = (TextView) linearLayout3.getChildAt(0);
                TextView textView2 = (TextView) linearLayout3.getChildAt(1);
                if (textView1.getText().toString().toLowerCase().trim().contains(queryText.toLowerCase().trim()) || textView2.getText().toString().toLowerCase().trim().contains(queryText.toLowerCase().trim())){
                    child.setVisibility(View.VISIBLE);
                }
            }
        }
        if (linearLayout.getChildCount() == 0){
            Snackbar snackbar = Snackbar.make(scrollView, "No results found!", Snackbar.LENGTH_LONG);
            snackbar.show();

        }
    }


}