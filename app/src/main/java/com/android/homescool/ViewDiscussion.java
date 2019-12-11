package com.android.homescool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewDiscussion extends AppCompatActivity {

    ImageView imageDiscussion;
    TextView tagsDiscussion, subjectDiscussion, titleDiscussion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_discussion);

        Intent intent = getIntent();
        String imageBitmap = intent.getStringExtra("imageBitmap");
        String tags = intent.getStringExtra("tags");
        String subject = intent.getStringExtra("subject");
        String title = intent.getStringExtra("title");

        byte[] decodedByteArray = android.util.Base64.decode(imageBitmap, Base64.DEFAULT);
        final Bitmap imageBit = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);

        imageDiscussion = findViewById(R.id.image_discussion);
        tagsDiscussion =findViewById(R.id.tags_discussion);
        subjectDiscussion = findViewById(R.id.subject_discussion);
        titleDiscussion = findViewById(R.id.discussion_title);

        imageDiscussion.setImageBitmap(imageBit);
        tagsDiscussion.setText(tags);
        titleDiscussion.setText(title);
        subjectDiscussion.setText(subject);

    }
}
