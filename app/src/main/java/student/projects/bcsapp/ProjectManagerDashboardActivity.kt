package student.projects.bcsapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import student.projects.bcsapp.projectmanager.LogoutFragment
import student.projects.bcsapp.projectmanager.ProjectManagerUpdateReportFragment
import student.projects.bcsapp.ui.messages.MessagesFragment

class ProjectManagerDashboardActivity : AppCompatActivity(),
    LogoutFragment.LogoutListener {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private var currentItemId: Int = R.id.navigation_dashboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_dashboard_host)

        // Load default fragment
        replaceFragment(ProjectManagerDashboardFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    currentItemId = item.itemId
                    replaceFragment(ProjectManagerDashboardFragment())
                    true
                }

                R.id.navigation_other -> {
                    currentItemId = item.itemId
                    replaceFragment(AssignContractorFragment())
                    true
                }

                R.id.navigation_update_reports -> {
                    currentItemId = item.itemId
                    replaceFragment(ProjectManagerUpdateReportFragment())
                    true
                }
                R.id.nav_messages -> {
                    currentItemId = item.itemId
                    replaceFragment(MessagesFragment())
                    true
                }

                R.id.navigation_logout -> {
                    replaceFragment(LogoutFragment())
                    false
                }

                else -> false
            }
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
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onLogoutCancelled() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = currentItemId
    }
}