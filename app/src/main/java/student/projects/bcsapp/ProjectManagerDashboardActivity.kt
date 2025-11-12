package student.projects.bcsapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import student.projects.bcsapp.projectmanager.ProjectManagerUpdateReportFragment

class ProjectManagerDashboardActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_dashboard_host)

        // Load default fragment
        replaceFragment(ProjectManagerDashboardFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_dashboard -> replaceFragment(ProjectManagerDashboardFragment())
                R.id.navigation_other -> replaceFragment(AssignContractorFragment())
                R.id.navigation_update_reports -> replaceFragment(ProjectManagerUpdateReportFragment())
                else -> false
            }
            true
        }

        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)

        tvGreeting.text = "Welcome back,"

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("Users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val managerName = doc.getString("name") ?: "User"
                        tvUserName.text = managerName
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch user info: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
        // ------------------------------
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
