package com.example.storagetest

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storagetest.databinding.ActivityMainBinding
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        binding.btnTest.setOnClickListener {
            val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc")
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    Log.d("Daisy", "image uri is $uri")
                }
                cursor.close()
            }
        }

        binding.btnBrowseAlbum.setOnClickListener {
            val intent = Intent(this, BrowseAlbumActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddPhoto.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)
            val displayName = "hsf.jpg"
            val mineType ="image/jpeg"
            val compressFormat = Bitmap.CompressFormat.JPEG

            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mineType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            } else {
                //用的是路径拼接
                values.put(MediaStore.MediaColumns.DATA, "${Environment.getExternalStorageDirectory().path}/" +
                        "${Environment.DIRECTORY_DCIM}/${displayName}")
            }
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(compressFormat, 100, outputStream)
                    outputStream.close()
                    Toast.makeText(this, "Add bitmap to album succeeded", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnDownload.setOnClickListener {
            val fileUrl = "http://guolin.tech/android.txt"
            val fileName = "android_hsf.txt"
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Toast.makeText(this, "你必须使用大于或等于Android10的设备", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            thread {
                try {
                    val url = URL(fileUrl)
                    val connection = (url.openConnection() as HttpURLConnection).also {
                        it.requestMethod = "GET"
                        it.connectTimeout = 8000
                        it.readTimeout = 8000
                    }
                    val inputStream = connection.inputStream
                    val bis = BufferedInputStream(inputStream)
                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    if (uri != null) {
                        val outputStream = contentResolver.openOutputStream(uri)
                        if (outputStream != null) {
                            val bos = BufferedOutputStream(outputStream)
                            val buffer = ByteArray(1024)
                            var bytes = bis.read(buffer)
                            while (bytes >= 0) {
                                bos.write(buffer, 0, bytes)
                                bos.flush()
                                bytes = bis.read(buffer)
                            }
                            bos.close()
                            runOnUiThread {
                                Toast.makeText(this, "下载完毕", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    bis.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun checkPermission() {
        val permissionsToRequire = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequire.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequire.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionsToRequire.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequire.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You must allow all the permissions.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}