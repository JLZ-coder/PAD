package es.ucm.fdi.mybooker.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.ClientScheduleAdapter
import es.ucm.fdi.mybooker.objects.ItemClientSchedule
import es.ucm.fdi.mybooker.objects.itemEnterprise
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


// Lo utilizaremos para recuperar, de la base de datos, las citas que tenga el usuario
private const val ARG_PARAM1 = "uid"

/**
 * A simple [Fragment] subclass.
 * Use the [scheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment() : Fragment()
{

    private var uid: String? = ""
    private lateinit var agenda: MutableList<ItemClientSchedule>

    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        arguments?.let {
            this.uid = it.getString(ARG_PARAM1)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        val view: View = inflater.inflate(R.layout.fragment_schedule, container, false)

        val emptyText: TextView = view.findViewById(R.id.empty_view)
        emptyText.visibility = View.GONE

        val mRecyclerView : RecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        if (uid == "") {
            FirebaseCrashlytics.getInstance().recordException(Exception("EL MAIL HA LLEGADO VACÍO"))
        }

        uid.let {
            db.collection("reserves").whereEqualTo("id_cliente", uid)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    if (firebaseFirestoreException != null) {
                        FirebaseCrashlytics.getInstance().recordException(Exception("firebaseFirestoreException $firebaseFirestoreException"))
                        return@addSnapshotListener
                    }
                    agenda = ArrayList()
                    querySnapshot!!.documents.forEach {
                        val date: com.google.firebase.Timestamp = it.data?.get("hora") as com.google.firebase.Timestamp
                        val name: String = it.data?.get("ent_name").toString()
                        val location: String  = it.data?.get("ent_address").toString()

                        agenda.add(ItemClientSchedule("Fecha: ${date.toDate()}", "Cita con: $name", "Dirección: $location", it.id, uid!!))
                    }
                    if(agenda.isNotEmpty()) {
                        setAdapter(inflater.context, mRecyclerView)
                    } else {
                        emptyText.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                    }
                }
        }

        return view
    }

    private fun setAdapter(context: android.content.Context, mRecyclerView: RecyclerView)
    {

        val mAdapter : ClientScheduleAdapter = ClientScheduleAdapter(agenda)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = mAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(uid: String?): ScheduleFragment? {
            val myFragment = ScheduleFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, uid)
            myFragment.arguments = args
            return myFragment
        }
    }
}