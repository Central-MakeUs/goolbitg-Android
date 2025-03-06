package com.project.presentation.util

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object PhotoUtil {
    fun convertBitmapToUri(context: Context, bitmap: Bitmap): Uri?{
        return try{
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // 디렉토리가 없으면 생성
            val file = File(cachePath, "image.png")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()

            // 2. FileProvider를 통해 URI 얻기
            val contentUri: Uri? = FileProvider.getUriForFile(
                context,
                "com.project.goolbi.fileprovider",
                file
            )
            contentUri
        }catch (e: Exception){
            null
        }
    }

    // 사진
    /**
     * Android 10 이상부터 사진파일 만들기 용 URI 세팅
     *
     * @param contentResolver getContentResolver()
     * @param fileName        fileName (확장자 없으면 jpg)
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun getPhotoUriForAndroidQ(
        contentResolver: ContentResolver,
        folderName: String,
        fileName: String
    ): Uri? {
        val contentValues = ContentValues()
        // 확장자 추출
        var extension: String? = null
        val index = fileName.lastIndexOf('.')
        if (index != -1) {
            extension = fileName.substring(index)
        }
        if (extension != null) {
            // 확장자가 있는 경우
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        } else {
            // 확장자가 없는 경우
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.jpg")
        }
        if (extension == null) extension = "jpg"
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/$extension")
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + File.separator + folderName
        )
        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    /**
     * path(파일경로) -> uri
     *
     * @param contentResolver contentResolver
     * @param filePath        filePath (full path)
     */
    @SuppressLint("Range")
    fun pathToUri(contentResolver: ContentResolver, filePath: String): Uri? {
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
            "_data = '$filePath'", null, null
        )
        cursor!!.moveToNext()
        val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        if (!cursor.isClosed) cursor.close()
        return ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id.toLong()
        )
    }

    /**
     * uri -> 실제 파일 경로
     *
     * @param contentResolver getContentResolver()
     * @param contentUri      file uri
     */
    @SuppressLint("Range")
    fun getRealPathFromURI(contentResolver: ContentResolver, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)

        val cursor: Cursor? = contentResolver.query(contentUri, proj, null, null, null)
        cursor?.moveToNext()
        val path = cursor?.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
        val uri = Uri.fromFile(File(path))
        cursor?.close()
        return path
    }

    fun bitmapToBase64Str(photo: Bitmap?): String {
        return try {
            if (photo != null) {
                val outputStream = ByteArrayOutputStream()
                photo.compress(CompressFormat.PNG, 100, outputStream)
                val profileImage = outputStream.toByteArray()
                Base64.encodeToString(profileImage, Base64.NO_WRAP)
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun base64StrToBitmap(base64: String): Bitmap? {
        return try {
            val encodeByte: ByteArray =
                Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }

    // 뷰를 비트맵으로 전환
    fun getBitmapFromView(view: View?): Bitmap? {
        if (view == null) return null
        val bitmap = Bitmap.createBitmap(
            view.width, view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    // 인자로 받아오는 bitmap을 앨범에 저장하는 함수
    fun savePhotoInAlbum(context: Context, bitmap: Bitmap): Boolean {
        val state = Environment.getExternalStorageState()
        if (state == Environment.MEDIA_MOUNTED) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/InBody hi")
                }
            }

            val contentResolver = context.contentResolver
            val item = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            var fos: FileOutputStream? = null
            try {
                val pdf =
                    contentResolver.openFileDescriptor(
                        item!!,
                        "w",
                        null
                    ) ?: return false

                val strToByte: ByteArray = bitmapToByteArray(bitmap)!!
                fos = FileOutputStream(pdf.fileDescriptor)
                fos.write(strToByte)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(
                        MediaStore.Images.Media.IS_PENDING,
                        0
                    )
                    contentResolver.update(item, contentValues, null, null)
                }
                return true

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fos?.close()
            }
        }
        return false
    }

    /**
     * 공유하기(default share)
     */
    fun shareDefault(context: Context, view: View) {
        try {
            capturePhotoInCache(context, view)

            val filePath = context.cacheDir.toString()
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                File(filePath, "temporary_file.png")
            )
            val share = Intent(Intent.ACTION_SEND)
            share.putExtra(Intent.EXTRA_STREAM, uri)
            share.type = "image/*"
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val intent = Intent.createChooser(share, "Share Image")
            val resolvedIntentActivities =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager
                        .queryIntentActivities(
                            intent,
                            PackageManager.ResolveInfoFlags.of(MATCH_DEFAULT_ONLY.toLong())
                        )
                } else {
                    context.packageManager
                        .queryIntentActivities(intent, MATCH_DEFAULT_ONLY)
                }
            for (resolvedIntentInfo in resolvedIntentActivities) {
                val packageName = resolvedIntentInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {

        }
    }

    // 비트맵을 바이트Array로 변환
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    // 비트맵을 바이트Array로 변환
    fun bitmapToByteArray(bitmap: Bitmap, quality: Int): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }

    // 이미지 Downscaling을 위한 함수
    fun downscaleImage(context: Context, uri: Uri, targetSize: Int): ByteArray? {
        var inputStream: InputStream? = null
        try {
            // ContentResolver를 사용하여 URI로부터 InputStream을 가져옴
            val contentResolver = context.contentResolver
            inputStream = contentResolver.openInputStream(uri)

            // BitmapFactory를 사용하여 InputStream에서 Bitmap 로드
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)

            // 이미지의 가로 및 세로 크기를 계산하여 Downscaling할 비율 설정
            val imageWidth = options.outWidth
            val imageHeight = options.outHeight
            val scaleFactor = (imageWidth / targetSize).coerceAtLeast(imageHeight / targetSize)

            // 비율에 따라 Downscaling하여 Bitmap 생성
            options.inJustDecodeBounds = false
            options.inSampleSize = scaleFactor
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)

            // Bitmap을 ByteArray로 변환하여 서버로 전송
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(CompressFormat.JPEG, 80, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return null
    }

    fun uriToBitmap(context: Context, uri: String): Bitmap?{
        var inputStream: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            // ContentResolver를 사용하여 URI로부터 InputStream을 가져옴
            val contentResolver = context.contentResolver
            inputStream = contentResolver.openInputStream(Uri.parse(uri))

            // BitmapFactory를 사용하여 InputStream에서 Bitmap 로드
            val options = BitmapFactory.Options()

            // 압축된 Bitmap을 가져옴
            bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return bitmap
    }

    // Exif 정보에서 이미지 회전 각도 가져오기
    private fun getExifOrientation(context: Context, uri: Uri): Int {
        var orientation = 0
        try {
            val projection = arrayOf(MediaStore.Images.ImageColumns.ORIENTATION)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(projection[0])
                    orientation = it.getInt(columnIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return orientation
    }

    // 이미지의 압축 비율을 계산하는 함수
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // 원본 이미지의 높이와 너비
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 2
        if (height > reqHeight || width > reqWidth) {
            // 요청된 높이와 너비보다 원본이 큰 경우 압축 비율 계산
            val halfHeight = height / 2
            val halfWidth = width / 2

            // inSampleSize를 2의 제곱수로 설정하여 메모리 사용을 최적화
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private const val MAX_WIDTH = 512
    private const val MAX_HEIGHT = 512

    fun decodeBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        // 첫 번째 스트림으로 이미지 크기 정보 읽기
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        }

        // inSampleSize 계산
        options.inSampleSize = calculateInSampleSize(options)
        options.inJustDecodeBounds = false

        // 두 번째 스트림으로 실제 비트맵 디코딩
        val bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        }

        // Exif 정보로 회전 처리
        context.contentResolver.openInputStream(uri)?.use { exifInput ->
            val exif = ExifInterface(exifInput)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
            return if (bitmap != null) {
                rotateBitmap(bitmap, orientation)
            } else {
                null
            }
        }

        return bitmap
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > MAX_HEIGHT || width > MAX_WIDTH) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= MAX_HEIGHT && halfWidth / inSampleSize >= MAX_WIDTH) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(uri) ?: return null

        val exif = if (Build.VERSION.SDK_INT > 23) {
            ExifInterface(input)
        } else {
            ExifInterface(uri.path!!)
        }

        return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // 비트맵 회전
    // 휴대폰마다 회전되어서 찍히는 경우가 있어서, 해당 함수를 통해 정방향으로 맞춰줌
    fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL ->
                return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL ->
                matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 ->
                matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        return try {
            val bmRotated =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    fun capturePhotoInCache(context: Context, view: View) {
        try {
            val icon: Bitmap = getBitmapFromView(view)!!
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/png"
            val bytes = ByteArrayOutputStream()
            icon.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val f = File(context.cacheDir.toString() + "/temporary_file.png")
            var fo: FileOutputStream? = null
            try {
                fo = FileOutputStream(f)
                fo.write(bytes.toByteArray())
                fo.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fo?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareImage(context: Context, imageUrl: String) {
        // Glide를 이용하여 비트맵으로 이미지 로드
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    // 비트맵을 캐시 디렉토리에 임시 파일로 저장
                    val cachePath = File(context.cacheDir, "images")
                    cachePath.mkdirs()
                    val file = File(cachePath, "shared_image.png")
                    FileOutputStream(file).use { out ->
                        resource.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    // FileProvider를 이용해 공유 가능한 URI 생성 (여기서 "com.your.package.fileprovider"는 실제 패키지명으로 변경)
                    val uri = FileProvider.getUriForFile(
                        context,
                        "com.project.goolbi.fileprovider",
                        file
                    )
                    // 공유 인텐트 생성
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "image/png"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "이미지 공유"))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // 필요 시 처리
                }
            })
    }
}
