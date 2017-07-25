package com.noveogroup.evgeny.awersomeproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class Activity3 extends AppCompatActivity {


    static final int REQUEST_TAKE_PHOTO = 225;
    static final String TAG = "MainActivity";
    @BindView(R.id.imageView)
    public ImageView imageView;
    String mCurrentPhotoPath;
    List<ClarifaiOutput<Concept>> predictionResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen3);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button2)
    public void onCameraOpenClick() {
        dispatchTakePictureIntent();
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

                Log.d("SCREEN3", "File create err: ", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.noveogroup.evgeny.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //File imgFile = new File(mCurrentPhotoPath);
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

            //Bitmap d = new BitmapDrawable(ctx.getResources() , w.photo.getAbsolutePath()).getBitmap();
            int nh = (int) ( bitmap.getHeight() * (1024.0 / bitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

//            if (imgFile.exists()) {
//                try {
//                    Bitmap myBitmap = Glide.with(this).asBitmap().load(imgFile).into(340, 340).get();
//                    imageView.setImageBitmap(myBitmap);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//
//            }
            imageView.setImageBitmap(scaled);
//            Glide.with(this).load(imgFile)
//                    .into(imageView);
            sendToCloud();

        }
    }

    void sendToCloud() {
        AsyncTask<Void,Void, List<ClarifaiOutput<Concept>>> asyncTask = new AsyncTask<Void, Void, List<ClarifaiOutput<Concept>>>() {
            @Override
            protected List<ClarifaiOutput<Concept>> doInBackground(Void... params) {
                Log.d("asd","doInbackground");
                final ClarifaiClient client = new ClarifaiBuilder("f7bcefcc6cbf45219549bc97714c8604").buildSync();

               return client.getDefaultModels().generalModel() // You can also do Clarifai.getModelByID("id") to get custom models
                        .predict()
                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(new File(mCurrentPhotoPath))))
                        .executeSync()
                        .get();
            }

            @Override
            protected void onPostExecute(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
                predictionResults = clarifaiOutputs;
                Log.d("asd","onPost");
            }
        };
        asyncTask.execute((Void[]) null);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getFilesDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}

