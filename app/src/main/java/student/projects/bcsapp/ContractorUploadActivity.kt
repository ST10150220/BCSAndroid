package student.projects.bcsapp


import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import retrofit2.http.Url
import java.util.UUID

class ContractorUploadActivity : AppCompatActivity() {

    private lateinit var etTask: EditText
    private lateinit var btnUpload: Button
    private lateinit var btnSelectFiles: Button
    private lateinit var tvSelectedFiles: TextView

    private val selectedFiles = mutableListOf<Uri>()
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contractor_upload)

        etTask = findViewById(R.id.etTask)
        btnUpload = findViewById(R.id.btnUpload)
        btnSelectFiles = findViewById(R.id.btnSelectFiles)
        tvSelectedFiles = findViewById(R.id.tvSelectedFiles)

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

            if (taskName.isEmpty()){
                Toast.makeText(this, "Please Enter a task", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedFiles.isEmpty()){
                Toast.makeText(this, "Please Select a file", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uploadFiles(taskName)
        }
    }

    private fun uploadFiles(taskName: String) {
        for (fileUri in selectedFiles) {
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("contractor_uploads/$fileName")
            val uploadTask = storageRef.putFile(fileUri)

            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveFileUrl(taskName, uri.toString())
                }

            }.addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveFileUrl (taskName: String, fileUrl: String){
        val fileData = hashMapOf(
            "taskName" to taskName,
            "fileUrl" to fileUrl,
          //  "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("reports")
            .add(fileData)
            .addOnSuccessListener {
                Toast.makeText(this, "File uploaded successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save file info: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
