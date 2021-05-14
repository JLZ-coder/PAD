package es.ucm.fdi.mybooker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.R

class AddShiftFragment : Fragment() {

    companion object {
        fun newInstance() = AddShiftFragment()
    }

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid

    private lateinit var viewModel: AddShiftViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_shift_fragment, container, false)

        val start_input: EditText = root.findViewById(R.id.editTextTime_shift_start)
        val end_input: EditText = root.findViewById(R.id.editTextTime_shift_end)
        val period: EditText = root.findViewById(R.id.editTextNumber_period)
        val lunes: CheckBox = root.findViewById(R.id.checkBox_lunes)
        val martes: CheckBox = root.findViewById(R.id.checkBox_martes)
        val miercoles: CheckBox = root.findViewById(R.id.checkBox_miercoles)
        val jueves: CheckBox = root.findViewById(R.id.checkBox_jueves)
        val viernes: CheckBox = root.findViewById(R.id.checkBox_viernes)
        val sabado: CheckBox = root.findViewById(R.id.checkBox_sabado)
        val domingo: CheckBox = root.findViewById(R.id.checkBox_domingo)
        val days = listOf<CheckBox>(lunes, martes, miercoles, jueves, viernes, sabado, domingo)
        val done_button: Button = root.findViewById(R.id.button_add_shift)

        done_button.setOnClickListener() {
            
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddShiftViewModel::class.java)
        // TODO: Use the ViewModel
    }

}