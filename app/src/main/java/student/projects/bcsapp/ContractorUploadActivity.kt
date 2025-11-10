package student.projects.bcsapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ContractorUploadActivity : AppCompatActivity() {

    private lateinit var etTask: EditText
    private lateinit var btnUpload: Button
    private lateinit var btnSelectFiles: Button
    private lateinit var tvSelectedFiles: TextView

    private val PICK_FILE_REQUEST = 1
    private var selectedFiles: MutableList<Uri> = mutableListOf()

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



    }
}
