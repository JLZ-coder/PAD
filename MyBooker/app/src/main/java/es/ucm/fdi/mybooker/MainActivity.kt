package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
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
import es.ucm.fdi.mybooker.fragment.SearchFragment

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
    private  var currentFragment: Fragment? = null
    //Parameters
    private lateinit var name:String
    private lateinit var email:String
    //Cuenta atras
    private var presionado:Long = 0
    //Menu
    private lateinit var btnSearch: MenuItem
    private var booleanAux: Boolean = false

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
            changeFragment(currentFragment as HomeFragment)
            booleanAux = true
        }else {
            booleanAux = savedInstanceState?.getBoolean("fragment")
        }




        analytics();


        //setUpRecyclerView()
        setUp()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(currentFragment is HomeFragment) {
            outState.putBoolean("fragment", true)
        }
        else {
            outState.putBoolean("fragment", false)
        }


    }
    private fun setUp(){

        val boolButtonNavigation = BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navigation_home -> {
                    btnSearch.isVisible = true
                    currentFragment = HomeFragment.newInstance()
                    changeFragment(currentFragment as HomeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    btnSearch.isVisible = false
                    currentFragment = ScheduleFragment.newInstance(email)!!
                    changeFragment(currentFragment as ScheduleFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    btnSearch.isVisible = false
                    currentFragment = ProfileFragment.newInstance()
                    val bundle: Bundle = Bundle()
                    bundle.putString("name", name)
                    bundle.putString("email", email)
                    (currentFragment as ProfileFragment).arguments = bundle
                    changeFragment(currentFragment as ProfileFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }

        mBottonNavigation.setOnNavigationItemSelectedListener(boolButtonNavigation)


    }


    private fun changeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        btnSearch = menu?.findItem(R.id.search)!!

                    return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        btnSearch.isVisible = booleanAux
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.search->{//Fragmento
                btnSearch.isVisible = true
                currentFragment = SearchFragment.newInstance()
                val bundle: Bundle = Bundle()
                bundle.putString("type", name)
                (currentFragment as SearchFragment).arguments = bundle
                changeFragment(currentFragment as SearchFragment)
                true
            }

            android.R.id.icon-> {

                Toast.makeText(this, "F", Toast.LENGTH_SHORT).show();
                onBackPressed();
                btnSearch.isVisible = true
                currentFragment = HomeFragment.newInstance()
                changeFragment(currentFragment as HomeFragment)

                false
            }

            else -> super.onOptionsItemSelected(item)

        }
    }
    override fun onBackPressed() {


        if (supportFragmentManager.backStackEntryCount > 1) {
            Toast.makeText(this, "F", Toast.LENGTH_SHORT).show();
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
