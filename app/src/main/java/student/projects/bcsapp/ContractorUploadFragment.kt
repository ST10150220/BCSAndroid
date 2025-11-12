package student.projects.bcsapp.contractor

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Blob
import com.google.firebase.storage.FirebaseStorage
import student.projects.bcsapp.R
import java.io.IOException

class ContractorUploadFragment : Fragment() {

    private lateinit var etTask: EditText
    private lateinit var btnUpload: Button
    private lateinit var btnSelectFiles: Button
    private lateinit var tvSelectedFiles: TextView

    private val selectedFiles = mutableListOf<Uri>()
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_contractor_upload, container, false)

        etTask = view.findViewById(R.id.etTask)
        btnUpload = view.findViewById(R.id.btnUpload)
        btnSelectFiles = view.findViewById(R.id.btnSelectFiles)
        tvSelectedFiles = view.findViewById(R.id.tvSelectedFiles)

        setupFilePicker()

        return view
    }

    private fun setupFilePicker() {
        val pickFilesLauncher = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (uris.isNotEmpty()) {
                selectedFiles.clear()
                selectedFiles.addAll(uris)
                val fileNames = uris.joinToString("\n") { uri -> uri.lastPathSegment ?: "Unknown file" }
                tvSelectedFiles.text = "Selected Files:\n$fileNames"
            } else {
                tvSelectedFiles.text = "No file selected"
            }
        }

        btnSelectFiles.setOnClickListener {
            pickFilesLauncher.launch(arrayOf("*/*"))
        }

        btnUpload.setOnClickListener {
            val taskName = etTask.text.toString().trim()

            if (taskName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a task", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedFiles.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a file", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadFiles(taskName)
        }
    }

    private fun uploadFiles(taskName: String) {
        for (fileUri in selectedFiles) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(fileUri)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null) {
                    val blob = Blob.fromBytes(fileBytes)
                    saveFile(taskName, blob, fileUri.lastPathSegment ?: "Unknown File")
                } else {
                    Toast.makeText(requireContext(), "File was not read", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(requireContext(), "Error reading file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveFile(taskName: String, blob: Blob, fileName: String) {
        val fileData = hashMapOf(
            "taskName" to taskName,
            "fileName" to fileName,
            "fileData" to blob,
            "status" to "pending"
        )

        firestore.collection("reports")
            .add(fileData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
