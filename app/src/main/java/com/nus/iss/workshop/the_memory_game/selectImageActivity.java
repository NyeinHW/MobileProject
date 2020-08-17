package com.nus.iss.workshop.the_memory_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class selectImageActivity extends AppCompatActivity
        implements View.OnClickListener{

    private ArrayList<Image> imageList=new ArrayList<>();
    private ArrayList<Image> selectedImageList=new ArrayList<>();
    private ArrayList<Integer> selectedImages=new ArrayList<>();
    private int selectImageCounter;
    private TextView progressBarTextView;
    private ProgressBar progressBar;
    private String htmlURL;
    File mTargetFile;
    private static final int numDisplayImage = 20;
    private int numDownloadedImage;
    private int PROGRESS_UPDATE =1;
    private int DOWNLOAD_COMPLETED =2;
    private int numImageToDownload;
    private String website ="https://stocksnap.io/search/";
    MediaPlayer player;

    @SuppressWarnings("deprecation")
    @SuppressLint("HandlerLeak")
    Handler mainHdl = new Handler(){
        public void handleMessage(@NonNull Message msg){
            if(msg.what == PROGRESS_UPDATE){
                progressBar.setProgress(msg.arg1);
                progressBarTextView.setText
                        (String.format("Download %d of %d images", numDownloadedImage, numDisplayImage));
            }
            if(msg.what == DOWNLOAD_COMPLETED){
                ImageView imageView = (ImageView) findViewById(getResources().
                        getIdentifier("imageView" + msg.arg1, "id",getPackageName()));
                Bitmap bitmap = (Bitmap)msg.obj;
                imageView.setImageBitmap(bitmap);
                //Add to list of Image
                Image image = new Image(msg.arg1,bitmap);
                imageList.add(image);
                if(numDownloadedImage == numImageToDownload){
                    setImageOnClickListener();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        Button fetchBtn = findViewById(R.id.fetchBtn);
        fetchBtn.setOnClickListener(this);

        player=MediaPlayer.create(this,R.raw.song2);
        player.start();
        player.setLooping(true);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(numDisplayImage);

        progressBarTextView = findViewById(R.id.progressBarTextView);
        progressBarTextView.setText("Enter URL and press Fetch!");

    }

    @Override
    public void onClick(View v) {

        //Fetch button clicked. Starts to fetch image
        if(v.getId() == R.id.fetchBtn) {
            startFetchImage();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String htmlText = getHTMLTxt(htmlURL);
                    ArrayList<String> imgTags = getImgTags(htmlText);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    downloadImageBitmap(imgTags);
                }
            }).start();
        }

        for(int i = 0; i < numImageToDownload; i++ ){

            if(v.getId() == getResources().getIdentifier("imageView" + i, "id",getPackageName())){
                selectImage(i,(ImageView) v);
            }
        }
    }

    void selectImage(int i,ImageView v){
        TextView selectedText=(TextView)findViewById(R.id.selectedCount);
        if(selectedImages.contains(i)){
            selectedImages.remove(new Integer(i));
            v.setImageAlpha(255);
            selectImageCounter--;
        }
        else{
            selectedImages.add(i);
            v.setImageAlpha(100);
            selectImageCounter++;
            if(selectImageCounter == 6){
                System.out.println("images =6");
                startGameActivity();
            }
        }
        selectedText.setText(selectImageCounter+"/6 Images Selected");
    }

    void startFetchImage(){
        final EditText editText = findViewById(R.id.fetchTxt);
        //Initialization
        htmlURL = website+editText.getText().toString();
        numDownloadedImage = 0;
        selectImageCounter =0;
        resetImageView();
        imageList.clear();
        progressBarTextView.setText("Download 0 of "
                + numDisplayImage + " images");

        progressBar.setProgress(0);
    }

    protected void resetImageView(){
        for(int i = 0; i<numDisplayImage; i++) {
            ImageView imageView = (ImageView) findViewById(getResources().
                    getIdentifier("imageView" + i, "id",getPackageName()));
            imageView.setImageBitmap(null);
        }
    }

    String getHTMLTxt(String urlString){
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                while(scanner.hasNext()){
                    stringBuffer.append(scanner.nextLine());
                }
                connection.disconnect();
            }
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
        return stringBuffer.toString();
    }

    ArrayList<String> getImgTags(String htmlText){
        ArrayList<String> imgTags = new ArrayList<String>();
        int lastIndex = 0;
        while(true){
            int index1 = htmlText.indexOf("<img", lastIndex);
            int index2 = htmlText.indexOf(">", index1);
            if(index1 == -1 || index2 == -1) break;
            String imgTag = htmlText.substring(index1, index2 + 1);
            if(imgTag.contains("http") && !imgTag.contains("visibility:hidden")) {
                imgTags.add(imgTag);
            }
            lastIndex = index2;
        }
        return imgTags;
    }

    protected void downloadImageBitmap (ArrayList<String> imageTags){

        // Multi-thread download images
        //int len = imageTags.size();
        numImageToDownload = imageTags.size();
        if(numImageToDownload > numDisplayImage) numImageToDownload=numDisplayImage;
        for(int i=0; i<numImageToDownload; i++){

            String imageTag = imageTags.get(i);
            int firstIndex = imageTag.indexOf("http");
            int secondIndex = imageTag.indexOf("\"",firstIndex);
            try{
                final URL imageURL = new URL(imageTag.substring(firstIndex,secondIndex));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadImage(imageURL);
                    }
                }).start();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    protected void downloadImage(URL url){
        int imageLen = 0;
        int convertProgress = 0;
        int readLen = 0;
        Bitmap bitmap = null;

        byte[] imgBytes;

        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            imageLen = connection.getContentLength();
            imgBytes = new byte[imageLen];

            InputStream in = url.openStream();
            BufferedInputStream bufIn = new BufferedInputStream(in,2048);

            byte[] data = new byte[1024];

            while((readLen = bufIn.read(data)) != -1){
                System.arraycopy(data,0,imgBytes,convertProgress,readLen);
                convertProgress += readLen;
            }
            bitmap = BitmapFactory.decodeByteArray(imgBytes,0,imageLen);
            connection.disconnect();
            numDownloadedImage++;
            updateProgress(numDownloadedImage);
            updateImage(bitmap,numDownloadedImage-1);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void updateProgress(int progress){
        Message msg = new Message();
        msg.what=PROGRESS_UPDATE;
        msg.arg1= progress;
        mainHdl.sendMessage(msg);
    }

    protected void updateImage(Bitmap bitmap, int id){
        Message msg = new Message();
        msg.what = DOWNLOAD_COMPLETED;
        msg.obj = bitmap;
        msg.arg1 = id;
        mainHdl.sendMessage(msg);
    }

    protected void setImageOnClickListener(){
        for(int i = 0; i < numImageToDownload; i++){
            ImageView imageView =(ImageView) findViewById(getResources().getIdentifier("imageView"+ i,"id",getPackageName()));
            imageView.setOnClickListener(this);
        }
    }

    protected void startGameActivity(){
        compileSelectedImages();
        writeImageToFile();
        player.stop();
        Intent intent = new Intent(this,gameActivity.class);
        startActivity(intent);
    }

    protected void compileSelectedImages(){
        if(selectedImageList.size() != 0){
            selectedImageList.clear();
        }
        for(int i=0; i<6; i++) {
            int id = selectedImages.get(i);
            System.out.println("id is"+id);
            selectedImageList.add(imageList.get(id));
            System.out.println(selectedImageList.get(i));

        }
    }

    protected void writeImageToFile(){
        imageSaver imageUtil = new imageSaver(getApplicationContext());
        for(int i=0; i<6; i++){
            Bitmap imageBitmap = selectedImageList.get(i).getBitmap();
            imageUtil
                    .setDirectoryName("images")
                    .setFileName("imageCard"+i+".png")
                    .deleteFile();

            imageUtil
                    .setDirectoryName("images")
                    .setFileName("imageCard"+i+".png")
                    .save(imageBitmap);

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        player.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.seekTo(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

}
