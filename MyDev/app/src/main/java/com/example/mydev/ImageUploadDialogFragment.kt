package com.example.mydev

import android.app.DatePickerDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.mydev.api.RetrofitInstance
import com.example.mydev.model.ImageUploadRequest
import com.example.mydev.viewmodel.ImagesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Calendar

class ImageUploadDialogFragment : DialogFragment() {

    private lateinit var imgPreview: ImageView
    private lateinit var etInstagramIds: EditText
    private lateinit var btnPickDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnSave: Button

    private var selectedUri: Uri? = null
    private var selectedDate: String? = null

    // 업로드 성공 시 호출할 콜백
    private var onUploadSuccessListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_image_upload)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // findViewById 연결
        imgPreview = dialog!!.findViewById(R.id.imgPreview)
        etInstagramIds = dialog!!.findViewById(R.id.etInstagramIds)
        btnPickDate = dialog!!.findViewById(R.id.btnPickDate)
        tvSelectedDate = dialog!!.findViewById(R.id.tvSelectedDate)
        btnSave = dialog!!.findViewById(R.id.btnSave)

        // 미리보기
        selectedUri = arguments?.getParcelable(ARG_SELECTED_URI)
        selectedUri?.let { uri ->
            imgPreview.setImageURI(uri)
        }

        btnPickDate.setOnClickListener {
            pickDate()
        }

        btnSave.setOnClickListener {
            startUploadProcess()
        }
    }

    private fun pickDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dateDialog = DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                tvSelectedDate.text = selectedDate
            },
            year, month, day
        )
        dateDialog.show()
    }

    private fun startUploadProcess() {
        val instaIdsInput = etInstagramIds.text.toString().trim()
        val instaList = instaIdsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        if (selectedUri == null) {
            Toast.makeText(context, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (instaList.isEmpty()) {
            Toast.makeText(context, "인스타 ID를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedDate.isNullOrEmpty()) {
            Toast.makeText(context, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 1) 서버에 Presigned URL 요청 -> 2) S3에 PUT -> 3) /images/upload POST
        uploadToS3AndPost(instaList, selectedDate!!)
    }

    private fun uploadToS3AndPost(instagramIds: List<String>, dateString: String) {
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val filePath = "images/$fileName"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1) Presigned URL 요청
                val res1 = RetrofitInstance.awsS3Api.getPreSignedUrl(

                    filePath
                )
                if (!res1.isSuccessful || res1.body() == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Presigned URL 발급 실패", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                val preSignedUrl = res1.body()!!.presignedUrl

                // 2) 파일 -> RequestBody (raw)
                val file = createTempFileFromUri(selectedUri!!)
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

                // 3) S3에 PUT 요청
                val res2 = RetrofitInstance.awsS3Api.uploadImageToS3(preSignedUrl, requestBody)
                if (!res2.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "S3 업로드 실패", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // 4) 최종 S3 URL 생성
                val finalImageUrl = "https://ggred0198.s3.ap-northeast-2.amazonaws.com/$filePath"

                // 5) 백엔드에 POST /images/upload 요청
                val isoDate = "${dateString}T00:00:00"
                val request = ImageUploadRequest(
                    url = finalImageUrl,
                    instagramIds = instagramIds,
                    createdAt = isoDate
                )

                val res3 = RetrofitInstance.imageApi.uploadImage(request)
                if (res3.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "업로드 성공!", Toast.LENGTH_SHORT).show()
                        onUploadSuccessListener?.invoke()
                        dismiss()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "백엔드 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "에러: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun createTempFileFromUri(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "tmp_upload_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun createMultipartBody(file: File): MultipartBody.Part {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }

    /** 콜백 등록 */
    fun setOnUploadSuccessListener(listener: () -> Unit) {
        onUploadSuccessListener = listener
    }

    companion object {
        private const val ARG_SELECTED_URI = "ARG_SELECTED_URI"

        fun newInstance(uri: Uri): ImageUploadDialogFragment {
            val fragment = ImageUploadDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_SELECTED_URI, uri)
            fragment.arguments = bundle
            return fragment
        }
    }
}

