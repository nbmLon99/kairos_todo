package com.nbmlon.managemodule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class FileManager {

    protected final Context mContext;
    protected final String firstFolder;
    protected final File mStroage;
    protected String secondFolder;

    public FileManager(Context context,String folder){
        mContext = context;
        firstFolder = folder;
        mStroage = mContext.getApplicationContext().getFilesDir();

    }




    public static class RecordImage extends FileManager{


        /** For Save uploading Images for todoitems**/
        public RecordImage(Context context, String Dstdate, String title) {
            super(context, Dstdate);
            secondFolder = title;
        }


        public ArrayList<Uri> getUrisFromFile() {
            ArrayList<Uri> retBitmaps = new ArrayList<>();
            File folder = new File(mStroage.getAbsolutePath()+ "/" + firstFolder + "/" + secondFolder);

            if (!folder.exists())
                return retBitmaps;

            File[] files = folder.listFiles();


            for (File file : files) {
                retBitmaps.add(Uri.fromFile(file.getAbsoluteFile()));
            }
            return retBitmaps;
        }



        public void saveImageUrisToJPEG(ArrayList<Uri> uris){
            new Thread((Runnable) () -> {
                try {
                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
                    for (Uri uri : uris) {
                        Bitmap bitmap;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            ImageDecoder.Source source = null;
                            source = ImageDecoder.createSource(mContext.getContentResolver(), uri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        }
                        else{
                            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                        }
                        bitmaps.add(bitmap);
                    }

                    File folder = new File(mStroage.getAbsolutePath() + "/" + firstFolder + "/" + secondFolder);
                    if (!folder.exists())
                        folder.mkdirs();
                    else {
                        File[] files = folder.listFiles();
                        for (File file : files)
                            file.delete();
                    }



                    int index = 0;
                    for (Bitmap bitmap : bitmaps) {
                        String filePath = folder.getAbsolutePath() + "/" + index++ + ".jpg";

                        File tempFile = new File(filePath);
                        tempFile.createNewFile();
                        FileOutputStream out = new FileOutputStream(tempFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).run();
        }

        /** Delete Saved File with folder **/
        public void deleteSavedFile() {
            new Thread((Runnable) () -> {
                File folder = new File(mStroage.getAbsolutePath() + "/" + firstFolder + "/" + secondFolder);
                if (!folder.exists())
                    return;
                else {
                    File[] files = folder.listFiles();

                    for (File file : files)
                        file.delete();
                    folder.delete();
                }
                File upper_folder = new File(mStroage.getAbsolutePath() + "/" + firstFolder);
                if (upper_folder.length() == 0)
                    upper_folder.delete();
            }).run();
        }


    }









    public static class Gallery extends FileManager{

        /** For Save ResultCalender **/
        public Gallery(Context context){
            super(context, "Gallery");
        }

        /** Save ResGallery
         *          format : * ~~~~/ Gallery /year / month.jpg
         * **/
        public void saveResultBitmap(Bitmap bm, int year, int month) throws IOException {
            secondFolder = Integer.toString(year);
            try {
                File folder = new File(mStroage.getAbsolutePath() + "/" + firstFolder + "/" + secondFolder );

                if (!folder.exists())
                    folder.mkdirs();

                String filePath = folder.getAbsolutePath() + "/" + month + ".jpg";
                File tempFile = new File(filePath);
                tempFile.createNewFile();
                FileOutputStream out = new FileOutputStream(tempFile);

                bm.compress(Bitmap.CompressFormat.JPEG, 80, out);

                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public ArrayList<Integer> getPossibleGalleryYear(){
            ArrayList<Integer> ret = new ArrayList<>();

            File folder = new File(mStroage.getAbsolutePath()+ "/" + firstFolder );

            if (!folder.exists())
                return ret;

            File[] files = folder.listFiles();

            for (File file : files)
                ret.add(Integer.parseInt(file.getName()));
            return ret;
        }

        /** Load ImageUri for ResGallery **/
        public ArrayList<Uri> getResultGalleryBitmaps(int year){
            secondFolder = Integer.toString(year);
            ArrayList<Uri> ret = new ArrayList<>(Arrays.asList(null, null, null, null,
                    null, null, null, null,
                    null, null, null, null));
            File folder = new File(mStroage.getAbsolutePath()+ "/" + firstFolder + "/" + secondFolder);

            if (!folder.exists())
                return ret;



            File[] files = folder.listFiles();

            for (File file : files){
                String fileName = file.getName();
                fileName = fileName.substring(0, fileName.indexOf("."));
                ret.set(Integer.parseInt(fileName) -1, (Uri.fromFile(file.getAbsoluteFile())));
            }

            return ret;
        }


        /** Delete Saved File with folder **/
        public int deleteCalender(int year, int month) {
            secondFolder = Integer.toString(year);
            String filename = Integer.toString(month);

            int left = 0;
            File folder = new File(mStroage.getAbsolutePath() + "/" + firstFolder + "/" + secondFolder);
            if (!folder.exists())
                return 0;
            else {
                File[] files = folder.listFiles();
                for (File file : files){
                    if (file.getName().equals(filename + ".jpg"))
                        file.delete();
                    else
                        left++;
                }

                if (left <= 0)
                    folder.delete();
                return left;

            }
        }
    }
}
