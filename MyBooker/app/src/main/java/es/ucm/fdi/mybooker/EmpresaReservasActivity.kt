package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import es.ucm.fdi.mybooker.adapters.ReserveFirestoreAdapter
import es.ucm.fdi.mybooker.databinding.ActivityEmpresaReservasBinding
import es.ucm.fdi.mybooker.objects.itemEnterprise_2
import es.ucm.fdi.mybooker.objects.itemReserve


class EmpresaReservasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmpresaReservasBinding
    //private lateinit var reserveAdapter: ReserveAdapter
    private var adapter: ReserveFirestoreAdapter? = null
    private lateinit var userInfo: itemEnterprise_2

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa_reservas)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_empresa_reservas)

        val from_login:Bundle? = intent.extras
        val userId = from_login?.getString("userId")
        userInfo = getUserInfo(userId)
        //getReservas(userId)

        val query: Query = db.collection("reserves").whereEqualTo("id_enterprise", userId)
        val options = FirestoreRecyclerOptions.Builder<itemReserve>().setQuery(query, itemReserve::class.java).build()
        adapter = ReserveFirestoreAdapter(options)
        binding.empresaResumen.layoutManager = LinearLayoutManager(this)
        binding.empresaResumen.adapter = adapter

        //setUp(userInfo)
        // Update the timestamp field with the value from the server
        /*val updates = hashMapOf<String, Any>(
            "hora" to FieldValue.serverTimestamp()
        )

        db.collection("reserves").whereEqualTo("id_enterprise", userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    val documents: QuerySnapshot? = task.getResult()
                    if (documents != null) {
                        for (document in documents) {
                            document.reference.update(updates)
                        }

                    }
                    else {
                        mAuth.signOut()
                        val i = Intent(this, ActivityLogin::class.java)
                        startActivity(i)
                    }
                }
            }*/
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

    private fun gotoHorario() {
        val i = Intent(this, EmpresaHorarioActivity::class.java).apply {
            putExtra("userInfo", userInfo)
        }
        startActivity(i)
    }

    private fun gotoSettings() {
        val i = Intent(this, EmpresaSettingsActivity::class.java).apply {
            putExtra("userInfo", userInfo)
        }
        startActivity(i)
    }

    private fun setUp(userInfo: itemEnterprise_2) {
        binding.empresaTitle.text = userInfo.name.toString()

        binding.logout.setOnClickListener() {
            mAuth.signOut()
            val i = Intent(this, ActivityLogin::class.java)
            startActivity(i)
        }

        val boolBottonNavigation = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> return@OnNavigationItemSelectedListener true
                R.id.navigation_dashboard -> gotoHorario()
                R.id.navigation_profile -> gotoSettings()
            }
            false
        }
        binding.navigationView.setOnNavigationItemSelectedListener(boolBottonNavigation)
    }

    private fun getUserInfo(userId: String?): itemEnterprise_2 {
        var Info = itemEnterprise_2("userId","name", "email", "category", "location", 1)
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful()) {
                        val document: DocumentSnapshot? = task.getResult()
                        if (document != null) {
                            val name = document.getString("name")
                            val email = document.getString("email")
                            val cp = document.get("cp") as Long
                            val category = document.getString("category")
                            val location = document.getString("location")

                            Info = itemEnterprise_2(userId, name, email, category, location, cp)
                            setUp(Info)
                        } else {
                            mAuth.signOut()
                            val i = Intent(this, ActivityLogin::class.java)
                            startActivity(i)
                        }
                    }
                }
        }
        return Info
    }

}