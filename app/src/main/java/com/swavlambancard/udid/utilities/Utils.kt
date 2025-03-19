package com.swavlambancard.udid.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.*
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.swavlambancard.udid.R
import com.swavlambancard.udid.callBack.DialogCallback
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.ui.PdfViewerActivity
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList
import kotlin.math.*

object EncryptionModel {

    private const val DATA_KEY = "bJG/tDlHg7oV41Mgx5+ZV62t6WWUMGMMd1v1z07YgYI="

    fun aesEncrypt(value: String, key: String? = null): String {
        val sKey = key ?: DATA_KEY

        // Generate random salt (8 bytes)
        val salt = ByteArray(8)
        SecureRandom().nextBytes(salt)

        // Generate key and IV using the same derivation process
        val (derivedKey, iv) = deriveKeyAndIv(sKey, salt)

        return try {
            // Create AES cipher instance
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKey = SecretKeySpec(derivedKey, "AES")
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

            // Encrypt the data
            val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))

            // Format output: salt:encryptedBase64:iv
            val components = listOf(
                salt.toHexString(),
                Base64.encodeToString(encrypted, Base64.NO_WRAP),
                iv.toHexString()
            )

            Base64.encodeToString(
                components.joinToString(":").toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun aesDecrypt(value: String, key: String? = null): String {
        val sKey = key ?: DATA_KEY

        return try {
            // Decode base64 input
            val decodedInput = Base64.decode(value, Base64.NO_WRAP).toString(Charsets.UTF_8)
            val components = decodedInput.split(":")
            if (components.size != 3) return ""

            // Extract components
            val salt = components[0].hexToBytes()
            val encryptedData = Base64.decode(components[1], Base64.NO_WRAP)
            val iv = components[2].hexToBytes()

            // Generate key and IV using the same derivation process
            val (derivedKey, _) = deriveKeyAndIv(sKey, salt)

            // Create AES cipher instance for decryption
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKey = SecretKeySpec(derivedKey, "AES")
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

            // Decrypt the data
            val decrypted = cipher.doFinal(encryptedData)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun deriveKeyAndIv(key: String, salt: ByteArray): Pair<ByteArray, ByteArray> {
        val keyMaterial = key.toByteArray(Charsets.UTF_8)
        val saltedData = mutableListOf<Byte>()
        var dx = ByteArray(0)

        while (saltedData.size < 48) {
            val context = dx + keyMaterial + salt
            dx = MessageDigest.getInstance("MD5").digest(context)
            saltedData.addAll(dx.toList())
        }

        val derivedKey = saltedData.take(32).toByteArray() // 32 bytes for AES-256
        val iv = saltedData.drop(32).take(16).toByteArray() // 16 bytes for IV
        return Pair(derivedKey, iv)
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { String.format("%02x", it) }
    }

    private fun String.hexToBytes(): ByteArray {
        val result = ByteArray(length / 2)
        for (i in indices step 2) {
            result[i / 2] =
                ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
        }
        return result
    }
}


object Utility {
    private const val PREF_NAME = "AppPreferences"
    private const val THEME_KEY = "ThemeMode"
    fun getMessageType(messageType: String): String {
        return when (messageType) {
            AppConstants.TEXT -> {
                messageType
            }

            AppConstants.IMAGE -> {
                "\uD83D\uDCF7 $messageType"
            }

            AppConstants.VIDEO -> {
                "\uD83C\uDFA5 $messageType"
            }

            AppConstants.AUDIO -> {
                messageType
            }

            else -> {
                messageType
            }
        }
    }

    fun setLocale(activity: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        savePreferencesString(activity, "locale", languageCode)
    }

    fun rotateDrawable(drawable: Drawable?, angle: Float): Drawable {
        drawable?.mutate() // Mutate the drawable to avoid affecting other instances

        val rotateDrawable = RotateDrawable()
        rotateDrawable.drawable = drawable
        rotateDrawable.fromDegrees = 0f
        rotateDrawable.toDegrees = angle
        rotateDrawable.level = 10000 // Needed to apply the rotation

        return rotateDrawable
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    fun convertIntoKms(miles: Double): Double {
        return 1.609 * miles
    }

    fun convertIntoMiles(km: Double): Double {
        return km / 1.609
    }

//    fun showImageDialog(context: Context,image:String) {
//        val dialog = Dialog(context,android.R.style.Theme_Translucent_NoTitleBar)
//        dialog.setContentView(R.layout.imagedialog)
//        dialog.setCancelable(true)
//        dialog.setCanceledOnTouchOutside(true)
//        dialog.window!!.setLayout(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        dialog.window!!.setGravity(Gravity.CENTER)
//        val lp: WindowManager.LayoutParams = dialog.window!!.attributes
//        lp.dimAmount = 0.75f
//        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window!!.attributes = lp
//        val dialogImage = dialog.findViewById(R.id.ivImg) as ImageView
//        val ivClose = dialog.findViewById(R.id.ivClose) as ImageView
//
//        Log.d("image",image)
//
//        Glide.with(context).load(image)
//            .placeholder(R.color.black)
//            .into(dialogImage)
//
//        ivClose.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//    }

    fun dateConvertToEndDate(dateString: String): String {
        return try {
            val originalFormat: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            val targetFormat: DateFormat = SimpleDateFormat("dd MMM, yyyy")
            val date: Date = originalFormat.parse(dateString)

            targetFormat.format(date).uppercase(Locale.getDefault())
        } catch (e: Exception) {
            ""
        }
    }

    fun convertDate(inputDate: String): String {
        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Parse the input date and format it into the desired format
        val date: Date = inputFormat.parse(inputDate)!!
        return outputFormat.format(date)
    }

    fun setBlueUnderlinedText(textView: TextView, text: String) {
        val spannableString = SpannableString(text)

        // Set text color to blue
        spannableString.setSpan(ForegroundColorSpan(Color.BLUE), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Underline the text
        spannableString.setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannableString
    }

    fun baseToUrl(context: Context, baseString: String) {
        if (baseString.isNullOrEmpty()) {
            Toast.makeText(context, "No document found", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val fileUri: Uri?
            val mimeType: String?

            when {
                baseString.startsWith("data:image/") -> {
                    val tempFile = decodeBase64ToFile(context, baseString, "image.jpg")
                    fileUri = tempFile?.let {
                        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider",
                            it
                        )
                    }
                    mimeType = "image/*"
                }
                baseString.startsWith("data:application/pdf;base64,") -> {
                    val tempFile = decodeBase64ToFile(context, baseString, "document.pdf")
                    fileUri = tempFile?.let {
                        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider",
                            it
                        )
                    }
                    mimeType = "application/pdf"
                }
                baseString.startsWith("content://") || baseString.startsWith("file://") -> {
                    // Local file, no need to copy it again
                    fileUri = Uri.parse(baseString)
                    mimeType = context.contentResolver.getType(fileUri) ?: "*/*"
                }
                else -> {
                    Toast.makeText(context, "Invalid file format", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            if (fileUri != null) {
                val intent = Intent(context, PdfViewerActivity::class.java).apply {
                    putExtra("fileUri", fileUri.toString())
                    putExtra("mimeType", mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Failed to process file", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error processing file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun decodeBase64ToFile(context: Context,base64String: String, fileName: String): File? {
        return try {
            val cleanBase64 = base64String.substringAfter(",") // Remove MIME prefix
            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val file = File(context.cacheDir, fileName)

            FileOutputStream(file).use { it.write(decodedBytes) }
            file
        } catch (e: Exception) {
            Log.e("Base64Conversion", "Failed to decode file", e)
            null
        }
    }
    fun openFile(url: String,context: Context) {
        Log.d("document:",url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No browser found to open this link", Toast.LENGTH_SHORT).show()
        }
    }

    fun dateConvertToFormat(dateString: String?): String {
        return try {
            val originalFormat: DateFormat =
                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val targetFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
            val date: Date = originalFormat.parse(dateString)

            targetFormat.format(date).uppercase(Locale.getDefault())
        } catch (e: Exception) {
            ""
        }
    }

    fun convertToArrayList(input: String): ArrayList<String> {
        return ArrayList(input.split(","))
    }

    fun filterMatchingIds(idList: ArrayList<String>, people: List<DropDownResult>): ArrayList<DropDownResult> {
        return ArrayList(people.filter { it.id in idList })
    }


    fun getYearFromDate(dateString: String?, format: String= "dd/MM/yyyy"): Int? {
        if (dateString.isNullOrBlank()) return null // Check if date is null or empty

        return try {
            // For Android API 26+ (Oreo and above)
            val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format))
            date.year
        } catch (e: Exception) {
            try {
                // For older Android versions
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                val date: Date? = sdf.parse(dateString)
                val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
                date?.let { yearFormat.format(it).toInt() }
            } catch (ex: Exception) {
                null // Return null if parsing fails
            }
        }
    }

    fun filterDropDownResultsAboveSelected(yearList: List<DropDownResult>, selectedDate: String): ArrayList<DropDownResult> {
        val selectedYear = getYearFromDate(selectedDate) ?: return arrayListOf() // Get year from date

        return ArrayList(yearList.filter { it.name.toIntOrNull() ?: 0 >= selectedYear }) // Convert to ArrayList
    }


    fun dateConvertToFormatYYYYMMDD(dateString: String?): String {
        return try {
            val originalFormat: DateFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val targetFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date: Date = originalFormat.parse(dateString)

            targetFormat.format(date).uppercase(Locale.getDefault())
        } catch (e: Exception) {
            ""
        }
    }

    fun setMandatoryField(textView: TextView, label: String) {
        val fullText = "$label *"
        val spannableString = SpannableString(fullText)

        // Set asterisk (*) to red color
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF3D00")),
            fullText.length - 1, fullText.length,  // Apply only to the asterisk
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableString
    }


    fun getFileNameFromUrl(url: String?): String {
        if (url.isNullOrBlank()) return "" // Handle null or empty string case
        return url.substringAfterLast("/", "")
    }

    fun getNameById(id: String, list: List<DropDownResult>): String {
        if (id == "0") return ""
        return list.find { it.id == id }?.name ?: ""
    }

    fun dateConvertToString(dateString: String): String {
        return try {
            val originalFormat: DateFormat =
                SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            val targetFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'00:00:00'+'00:00")
            val date: Date = originalFormat.parse(dateString)

            targetFormat.format(date).uppercase(Locale.getDefault())
        } catch (e: Exception) {
            ""
        }
    }

    fun dateconvertWithTime(dateString: String): String {
        try {
            val originalFormat: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val targetFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
            val date: Date = originalFormat.parse(dateString)
            val formattedDate: String = targetFormat.format(date)
            return formattedDate
        } catch (e: Exception) {
            return ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateDifference(FutureDate: String): String {
        var totalTime = 0
        var unit: String? = null
        try {
            // val netDate = java.sql.Date(java.lang.Long.parseLong(FutureDate) * 1000)
            val parseFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            parseFormat.timeZone = TimeZone.getTimeZone("UTC")
            var date: Date? = null
            try {
                date = parseFormat.parse(FutureDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val oldDate = Date()

            var diff = oldDate.time - date!!.time

            val year = diff / (24 * 60 * 60 * 24 * 365 * 1000L)
            diff -= year * (24 * 60 * 60 * 24 * 365 * 1000L)

            val month = diff / (24 * 60 * 60 * 30 * 1000L)
            diff -= month * (24 * 60 * 60 * 30 * 1000L)

            val week = diff / (24 * 60 * 60 * 7 * 1000)
            diff -= week * (24 * 60 * 60 * 7 * 1000)

            val days = diff / (24 * 60 * 60 * 1000)
            diff -= days * (24 * 60 * 60 * 1000)

            val hours = diff / (60 * 60 * 1000)
            diff -= hours * (60 * 60 * 1000)

            val minutes = diff / (60 * 1000)
            diff -= minutes * (60 * 1000)

            val seconds = diff / 1000

            if (year != 0L) {
                totalTime = year.toInt()
                if (totalTime == 1) {
                    unit = "Yr"
                } else {
                    unit = "Yrs"
                }
                // unit = "day"
            } else if (month != 0L) {
                totalTime = month.toInt()
                if (totalTime == 1) {
                    unit = "Month"
                } else {
                    unit = "Months"
                }
                // unit = "day"
            } else if (week != 0L) {
                totalTime = week.toInt()
                if (totalTime == 1) {
                    unit = "Week"
                } else {
                    unit = "Weeks"
                }
                // unit = "day"
            } else if (days != 0L) {
                totalTime = days.toInt()
                if (totalTime == 1) {
                    unit = "Day"
                } else {
                    unit = "Days"
                }
                // unit = "day"
            } else if (hours != 0L) {
                totalTime = hours.toInt()
                if (totalTime == 1) {
                    unit = "Hr"
                } else {
                    unit = "Hrs"
                }
            } else if (minutes != 0L) {
                totalTime = minutes.toInt()
                unit = "Min"
            } else if (seconds != 0L) {
                totalTime = seconds.toInt()
                unit = "Sec"

            } else {
                totalTime = 1
                unit = "Sec"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "$totalTime $unit ago"
    }

    fun getTimeAgo2(dateString: String): String {
        try {
            var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            //dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            Log.d("Time----", TimeZone.getDefault().toString())
            val pasTime = dateFormat.parse(dateString)
            //dateFormat.timeZone = TimeZone.getDefault();
            val msDiff = Calendar.getInstance().timeInMillis - pasTime.time
            val calDiff = Calendar.getInstance()
            calDiff.timeInMillis = msDiff
            val msg = "${calDiff.get(Calendar.YEAR) - 1970} year, " +
                    "${calDiff.get(Calendar.MONTH)} months, " +
                    "${calDiff.get(Calendar.DAY_OF_MONTH)} days"
            val years = calDiff.get(Calendar.YEAR) - 1970
            val months = calDiff.get(Calendar.MONTH)
            val days = calDiff.get(Calendar.DAY_OF_MONTH)

            if (years == 0) {
                if (months == 0) {
                    dateFormat = if (days <= 1) {
                        if (DateUtils.isToday(pasTime.time)) {
                            SimpleDateFormat("hh:mm a")
                        } else {
                            SimpleDateFormat("'Yesterday'")
                        }

                    } else {
                        if (days < 7) {
                            SimpleDateFormat("EEEE")
                        } else {
                            SimpleDateFormat("dd MMM")
                        }
                    }
                } else {
                    dateFormat = SimpleDateFormat("dd MMM")
                }
            } else {
                dateFormat = SimpleDateFormat("MM-dd-yyyy")
            }

            return dateFormat.format(pasTime).toString()
        } catch (e: Exception) {

        }
        return ""
    }

    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS
    private const val MONTH_MILLIS = 30 * DAY_MILLIS
    private const val YEAR_MILLIS = 365 * DAY_MILLIS
    fun getTimeAgo(dateString: String): String {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+05:30")
            //dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            Log.d("Time----", TimeZone.getDefault().toString())
            val pasTime = dateFormat.parse(dateString)
            //dateFormat.timeZone = TimeZone.getDefault()

            var time = pasTime.time
            if (time < 1000000000000L) {
                time *= 1000
            }

            val now = currentDate().time
            if (time > now || time <= 0) {
                return /*"in the future"*/"seconds ago"
            }
            val diff = now - time
            return when {
                diff < MINUTE_MILLIS -> "seconds ago"
                diff < 2 * MINUTE_MILLIS -> "a minute ago"
                diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
                diff < 2 * HOUR_MILLIS -> "an hour ago"
                diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
                diff < 48 * HOUR_MILLIS -> "yesterday"
//                diff < MONTH_MILLIS -> "${diff / DAY_MILLIS} days ago"
//                diff < 2 * MONTH_MILLIS -> "a month ago"
//                diff < YEAR_MILLIS -> "${diff / MONTH_MILLIS} months ago"
//                diff < 2 * YEAR_MILLIS -> "a year ago"
//                else -> "${diff / YEAR_MILLIS} years ago"
                else -> "${diff / DAY_MILLIS} days ago"
            }
        } catch (e: Exception) {
            return ""
        }
    }

    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val PASSWORD_PATTERN = "(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,15}"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    object ProcessDialog {
        private var progressDialog: Dialog? = null
        fun start(context: Context) {
            if (!isShowing) {
                if (!(context as Activity).isFinishing) {
                    progressDialog = Dialog(context)
                    progressDialog!!.setCancelable(false)
                    /*     progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);*/progressDialog!!.window!!
                        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                    progressDialog!!.setContentView(R.layout.progress_loader)
                    progressDialog!!.show()
                }
            }
        }

        fun dismiss() {
            try {
                if (progressDialog != null && progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            } catch (e: IllegalArgumentException) {
                // Handle or log or ignore
            } catch (e: java.lang.Exception) {
                // Handle or log or ignore
            } finally {
                progressDialog = null
            }
        }

        val isShowing: Boolean
            get() = if (progressDialog != null) {
                progressDialog!!.isShowing
            } else {
                false
            }
    }


    fun getDatePatter(dateString: String): List<String>? {
        try {
            return dateString.split("/")
        } catch (e: Exception) {
            return null
        }
    }

    fun savePreferencesBoolean(context: Context, key: String, value: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getPreferencesBoolean(context: Context, key: String): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(key, false)
    }

    fun getPreferencesInt(context: Context, key: String): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getInt(key, 0)
    }

    fun checkGif(path: String): Boolean {
        return path.matches(GIF_PATTERN.toRegex())
    }

    fun pad(num: Int): String {
        return if (num < 10) "0$num" else "$num"
    }

    fun getBitmapFromURL(src: String): Bitmap? {
        class Converter : AsyncTask<Void, Void, Bitmap>() {
            lateinit var myBitmap: Bitmap
            override fun doInBackground(vararg params: Void?): Bitmap {
                try {
                    val url = URL(src)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    myBitmap = BitmapFactory.decodeStream(input)
                    return myBitmap
                } catch (e: IOException) {
                }
                return myBitmap
            }
        }
        Converter().execute()
        return null
    }

    fun saveTempBitmap(context: Context, mBitmap: Bitmap): String? {

        val outputDir = context.cacheDir

        var file: File? = null
        try {
            file = File.createTempFile("temp_post_img", ".jpg", outputDir)
            //outputFile.getAbsolutePath();
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val f3 = File(Environment.getExternalStorageDirectory().toString() + "/inpaint/")
        if (!f3.exists()) {
            f3.mkdirs()
        }
        var outStream: OutputStream? = null
        //File file = new File(Environment.getExternalStorageDirectory() + "/inpaint/"+"seconds"+".png");
        try {
            outStream = FileOutputStream(file!!)
            mBitmap.compress(CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()

            //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        //getPath( Uri.parse(file.getAbsolutePath()), context);

        return file.absolutePath//getPath( Uri.parse(file.getAbsolutePath()), context);
    }

    @Throws(IOException::class)
    fun rotateRequiredImage(context: Context, img: Bitmap, selectedImage: Uri): Bitmap {

        val input = context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface
        if (Build.VERSION.SDK_INT > 23)
            ei = ExifInterface(input!!)
        else
            ei = ExifInterface(selectedImage.path!!)

        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> return rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> return rotateImage(img, 270)
            else -> return img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }


    fun scaleDown(
        realImage: Bitmap, maxImageSize: Float,
        filter: Boolean,
    ): Bitmap {
        val ratio = Math.min(
            maxImageSize / realImage.width,
            maxImageSize / realImage.height
        )
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)

        return Bitmap.createScaledBitmap(
            realImage, width,
            height, filter
        )
    }

    fun getTimeFromTimestamp(timestamp: Long): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val dateString: String = formatter.format(Date(timestamp))
        return dateString
    }

    fun getDateFromTimestamp(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd/MMM/yy", Locale.ENGLISH)
        val dateString: String = formatter.format(Date(timestamp))
        return dateString
    }

    fun convertSecondsInFormat(sec: Long): String {
        val d = Date(sec * 1000L)
        val df = SimpleDateFormat("HH:mm:ss") // HH for 0-23

        df.timeZone = TimeZone.getTimeZone("GMT")
        val time = df.format(d)
        return time
    }


    private const val GIF_PATTERN = "(.+?)\\.gif$"
    fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
        val result: String?
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    class SafeClickListener(
        private var defaultInterval: Int = 1000,
        private val onSafeCLick: (View) -> Unit,
    ) : View.OnClickListener {
        private var lastTimeClicked: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeCLick(v)
        }
    }

    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun changeStatusBar(context: AppCompatActivity) {
        val window = context.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#4D4D4D")
        }
    }

    fun savePreferencesString(context: Context, key: String, value: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun savePreferencesArrayList(context: Context, key: String, value: ArrayList<String>) {
        val set = HashSet<String>()
        set.addAll(value)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putStringSet(key, set)
        editor.apply()
    }


    fun getPreferencesArrayList(context: Context, key: String): ArrayList<String> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val set = sharedPreferences.getStringSet(key, null)
        val list = ArrayList<String>()
        if (set != null && set.size > 0) {
            list.addAll(set)
        }
        return list
    }

    fun savePreferencesInt(context: Context, key: String, k: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putInt(key, k)
        editor.apply()
    }

    fun saveLanguageInPreference(context: Context, key: String, value: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getLanguageInPreference(context: Context, key: String): String? {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context) as SharedPreferences
        return sharedPreferences.getString(key, "")
    }

    fun getPreferenceString(context: Context, key: String): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, "").toString()
    }

    fun getPreferenceInt(context: Context, key: String): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getInt(key, 0)
    }

    fun getPreferenceBoolean(context: Context?, key: String): Boolean {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean(key, false)
    }

    fun maskAadharNumber(aadharNumber: String): String {
        // Check if Aadhar number is valid
        if (aadharNumber.length != 12 || !aadharNumber.matches("[0-9]+".toRegex())) {
            // Not a valid Aadhar number, return original
            return aadharNumber
        }

        // Masking Aadhar number
        val maskedAadhar = StringBuilder()
        for (i in 0 until aadharNumber.length) {
            if (i >= 8) {
                maskedAadhar.append(aadharNumber[i]) // Keep the first 4 and last 4 digits unmasked
            } else {
                maskedAadhar.append('*') // Mask the middle 4 digits
            }
        }
        return maskedAadhar.toString()
    }

    fun clearAllPreferencesExceptDeviceToken(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.remove(PrefEntities.TOKEN)
        editor.remove(AppConstants.LOGIN_DATA)
        editor.apply()
    }

    fun showSnackBar(viewLayout: View?, toastMessage: String?) {
        try {
            Snackbar.make(viewLayout!!, toastMessage!!, Snackbar.LENGTH_LONG).show()
        } catch (exception: Exception) {
//            Logger.e("log:", exception.toString())
        }
    }

    fun showSnackbar(view: View, message: String) {
        var view = view
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
        view.layoutParams = params
        view.setBackgroundColor(Color.parseColor("#AC0000"))
        snackbar.show()
    }

    fun showSnackbarIndex(view: View, message: String, onDismiss: (() -> Unit)? = null) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.view.apply {
            val params = layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.BOTTOM
            layoutParams = params
            setBackgroundColor(Color.parseColor("#AC0000"))
        }

        // Add callback to detect when the Snackbar is dismissed
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                onDismiss?.invoke()
            }
        })

        snackbar.show()
    }


    fun showSnackbarSuccess(view: View, message: String) {
        var view = view
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
        view.layoutParams = params
        view.setBackgroundColor(Color.parseColor("#2DA836"))
        snackbar.show()
    }

    fun getCorrectedDayOrMonth(value: Int): String {
        return if (value < 10) {
            "0$value"
        } else {
            value.toString()
        }
    }

    fun roundOff(value: Double, places: Int): Double {
        //  require(places >= 0)
        var bd = BigDecimal.valueOf(value)
        bd = bd.setScale(1, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboardOnOutSideTouch(view: View, activity: Activity) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                hideKeyboard(activity)
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                hideKeyboardOnOutSideTouch(innerView, activity)
            }
        }
    }

    fun convertNumberToGerman(num: Double): String {
        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        return nf.format(num)
    }

    fun convertDateEnglish(pre: String, dateString: String, post: String): String {
        val parseFormat = SimpleDateFormat(pre, Locale.ENGLISH)
        parseFormat.timeZone = TimeZone.getTimeZone("UTC") as TimeZone
        var date = Date()
        try {
            date = parseFormat.parse(dateString)
            parseFormat.timeZone = TimeZone.getDefault()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat(post, Locale.ENGLISH)
        format.timeZone = TimeZone.getDefault()
        // Log.e("dateTimeStamp",date)
        return format.format(date)
    }


    fun checkPhonePermission(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun changeStatusBarColor(context: AppCompatActivity, color: Int) {
        val window = context.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(context, color)
        }
    }

    fun getDateInEnglish(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
        val dateString: String = formatter.format(Date(timestamp))
        return dateString
    }


    fun getDateWithTime(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd MMMM - HH:mm", Locale.ENGLISH)
        val dateString: String = formatter.format(Date(timestamp))
        return dateString
    }

    fun getTime(timestamp: Long): String {
        val formatter = SimpleDateFormat("HH:mm a", Locale.ENGLISH)
        val dateString: String = formatter.format(Date(timestamp))
        return dateString
    }

    fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun changeLocale(context: Context): Context {
        var ctx = context
        if (getPreferenceString(context, AppConstants.LOCALE) != "") {
            val res = context.resources
            val dm = res?.displayMetrics
            val conf = res?.configuration
            conf?.setLocale(
                Locale(
                    getPreferenceString(
                        context,
                        AppConstants.LOCALE
                    )
                )
            ) // API 17+ only.
            res.updateConfiguration(conf, dm)
            ctx = context.createConfigurationContext(conf!!)
        }
        return ctx
    }

    class Run {
        companion object {
            fun after(delay: Long, process: () -> Unit) {
                Handler().postDelayed({
                    process()
                }, delay)
            }
        }
    }

    fun dp2px(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun showImageFromGlide(context: Context?, view: ImageView, imageUrl: String, placeHolder: Int) {
        if (imageUrl.isNotEmpty()) {
            if (context != null) {
                /*  Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE)
                      .skipMemoryCache(true).placeholder(placeHolder).error(placeHolder)
                      .into(view)*/

                Glide.with(context).load(imageUrl).placeholder(placeHolder).error(placeHolder)
                    .into(view)
            }
        }
    }

//    fun setHintText(
//        context: Context,
//        hintText: String,
//        view: TextInputLayout,
//        isFocused: Boolean,
//        isEmpty: Boolean
//    ) {
//        view.hint = ""
//        var hintColor: Int? = null
//        hintColor = if (isFocused)
//            R.color.black
//        else
//            R.color.black
//
//        if (isEmpty)
//            hintColor = R.color.grey_text
//
//        val builder = SpannableStringBuilder()
//        val firstPartColoredString = SpannableString(hintText)
//        firstPartColoredString.setSpan(
//            ForegroundColorSpan(
//                ContextCompat.getColor(
//                    context,
//                    hintColor
//                )
//            ), 0, hintText.length, 0
//        )
//
//        builder.append(firstPartColoredString)
//        view.hint = builder
//    }

//    fun setHint(
//        context: Context,
//        hintText: String,
//        view: TextInputLayout,
//        isFocused: Boolean,
//        isEmpty: Boolean
//    ) {
//        view.hint = ""
//        var hintColor: Int? = null
//        hintColor = if (isFocused)
//            R.color.black
//        else
//            R.color.black
//
//        if (isEmpty)
//            hintColor = R.color.grey_text
//        /* }else{
//             hintColor=R.color.grey_text
//         }*/
//
//
//        val partSecond = "*"
//        val builder = SpannableStringBuilder()
//        val firstPartColoredString = SpannableString(hintText)
//        firstPartColoredString.setSpan(
//            ForegroundColorSpan(
//                ContextCompat.getColor(
//                    context,
//                    hintColor
//                )
//            ), 0, hintText.length, 0
//        )
//
//        builder.append(firstPartColoredString)
//        val secondPartColoredString = SpannableString(partSecond)
//        secondPartColoredString.setSpan(
//            ForegroundColorSpan(
//                ContextCompat.getColor(
//                    context,
//                    R.color.yellow_dark
//                )
//            ), 0, partSecond.length, 0
//        )
//        builder.append(secondPartColoredString)
//
//        view.hint = builder
//    }

    fun spannableString() {

    }


//    fun setHintTextView(context: Context, hintText: String, view: ArialRegularEditText) {
//        view.hint = ""
//        val hintColor =
//            R.color.grey_text
//
//        val partSecond = "*"
//        val builder = SpannableStringBuilder()
//        val firstPartColoredString = SpannableString(hintText)
//        firstPartColoredString.setSpan(
//            ForegroundColorSpan(
//                ContextCompat.getColor(
//                    context,
//                    hintColor
//                )
//            ), 0, hintText.length, 0
//        )
//
//        builder.append(firstPartColoredString)
//        val secondPartColoredString = SpannableString(partSecond)
//        secondPartColoredString.setSpan(
//            ForegroundColorSpan(
//                ContextCompat.getColor(
//                    context,
//                    R.color.yellow_dark
//                )
//            ), 0, partSecond.length, 0
//        )
//        builder.append(secondPartColoredString)
//
//        view.hint = builder
//    }

//    fun setSpannable(context: Context, msg: String, startingIndex: Int, view: TextView) {
//        val clickableSpan: ClickableSpan = object : ClickableSpan() {
//
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.isUnderlineText = false
//                ds.color = ContextCompat.getColor(context, R.color.yellow_dark)
//                view.highlightColor = Color.TRANSPARENT;
//            }
//
//            override fun onClick(widget: View) {
//            }
//        }
//        val content = SpannableString(msg)
//        content.setSpan(clickableSpan, startingIndex, msg.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        view.text = content
//        view.movementMethod = LinkMovementMethod.getInstance()
//    }

    fun setSpannableWhite(context: Context, msg: String, startingIndex: Int, view: TextView) {
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(context, R.color.white)
                view.highlightColor = Color.TRANSPARENT
            }

            override fun onClick(widget: View) {
            }
        }
        val content = SpannableString(msg)
        content.setSpan(clickableSpan, startingIndex, msg.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.text = content
        view.movementMethod = LinkMovementMethod.getInstance()
    }

//    fun calenderOpen(
//        context: Context,
//        view: TextInputLayout,
//        editText: TextInputEditText,
//        hintText: String,
//        dateFrom: String
//    ) {
//        val cal: Calendar = Calendar.getInstance()
//        val year: Int = cal.get(Calendar.YEAR)
//        val month: Int = cal.get(Calendar.MONTH)
//        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
//        val dialog = DatePickerDialog(
//            context,
//            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//            { _, year, month, day ->
//                var month = month
//                month += 1
//                Log.d("Date", "onDateSet: MM/dd/yyy: $month/$day/$year")
//                val date = "$day-$month-$year"
//                val dateSet = dateConvert(date)
//                editText.setText(dateSet)
//                view.defaultHintTextColor =
//                    ContextCompat.getColorStateList(context, R.color.black)
//            },
//            year, month, day
//        )
//        dialog.setButton(
//            DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel)
//        ) { _, which ->
//            if (which == DialogInterface.BUTTON_NEGATIVE) {
//                editText.clearFocus()
//                view.hintTextColor = ContextCompat.getColorStateList(context, R.color.grey_text)
//                setHint(
//                    context,
//                    hintText,
//                    view,
//                    false,
//                    isEmpty = editText.text.toString().isEmpty()
//                )
//            }
//        }
//        dialog.setCancelable(false)
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        when (dateFrom) {
//            "A" -> {
//                dialog.datePicker.maxDate = System.currentTimeMillis()
//            }
//            "B" -> {
//                dialog.datePicker.minDate = System.currentTimeMillis() - 1000
//            }
//        }
//        if(editText.text.toString().isNotEmpty()) {
//            val fromUser = SimpleDateFormat("dd MMM yyyy")
//            val myFormat = SimpleDateFormat("dd/MM/yyyy")
//            val reformattedStr = myFormat.format(fromUser.parse(editText.text.toString()))
//            try {
//                val d = myFormat.parse(reformattedStr)
//                val cal = Calendar.getInstance()
//                cal.time = d
//                val month = cal[Calendar.MONTH] //YOUR MONTH IN INTEGER
//                val year = cal[Calendar.YEAR] //YOUR YEAR IN INTEGER
//                val day = cal[Calendar.DAY_OF_MONTH] //YOUR DAY IN INTEGER
//                dialog.updateDate(year, month, day)
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
//        }
//
//        dialog.show()
//    }

//    fun calenderOpen1(
//        context: Context,
//        view: TextInputLayout,
//        editText: TextInputEditText,
//        hintText: String,
//        dateFrom: String
//    ) {
//        val cal: Calendar = Calendar.getInstance()
//        val year: Int = cal.get(Calendar.YEAR)
//        val month: Int = cal.get(Calendar.MONTH)
//        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
//        val dialog = DatePickerDialog(
//            context,
//            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//            { _, year, month, day ->
//                var month = month
//                month += 1
//                Log.d("Date", "onDateSet: MM/dd/yyy: $month/$day/$year")
//                val date = "$day-$month-$year"
//                val dateSet = dateConvert(date)
//                editText.setText(dateSet)
//                view.defaultHintTextColor =
//                    ContextCompat.getColorStateList(context, R.color.black)
//            },
//            year, month, day
//        )
//        dialog.setButton(
//            DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel)
//        ) { _, which ->
//            if (which == DialogInterface.BUTTON_NEGATIVE) {
//                editText.clearFocus()
//                view.hintTextColor = ContextCompat.getColorStateList(context, R.color.grey_text)
//                setHintText(
//                    context,
//                    hintText,
//                    view,
//                    false,
//                    isEmpty = editText.text.toString().isEmpty()
//                )
//            }
//        }
//        dialog.setCancelable(false)
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        when (dateFrom) {
//            "A" -> {
//                dialog.datePicker.maxDate = System.currentTimeMillis()
//            }
//            "B" -> {
//                dialog.datePicker.minDate = System.currentTimeMillis() - 1000
//            }
//        }
//        if(editText.text.toString().isNotEmpty()) {
//            val fromUser = SimpleDateFormat("dd MMM yyyy")
//            val myFormat = SimpleDateFormat("dd/MM/yyyy")
//            val reformattedStr = myFormat.format(fromUser.parse(editText.text.toString()))
//            try {
//                val d = myFormat.parse(reformattedStr)
//                val cal = Calendar.getInstance()
//                cal.time = d
//                val month = cal[Calendar.MONTH] //YOUR MONTH IN INTEGER
//                val year = cal[Calendar.YEAR] //YOUR YEAR IN INTEGER
//                val day = cal[Calendar.DAY_OF_MONTH] //YOUR DAY IN INTEGER
//                dialog.updateDate(year, month, day)
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
//        }
//
//        dialog.show()
//    }

    private fun dateConvert(dateString: String): String {
        return try {
            val originalFormat: DateFormat =
                SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
            val targetFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
            val date: Date = originalFormat.parse(dateString)
            val formattedDate: String = targetFormat.format(date).toString()
                .uppercase(Locale.getDefault())
            formattedDate
        } catch (e: Exception) {
            ""
        }
    }

    fun dateConvert1(dateString: String): String {
        return try {
            val originalFormat: DateFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val targetFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
            val date: Date = originalFormat.parse(dateString)
            val formattedDate: String = targetFormat.format(date).toString()
                .uppercase(Locale.getDefault())
            formattedDate
        } catch (e: Exception) {
            ""
        }
    }

    fun dateConvert2(dateString: String): String {
        return try {
            val originalFormat: DateFormat =
                SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            val targetFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
            val date: Date = originalFormat.parse(dateString)
            val formattedDate: String = targetFormat.format(date).toString()
                .uppercase(Locale.getDefault())
            formattedDate
        } catch (e: Exception) {
            ""
        }
    }

//    fun showCameraGalleryDialog(
//        context: Context,
//        is_PDF: Boolean,
//        callback: DialogCallbackCameraGallery
//    ) {
//        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
//        dialog.setCancelable(true)
//        dialog.setCanceledOnTouchOutside(true)
//        dialog.window!!.setLayout(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        dialog.window!!.setGravity(Gravity.CENTER)
//        val lp: WindowManager.LayoutParams = dialog.window!!.attributes
//        lp.dimAmount = 0.75f
//        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window
//        dialog.window!!.attributes = lp
//        dialog.setContentView(R.layout.dialog_camera_gallery)
//        val pdfLayout = dialog.findViewById(R.id.pdfLayout) as LinearLayout
//        val llCamera = dialog.findViewById(R.id.ll_camera) as LinearLayout
//        val llGallery = dialog.findViewById(R.id.ll_gallery) as LinearLayout
//        val ll_pdf = dialog.findViewById(R.id.ll_pdf) as LinearLayout
//
//        if (is_PDF) {
//            pdfLayout.showView()
//            llCamera.showView()
//            llGallery.showView()
//        }
//        llCamera.setOnClickListener {
//            dialog.dismiss()
//            callback.onYes(AppConstants.CAMERA)
//        }
//        llGallery.setOnClickListener {
//            dialog.dismiss()
//            callback.onYes(AppConstants.GALLERY)
//        }
//        ll_pdf.setOnClickListener {
//            dialog.dismiss()
//            callback.onYes(AppConstants.PDF)
//        }
//        dialog.show()
//    }
//
//    fun showAlertDialog(
//        context: Context,
//        headingTitle: String,
//        bold_text: String,
//        msg: String,
//        image: Int,
//        callback: DialogCallback
//    ) {
//        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
//        dialog.setCancelable(true)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.window!!.setLayout(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        dialog.window!!.setGravity(Gravity.CENTER)
//        val lp: WindowManager.LayoutParams = dialog.window!!.attributes
//        lp.dimAmount = 0.75f
//        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window
//        dialog.window!!.attributes = lp
//        dialog.setContentView(R.layout.dialog_verification)
//        val dialogMsg = dialog.findViewById(R.id.tvText) as TextView
//        val dialogTitle = dialog.findViewById(R.id.tvHeadingText) as TextView
//        val yesBtn = dialog.findViewById(R.id.ivClose) as ImageView
//        dialogMsg.text = highlightTextString(msg, bold_text)
//        if (headingTitle.isNotEmpty()) {
//            dialogTitle.showView()
//            dialogTitle.text = headingTitle
//        }
//
//        Glide.with(context).load(image).into(yesBtn)
//
//
//        yesBtn.setOnClickListener {
//            dialog.dismiss()
//            callback.onYes()
//        }
//        dialog.show()
//    }

    fun Long.readableFormat(): String {
        if (this <= 0) return "0"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble()))
            .toString() + " " + units[digitGroups]
    }


//    fun showDeleteAlertDialog(context: Context, delete_text: String, callback: DialogCallback) {
//        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
//        dialog.setCancelable(false)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.window!!.setLayout(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        dialog.window?.setGravity(Gravity.CENTER)
//        val lp: WindowManager.LayoutParams = dialog.window!!.attributes
//        lp.dimAmount = 0.5f
//        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window
//        dialog.window?.attributes = lp
//        dialog.setContentView(R.layout.delete_dialog)
//        val tvDelete_text = dialog.findViewById(R.id.tvDelete_text) as TextView
//        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
//
//        val ivDelete = dialog.findViewById(R.id.ivDelete) as ImageView
//
//        tvDelete_text.text = delete_text
//
//        if (delete_text == context.getString(R.string.delete_message_medical) || delete_text == context.getString(
//                R.string.do_you_want_to_change_citizenship
//            ) || delete_text == context.getString(R.string.do_you_want_to_change_passport) || delete_text == context.getString(
//                R.string.do_you_want_to_change_driving_license
//            ) || delete_text == context.getString(R.string.do_you_want_to_change_proof_age)
//        ) {
//            ivDelete.setImageDrawable(context.getDrawable(R.mipmap.yes))
//        }
//
//        ivDelete.setOnClickListener {
//            dialog.dismiss()
//            callback.onYes()
//        }
//        //  ivClose.setOnClickListener { dialog.dismiss() }
//        tvCancel.setOnClickListener {
//            dialog.dismiss()
//
//        }
//        dialog.show()
//    }
//
//    fun showConfirmationAlertDialog(
//        context: Context,
//        showText: Int,
//        image: Int,
//        callback: DialogCallback
//    ) {
//        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
//        dialog.setCancelable(false)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.window!!.setLayout(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        dialog.window?.setGravity(Gravity.CENTER)
//        val lp: WindowManager.LayoutParams = dialog.window!!.attributes
//        lp.dimAmount = 0.5f
//        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window
//        dialog.window?.attributes = lp
//        dialog.setContentView(R.layout.item_confirmation_dialog)
//        val tvShowText = dialog.findViewById(R.id.tvShowText) as TextView
//        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
//
//        val ivConfirm = dialog.findViewById(R.id.ivConfirm) as ImageView
//
//
//
//        tvShowText.setText(showText)
//        Glide.with(context).load(image).into(ivConfirm)
//
//        ivConfirm.setOnClickListener {
//            dialog.dismiss()
//            callback.onYes()
//        }
//        //  ivClose.setOnClickListener { dialog.dismiss() }
//        tvCancel.setOnClickListener {
//            dialog.dismiss()
//
//        }
//        dialog.show()
//    }

//    fun showFullPostDialog(context: Context,item: Result, likeListener : OnItemClickListener) {
//        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
//        dialog.setCancelable(true)
//        dialog.setCanceledOnTouchOutside(true)
//        dialog.window!!.setLayout(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        dialog.window?.setGravity(Gravity.CENTER)
//        val lp: WindowManager.LayoutParams = dialog.window!!.attributes
//        lp.dimAmount = 0.5f
//        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window
//        dialog.window?.attributes = lp
//        dialog.setContentView(R.layout.dialog_your_talent)
//<<<<<<< HEAD
//=======
////        var adapter:TalentAdapter=TalentAdapter(context,likeListener,)
//>>>>>>> b4301321a8e8c2f2129a983c234627ae07c6a640
//        var count : Int ?=null
//        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
//        val tvMessage = dialog.findViewById(R.id.tvMessage) as TextView
//        val likeCount = dialog.findViewById(R.id.likeCount) as TextView
//        val tvUrl = dialog.findViewById(R.id.tvUrl) as TextView
//        val ivCross = dialog.findViewById(R.id.ivCross) as ImageView
//        val dislikeIcon = dialog.findViewById(R.id.dislikeIcon) as ImageView
//        val likeIcon = dialog.findViewById(R.id.likeIcon) as ImageView
//        likeCount.text= item.likes.toString()
//        count = item.likes?.plus(1)
//        if (item.is_liked==true)
//        {
//           dislikeIcon.visibility=View.GONE
//           likeIcon.visibility=View.VISIBLE
//        }
//        else
//        {
//            dislikeIcon.visibility=View.VISIBLE
//            likeIcon.visibility=View.GONE
//        }
//        dislikeIcon.setOnClickListener {
//<<<<<<< HEAD
//=======
////            `adapter.updateItemIcon(item, false)
//>>>>>>> b4301321a8e8c2f2129a983c234627ae07c6a640
//            likeListener.onItemClicked(LikesRequest(getPreferenceInt(context,AppConstants.USER_ID),"add",item.id!!),likeIcon, dislikeIcon,count,likeCount)
////            dislikeIcon.visibility=View.GONE
////           likeIcon.visibility=View.VISIBLE
////           likeCount.text= count.toString()
//        }
//        likeIcon.setOnClickListener {
//
//            likeListener.onItemClicked(LikesRequest(getPreferenceInt(context,AppConstants.USER_ID),"delete",item.id!!),likeIcon, dislikeIcon,count,likeCount)
////           dislikeIcon.visibility=View.VISIBLE
//
////           likeIcon.visibility=View.GONE
//            //            likeCount.text= count?.minus(1).toString()
//        }
//
//        tvTitle.text = item.title
//        tvMessage.text = item.post_contents
//        tvUrl.text = item.post_url
//        ivCross.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
     fun showConfirmationAlertDialog(
    context: Context,
    callback: DialogCallback,
    text: String
    ) {
        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)
        val lp: WindowManager.LayoutParams = dialog.window!!.attributes
        lp.dimAmount = 0.5f
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window
        dialog.window?.attributes = lp
        dialog.setContentView(R.layout.item_confirmation_dialog)
        val tvCancel: TextView = dialog.findViewById(R.id.tvCancel)
        val ivConfirm: TextView = dialog.findViewById(R.id.tvConfirm)
        val tvShowText: TextView = dialog.findViewById(R.id.tvShowText)
        tvShowText.text = text
        ivConfirm.setOnClickListener {
            dialog.dismiss()
            callback.onYes()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
//
//    fun showImageDialog(context: Context,image:String) {
//        val dialog = Dialog(context,android.R.style.Theme_Translucent_NoTitleBar)
//        dialog.setContentView(R.layout.dialog_image)
//        dialog.setCancelable(true)
//        dialog.setCanceledOnTouchOutside(true)
//        dialog.window!!.setLayout(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        dialog.window!!.setGravity(Gravity.CENTER)
//        val lp: WindowManager.LayoutParams = dialog.window!!.attributes
//        lp.dimAmount = 0.75f
//        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window!!.attributes = lp
//        val dialogImage = dialog.findViewById(R.id.ivImg) as ImageView
//        Glide.with(context).load(image)
//            .error(
//                R.mipmap.place_holder_profile
//            ).into(dialogImage)
//        dialog.show()
//    }

    fun isFileLessThan3MB(file: File): Boolean {
        val maxFileSize = 5 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize < maxFileSize
    }

    fun isFileGreaterThan2MB(file: File): Boolean {
        val maxFileSize = 2 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize > maxFileSize
    }

    fun highlightTextString(completeText: String, searchText: String): CharSequence {
        val temp = completeText.lowercase(Locale.getDefault())
        val highlightText = SpannableStringBuilder(completeText)
        val pattern: Pattern = Pattern.compile(searchText.lowercase(Locale.getDefault()))
        val matcher: Matcher = pattern.matcher(temp)
        while (matcher.find()) {
            val styleSpan = StyleSpan(Typeface.BOLD)
            highlightText.setSpan(styleSpan, matcher.start(), matcher.end(), 0)
        }
        return highlightText
    }

    fun getBitmapFromPath(filePath: String?): File {

        val imageFile = File(filePath)
        val fout: OutputStream = FileOutputStream(imageFile)
        val bitmap = BitmapFactory.decodeFile(filePath)
        bitmap.compress(CompressFormat.PNG, 80, fout)
        fout.flush()
        fout.close()
        return imageFile
    }

    fun isValidUrl(url: String?): Boolean {
        if (url == null) {
            return false
        }
        if (URLUtil.isValidUrl(url)) {
            // Check host of url if youtube exists
            val uri = Uri.parse(url)
            if ("www.youtube.com" == uri.host) {
                return true
            }
            // Other way You can check into url also like
            //if (url.startsWith("https://www.youtube.com/")) {
            //return true;
            //}
        }
        // In other any case
        return false
    }

    fun printDifference(startDate: String, endDate: String): String {
        //milliseconds
        var calculate: String = ""

        var dateFormat: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        var date1: Date = dateFormat.parse(startDate)
        var date2: Date = dateFormat.parse(endDate)

        Log.e("current", date1.toString() + "," + date2)

        var different = date1.time - date2.time


        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli
        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli

        try {

            if ((elapsedDays.toString().replace("-", "")).toInt() > 365) {
                calculate = (elapsedDays / 365).toString().plus(" years ago")

            } else if ((elapsedDays.toString().replace("-", "")).toInt() in 31..364) {
                calculate = (elapsedDays / 30).toString().plus(" months ago")


            } else if ((elapsedDays.toString().replace("-", "")).toInt() in 1..30) {
                calculate = elapsedDays.toString().plus(" days ago")


            } else if ((elapsedHours.toString().replace("-", "")).toInt() in 1..24) {
                calculate = elapsedHours.toString().plus(" hours ago")

            } else if ((elapsedMinutes.toString().replace("-", "")).toInt() in 1..60) {
                calculate = elapsedMinutes.toString().plus(" min ago")

            } else if ((elapsedSeconds.toString().replace("-", "")).toInt() in 1..60) {
                calculate = elapsedSeconds.toString().plus(" sec ago")

            }
        } catch (e: Exception) {

        }

        Log.e(
            "datesss",
            elapsedDays.toString() + "," + elapsedHours + "," + elapsedMinutes + "," + elapsedSeconds
        )
        System.out.printf(
            "%d days, %d hours, %d minutes, %d seconds%n",
            elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds
        )
        return calculate.replace("-", "")
    }

    fun currentSimpleDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

        return sdf

    }

    fun openWebPages(url: String, context: Context) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.setPackage("com.android.chrome")
        try {
            context.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            // Chrome is probably not installed
            // Try with the default browser
            i.setPackage(null)
            context.startActivity(i)
        }
    }

    fun getFormattedDate(dateStr: String): String {
        try {
            var format: SimpleDateFormat? = null

            if (dateStr.startsWith("1") && !dateStr.startsWith("11"))
                format = SimpleDateFormat("dd'st' MMMM yyyy")
            else if (dateStr.startsWith("2") && !dateStr.startsWith("12"))
                format = SimpleDateFormat("dd'nd' MMMM yyyy")
            else if (dateStr.startsWith("3") && !dateStr.startsWith("13"))
                format = SimpleDateFormat("dd'rd' MMMM yyyy")
            else
                format = SimpleDateFormat("dd'th' MMMM yyyy")

            var format1 = SimpleDateFormat("dd MMM yyyy")
            val date: Date = format.parse(dateStr)
            val formattedDate: String = format1.format(date).toString()
                .uppercase(Locale.getDefault())

            return formattedDate
        } catch (ex: Exception) {
            return dateStr
        }
    }

    fun getFormatedAmount(amount: Double): String? {
        val formatter = DecimalFormat("#,###.00")
        return formatter.format(amount)
    }

    fun versionName(context: Context): String {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionName
//            println(version)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }
    fun saveThemeMode(context: Context, isDarkMode: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(THEME_KEY, isDarkMode).apply()
    }

    fun isDarkMode(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(THEME_KEY, false) // Default is light mode
    }
}
