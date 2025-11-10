package student.projects.bcsapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ContractorUploadActivity : AppCompatActivity() {

    private lateinit var etTask: EditText
    private lateinit var btnUpload: Button
    private lateinit var btnSelectFiles: Button
    private lateinit var tvSelectedFile: TextView

    private val PICK_FILE_REQUEST = 1
    private var selectedFiles: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contractor_upload)

        btnSelectFiles = findViewById(R.id.btnSelectFiles)

    }
}
