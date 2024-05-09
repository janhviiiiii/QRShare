package com.example.qrshare;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


//not using
//not using
//not using

public class Generate_Result extends AppCompatActivity {

    private ImageView img_gen_qr_code;
    private ImageButton btn_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_result);


        img_gen_qr_code= findViewById(R.id.img_gen_qr_code);
        btn_download= findViewById(R.id.btn_download);

//        //Retrieve rhe bitmap from intent extras
//        Bundle extras = getIntent().getExtras();
//        if(extras != null && extras.containsKey("img_gen_qr_code")){
//            Bitmap bitmap = extras.getParcelable("img_gen_qr_code");
//            if(bitmap != null){
//                img_gen_qr_code.setImageBitmap(bitmap);
//            }
//        }
//
//        Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("img_gen_qr_code");
//
//        img_gen_qr_code.setImageBitmap(bitmap);
//            try{
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), bitmap);
//                img_gen_qr_code.setImageBitmap(bitmap);
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }




    }
}