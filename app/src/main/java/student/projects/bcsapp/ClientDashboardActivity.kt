package student.projects.bcsapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import student.projects.bcsapp.databinding.ActivityClientDashboardBinding
import student.projects.bcsapp.projectmanager.LogoutFragment
import student.projects.bcsapp.ui.messages.MessagesFragment

class ClientDashboardActivity : AppCompatActivity(), LogoutFragment.LogoutListener {

    private lateinit var binding: ActivityClientDashboardBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var clientName: String = ""
    private var clientEmail: String = ""
    private var currentItemId: Int = R.id.nav_request_form

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPrefs.getString("userName", "User")

        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)

        tvGreeting.text = "Welcome back,"
        tvUserName.text = userName

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("Users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        clientName = doc.getString("name") ?: ""
                        clientEmail = doc.getString("email") ?: ""

                        // Save clientEmail to SharedPreferences so fragments can use it
                        val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        sharedPrefs.edit()
                            .putString("userEmail", clientEmail)
                            .apply()

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
        if (supportFragmentManager.findFragmentById(binding.fragmentContainer.id) == null) {
            supportFragmentManager.commit {
                replace(binding.fragmentContainer.id, RequestFormFragment.newInstance(clientEmail))
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_request_form -> {
                    currentItemId = item.itemId
                    supportFragmentManager.commit {
                        replace(binding.fragmentContainer.id, RequestFormFragment.newInstance(clientEmail))
                    }
                    true
                }

                R.id.nav_my_requests -> {
                    currentItemId = item.itemId
                    supportFragmentManager.commit {
                        replace(binding.fragmentContainer.id, MyRequestsFragment())
                    }
                    true
                }

                R.id.nav_messages -> {
                    currentItemId = item.itemId
                    supportFragmentManager.commit {
                        replace(binding.fragmentContainer.id, MessagesFragment.newInstance())
                    }
                    true
                }

                R.id.navigation_logout -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentContainer.id, LogoutFragment())
                    }
                    false
                }

                else -> false
            }
        }
    }
    override fun onLogoutCancelled() {
        binding.bottomNavigation.selectedItemId = currentItemId
    }
}
