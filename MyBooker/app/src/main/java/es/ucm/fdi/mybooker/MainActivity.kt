package es.ucm.fdi.mybooker

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
// import android.widget.SearchView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.fragment.*

enum class ProviderType {
    MAIL
}
data class StateFragment(val currentTag: String, var oldTag: String)

class MainActivity : AppCompatActivity(), HomeFragment.Actualizar, SearchFragment.Actualizar
{
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var mRecyclerView : RecyclerView

    //Navigation view
    private lateinit var mBottonNavigation : BottomNavigationView

    //Parameters
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var image: String

    //Cuenta atras
    private var presionado:Long = 0
    //Menu
    private lateinit var btnSearch: MenuItem
    //Tag fragments
    private  var currentTag: String = "homeFragment"
    private  var oldTag: String = "homeFragment"
    private var stack = mutableListOf<StateFragment>()
    private var currentMenuItemId: Int = R.id.navigation_home

    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBottonNavigation = findViewById<BottomNavigationView>(R.id.navigationView)
        title = "MyBooker"

        val bundle:Bundle? = intent.extras
        email = bundle?.getString("email").toString()
        name = bundle?.getString("userName").toString()
        image = bundle?.getString("profileImg").toString()

        if (savedInstanceState == null){
            loadFirstFragment()
        }else{
            currentTag = savedInstanceState?.getString("fragment").toString()
            currentMenuItemId = savedInstanceState?.getInt("idItem")
        }

        analytics();
        setUp()
    }

    override fun onSaveInstanceState(outState: Bundle)
    {

        super.onSaveInstanceState(outState)

        outState.putString("fragment", currentTag)
        outState.putInt("idItem", currentMenuItemId)
    }
    private fun setUp(){

        val boolButtonNavigation = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if(currentMenuItemId != item.itemId) {
                currentMenuItemId = item.itemId

                when (item.itemId) {
                    R.id.navigation_home -> {
                        changeFragment(HomeFragment.newInstance(), "homeFragment")
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_dashboard -> {
                        changeFragment(ScheduleFragment.newInstance(mAuth.currentUser.uid)!!, "scheduleFragment")
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_profile -> {
                        var profile = ProfileFragment.newInstance()
                        val bundle: Bundle = Bundle()
                        bundle.putString("name", name)
                        bundle.putString("email", email)
                        bundle.putString("profileImg", image)
                        profile.arguments = bundle
                        changeFragment(profile, "profileFragment")
                        return@OnNavigationItemSelectedListener true
                    }
                }
            }
            return@OnNavigationItemSelectedListener false
        }

        mBottonNavigation.setOnNavigationItemSelectedListener(boolButtonNavigation)


    }

    private fun changeFragment(fragment: Fragment, tag:String)
    {

        if(currentTag != tag) {
            val transaction = supportFragmentManager.beginTransaction()

            val currentFragment = supportFragmentManager.findFragmentByTag(currentTag)
            val fragmentToReplaceByTag = supportFragmentManager.findFragmentByTag(tag)

            //Actualizamos los tags
            oldTag = currentTag
            currentTag = tag


            if (fragmentToReplaceByTag != null) {
                currentFragment?.let { transaction.hide(it).show(fragmentToReplaceByTag) }
            } else {
                currentFragment?.let { transaction.hide(it).add(R.id.container, fragment, tag) }
            }
            transaction.commit()

            //Añadimos a la pila
            stack.add(StateFragment(currentTag, oldTag))
        }
    }

    //Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {

        menuInflater.inflate(R.menu.search, menu)
        btnSearch = menu?.findItem(R.id.search)!!
        val searchView = btnSearch.actionView as SearchView
        searchView.queryHint = "Search View Hint"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                val fragment = SearchFragment.newInstance()
                val bundle: Bundle = Bundle()
                bundle.putString("category", "")
                bundle.putString("name", query)
                fragment.arguments = bundle
                changeFragment(fragment, "searchFragment")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        btnSearch.isVisible = true
        return super.onPrepareOptionsMenu(menu)
    }

    private fun loadFirstFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(
            R.id.container,
            HomeFragment.newInstance(),
            "homeFragment")
        transaction.commit()
    }

    private fun recoverFragment(){
        val lastState = stack.last()

        val transaction = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.findFragmentByTag(lastState.currentTag)
        val oldFragment = supportFragmentManager.findFragmentByTag(lastState.oldTag)

        if (currentFragment?.isVisible!! && oldFragment?.isHidden!!) {
            transaction.hide(currentFragment).show(oldFragment)
        }


        if(currentFragment is SearchFragment || currentFragment is EnterpriseFragment)
            transaction.remove(currentFragment)
        transaction.commit()

        stack.remove(stack.last())
        if (stack.isEmpty()) {
            currentTag = "homeFragment"
            oldTag = "homeFragment"
        } else {
            currentTag = stack.last().currentTag
            oldTag = stack.last().oldTag
        }
    }

    override fun onBackPressed() {

        if (stack.size >= 1) {
            recoverFragment()
        }else{
            if (presionado + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            }else
                Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();

            presionado = System.currentTimeMillis();
        }
    }

    private fun analytics()
    {

        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "MainActivity opened")
        analytics.logEvent("InitScreen", bundle)
    }

    override fun onStart()
    {
        super.onStart()

        if(mAuth.currentUser == null){
            finish()
        }
    }

    override fun actualizarStack(fragment: Fragment, tag:String) {
        changeFragment(fragment,tag)
    }

    override fun actualizarStackProfile(fragment: Fragment, tag: String) {
        changeFragment(fragment,tag)

        Toast.makeText(this, "entra", Toast.LENGTH_SHORT).show()
    }
}