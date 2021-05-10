package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.adapters.ReserveAdapter
import es.ucm.fdi.mybooker.databinding.ActivityEmpresaReservasBinding
import es.ucm.fdi.mybooker.objects.itemReserve

class EmpresaReservasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmpresaReservasBinding
    private lateinit var reserveAdapter: ReserveAdapter

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa_reservas)



        binding = DataBindingUtil.setContentView(this, R.layout.activity_empresa_reservas)

        val boolBottonNavigation = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> return@OnNavigationItemSelectedListener true
                R.id.navigation_dashboard -> gotoHorario()
                R.id.navigation_profile -> gotoSettings()
            }
            false
        }
        binding.navigationView.setOnNavigationItemSelectedListener(boolBottonNavigation)

        binding.empresaTitle.text = "name"

        val uno = itemReserve("15:00", "YO", 1)
        val dos = itemReserve("15:10", "YO TAMBIEN", 1)
        reserveAdapter = ReserveAdapter(listOf(
            uno, dos
        ))
        binding.empresaResumen.layoutManager = LinearLayoutManager(this)
        binding.empresaResumen.adapter = reserveAdapter

        binding.logout.setOnClickListener() {
            mAuth.signOut()
            val i = Intent(this, ActivityLogin::class.java)
            startActivity(i)
        }
    }

    private fun gotoHorario() {
        val i = Intent(this, EmpresaHorarioActivity::class.java).apply {
            putExtra("userId", findViewById<EditText>(R.id.editTextUser).text.toString())
        }
        i.putExtra("tipoUsuario", "usuario")
        startActivity(i)
    }

    private fun gotoSettings() {

    }

}