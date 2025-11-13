package student.projects.bcsapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import student.projects.bcsapp.contractor.ContractorRequestsFragment
import student.projects.bcsapp.contractor.ContractorUploadFragment
import student.projects.bcsapp.projectmanager.LogoutFragment
import student.projects.bcsapp.ui.messages.MessagesFragment

class ContractorDashboardActivity : AppCompatActivity(),
    LogoutFragment.LogoutListener {

    private var currentItemId: Int = R.id.nav_view_requests

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contractor_dashboard)

        val tvContractorName = findViewById<TextView>(R.id.tvContractorName)
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val contractorName = sharedPref.getString("userName", "Contractor")
        tvContractorName.text = contractorName

        val bottomNav = findViewById<BottomNavigationView>(R.id.contractor_bottom_nav)

        // Load default fragment
        loadFragment(ContractorRequestsFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_view_requests -> {
                    currentItemId = item.itemId
                    loadFragment(ContractorRequestsFragment())
                    true
                }
                R.id.nav_upload_report -> {
                    currentItemId = item.itemId
                    loadFragment(ContractorUploadFragment())
                    true
                }
                R.id.nav_messages -> {
                    currentItemId = item.itemId
                    loadFragment(MessagesFragment())
                    true
                }
                R.id.navigation_logout -> {
                    showLogoutDialog(bottomNav)
                    false // prevent immediate selection
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contractor_fragment_container, fragment)
            .commit()
    }

    private fun showLogoutDialog(bottomNav: BottomNavigationView) {
        val logoutFragment = LogoutFragment()
        supportFragmentManager.beginTransaction()
            .add(logoutFragment, "LogoutDialog")
            .commit()
    }


    override fun onLogoutCancelled() {
        findViewById<BottomNavigationView>(R.id.contractor_bottom_nav).selectedItemId = currentItemId
    }
}