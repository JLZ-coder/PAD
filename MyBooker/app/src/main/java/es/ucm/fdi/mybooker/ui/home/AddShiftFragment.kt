package es.ucm.fdi.mybooker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    private lateinit var viewModel: AddShiftViewModel
    private lateinit var new_shift: itemShift

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_shift_fragment, container, false)

        val start_input: EditText = root.findViewById(R.id.editTextTime_shift_start)
        val end_input: EditText = root.findViewById(R.id.editTextTime_shift_end)
        val period: EditText = root.findViewById(R.id.editTextNumber_period)
        val max_personas: EditText = root.findViewById(R.id.editTextNumber_maxPersonas)
        val lunes: CheckBox = root.findViewById(R.id.checkBox_lunes)
        val martes: CheckBox = root.findViewById(R.id.checkBox_martes)
        val miercoles: CheckBox = root.findViewById(R.id.checkBox_miercoles)
        val jueves: CheckBox = root.findViewById(R.id.checkBox_jueves)
        val viernes: CheckBox = root.findViewById(R.id.checkBox_viernes)
        val sabado: CheckBox = root.findViewById(R.id.checkBox_sabado)
        val domingo: CheckBox = root.findViewById(R.id.checkBox_domingo)
        val days = listOf<CheckBox>(lunes, martes, miercoles, jueves, viernes, sabado, domingo)
        val done_button: Button = root.findViewById(R.id.button_add_shift)

        done_button.setOnClickListener {view : View ->
            val f_start = start_input.text.toString()
            val f_end = end_input.text.toString()
            val f_period = period.text.toString().toInt()
            val f_max_personas = max_personas.text.toString().toInt()
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

            new_shift = itemShift(userId, f_start, f_end, f_period, f_max_personas, f_days)
            db.collection("shifts").add(new_shift)
                .addOnSuccessListener { documentReference ->
                    Log.d("addShiftFragment", "DocumentSnapshot written with ID: ${documentReference.id}")
                    view.findNavController().navigate(R.id.action_navigation_add_shift_to_navigation_home)
                }
                .addOnFailureListener { e ->
                    Log.w("addShiftFragment", "Error adding document", e)
                }
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddShiftViewModel::class.java)
        // TODO: Use the ViewModel
    }

}