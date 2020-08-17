package com.nus.iss.workshop.the_memory_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class imageSaver {

    private String directoryName = "images";
    private String fileName = "imageCard.png";
    private Context context;

    public imageSaver(Context context){
        this.context = context;
    }

    public imageSaver setFileName(String fileName){
        this.fileName = fileName;
        return this;
    }
    public imageSaver setDirectoryName (String directoryName){
        this.directoryName = directoryName;
        return this;
    }

    public void save(Bitmap bitmapImage){
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        finally{
            try{
                if(fileOutputStream !=null){
                    fileOutputStream.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public File createFile(){
        File directory;
        directory = context.getDir(directoryName,Context.MODE_PRIVATE);
        if(!directory.exists() && !directory.mkdirs()){
            Log.e("ImageSave","Failed to create directory: " + directory);
        }
        return new File(directory,fileName);
    }

    public Bitmap load(){
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(fileInputStream);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if( fileInputStream!=null){
                    fileInputStream.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
    public void deleteFile(){
        File file = createFile();
        file.delete();
    }
}
