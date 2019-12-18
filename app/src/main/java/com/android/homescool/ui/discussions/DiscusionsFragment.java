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

import java.util.HashMap;
import java.util.Map;

public class DiscusionsFragment extends Fragment {

    private FloatingActionButton fabAddDiscussion;
    private DatabaseReference mDatabase;
    LinearLayout linearLayout;
    NestedScrollView scrollView;
    CardView searchCard;
    EditText searchEditText;

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


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageEncoded = snapshot.child("imageEncoded").getValue().toString();
                    String subject = snapshot.child("subject").getValue().toString();
                    String tags = snapshot.child("tags").getValue().toString();
                    String title = snapshot.child("title").getValue().toString();
                    String userImg = snapshot.child("userImg").getValue().toString();
                    String name = snapshot.child("displayName").getValue().toString();
                    String body = snapshot.child("body").getValue().toString();
                    displayDiscussion(imageEncoded, subject,tags, title, userImg, name, body);
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

    public void displayDiscussion(final String imageEncoded, final String subject, final String tags, String title, final String userImg, final String name, final String body){
        byte[] decodedByteArray = Base64.decode(imageEncoded, Base64.DEFAULT);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);

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

//        





        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,25,10,10);
        cardView.setLayoutParams(params);
        cardView.setElevation(5);
//        cardView.setCardElevation(10);
//        cardView.setRadius(5);

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

//        TextView tagsText = new TextView(getContext());
//        LinearLayout.LayoutParams tagsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        tagsText.setLayoutParams(tagsParams);
//        tagsParams.setMargins(20, 20, 20, 20);
//        tagsText.setTextSize(14);
//        tagsText.setMaxLines(4);
//        tagsText.setTextColor(Color.parseColor("#5e5e5e"));
//        tagsText.setText(tags);
//        discussionLayout.addView(tagsText);


//        if(imageEncoded != null){
//            ImageView discussionImage = new ImageView(getContext());
//            LinearLayout.LayoutParams discussionImageParams = new LinearLayout.LayoutParams(300, 500);
//            discussionImageParams.setMargins(20, 20, 20, 20);
//            discussionImage.setLayoutParams(discussionImageParams);
//
//            discussionImage.setScaleType(ImageView.ScaleType.FIT_XY);
//
//            discussionImage.setImageBitmap(imageBitmap);
//
//            discussionLayout.addView(discussionImage);
//        }


//        TextView usernameView = new TextView(getContext());
//        LinearLayout.LayoutParams userParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        usernameView.setLayoutParams(userParams);
//        usernameView.setTextColor(Color.parseColor("#000000"));
//        usernameView.setTextSize(12);
//        usernameView.setText(name);
//        usernameView.setMaxLines(2);

//        TextView titleView = new TextView(getContext());
//        LinearLayout.LayoutParams titleParms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        titleParms.setMargins(0,10,0,10);
//        titleView.setLayoutParams(titleParms);
//        titleView.setTextColor(Color.parseColor("#000000"));
//        titleView.setTextSize(28);
//        titleView.setText(title);


        TextView newText = new TextView(getContext());
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(40,0,0,10);
        newText.setLayoutParams(textParams);
        newText.setTextColor(Color.parseColor("#000000"));
        newText.setTextSize(18);
        newText.setText(title);
        newText.setMaxLines(2);

        if (newText.getText().toString().length()>54){
            String newTextText = newText.getText().toString();
            newTextText = newTextText.substring(0, 45) + "...";
            newText.setText(newTextText);
        }





        TextView newText2 = new TextView(getContext());
        LinearLayout.LayoutParams textParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (tags.length()<20){
            textParams2.setMargins(40,10,20,5);
        } else {
            textParams2.setMargins(40,10,20,5);
        }


        TextView newText3 = new TextView(getContext());
        LinearLayout.LayoutParams textParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (tags.length()<20){
            textParams3.setMargins(40,5,20,20);
        } else {
            textParams3.setMargins(40,5,20,20);
        }

        if (newText3.getText().toString().length()>54){
            String newText3Text = newText3.getText().toString();
            newText3Text = newText3Text.substring(0, 45) + "...";
            newText.setText(newText3Text);
        }

        TextView newText4 = new TextView(getContext());
        LinearLayout.LayoutParams textParams4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (tags.length()<20){
            textParams4.setMargins(40,5,20,20);
        } else {
            textParams4.setMargins(40,5,20,20);
        }

        if (newText4.getText().toString().length()>54){
            String newText4Text = newText4.getText().toString();
            newText4Text = newText4Text.substring(0, 45) + "...";
            newText.setText(newText4Text);
        }

        TextView newText5 = new TextView(getContext());
        LinearLayout.LayoutParams textParams5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (tags.length()<20){
            textParams5.setMargins(40,5,20,20);
        } else {
            textParams5.setMargins(40,5,20,20);
        }

        if (newText5.getText().toString().length()>54){
            String newText5Text = newText5.getText().toString();
            newText5Text = newText5Text.substring(0, 45) + "...";
            newText.setText(newText5Text);
        }

//        newText2.setLayoutParams(textParams2);
//        newText3.setLayoutParams(textParams3);
//        newText4.setLayoutParams(textParams4);
//        newText5.setLayoutParams(textParams5);
//        newText2.setText(body);
//        newText3.setText(subject);
//        newText4.setText(name);
//        newText5.setText(tags);
//        usernameView.setText(name);

        if (newText2.getText().toString().length()>54){
            String newText2Text = newText2.getText().toString();
            newText2Text = newText2Text.substring(0, 54) + "...";
            newText2.setText(newText2Text);
        }

        LinearLayout newLayout2 = new LinearLayout(getContext());
        newLayout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newLayout2.setOrientation(LinearLayout.VERTICAL);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String img = user.getPhotoUrl().toString();

        

//        newImage.setImageBitmap(imageBitmap);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewDiscussion.class);
                intent.putExtra("imageBitmap", imageEncoded);
                intent.putExtra("subject", subject);
                intent.putExtra("tags", tags);
                startActivity(intent);
            }
        });

//        newLayout2.addView(newText);
//        newLayout2.addView(newText2);
//        newLayout2.addView(newText3);
//        horizontalLayout.addView(newLayout2);

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