package com.kharjul.smile;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private final int SELECT_PHOTO = 1;
    //private ImageView ivImage, ivImageProcessed;
    Mat src;
    ImageView imageView;
    Bitmap imageInbitmap;
    static int ACTION_MODE = 0;
    File mCascadeFile;
    CascadeClassifier haarCascade;
    String picturePath;
    private static final int  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,
                MainActivity.this, mOpenCVCallBack)) {
            Log.e("TEST", "Cannot connect to OpenCV Manager");
        }

        if (Build.VERSION.SDK_INT >= 23){
// Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }else
            {

            }
        }

        }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {


                }
                return;

            }
        }
    }



    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        private static final String TAG = "MyActivity";
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                   // src = new Mat(imageInbitmap.getHeight(), imageInbitmap.getWidth(), CvType.CV_8UC4);
                    Log.i(TAG, "OpenCV loaded successfully");
                    try {


                        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "cascade.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                            System.out.println("inside while loop");
                        }
                        is.close();
                        os.close();
                        System.out.println("cascade file path" + mCascadeFile.getAbsolutePath());
                        haarCascade = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        haarCascade.load(mCascadeFile.getAbsolutePath());
                        if (haarCascade.empty())
                        {
                            Log.i("Cascade Error","Failed to load cascade classifier");
                                    haarCascade = null;
                        }
                    }catch(Exception e)
                    {
                        Log.i("Cascade Error: ","Cascade not found");
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void load_image(View v)
    {
        // choose Intent.ACTION_VIEW for just viewing images
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));

       Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://media/internal/images/media"));
        //choose startActivity for just starting gallary
        //startActivity(intent);
       startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void Exit_smile(View v)
    {
        finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            System.out.println(selectedImage);
            System.out.println(filePathColumn[0]);
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            System.out.println(columnIndex);
             picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageView = (ImageView) findViewById(R.id.loadImage);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageInbitmap = BitmapFactory.decodeFile(picturePath);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Exit_Menu) {
            finish();
            System.exit(0);
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void findFaces(View v){
        src = new Mat(imageInbitmap.getHeight(), imageInbitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(imageInbitmap, src);
/*      Imgproc.blur(src, src, new Size(3, 3));
        Bitmap processedImage = Bitmap.createBitmap(src.cols(),
                src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, processedImage);
        imageView.setImageBitmap(processedImage);*/
        Mat mGray = Imgcodecs.imread(picturePath, 0);
        MatOfRect faces = new MatOfRect();
        if(haarCascade != null)
        {
            haarCascade.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(200, 200), new Size());

        }
        System.out.println("Length of faces array " + faces.toArray().length);
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(src, facesArray[i].tl(),  facesArray[i].br(), new Scalar(100), 3);

        System.out.println("Length of array " + facesArray.length);
        Bitmap processedImage = Bitmap.createBitmap(src.cols(),  src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, processedImage);
        imageView.setImageBitmap(processedImage);

        //return mRgba;
  /*      Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
       // i.putExtra("ACTION_MODE", MEAN_BLUR);
        startActivity(i);*/
    }
}
