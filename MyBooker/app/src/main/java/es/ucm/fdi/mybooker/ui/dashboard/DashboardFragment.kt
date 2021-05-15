package es.ucm.fdi.mybooker.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.ReserveFirestoreAdapter

class DashboardFragment : Fragment() {

  private lateinit var dashboardViewModel: DashboardViewModel
  private var mAdapter: ReserveFirestoreAdapter? = null

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
    mAdapter = dashboardViewModel.options?.let { ReserveFirestoreAdapter(it) }
    reservas.adapter = mAdapter

    mAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onItemRangeInserted(positionStart : Int, itemCount: Int) {
        val totalNumberOfItems = itemCount
        if (totalNumberOfItems > 0) {
          n_reservas.setText("Reservas para HOY")
        }
      }

      override fun onItemRangeRemoved(positionStart : Int, itemCount: Int) {
        val totalNumberOfItems = itemCount
        if (totalNumberOfItems <= 1) {
          n_reservas.setText("Aún no hay reservas para hoy")
        }
      }
    })


    val allreserves_btn: Button = root.findViewById(R.id.button_enterprise_allreserves)
    allreserves_btn.setOnClickListener { view ->
      view.findNavController().navigate(R.id.action_navigation_dashboard_to_navigation_allreserves)
    }

    return root
  }

  override fun onStart() {
    super.onStart()
    mAdapter!!.startListening()
  }

  override fun onStop() {
    super.onStop()
    mAdapter!!.stopListening()
  }
}