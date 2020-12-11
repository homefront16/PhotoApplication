package com.example.photoapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SELECT_A_PHOTO = 2;

    List<Uri> uriList;
    String currentPhotoPath;


    Button btnLoad, btnTake, btnList;
    TextView tvMessage;
    ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoad = findViewById(R.id.btnLoad);
        btnTake = findViewById(R.id.btnTake);
        btnList = findViewById(R.id.btnList);
        tvMessage = findViewById(R.id.tvMessage);
        ivPhoto = findViewById(R.id.ivPhoto);

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to select a photo from the gallery
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // start with the intent with a request code
                startActivityForResult(i, SELECT_A_PHOTO);

            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), PhotoList.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uriList = ( (MyApplication)this.getApplication() ).getUriList();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
          // get the bitmap from the file name
            //Bitmap imageTaken = BitmapFactory.decodeFile(currentPhotoPath);
            // Set the image in the ivPhoto view
            //ImageView ivPhoto;
            //ivPhoto = findViewById(R.id.ivPhoto);
           // ivPhoto.setImageBitmap(imageTaken);
            // Show the file nam in the text view
            //TextView tvMessage;
            //tvMessage = findViewById(R.id.tvMessage);
            Glide.with(this).load(currentPhotoPath).into(ivPhoto);
            tvMessage.setText(currentPhotoPath);
            uriList.add(Uri.fromFile(new File(currentPhotoPath)));
        }
        if (requestCode == SELECT_A_PHOTO && resultCode == RESULT_OK){
            Uri selectedPhoto = data.getData();
            Glide.with(this).load(selectedPhoto).into(ivPhoto);
            tvMessage.setText(selectedPhoto.toString());


            uriList.add(selectedPhoto);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI =
                        FileProvider.getUriForFile(this,
                        "com.example.photoapplication",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}