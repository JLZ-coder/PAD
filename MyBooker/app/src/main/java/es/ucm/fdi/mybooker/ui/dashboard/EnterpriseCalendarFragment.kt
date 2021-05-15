package es.ucm.fdi.mybooker.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.ReserveFirestoreAdapter
import es.ucm.fdi.mybooker.objects.itemReserve
import java.util.*


class EnterpriseCalendarFragment : Fragment() {
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid
    private lateinit var mAdapter: ReserveFirestoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_enterprise_calendar, container, false)
        val calendar: CalendarView = root.findViewById(R.id.calendarView_enterprise)
        val reservas_title: TextView = root.findViewById(R.id.TextView_enterprise_calendar)
        val reservas: RecyclerView = root.findViewById(R.id.RecyclerView_enterprise_calendar_reserve)

        calendar.setOnDateChangeListener(object : CalendarView.OnDateChangeListener {
            override fun onSelectedDayChange(
                view: CalendarView,
                year: Int,
                month: Int,
                dayOfMonth: Int
            ) {
                reservas_title.setText("No hay reservas para este día")
                val current_day = Calendar.getInstance(TimeZone.getTimeZone("UTC+2"))
                current_day.set(year, month, dayOfMonth)
                current_day.set(Calendar.HOUR_OF_DAY, 0)
                current_day.set(Calendar.MINUTE, 0);
                val start_of_day = current_day.time
                current_day.add(Calendar.DATE, 1)
                val end_of_day = current_day.time

                val query: Query = db.collection("reserves").whereGreaterThanOrEqualTo("hora", start_of_day)
                    .whereLessThan("hora", end_of_day)
                    .whereEqualTo("id_enterprise", userId)
                val options = FirestoreRecyclerOptions.Builder<itemReserve>().setQuery(query, itemReserve::class.java).build()
                reservas.layoutManager = LinearLayoutManager(activity)
                mAdapter.updateOptions(options)
            }
        })

        reservas_title.setText("No hay reservas para este día")
        val today = Calendar.getInstance(TimeZone.getTimeZone("UTC+2"))
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0);
        val start_of_day = today.time
        today.add(Calendar.DATE, 1)
        val end_of_day = today.time

        val query: Query = db.collection("reserves").whereGreaterThanOrEqualTo("hora", start_of_day)
            .whereLessThan("hora", end_of_day)
        val options = FirestoreRecyclerOptions.Builder<itemReserve>().setQuery(query, itemReserve::class.java).build()
        reservas.layoutManager = LinearLayoutManager(activity)
        mAdapter = ReserveFirestoreAdapter(options)
        reservas.adapter = mAdapter

        mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart : Int, itemCount: Int) {
                val totalNumberOfItems = itemCount
                if (totalNumberOfItems > 0) {
                    reservas_title.setText("Reservas para este día")
                }
            }
            override fun onItemRangeRemoved(positionStart : Int, itemCount: Int) {
                val totalNumberOfItems = itemCount
                if (totalNumberOfItems <= 1) {
                    reservas_title.setText("No hay reservas para este día")
                }
            }
        })


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