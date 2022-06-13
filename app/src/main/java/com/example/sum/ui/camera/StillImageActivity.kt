package com.example.sum.ui.camera

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sum.MainActivity
import com.example.sum.R
import com.example.sum.utility.mlkit.BitmapUtils
import com.example.sum.utility.mlkit.GraphicOverlay
import com.example.sum.utility.mlkit.TextRecognitionProcessor
import com.example.sum.utility.mlkit.VisionImageProcessor
import com.example.sum.utility.put
import com.google.android.gms.common.annotation.KeepName
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.io.IOException


/** Activity demonstrating different image detector features with a still image from camera.  */
@KeepName
class StillImageActivity : AppCompatActivity() {

    lateinit var adapter: GroupAdapter<GroupieViewHolder>
    lateinit var spinner: Spinner
    private var preview: ImageView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var selectedMode =
        TEXT_RECOGNITION_LATIN
    private var selectedSize: String? =
        SIZE_SCREEN
    private var isLandScape = false
    private var imageUri: Uri? = null
    // Max width (portrait mode)
    private var imageMaxWidth = 0
    // Max height (portrait mode)
    private var imageMaxHeight = 0
    private var imageProcessor: VisionImageProcessor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_still_image)
        adapter = GroupAdapter<GroupieViewHolder>()
        spinner  = findViewById<Spinner>(R.id.spinner);

        findViewById<View>(R.id.select_image_button)
            .setOnClickListener { view: View ->
                // Menu for selecting either: a) take new photo b) select from existing
                val popup = PopupMenu(this@StillImageActivity, view)
                popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                    val itemId =
                        menuItem.itemId
                    if (itemId == R.id.select_images_from_local) {
                        startChooseImageIntentForResult()
                        //tags = emptyList();
                        return@setOnMenuItemClickListener true
                    } else if (itemId == R.id.take_photo_using_camera) {
                        startCameraIntentForResult()
                        //tags = emptyList();
                        return@setOnMenuItemClickListener true
                    }
                    false
                }

                val inflater = popup.menuInflater
                inflater.inflate(R.menu.camera_button_menu, popup.menu)
                popup.show()
            }
        preview = findViewById(R.id.preview)
        graphicOverlay = findViewById(R.id.graphic_overlay)

        isLandScape =
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (savedInstanceState != null) {
            imageUri =
                savedInstanceState.getParcelable(KEY_IMAGE_URI)
            imageMaxWidth =
                savedInstanceState.getInt(KEY_IMAGE_MAX_WIDTH)
            imageMaxHeight =
                savedInstanceState.getInt(KEY_IMAGE_MAX_HEIGHT)
            selectedSize =
                savedInstanceState.getString(KEY_SELECTED_SIZE)
        }

        findViewById<View>(R.id.select_search_button)
            .setOnClickListener { view: View ->

                val sharedPreference =  this.getSharedPreferences("SCHEDULE", Context.MODE_PRIVATE)
                sharedPreference.apply {
                    put("ScheduleTime",19)
                    put("ScheduleName","Largo do Cardal#")
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        val rootView = findViewById<View>(R.id.root)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    imageMaxWidth = rootView.width
                    imageMaxHeight =
                        rootView.height - findViewById<View>(R.id.control).height
                    if (SIZE_SCREEN == selectedSize) {
                        tryReloadAndDetectInImage()
                    }
                }
            })



    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        createImageProcessor()
        tryReloadAndDetectInImage()
        if(tags.size>0) {
            /*          tags.forEach {
                          adapter.add(ChatLabelButton(it))

                      }*/
            var spinnerAdapter = ArrayAdapter(
                this,
                androidx.transition.R.layout.support_simple_spinner_dropdown_item,
                tags
            )
            spinner.adapter = spinnerAdapter
        }
    }

    public override fun onPause() {
        super.onPause()
        imageProcessor?.run {
            this.stop()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run {
            this.stop()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            KEY_IMAGE_URI,
            imageUri
        )
        outState.putInt(
            KEY_IMAGE_MAX_WIDTH,
            imageMaxWidth
        )
        outState.putInt(
            KEY_IMAGE_MAX_HEIGHT,
            imageMaxHeight
        )
        outState.putString(
            KEY_SELECTED_SIZE,
            selectedSize
        )
    }

    private fun startCameraIntentForResult() { // Clean up last time's image
        imageUri = null
        preview!!.setImageBitmap(null)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(
                takePictureIntent,
                REQUEST_IMAGE_CAPTURE
            )
        }
    }

    private fun startChooseImageIntentForResult() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_CHOOSE_IMAGE
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            tryReloadAndDetectInImage()
        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            imageUri = data!!.data
            tryReloadAndDetectInImage()
            if(tags.size>0) {
                var spinnerAdapter = ArrayAdapter(
                    this,
                    androidx.transition.R.layout.support_simple_spinner_dropdown_item,
                    tags
                )
                spinner.adapter = spinnerAdapter
            }
            var aux =1

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun tryReloadAndDetectInImage() {
        Log.d(
            TAG,
            "Try reload and detect image"
        )
        try {
            if (imageUri == null) {
                return
            }

            if (SIZE_SCREEN == selectedSize && imageMaxWidth == 0) {
                // UI layout has not finished yet, will reload once it's ready.
                return
            }

            val imageBitmap = BitmapUtils.getBitmapFromContentUri(contentResolver, imageUri) ?: return
            // Clear the overlay first
            graphicOverlay!!.clear()

            preview!!.setImageBitmap(imageBitmap)
            if (imageProcessor != null) {
                graphicOverlay!!.setImageSourceInfo(
                    imageBitmap.width, imageBitmap.height, /* isFlipped= */false
                )

                imageProcessor!!.processBitmap(imageBitmap, graphicOverlay)


            } else {
                Log.e(
                    TAG,
                    "Null imageProcessor, please check adb logs for imageProcessor creation error"
                )
            }
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Error retrieving saved image"
            )
            imageUri = null
        }
    }

    private fun createImageProcessor() {
        try {
            when (selectedMode) {
                TEXT_RECOGNITION_LATIN ->
                    imageProcessor =
                        TextRecognitionProcessor(this, TextRecognizerOptions.Builder().build())

                else -> Log.e(
                    TAG,
                    "Unknown selectedMode: $selectedMode"
                )
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Can not create image processor: $selectedMode",
                e
            )
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }


    companion object {

        var tags = listOf<String>();


        private const val TAG = "StillImageActivity"
        private const val TEXT_RECOGNITION_LATIN = "Text Recognition Latin"

        private const val SIZE_SCREEN = "w:screen" // Match screen width
        private const val KEY_IMAGE_URI = "com.example.sum.utility.mlkit.KEY_IMAGE_URI"
        private const val KEY_IMAGE_MAX_WIDTH = "com.example.sum.utility.mlkit.KEY_IMAGE_MAX_WIDTH"
        private const val KEY_IMAGE_MAX_HEIGHT = "com.example.sum.utility.mlkit.KEY_IMAGE_MAX_HEIGHT"
        private const val KEY_SELECTED_SIZE = "com.example.sum.utility.mlkit.KEY_SELECTED_SIZE"
        private const val REQUEST_IMAGE_CAPTURE = 1001
        private const val REQUEST_CHOOSE_IMAGE = 1002
    }

}


