package com.app.besmartybd.memegenerator;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST=1;
    private static final int RESULT_LOAD_IMAGE=2;

    Button load,save,share,go;
    TextView textView1,textView2;
    EditText editText1,editText2;
    ImageView imageView;
    String currentImage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);

            }
        }
        else {

        }

        load=findViewById(R.id.load);
        save=findViewById(R.id.save);
        share=findViewById(R.id.share);
        go=findViewById(R.id.go);

        textView1=findViewById(R.id.textView1);
        textView2=findViewById(R.id.textView2);

        editText1=findViewById(R.id.editText1);
        editText2=findViewById(R.id.editText2);

        imageView=findViewById(R.id.imageView);

        save.setEnabled(false);
        share.setEnabled(false);

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,RESULT_LOAD_IMAGE);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View content= findViewById(R.id.lay);
                Bitmap bitmap= getScreenshot(content);
                currentImage ="meme" + System.currentTimeMillis()+".png";
                store(bitmap,currentImage);
                share.setEnabled(true);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(currentImage);

            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView1.setText(editText1.getText().toString());
                textView2.setText(editText2.getText().toString());

                editText1.setText("");
                editText2.setText("");
                save.setEnabled(true);
            }
        });

    }

    public  static Bitmap getScreenshot(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap= Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(true);
        return bitmap;
    }
    public void store(Bitmap bm,String filename){
        String dirpath= Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Meme";
        File dir =new File(dirpath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dirpath,filename);
        try{
            FileOutputStream fos=new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();;
            fos.close();
            Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(this,"Error Saving",Toast.LENGTH_LONG).show();
        }
    }

    private  void shareImage(String fileName){
        String dirPath= Environment.getExternalStorageDirectory().getAbsolutePath() +"/Meme";
        Uri uri=Uri.fromFile(new File(dirPath, fileName));
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try{
            startActivity(Intent.createChooser(intent, "Share via"));
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "No Sharing app Found",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK  && null !=data){
            Uri selectedImage=data.getData();
            String[] filePathColumn= {MediaStore.Images.Media.DATA};
            Cursor cursor= getContentResolver().query(selectedImage, filePathColumn,null,null,null);
            cursor.moveToFirst();
            int ColumnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturepath = cursor.getString(ColumnIndex);
            cursor.close();
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturepath));
            save.setEnabled(true);
            share.setEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){

                    }
                }
                else {
                    Toast.makeText(this,"No Permission Granted",Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }
}
