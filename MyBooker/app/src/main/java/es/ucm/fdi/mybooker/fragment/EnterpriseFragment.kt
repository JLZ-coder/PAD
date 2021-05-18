package es.ucm.fdi.mybooker.fragment

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.itemEnterprise
import java.lang.Exception
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EnterpriseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnterpriseFragment : Fragment() {
    private var db = FirebaseFirestore.getInstance()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var objeto: String
    lateinit var enterprise: itemEnterprise

    private val c = Calendar.getInstance()
    //Layout
    private lateinit var nameText: TextView
    private lateinit var locationText: TextView
    private lateinit var cpText: TextView
    private lateinit var categoryText: TextView

    private lateinit var date: EditText
    private lateinit var mRecycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enterprise, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            objeto = it.getString("objectEnterprise").toString()

        }
       var gson = Gson()

        enterprise = gson.fromJson(objeto, itemEnterprise::class.java)

        //Rellenamos cabecera
        nameText = view?.findViewById<TextView>(R.id.nameEmpress)!!
        locationText = view?.findViewById<TextView>(R.id.locationEmpress)!!
        cpText = view?.findViewById<TextView>(R.id.cpEmpress)!!
        categoryText = view?.findViewById<TextView>(R.id.categoryEmpress)!!

        nameText.text = enterprise.enterpriseName
        locationText.text = enterprise.enterpriseAddress
        cpText.text = enterprise.cp
        categoryText.text = enterprise.enterpriseCategory

        //Recycler view
        mRecycler = view?.findViewById<RecyclerView>(R.id.hoursFree)!!
        //Mostramos Dialog en editText

        date = view?.findViewById<EditText>(R.id.date)!!

        date.setOnClickListener(){
            showDatePiackerDialog()

        }

    }

    private fun showDatePiackerDialog() {
            val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
                date.setText(selectedDate)

                c.set(year,month,day)

                val numberDay = dayOfWeek(c.get(Calendar.DAY_OF_WEEK))

                recycler(selectedDate, numberDay)

            })
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun dayOfWeek(dayWeek:Int): Int{
        return when(dayWeek){
            1 -> 6
            2 -> 0
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 5
            else -> -1
        }
    }

    private fun recycler(selectDate:String, numberDay:Int){
        //Sacamos los horarios
        db.collection("shifts").whereEqualTo("id_enterprise", enterprise.empresaId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    
                }
                if(enterprises.isNotEmpty()) {
                    setAdapter(inflater.context, mRecyclerView)
                } else {
                    emptyText.visibility = View.VISIBLE
                    emptyText.text = "No se han encontrado empresas con esos parámetros"
                    mRecyclerView.visibility = View.GONE
                }
            }.addOnFailureListener { exception ->
                FirebaseCrashlytics.getInstance().recordException(Exception("ERROR: Error getting documents ${exception.message}"))
            }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EnterpriseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() : EnterpriseFragment = EnterpriseFragment()
    }
}

