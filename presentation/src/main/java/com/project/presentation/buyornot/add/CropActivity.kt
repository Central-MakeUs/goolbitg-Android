package com.project.presentation.buyornot.add

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener
import com.project.presentation.R
import com.project.presentation.databinding.ActivityCropBinding
import com.project.presentation.util.PhotoUtil.convertBitmapToUri

class CropActivity : AppCompatActivity(),
    OnSetImageUriCompleteListener,
    CropImageView.OnCropImageCompleteListener {
    private var _binding: ActivityCropBinding? = null
    private val binding get() = _binding!!

    private var options: CropImageOptions? = null
    private val openPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        binding.cropImageView.setImageUriAsync(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        val onApplyWindowInsetsListener =
            OnApplyWindowInsetsListener { _: View?, windowInsets: WindowInsetsCompat ->
                val insets =
                    windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                (binding.root.layoutParams as FrameLayout.LayoutParams).apply {
                    topMargin = insets.top
                    bottomMargin = insets.bottom
                }
                windowInsets
            }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, onApplyWindowInsetsListener)

        setOptions()

        initListener()

        val uri = intent.getStringExtra(TARGET_PHOTO_URI_KEY)
        if (uri == null) finish()

        binding.cropImageView.setImageUriAsync(Uri.parse(uri))

        binding.cropImageView.setOnSetImageUriCompleteListener(this)
        binding.cropImageView.setOnCropImageCompleteListener(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.cropImageView.setOnSetImageUriCompleteListener(null)
        binding.cropImageView.setOnCropImageCompleteListener(null)
        _binding = null
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {

    }

    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        if (result.error == null) {
            val imageBitmap = if (binding.cropImageView.cropShape == CropImageView.CropShape.OVAL) {
                result.bitmap?.let(CropImage::toOvalBitmap)
            } else {
                result.bitmap
            }
            if (imageBitmap != null) {
                val localUri = convertBitmapToUri(context = this@CropActivity, bitmap = imageBitmap)
                if(localUri != null){
                    val intent = Intent().apply{
                        putExtra(CROP_IMAGE_RESULT_URI, localUri.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
                Log.d("TAG", "onCropImageComplete: ${localUri.toString()}")
            } else {
                finish()
            }
        }
    }

    private fun setOptions() {
        binding.cropImageView.cropRect = Rect(100, 300, 500, 1200)
        this.options = CropImageOptions(
            fixAspectRatio = true,
            guidelines = CropImageView.Guidelines.ON_TOUCH,
            scaleType = CropImageView.ScaleType.CENTER_INSIDE
        )

        binding.cropImageView.setImageCropOptions(options!!)
    }

    private fun initListener() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
            tvCrop.setOnClickListener {
                cropImageView.croppedImageAsync()
            }
        }
    }

    companion object {
        const val TARGET_PHOTO_URI_KEY = "target_photo_uri_key"
        const val CROP_IMAGE_RESULT_URI = "crop_image_result_uri"
    }
}
