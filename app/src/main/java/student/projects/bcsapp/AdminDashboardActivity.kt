package student.projects.bcsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnRegister = findViewById<Button>(R.id.btnRegisterUser)
        val btnReport = findViewById<Button>(R.id.btnReport)

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterUserActivity::class.java))
            finish()
        }

        btnReport.setOnClickListener {
            startActivity((Intent(this, AdminReportActivity::class.java)))
            finish()
        }
    }
}
