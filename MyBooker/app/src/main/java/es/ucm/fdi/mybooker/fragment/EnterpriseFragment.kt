package es.ucm.fdi.mybooker.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import es.ucm.fdi.mybooker.MainActivity
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.HoursAdapter
import es.ucm.fdi.mybooker.objects.ItemHours
import es.ucm.fdi.mybooker.objects.itemEnterprise
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EnterpriseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnterpriseFragment : Fragment(), HoursAdapter.onClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //Firebase
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid

    private lateinit var objeto: String
    lateinit var enterprise: itemEnterprise

    private var c = Calendar.getInstance(TimeZone.getTimeZone("UTC+2"))

    private lateinit var hours: MutableList<ItemHours>
    private lateinit var hoursAdap: MutableList<ItemHours>
    private lateinit var listReserva: MutableList<String>
    //Layout
    private lateinit var nameText: TextView
    private lateinit var locationText: TextView
    private lateinit var cpText: TextView
    private lateinit var categoryText: TextView

    private lateinit var date: EditText
    private lateinit var mRecycler: RecyclerView
    private lateinit var emptyText: TextView

    private lateinit var reservar: Button
    private lateinit var numberPer: EditText

    //Lista de checked
    private var checkList: MutableMap<Int,ItemHours> = mutableMapOf<Int,ItemHours>()

    //Dia seleccionado
    private  var selectedDate :String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Reserva"
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enterprise, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var day = selectedDate
        outState.putString("dia",day)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            objeto = it.getString("objectEnterprise").toString()

        }
        var gson = Gson()
        enterprise = gson.fromJson(objeto, itemEnterprise::class.java)
        if(savedInstanceState != null){

            var recoverDay: String = savedInstanceState?.get("dia") as String

            if(recoverDay != ""){
                var splitDay = recoverDay.split("-")
                selectedDate = recoverDay
                c = Calendar.getInstance(TimeZone.getTimeZone("UTC+2"))

                this.c.set(splitDay[2].toInt(),splitDay[1].toInt()-1,splitDay[0].toInt())

                recycler(recoverDay)
            }
        }

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
        emptyText = view?.findViewById(R.id.empty_view)!!
        //Mostramos Dialog en editText

        date = view?.findViewById<EditText>(R.id.date)!!

        date.setOnClickListener(){
            showDatePiackerDialog()
        }
        //Button reservar
        reservar = view?.findViewById<Button>(R.id.reservar)!!
        //Numero personas
        numberPer = view?.findViewById<EditText>(R.id.person)!!

        reservar.setOnClickListener(){
            if(checkList.isEmpty()){
                Toast.makeText(this@EnterpriseFragment.context, "Seleccione al menos una hora", Toast.LENGTH_SHORT).show()
            }else if(numberPer.text.isEmpty()){
                Toast.makeText(this@EnterpriseFragment.context, "Debe especificar el n??mero de personas", Toast.LENGTH_SHORT).show()
            }else if(c < Calendar.getInstance()){
                Toast.makeText(this@EnterpriseFragment.context, "D??a incorrecto", Toast.LENGTH_SHORT).show()
            }
            else{
                var listaDefinitva = ArrayList<ItemHours>()
                for((k,o) in checkList){
                    listaDefinitva.add(o)
                }
                listaDefinitva.sortBy {
                        itemHours ->  itemHours.start
                }
                showReservateDialog(listaDefinitva)
            }
        }
    }

    private fun showReservateDialog(list: ArrayList<ItemHours>){
        val newFragment = ReservateFragment.newInstance(list)
        newFragment.show(requireActivity().supportFragmentManager, "completeReserva")
    }

    private fun showDatePiackerDialog() {
            val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
               selectedDate = day.toString() + "-" + (month + 1) + "-" + year
                date.setText(selectedDate)

                c.set(year,month,day)
                recycler(selectedDate)

            })
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun recycler(selectDate:String){

        //Sacamos los horarios
        db.collection("shifts").whereEqualTo("id_enterprise", enterprise.empresaId).get()
            .addOnSuccessListener { documents ->
                val numberDay : Int = (c.get(Calendar.DAY_OF_WEEK) + 7 - c.firstDayOfWeek) % 7
                c.set(Calendar.HOUR_OF_DAY, 0)
                c.set(Calendar.MINUTE, 0);
                val startOfDay = c.time
                val aux = c
                aux.add(Calendar.DATE, 1)
                val endOfDay = c.time

                this.hours = ArrayList()

                for (document in documents) {
                   val aux : List<Int> = document.get("days") as List<Int>
                    var ok = false
                    var i = 0
                    while(i < aux.size && !ok){
                        if(aux[i] == numberDay) ok = true
                        i+=1
                    }

                    if(ok) {
                        hours.add(
                            ItemHours(
                                document.getString("start").toString(),
                                document.getString("end").toString(),
                                "libre",
                                document.getLong("period")?.toInt()!!
                            )
                        )
                    }
                }

                //Ordenamos seg??n la hora de start para mostrar en el layout por orden
                hours.sortBy {
                    itemHours ->  itemHours.start
                }

                this.listReserva = ArrayList()
                this.hoursAdap = ArrayList()

                db.collection("reserves").whereGreaterThanOrEqualTo("hora", startOfDay)
                    .whereLessThan("hora", endOfDay).whereEqualTo("id_enterprise", enterprise.empresaId).get()
                    .addOnSuccessListener{ documents->
                        //Buscamos en las reservas si existe alguna otra reserva para ponerla como ocupada
                        for(document in documents){
                            val reservation = Calendar.getInstance(Locale.ENGLISH)
                            val hora : Timestamp = document.getTimestamp("hora") as Timestamp

                            reservation.timeInMillis = hora.seconds * 1000
                            val date = DateFormat.format("H:m",reservation).toString()
                            this.listReserva.add(date)

                        }
                        //Ordenamos la lista
                        this.listReserva.sort()

                        //Creamos lista definitiva de horas disponibles
                        lastListHours()

                        //Llamar a adapter y mostrar por pantalla
                        if(hoursAdap.isNotEmpty()) {
                            setAdapter(layoutInflater.context, mRecycler)
                        } else {
                            setNotFoundView()
                        }

                    }.addOnFailureListener { exception ->
                        FirebaseCrashlytics.getInstance().recordException(Exception("ERROR: Error getting documents ${exception.message}"))
                    }
            }.addOnFailureListener { exception ->
                FirebaseCrashlytics.getInstance().recordException(Exception("ERROR: Error getting documents ${exception.message}"))
            }
    }

    private fun setNotFoundView()
    {
        emptyText.visibility = View.VISIBLE
        emptyText.text = "No hay horas disponibles"
        mRecycler.visibility = View.GONE
    }

    private fun setAdapter(context: Context, mRecyclerView: RecyclerView)
    {
        val mAdapter = HoursAdapter(hoursAdap, this)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = mAdapter
        emptyText.visibility = View.GONE
        mRecycler.visibility = View.VISIBLE
    }

    private fun lastListHours(){
        val start = Calendar.getInstance()
        start.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH))
        start.set(Calendar.MONTH, c.get(Calendar.MONTH))
        start.set(Calendar.YEAR, c.get(Calendar.YEAR))
        val end = Calendar.getInstance()
        end.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH))
        end.set(Calendar.MONTH, c.get(Calendar.MONTH))
        end.set(Calendar.YEAR, c.get(Calendar.YEAR))

        //Trataremos si hay mas de dos turnos
        for(i in hours){
            val splitStart = i.start.split(":")
            val splitEnd = i.end.split(":")
            //Horas
            start.set(Calendar.HOUR_OF_DAY,splitStart[0].toInt())
            end.set(Calendar.HOUR_OF_DAY,splitEnd[0].toInt())
            //Minutos
            start.set(Calendar.MINUTE,splitStart[1].toInt())
            end.set(Calendar.MINUTE,splitEnd[1].toInt())
            while(start < end){
                val horaS :String= start.get(Calendar.HOUR_OF_DAY).toString() + ":" + start.get(Calendar.MINUTE).toString()
                start.add(Calendar.MINUTE,i.period)
                val horaF :String= start.get(Calendar.HOUR_OF_DAY).toString() + ":" + start.get(Calendar.MINUTE).toString()

                var state = ""

                var ok = false
                var j = 0

                while(j < this.listReserva.size && !ok){

                    if(listReserva[j] == horaS) ok = true
                    j+=1
                }

                state = if(ok)
                    "ocupado"
                else
                    "libre"

                hoursAdap.add(ItemHours(horaS,horaF,state,i.period))
            }
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


    override fun openItemClick(hours: ItemHours, position: Int, checked: Boolean) {
       if(checked){//Se a??ade a la lista
           checkList[position] = hours
       }else{
            checkList.remove(position)
       }
    }

     fun onDialogPositiveClick(dialog: DialogFragment) {
        var name = ""
         db.collection("users").document(userId).get()
             .addOnCompleteListener() {
                 if(it.isSuccessful){
                     val document = it.getResult()
                     if (document != null) {
                         name = document.getString("name").toString()
                         addReserve(name)
                     }
                 }
             }
    }

    private fun addReserve(name:String){
        for((k,v) in checkList){
            /*
            * Hora -> Date
            * idCliente -> String
            * idEmpres -> String
            * Nombre -> String
            * N?? personas -> Int
            * */

            var horaCalendar = Calendar.getInstance()

            var splitDay = selectedDate.split("-")
            horaCalendar.set(splitDay[2].toInt(),splitDay[1].toInt()-1,splitDay[0].toInt())

            var horaSplit = v.start.split(":")
            horaCalendar.set(Calendar.HOUR_OF_DAY, horaSplit[0].toInt())
            horaCalendar.set(Calendar.MINUTE, horaSplit[1].toInt())
            horaCalendar.set(Calendar.SECOND, 0)
            horaCalendar.set(Calendar.MILLISECOND,0)
            //Formato timestamp
            val cal = horaCalendar.timeInMillis
            Toast.makeText(this@EnterpriseFragment.context, "Reserva completada", Toast.LENGTH_SHORT).show()
            val timestampStart = Timestamp(Date(cal))
            db.collection("reserves").add(
                mapOf(
                    "hora" to timestampStart,
                    "id_cliente" to userId,
                    "id_enterprise" to enterprise.empresaId,
                    "nombre_cliente" to name,
                    "personas" to numberPer.text.toString().toInt(),
                    "ent_name" to enterprise.enterpriseName,
                    "ent_address" to enterprise.enterpriseAddress
                )
            )
        }
        Toast.makeText(this@EnterpriseFragment.context, "Reserva completada", Toast.LENGTH_SHORT).show()
        val homeIntent = Intent(this@EnterpriseFragment.context, MainActivity::class.java)
        homeIntent.putExtra("userName", name)
        homeIntent.putExtra("email", mAuth.currentUser.email)
        homeIntent.putExtra("profileImg", mAuth.currentUser.photoUrl)

        startActivity(homeIntent);
    }
}

