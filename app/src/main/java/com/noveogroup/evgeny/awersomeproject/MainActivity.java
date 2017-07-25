package com.noveogroup.evgeny.awersomeproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiRequest;
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


//                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.dogs);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
        client.getDefaultModels().generalModel() // You can also do Clarifai.getModelByID("id") to get custom models
                .predict()
                .withInputs(
                        ClarifaiInput.forImage(ClarifaiImage.of("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8cBb59-SULE4c5JKXE85Jw8hwzrx1HCi5jyqVvFqGyxcXo20AYA"))
                        //ClarifaiInput.forImage(ClarifaiImage.of(byteArray))
                )
                .executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
                    @Override
                    public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
                        predictionResults = clarifaiOutputs;
                    }

                    @Override
                    public void onClarifaiResponseUnsuccessful(int errorCode) {

                    }

                    @Override
                    public void onClarifaiResponseNetworkError(IOException e) {

                    }
                });

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
