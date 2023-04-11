package com.example.storagetest

import android.content.ContentUris
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.storagetest.databinding.ActivityBrowseAlbumBinding
import kotlin.concurrent.thread

class BrowseAlbumActivity : AppCompatActivity() {

    private val imageList = ArrayList<Image>()

    private val checkedImages = HashMap<String, Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBrowseAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pickFiles = intent.getBooleanExtra("pick_files", false)
        title = if (pickFiles) "Pick Images" else "Browse Album"
        binding.recyclerView.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                //为什么前面要用object，不使用lambda，就是为了让这里可以使用this
                binding.recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                val column = 3
                val imageSize = binding.recyclerView.width / column
                val adapter = AlbumAdapter(this@BrowseAlbumActivity, imageList,
                    checkedImages, imageSize, pickFiles)
                binding.recyclerView.layoutManager = GridLayoutManager(this@BrowseAlbumActivity, column)
                binding.recyclerView.adapter = adapter
                loadImages(adapter)
                return false
            }
        })


    }

    private fun loadImages(adapter: AlbumAdapter) {
        thread {
            val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc")
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageList.add(Image(uri, false))
                }
                cursor.close()
            }
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }
}