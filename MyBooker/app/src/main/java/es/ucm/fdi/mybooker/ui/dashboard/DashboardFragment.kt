package es.ucm.fdi.mybooker.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.mybooker.ActivityLogin
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.ReserveFirestoreAdapter

class DashboardFragment : Fragment() {

  private lateinit var dashboardViewModel: DashboardViewModel
  private var adapter: ReserveFirestoreAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val userId = activity?.intent?.extras?.getString("userId")
    dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

    val root = inflater.inflate(R.layout.fragment_enterprise_dashboard, container, false)

    val title: TextView = root.findViewById(R.id.empresa_title)
    dashboardViewModel.title.observe(viewLifecycleOwner, Observer {
      title.text = it
    })

    val n_reservas: TextView = root.findViewById(R.id.empresa_num_reservas)
    dashboardViewModel.n_reservas.observe(viewLifecycleOwner, Observer {
      n_reservas.text = it
    })

    val reservas: RecyclerView = root.findViewById(R.id.empresa_resumen)
    reservas.layoutManager = LinearLayoutManager(activity)
    reservas.adapter = dashboardViewModel.firebase_adapter
    adapter = dashboardViewModel.firebase_adapter

    val btn_logout: Button = root.findViewById(R.id.logout_enterprise)
    btn_logout.setOnClickListener() {
      dashboardViewModel.mAuth.signOut()
      val i = Intent(activity, ActivityLogin::class.java)
      startActivity(i)
    }

    return root
  }

  override fun onStart() {
    super.onStart()
    adapter!!.startListening()
  }

  override fun onStop() {
    super.onStop()
    adapter!!.stopListening()
  }
}