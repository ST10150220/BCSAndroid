package student.projects.bcsapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import network.MaintenanceResponse
import student.projects.bcsapp.databinding.ActivityClientDashboardBinding

class ClientDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientDashboardBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var clientName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch logged-in client info first
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("Users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        clientName = doc.getString("name") ?: ""
                        loadDefaultFragment()
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

        setupBottomNavigation()
    }

    private fun loadDefaultFragment() {
        // Load the RequestFormFragment by default and pass the client name
        if (supportFragmentManager.findFragmentById(binding.fragmentContainer.id) == null) {
            supportFragmentManager.commit {
                replace(binding.fragmentContainer.id, RequestFormFragment.newInstance(clientName))
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_request_form -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentContainer.id, RequestFormFragment.newInstance(clientName))
                    }
                    true
                }
                else -> false
            }
        }
    }
}
