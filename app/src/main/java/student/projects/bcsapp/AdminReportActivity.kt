package student.projects.bcsapp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp


class AdminReportActivity : AppCompatActivity() {

    private lateinit var etTitle : EditText
    private lateinit var etDescription: EditText
    private lateinit var etReportType: EditText
    private lateinit var btnSubmit : Button
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_report)

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etReportType = findViewById(R.id.etReportType)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val reportType = etReportType.text.toString().trim()
            val createdById = auth.currentUser?.uid ?: "unknown"

            if(title.isEmpty() || description.isEmpty() || reportType.isEmpty()) {
                Toast.makeText(this, "Please fill in all info", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reportData = hashMapOf(
                "title" to title,
                "description" to description,
                "reportType" to reportType,
                "createdAt" to Timestamp.now(),
                "createdById" to createdById
            )

            db.collection("AdminReports").add(reportData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Report Submitted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Report could not be submitted", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
