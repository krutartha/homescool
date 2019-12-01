package com.android.homescool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewPaper extends AppCompatActivity {

    ImageView imagePaper;
    TextView tagsText;
    TextView subjectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_paper);

        Intent intent = getIntent();
        String imageBitmapString  = intent.getStringExtra("imageBitmap");
        String tags = intent.getStringExtra("tags");
        String subject = intent.getStringExtra("subject");

        byte[] decodedByteArray = android.util.Base64.decode(imageBitmapString, Base64.DEFAULT);
        final Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);

        imagePaper = (ImageView) findViewById(R.id.image_paper);
        tagsText = (TextView) findViewById(R.id.tags_paper);
        subjectText = (TextView) findViewById(R.id.subject_paper);

        imagePaper.setImageBitmap(imageBitmap);
        tagsText.setText(tags);
        subjectText.setText(subject);

    }
}
