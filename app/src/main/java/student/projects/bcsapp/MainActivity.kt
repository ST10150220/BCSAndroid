package student.projects.bcsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

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
        tvForgotPassword.setOnClickListener {
            showResetPasswordDialog()
        }
    }
    private fun showResetPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset Password")

        val input = EditText(this)
        input.hint = "Enter your email"
        builder.setView(input)

        builder.setPositiveButton("Send") { _, _ ->
            val email = input.text.toString().trim()
            if(email.isNotEmpty()) {
                sendPasswordReset(email)
            } else {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    private fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun checkUserRole(uid: String) {
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role")?.lowercase()
                    val email = document.getString("email") ?: ""
                    val name = document.getString("name") ?: ""

                    val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putString("userEmail", email)
                    editor.putString("userName", name)
                    editor.putString("userRole", role)
                    editor.apply()

                    when (role) {
                        "admin" -> {
                            Toast.makeText(this, "Welcome admin", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                            finish()
                        }
                        "client" -> {
                            Toast.makeText(this, "Welcome Client", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, ClientDashboardActivity::class.java))
                            finish()
                        }
                        "project manager" -> {
                            Toast.makeText(this, "Welcome Project Manager", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, ProjectManagerDashboardActivity::class.java))
                            finish()
                        }
                        "contractor" -> {
                            Toast.makeText(this, "Welcome Contractor", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, ContractorDashboardActivity::class.java))
                            finish()
                        }
                        else -> {
                            Toast.makeText(this, "Error: Unknown role", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error: User record not found", Toast.LENGTH_SHORT).show()
                    Log.e("FirestoreError", "No user document found for UID: $uid")
                }

            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch role: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("FirestoreError", "Error fetching role", e)
            }
    }
}
