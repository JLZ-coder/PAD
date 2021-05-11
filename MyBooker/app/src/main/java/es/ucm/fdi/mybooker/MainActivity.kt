package es.ucm.fdi.mybooker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.adapters.EnterpriseAdapter
import es.ucm.fdi.mybooker.fragment.HomeFragment
import es.ucm.fdi.mybooker.fragment.ProfileFragment
import es.ucm.fdi.mybooker.fragment.ScheduleFragment
import es.ucm.fdi.mybooker.objects.itemEnterprise

enum class ProviderType {
    MAIL
}

class MainActivity : AppCompatActivity()
{
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var mRecyclerView : RecyclerView


    //Navigation view
    private lateinit var mBottonNavigation : BottomNavigationView

    //Fragments
    private lateinit var currentFragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBottonNavigation = findViewById<BottomNavigationView>(R.id.navigationView)
        title = "MyBooker"

        //Fragmento inicial
        if (savedInstanceState == null){
            currentFragment = HomeFragment.newInstance()
            changeFragment(currentFragment)
        }

        val boolBottonNavigation = BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navigation_home -> {
                    currentFragment = HomeFragment.newInstance()
                    changeFragment(currentFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    currentFragment = ScheduleFragment.newInstance()
                    changeFragment(currentFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    currentFragment = ProfileFragment.newInstance()
                    changeFragment(currentFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        mBottonNavigation.setOnNavigationItemSelectedListener(boolBottonNavigation)

        analytics();

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        val name = bundle?.getString("name")

        //setUpRecyclerView()
        // setUp(email ?: "no user found", provider ?: "empty", name ?: "no name")
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun analytics()
    {

        // Trozo analytics
        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "MainActivity opened")
        analytics.logEvent("InitScreen", bundle)
    }
}

private fun BottomNavigationView.setOnNavigationItemSelectedListener(any: Any, any1: Any) {}