package com.noveogroup.evgeny.awersomeproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity {
    List<ClarifaiOutput<Concept>> predictionResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ClarifaiClient client = new ClarifaiBuilder("f7bcefcc6cbf45219549bc97714c8604").buildSync();
        //ClarifaiClient client = new Cla
        //new ClarifaiBuilder("{api-key}").buildSync().registerAsDefaultInstance();
        AsyncTask<Integer, Integer, List<ClarifaiOutput<Concept>>> asyncTask = new AsyncTask<Integer, Integer, List<ClarifaiOutput<Concept>>>() {
            @Override
            protected List<ClarifaiOutput<Concept>> doInBackground(Integer... params) {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.dogs);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                return client.getDefaultModels().generalModel() // You can also do Clarifai.getModelByID("id") to get custom models
                        .predict()
                        .withInputs(
                                //ClarifaiInput.forImage(ClarifaiImage.of("https://samples.clarifai.com/metro-north.jpg"))
                                ClarifaiInput.forImage(ClarifaiImage.of(byteArray))
                        )
                        .executeSync() // optionally, pass a ClarifaiClient parameter to override the default client instance with another one
                        .get();
            }
        };

//        try {
//            predictionResults = asyncTask.execute().get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//
//        }
//        final List<ClarifaiOutput<Concept>> predictionResults =
//                client.getDefaultModels().generalModel() // You can also do Clarifai.getModelByID("id") to get custom models
//                        .predict()
//                        .withInputs(
//                                ClarifaiInput.forImage(ClarifaiImage.of("https://samples.clarifai.com/metro-north.jpg"))
//                        )
//                        .executeSync() // optionally, pass a ClarifaiClient parameter to override the default client instance with another one
//                        .get();
        System.out.println("asd");
    }

    public void onClick(View view) {
        this.getFilesDir().toString();
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText(predictionResults.get(0).data().get(0).toString());
    }
}
