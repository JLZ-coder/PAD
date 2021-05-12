package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.fragment.HomeFragment
import es.ucm.fdi.mybooker.fragment.ProfileFragment
import es.ucm.fdi.mybooker.fragment.ScheduleFragment

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
    //Parameters
    private lateinit var name:String
    private lateinit var email:String
    //Cuenta atras
    private var presionado:Long = 0


    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBottonNavigation = findViewById<BottomNavigationView>(R.id.navigationView)
        title = "MyBooker"

        // TODO: No llega el usuario ni el mail ni nada. REVISAR
        // Lo he movido para aquí porque necesito el mail en Schedule
        val bundle:Bundle? = intent.extras
        email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider")
        name = bundle?.getString("userName").toString()



        title = "Inicio"

        if (savedInstanceState == null){
            currentFragment = HomeFragment.newInstance()
            changeFragment(currentFragment)
        }


        analytics();


        //setUpRecyclerView()
        setUp()
    }

    private fun setUp(){


        val boolBottonNavigation = BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navigation_home -> {
                    currentFragment = HomeFragment.newInstance()
                    changeFragment(currentFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    currentFragment = ScheduleFragment.newInstance(email)!!
                    changeFragment(currentFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    currentFragment = ProfileFragment.newInstance()
                    val bundle: Bundle = Bundle()
                    bundle.putString("name", name)
                    bundle.putString("email", email)
                    currentFragment.arguments = bundle
                    changeFragment(currentFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }

        mBottonNavigation.setOnNavigationItemSelectedListener(boolBottonNavigation)
    }


    private fun changeFragment(fragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {


        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        }else{
            if (presionado + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                super.onBackPressed();
            }else
                Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();

            presionado = System.currentTimeMillis();
        }
    }
    private fun analytics()
    {

        // Trozo analytics
        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "MainActivity opened")
        analytics.logEvent("InitScreen", bundle)
    }

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser == null){
            finish()
        }
    }


}
