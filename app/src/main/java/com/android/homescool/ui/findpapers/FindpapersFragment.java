package com.android.homescool.ui.findpapers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.homescool.R;
import com.android.homescool.UploadPaper;
import com.android.homescool.ViewPaper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindpapersFragment extends Fragment {

    private FindpapersViewModel findpapersViewModel;

    FloatingActionButton fabAddPaper;
    LinearLayout linearLayout;
    NestedScrollView scrollView;
    CardView searchCard;
    EditText searchEditText;

    private DatabaseReference mDatabase;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        findpapersViewModel =
                ViewModelProviders.of(this).get(FindpapersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_findpapers, container, false);
        setHasOptionsMenu(true);

        fabAddPaper = (FloatingActionButton) root.findViewById(R.id.fab_add_paper);
        linearLayout = (LinearLayout) root.findViewById(R.id.linear_layout_main);
        scrollView = (NestedScrollView) root.findViewById(R.id.scroll_view);
        searchCard = (CardView) root.findViewById(R.id.cardview_search);
        searchEditText = (EditText) root.findViewById(R.id.edit_text_search);

        fabAddPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UploadPaper.class));
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("qpapers");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageEncoded = snapshot.child("imageEncoded").getValue().toString();
                    String subject = snapshot.child("subject").getValue().toString();
                    String tags = snapshot.child("tags").getValue().toString();

                    displayQuestionPaper(imageEncoded, subject, tags);
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
        inflater.inflate(R.menu.findpaper_itemdetail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                displaySearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayQuestionPaper(final String imageEncoded, final String subject, final String tags){
        byte[] decodedByteArray = android.util.Base64.decode(imageEncoded, Base64.DEFAULT);
        final Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);

        RelativeLayout cardView = new RelativeLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,25,10,10);
        cardView.setLayoutParams(params);
        //cardView.setCardElevation(10);
        //cardView.setRadius(5);

        LinearLayout newLayout = new LinearLayout(getContext());
        newLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView newImage = new ImageView(getContext());
        newImage.setLayoutParams(new LinearLayout.LayoutParams(450, 300));
        newImage.setScaleType(ImageView.ScaleType.FIT_XY);

        TextView newText = new TextView(getContext());
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(40,10,20,10);
        newText.setLayoutParams(textParams);
        newText.setTextColor(Color.parseColor("#000000"));
        newText.setTextSize(16);
        newText.setText(tags);
        newText.setMaxLines(2);

        if (newText.getText().toString().length()>54){
            String newTextText = newText.getText().toString();
            newTextText = newTextText.substring(0, 45) + "...";
            newText.setText(newTextText);
        }


        TextView newText2 = new TextView(getContext());
        LinearLayout.LayoutParams textParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (tags.length()<20){
            textParams2.setMargins(40,20,20,20);
        } else {
            textParams2.setMargins(40,10,20,20);
        }

        newText2.setLayoutParams(textParams2);
        newText2.setText(subject + " other stuff");

        LinearLayout newLayout2 = new LinearLayout(getContext());
        newLayout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newLayout2.setOrientation(LinearLayout.VERTICAL);

        newImage.setImageBitmap(imageBitmap);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewPaper.class);
                intent.putExtra("imageBitmap", imageEncoded);
                intent.putExtra("subject", subject);
                intent.putExtra("tags", tags);
                startActivity(intent);
            }
        });

        newLayout2.addView(newText);
        newLayout2.addView(newText2);
        newLayout.addView(newImage);
        newLayout.addView(newLayout2);
        cardView.addView(newLayout);
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