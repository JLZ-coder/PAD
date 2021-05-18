package es.ucm.fdi.mybooker.ui_enterprise.schedule

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.itemShift

class AddShiftFragment : Fragment() {

    companion object {
        fun newInstance() = AddShiftFragment()
    }

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid

    private lateinit var new_shift: itemShift
    private var is_editShift = false
    private lateinit var mArrayAdapter: ArrayAdapter<CharSequence>
    private lateinit var start_input: EditText
    private lateinit var end_input: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_shift_fragment, container, false)

        start_input = root.findViewById(R.id.editTextTime_shift_start)
        end_input = root.findViewById(R.id.editTextTime_shift_end)
        //val period: EditText = root.findViewById(R.id.editTextNumber_period)
        //val max_personas: EditText = root.findViewById(R.id.editTextNumber_maxPersonas)
        val lunes: CheckBox = root.findViewById(R.id.checkBox_lunes)
        val martes: CheckBox = root.findViewById(R.id.checkBox_martes)
        val miercoles: CheckBox = root.findViewById(R.id.checkBox_miercoles)
        val jueves: CheckBox = root.findViewById(R.id.checkBox_jueves)
        val viernes: CheckBox = root.findViewById(R.id.checkBox_viernes)
        val sabado: CheckBox = root.findViewById(R.id.checkBox_sabado)
        val domingo: CheckBox = root.findViewById(R.id.checkBox_domingo)
        val days = listOf<CheckBox>(lunes, martes, miercoles, jueves, viernes, sabado, domingo)
        val done_button: Button = root.findViewById(R.id.button_add_shift)
        val del_button: Button = root.findViewById(R.id.button_del_shift)
        val spinner: Spinner = root.findViewById(R.id.Spinner_period)

        if (arguments != null) {
            if (requireArguments().containsKey("shift_id") && requireArguments().containsKey("shift")) {
                is_editShift = true
            }
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        activity?.baseContext?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.duration_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
                mArrayAdapter = adapter
            }
        }
        var spinnerPosition = mArrayAdapter.getPosition("10")
        if (spinnerPosition == -1) spinnerPosition = 1
        spinner.setSelection(spinnerPosition)

        if (is_editShift) {
            val editShift = requireArguments().getSerializable("shift") as itemShift
            start_input.setText(editShift.start)
            end_input.setText(editShift.end)
            var spinnerPosition = mArrayAdapter.getPosition(editShift.period.toString())
            if (spinnerPosition == -1) spinnerPosition = 1
            spinner.setSelection(spinnerPosition)
            editShift.days?.forEach {
                days[it].isChecked = true
            }
            del_button.visibility = View.VISIBLE
        }

        done_button.setOnClickListener {view : View ->
            var ok = true
            val f_start = start_input.text.toString()
            val f_end = end_input.text.toString()
            if (!f_start.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$".toRegex())) {
                start_input.error = "La hora no está en formato correcto"
                ok = false
            }
            if (!f_end.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$".toRegex())) {
                end_input.error = "La hora no está en formato correcto"
                ok = false
            }
            val f_period = spinner.selectedItem.toString().toInt()
            //val f_max_personas = max_personas.text.toString().toInt()
            val f_days = mutableListOf<Int>()
            days.forEach() {
                if (it.isChecked) {
                    when(it.text) {
                        getString(R.string.lunes) -> f_days.add(0)
                        getString(R.string.martes) -> f_days.add(1)
                        getString(R.string.miercoles) -> f_days.add(2)
                        getString(R.string.jueves) -> f_days.add(3)
                        getString(R.string.viernes) -> f_days.add(4)
                        getString(R.string.sabado) -> f_days.add(5)
                        getString(R.string.domingo) -> f_days.add(6)
                    }
                }
            }

            if (ok) {
                val f_start_hour = f_start.split(":").get(0).toInt()
                val f_start_minute = f_start.split(":").get(1).toInt()
                new_shift = itemShift(userId, f_start, f_end, f_period, f_days)
                check_overlaps(new_shift, view)
            }
        }

        del_button.setOnClickListener {view ->
            val alert = AlertDialog.Builder(activity)
            alert.setTitle("Eliminar Horario")
            alert.setMessage("¿Estas seguro?")
            alert.setPositiveButton("SI, estoy seguro", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    val shift_id = requireArguments().getString("shift_id")
                    if (shift_id != null) {
                        db.collection("shifts").document(shift_id).delete()
                            .addOnSuccessListener {
                                Log.d("addShiftFragment", "DocumentSnapshot successfully deleted!")
                                view.findNavController().navigate(R.id.action_navigation_add_shift_to_navigation_home)
                            }
                            .addOnFailureListener { e -> Log.w("addShiftFragment", "Error deleting document", e) }
                    }
                }
            })
            alert.setNegativeButton("Cancelar", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                }
            })

            alert.show()
        }

        return root
    }

    private fun upload(new_shift: itemShift, view : View) {
        if (is_editShift) {
            val shift_id = requireArguments().getString("shift_id")
            if (shift_id != null) {
                db.collection("shifts").document(shift_id).set(new_shift)
                    .addOnSuccessListener {
                        Log.d("addShiftFragment", "DocumentSnapshot written with ID: $shift_id")
                        view.findNavController().navigate(R.id.action_navigation_add_shift_to_navigation_home)
                    }
                    .addOnFailureListener { e->
                        Log.w("addShiftFragment", "Error adding document", e)
                    }
            }
        }
        else {
            db.collection("shifts").add(new_shift)
                .addOnSuccessListener { documentReference ->
                    Log.d("addShiftFragment", "DocumentSnapshot written with ID: ${documentReference.id}")
                    view.findNavController().navigate(R.id.action_navigation_add_shift_to_navigation_home)
                }
                .addOnFailureListener { e ->
                    Log.w("addShiftFragment", "Error adding document", e)
                }
        }
    }

    private fun check_overlaps(new_shift: itemShift, view: View) {
        val start_hours = new_shift.start?.split(":")?.get(0)?.toInt()
        val start_minutes = new_shift.start?.split(":")?.get(1)?.toInt()
        val end_hours = new_shift.end?.split(":")?.get(0)?.toInt()
        val end_minutes = new_shift.end?.split(":")?.get(1)?.toInt()
        val start_time = start_hours!! * 60 + start_minutes!!
        val end_time = end_hours!! * 60 + end_minutes!!

        if (start_time >= end_time) {
            end_input.error = "La hora final debe ser superior al del comienzo"
            return
        }

        db.collection("shifts").whereEqualTo("id_enterprise", userId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var ok = true
                    for (document in it.getResult()!!) {
                        val doc_start_hours = document.getString("start")?.split(":")?.get(0)?.toInt()
                        val doc_start_minutes = document.getString("start")?.split(":")?.get(1)?.toInt()
                        val doc_end_hours = document.getString("end")?.split(":")?.get(0)?.toInt()
                        val doc_end_minutes = document.getString("end")?.split(":")?.get(1)?.toInt()
                        val doc_start_time = doc_start_hours!! * 60 + doc_start_minutes!!
                        val doc_end_time = doc_end_hours!! * 60 + doc_end_minutes!!
                        if (!is_editShift) {
                            val doc_days = document.get("days") as List<Int>
                            doc_days.forEach {
                                if (it in new_shift.days!!) {
                                    if (start_time < doc_end_time && end_time > doc_start_time) {
                                        ok = false
                                    }
                                }
                            }
                            if (!ok) break
                        }
                        else if (document.id != requireArguments().getString("shift_id")){
                            val doc_days = document.get("days") as List<Int>
                            doc_days.forEach {
                                if (it in new_shift.days!!) {
                                    if (start_time < doc_end_time && end_time > doc_start_time) {
                                        ok = false
                                    }
                                }
                            }
                            if (!ok) break
                        }
                    }
                    if (ok) upload(new_shift, view)
                    else Toast.makeText(activity, "Las horas solapan con otros horarios", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(activity, "Firebase no respondió", Toast.LENGTH_LONG).show()
                }
            }
    }
}