package com.example.adaptive;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StoreUtils {
    /**
     * 我目录在myFile文件的情况下，会自己创建一个文件myFile
     */
    public static String getFilePath(Context context) {

        /* getFileDir()，得到的路径 ：/data/user/0/com.example.adaptive/files
           getCacheDir()，得到的路径：/data/user/0/com.example.adaptive/cache
         */

        //获取files根目录
        File fileDir = context.getFilesDir();
        Log.d("Daisy", "files的路径：" + fileDir);

        //获取文件（这里的new实际上并不是创建了一个真实的文件，而是一个概念上的文件，用于后面写入文件时用到）
        File myFile = new File(fileDir, "myFile");
        return myFile.getAbsolutePath();
    }

    /**
     * 如果前后两次调用该方法，writeContent的值会覆盖原来的
     */
    //写入文件
    public static void writeFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        try {
            File file = new File(filePath);

            //这一步执行，才创建了文件，我估计是要打开一个输出流，发现没有文件，所以就创建了
            //这里的第二个参数，代表是否追加内容，默认是false的
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            String writeContent = "I'm hsf\n";
            bos.write(writeContent.getBytes());  //写入前，要将String转为byte[]

            //如果以下两个不执行，那么将不会往文件中写入内容
            //bos.flush不执行，在这里没关系，我记得close在关闭时会将内容写入先
            bos.flush();
            bos.close();

        } catch (Exception e) {
            Log.d("Daisy", e.toString());
        }
    }

    //从文件读取
    public static void readFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        try {
            File file = new File(filePath);

            /* 如果文件myFile文件还没创建，那么这里的读取会报以下错误
            java.io.FileNotFoundException: /data/user/0/com.example.adaptive/files/myFile: open failed: ENOENT (No such file or directory)
             */
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fileInputStream);
            byte[] readContent = new byte[1024];

            int readLen = 0;
            while (readLen != -1) {
                readLen = bis.read(readContent, 0, readContent.length);
                if (readLen > 0) {
                    String content = new String(readContent);
                    Log.d("test", "read content:" + content.substring(0, readLen));
                }
            }
            fileInputStream.close();

            /*int realLen = bis.read(readContent, 0, readContent.length);
            while (realLen != -1) {
                String content = new String(readContent);
                Log.d("Daisy", "read content:" + content.substring(0, realLen));
                realLen = bis.read(readContent, 0, readContent.length);
            }*/
            fileInputStream.close();

        } catch (IOException e) {
            Log.d("Daisy", "异常 ：" + e.toString());
        }
    }

    public static void usePackagingOpenFileOutput(Context context) {
        try {
            //这一步，file文件夹下没有指定文件，或者压根就还没有files，那么会在files下创建myFile文件
            FileOutputStream fos = context.openFileOutput("myFile", MODE_PRIVATE);

            BufferedOutputStream bos = new BufferedOutputStream(fos);
            String writeContent = "I'm hsf\n";
            bos.write(writeContent.getBytes());

            bos.flush();
            bos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void usePackingOpenFileInput(Context context) {
        try {
            /* 如果files下没有myFile，那么会报以下异常
            java.io.FileNotFoundException: /data/user/0/com.example.adaptive/files/myFile: open failed: ENOENT (No such file or directory)
             */
            FileInputStream fis = context.openFileInput("myFile");
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] readContent = new byte[1024];

            int realLen = bis.read(readContent, 0, readContent.length);
            while (realLen != -1) {
                String content = new String(readContent);
                Log.d("Daisy", "read content:" + content.substring(0, realLen));
                realLen = bis.read(readContent, 0, readContent.length);
            }
            fis.close();

        } catch (IOException e) {
            Log.d("Daisy", "异常：" + e);
        }
    }

    public static void testSp(String fileName, String key, String value, Context context) {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(key) || TextUtils.isEmpty(value))
            return;

        SharedPreferences sp = context.getSharedPreferences(fileName, MODE_PRIVATE);

        sp.edit().putString(key, value).commit();

        String myValue = sp.getString(key, "");

        Log.d("Daisy", "我存的是啥：" + myValue);
    }

    /**
     * /data/user/0/com.example.adaptive/databases/ddd
     */
    public static void testDb(Context context) {
        //单纯调用这行代码的话，只会创建一个databases的文件
        Log.d("Daisy", context.getDatabasePath("ddd").getAbsolutePath());
    }

    /**
     * /data/user/0/com.example.adaptive/code_cache
     */
    public static void testGetCodeCacheDir(Context context) {
        Log.d("Daisy", context.getCodeCacheDir().getAbsolutePath());
    }

    /**
     * /data/user/0/com.example.adaptive
     * API>=24
     */
    public static void testGetDataDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d("Daisy", context.getDataDir().getAbsolutePath());
        }
    }
}
