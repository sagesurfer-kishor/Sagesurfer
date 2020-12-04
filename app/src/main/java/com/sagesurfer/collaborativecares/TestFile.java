package com.sagesurfer.collaborativecares;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.modules.selfcare.CareUploadActivity;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.snack.ShowSnack;

public class TestFile extends AppCompatActivity {
    ImageView img;
    Button btnl;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        requestWritePermission();


        btnl = findViewById(R.id.testClick);

        btnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), 1);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestWritePermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


    @Override
//Intent { dat=content://com.android.externalstorage.documents/document/primary:Mobile stolen issues.docx flg=0x1 }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            Uri uri = data.getData();

            String filePath = uri.getPath();
        }
    }

}
