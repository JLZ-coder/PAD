package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.adapters.EnterpriseAdapter
import es.ucm.fdi.mybooker.objects.itemEnterprise


enum class ProviderType {
    MAIL
}

class MainActivity : AppCompatActivity()
{
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mLogoutbtn : Button

    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLogoutbtn = findViewById(R.id.logout)
        mLogoutbtn.setOnClickListener() {
            mAuth.signOut()
            val i = Intent(this, ActivityLogin::class.java)
            startActivity(i)
        }

        analytics();

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        val name = bundle?.getString("name")

        setUpRecyclerView()
        // setUp(email ?: "no user found", provider ?: "empty", name ?: "no name")
    }

    private fun setUpRecyclerView()
    {
        val mAdapter : EnterpriseAdapter = EnterpriseAdapter(getSuperheros())

        mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
    }

    private fun analytics()
    {

        // Trozo analytics
        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "MainActivity opened")
        analytics.logEvent("InitScreen", bundle)
    }

    private fun setUp(email: String, provider: String, name: String)
    {
        title = "Inicio"

        val text = email + " " + provider + " " + name
        val textRefill = findViewById<TextView>(R.id.textRefill)
        textRefill.setText(text)
    }

    // Código para pruebas
    fun getSuperheros(): MutableList<itemEnterprise>
    {

        var empresas:MutableList<itemEnterprise> = ArrayList()
        empresas.add(itemEnterprise("https://firebasestorage.googleapis.com/v0/b/mybooker-6c774.appspot.com/o/fariolen.jpeg?alt=media&token=97015846-6787-4ecb-9f76-059dd42e97d9", "Empresa 1", "Calle 1", "Peluqueria"))
        empresas.add(itemEnterprise("https://firebasestorage.googleapis.com/v0/b/mybooker-6c774.appspot.com/o/fariolen.jpeg?alt=media&token=97015846-6787-4ecb-9f76-059dd42e97d9", "Empresa 2", "Calle 2", "Gym"))
        empresas.add(itemEnterprise("https://firebasestorage.googleapis.com/v0/b/mybooker-6c774.appspot.com/o/fariolen.jpeg?alt=media&token=97015846-6787-4ecb-9f76-059dd42e97d9", "Empresa 3", "Calle 3", "Belleza"))
        empresas.add(itemEnterprise("https://firebasestorage.googleapis.com/v0/b/mybooker-6c774.appspot.com/o/fariolen.jpeg?alt=media&token=97015846-6787-4ecb-9f76-059dd42e97d9", "Empresa 4", "Calle 4", "IT"))
        empresas.add(itemEnterprise("https://firebasestorage.googleapis.com/v0/b/mybooker-6c774.appspot.com/o/fariolen.jpeg?alt=media&token=97015846-6787-4ecb-9f76-059dd42e97d9", "Empresa 5", "Calle 5", "IT"))
        empresas.add(itemEnterprise("https://firebasestorage.googleapis.com/v0/b/mybooker-6c774.appspot.com/o/fariolen.jpeg?alt=media&token=97015846-6787-4ecb-9f76-059dd42e97d9", "Empresa 6", "Calle 6", "Padel"))
        empresas.add(itemEnterprise("https://firebasestorage.googleapis.com/v0/b/mybooker-6c774.appspot.com/o/fariolen.jpeg?alt=media&token=97015846-6787-4ecb-9f76-059dd42e97d9", "Empresa 7", "Calle 7", "Piscina"))

        return empresas
    }
}