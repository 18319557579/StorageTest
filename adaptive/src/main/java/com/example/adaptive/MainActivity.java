package com.example.adaptive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.adaptive.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding;
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这里只是读取，但是在内部存储的目录下，还是创建了一个files文件夹（但是文件myFiles依旧没创建）
                String path = StoreUtils.getFilePath(MainActivity.this);
                //路径：/data/user/0/com.example.adaptive/files/myFile
                Log.d("Daisy", "获取到的路径:" + path);

                StoreUtils.writeFile(path);
            }
        });

        binding.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = StoreUtils.getFilePath(MainActivity.this);

                StoreUtils.readFile(path);
            }
        });

        binding.btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreUtils.usePackagingOpenFileOutput(MainActivity.this);
            }
        });

        binding.btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreUtils.usePackingOpenFileInput(MainActivity.this);
            }
        });

        binding.btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreUtils.testSp("sp_test", "hsf", "黄绍飞", MainActivity.this);
            }
        });

        binding.btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreUtils.testDb(MainActivity.this);
            }
        });

        binding.btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreUtils.testGetCodeCacheDir(MainActivity.this);
            }
        });

        binding.btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreUtils.testGetDataDir(MainActivity.this);
            }
        });



        binding.btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastPic = ExternalUtils.getImagePath(MainActivity.this);
//                String lastPic = "/storage/emulated/0/Pictures/Screenshots/Screenshot_2023-04-17-18-23-34-18_651b5c74a671b75cef00c5b91170e3c1.jpg";

                //用Glide可以展示外部存储的图片
                /* 如果没有外部存储权限，则会报以下错
                 open failed: EACCES (Permission denied)
                 */
                Glide.with(MainActivity.this).load(lastPic).into(binding.ivShow);
            }
        });

    }


    /**
     * 申请外部存储权限
     */
    public void checkPermission() {
        List<String> permissionsToRequire = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequire.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        String[] ppp  = permissionsToRequire.toArray(new String[permissionsToRequire.size()]);
        if (!permissionsToRequire.isEmpty()) {
            ActivityCompat.requestPermissions(this, ppp, 0);
        }
    }
}