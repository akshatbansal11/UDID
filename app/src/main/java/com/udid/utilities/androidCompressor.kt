import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FileCompressor(private val context: Context) {
    companion object {
        private const val MAX_FILE_SIZE = 500 * 1024 // 500KB in bytes
    }

    sealed class CompressionResult {
        data class Success(val compressedFile: File) : CompressionResult()
        data class Error(val message: String) : CompressionResult()
    }

    /**
     * Main compression function that handles both images and PDFs
     */
    suspend fun compressFile(inputFile: File): CompressionResult {
        return try {
            // Check if compression is needed
            if (inputFile.length() <= MAX_FILE_SIZE) {
                return CompressionResult.Error("File is already under 500KB")
            }

            val fileExtension = inputFile.extension.lowercase()
            val compressedBytes = when {
                fileExtension in listOf("jpg", "jpeg", "png") -> {
                    compressImage(inputFile)
                }
                fileExtension == "pdf" -> {
                    compressPDF(inputFile)
                }
                else -> {
                    return CompressionResult.Error("Unsupported file format")
                }
            }

            // Save compressed file
            val compressedFile = saveCompressedFile(compressedBytes, inputFile.name)
            CompressionResult.Success(compressedFile)
        } catch (e: Exception) {
            CompressionResult.Error("Compression failed: ${e.message}")
        }
    }

    /**
     * Compresses image using quality reduction and resizing
     */
    private fun compressImage(imageFile: File): ByteArray {
        var bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        var quality = 90
        var scale = 1.0f
        var attempts = 0
        var compressedBytes: ByteArray

        do {
            val scaledWidth = (bitmap.width * scale).toInt()
            val scaledHeight = (bitmap.height * scale).toInt()

            // Resize bitmap if not first attempt
            if (scale != 1.0f) {
                bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    scaledWidth,
                    scaledHeight,
                    true
                )
            }

            ByteArrayOutputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                compressedBytes = outputStream.toByteArray()
            }

            // Adjust parameters for next attempt
            if (compressedBytes.size > MAX_FILE_SIZE) {
                quality -= 10
                scale *= 0.7f
                attempts++
            }
        } while (compressedBytes.size > MAX_FILE_SIZE && quality > 20 && attempts < 5)

        if (compressedBytes.size > MAX_FILE_SIZE) {
            throw Exception("Unable to compress image to target size")
        }

        return compressedBytes
    }

    /**
     * Compresses PDF by reducing quality of embedded images
     */
    private fun compressPDF(pdfFile: File): ByteArray {
        val newPdfDocument = PdfDocument()
        val parcelFileDescriptor = ParcelFileDescriptor.open(
            pdfFile,
            ParcelFileDescriptor.MODE_READ_ONLY
        )
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)

        try {
            // Process each page
            for (pageIndex in 0 until pdfRenderer.pageCount) {
                val page = pdfRenderer.openPage(pageIndex)
                
                // Create bitmap for the page
                val bitmap = Bitmap.createBitmap(
                    page.width,
                    page.height,
                    Bitmap.Config.ARGB_8888
                )
                
                // Render page to bitmap
                page.render(
                    bitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
                page.close()

                // Compress the page bitmap
                val compressedPageBytes = compressImage(createTempFileFromBitmap(bitmap))

                // Create new PDF page
                val newPage = newPdfDocument.startPage(PdfDocument.PageInfo.Builder(
                    bitmap.width,
                    bitmap.height,
                    pageIndex
                ).create())

                // Draw compressed bitmap on the page
                val compressedBitmap = BitmapFactory.decodeByteArray(
                    compressedPageBytes,
                    0,
                    compressedPageBytes.size
                )
                newPage.canvas.drawBitmap(compressedBitmap, 0f, 0f, null)
                newPdfDocument.finishPage(newPage)

                // Clean up
                bitmap.recycle()
                compressedBitmap.recycle()
            }

            // Save compressed PDF
            val outputStream = ByteArrayOutputStream()
            newPdfDocument.writeTo(outputStream)
            val compressedBytes = outputStream.toByteArray()

            if (compressedBytes.size > MAX_FILE_SIZE) {
                throw Exception("Unable to compress PDF to target size")
            }

            return compressedBytes
        } finally {
            pdfRenderer.close()
            parcelFileDescriptor.close()
            newPdfDocument.close()
        }
    }

    /**
     * Helper function to create temporary file from bitmap
     */
    private fun createTempFileFromBitmap(bitmap: Bitmap): File {
        val tempFile = File.createTempFile("temp_", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        return tempFile
    }

    /**
     * Saves compressed data to a file
     */
    private fun saveCompressedFile(compressedBytes: ByteArray, originalFileName: String): File {
        val extension = originalFileName.substringAfterLast(".", "")
        val compressedFile = File(
            context.cacheDir,
            "compressed_${System.currentTimeMillis()}.$extension"
        )
        
        FileOutputStream(compressedFile).use { fos ->
            fos.write(compressedBytes)
        }
        
        return compressedFile
    }
}
