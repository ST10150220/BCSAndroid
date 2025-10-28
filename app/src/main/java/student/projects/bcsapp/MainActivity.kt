package student.projects.bcsapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import student.projects.bcsapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val uid = auth.currentUser?.uid
                    Log.d("AuthDebug", "Logged in Uid: $uid")

                    if (uid != null) {
                        checkUserRole(uid)
                    } else {
                        Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("AuthError", "Login failed", task.exception)
                }
                }
            }

        }
    private fun checkUserRole (uid: String) {
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role")?.lowercase()
                    when (role) {
                        "admin" -> {
                            Toast.makeText(this, "Welcome admin", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                            finish()
                        }
                        else -> {
                            Toast.makeText(this, "Error: Unknown role", Toast.LENGTH_SHORT).show()
                        }

                    }
                }else {
                    Toast.makeText(this, "Error: User record not found", Toast.LENGTH_SHORT).show()
                    Log.e("FirestoreError", "No user document found for UID: $uid")
                }

            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch role: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("FirestoreError", "Error fetching role", e)
            }
    }
}