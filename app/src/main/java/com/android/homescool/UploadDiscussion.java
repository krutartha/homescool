package com.android.homescool;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UploadDiscussion extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String url = user.getPhotoUrl().toString();
    String name = user.getDisplayName();

    Bitmap bit = null;
    Bitmap imageBitmap = null;

    Spinner spinner;
    Button addPic, publish;
    ImageView mImageLabel;

    private static final int REQUEST_IMAGE_CAPTURE = 111;
    public static boolean hasImage;

    private DatabaseReference mDatabase;

    String subject;
    TextInputEditText tagsDiscussion, titleDiscussion, bodyDiscussion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_discussion);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addPic = findViewById(R.id.add_pic_button);
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onLaunchCamera();

                AlertDialog.Builder builder1 = new AlertDialog.Builder(UploadDiscussion.this);
                builder1.setMessage("How would you like to add an image?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Choose an image",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onLaunchGallery();
                            }
                        });

                builder1.setNegativeButton(
                        "Take an image",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onLaunchCamera();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        mImageLabel = findViewById(R.id.show_image);
        tagsDiscussion = findViewById(R.id.tags_discussion);
        spinner = findViewById(R.id.subjects_selector);
        titleDiscussion = findViewById(R.id.title_discussion);
        bodyDiscussion = findViewById(R.id.body_discussion);
        publish = findViewById(R.id.publish_discussion);

        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add("Math");
        categories.add("Physics");
        categories.add("Chemistry");
        categories.add("Biology");
        categories.add("Economics");
        categories.add("History");

        spinner.setPrompt("Subject");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encodeBitmapAndSaveToFirebase(bit);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        subject = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 0);
        }
    }

    public void onLaunchGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (pickPhoto.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(pickPhoto, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if (resultCode == UploadDiscussion.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    mImageLabel.setImageBitmap(imageBitmap);
                    bit = imageBitmap;

                }
                break;

            case 1:
                if(resultCode == UploadDiscussion.RESULT_OK){
                    Uri imageUri = data.getData();
                    try{
                        InputStream image_stream;
                        try {
                            image_stream = getApplicationContext().getContentResolver().openInputStream(imageUri);
                            imageBitmap = BitmapFactory.decodeStream(image_stream);
                            mImageLabel.setImageBitmap(imageBitmap);
                            bit = imageBitmap;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    bit = imageBitmap;
                }
                break;
        }




    }




    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference ref = mDatabase.child("discussion").push();
        ref.child("imageEncoded").setValue(imageEncoded);
        ref.child("title").setValue(titleDiscussion.getText().toString());
        ref.child("body").setValue(bodyDiscussion.getText().toString());
        ref.child("tags").setValue(tagsDiscussion.getText().toString());
        ref.child("subject").setValue(subject);
        ref.child("from").setValue(uid);
        ref.child("userImg").setValue(url);
        ref.child("displayName").setValue(name);
        ref.child("upvote").setValue(0);
        ref.child("downvote").setValue(0);
    }
}
