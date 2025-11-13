package student.projects.bcsapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import network.SendMessageFragment
import student.projects.bcsapp.projectmanager.LogoutFragment

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val navView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        navView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_messaging -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, SendMessageFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_reports -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AdminReportFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_register_user -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, RegisterUserFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_admin_dashboard -> {
                    supportFragmentManager.popBackStack(
                        null,
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    true
                }
                R.id.navigation_logout -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LogoutFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Load the dashboard fragment by default
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, AdminHomeFragment())
            .commit()
    }
}
