package com.android.homescool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewImageInDiscussion extends AppCompatActivity {

    ImageView showImage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_viewer);

        Intent intent = getIntent();
        String imageBitmap = intent.getStringExtra("image");


        byte[] decodedByteArray = android.util.Base64.decode(imageBitmap, Base64.DEFAULT);
        final Bitmap imageBit = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);

        showImage = findViewById(R.id.show_image);

        showImage.setImageBitmap(imageBit);
    }
}
