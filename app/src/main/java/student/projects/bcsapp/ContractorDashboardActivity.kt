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

class ContractorDashboardActivity : AppCompatActivity() {

    private lateinit var btnTemp : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contractor_dashboard)

        btnTemp = findViewById(R.id.btnTemp)

        btnTemp.setOnClickListener {
            startActivity(Intent(this, ContractorUploadActivity::class.java))
            finish()
        }
    }

}
