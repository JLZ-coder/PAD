package es.ucm.fdi.mybooker.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.itemShift

class ShiftFirestoreAdapter(options: FirestoreRecyclerOptions<itemShift>) : FirestoreRecyclerAdapter<itemShift, ShiftFirestoreAdapter.ShiftViewHolder>(options) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ShiftViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        val start_end: TextView = view.findViewById(R.id.start_end)
        val period: TextView = view.findViewById(R.id.period)
        val max_personas: TextView = view.findViewById(R.id.max_personas)
        val lunes: TextView = view.findViewById(R.id.lunes)
        val martes: TextView = view.findViewById(R.id.martes)
        val miercoles: TextView = view.findViewById(R.id.miercoles)
        val jueves: TextView = view.findViewById(R.id.jueves)
        val viernes: TextView = view.findViewById(R.id.viernes)
        val sabado: TextView = view.findViewById(R.id.sabado)
        val domingo: TextView = view.findViewById(R.id.domingo)
        val list_of_days = listOf<TextView>(lunes, martes, miercoles, jueves, viernes, sabado, domingo)

        fun render(turno : itemShift) {
            val start_hours = turno.start?.substringBefore(":")
            val start_minutes = turno.start?.substringAfter(":")
            val end_hours = turno.end?.substringBefore(":")
            val end_minutes = turno.end?.substringAfter(":")

            start_end.text = "$start_hours:$start_minutes-$end_hours:$end_minutes"
            period.text = "Cada ${turno.period.toString()} min"
            //max_personas.text = "${turno.max_personas.toString()} personas por cita"
            turno.days?.forEach {
                list_of_days[it].setBackgroundColor(Color.YELLOW)
            }
            /*itemView.setOnClickListener {
                clickListener(turno)
                val shift_id =
                val bundle = bundleOf("shift" to turno, "shift_id" to )
                it.findNavController().navigate(R.id.action_navigation_add_shift_to_navigation_home, )
            }*/
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ShiftViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_shift, viewGroup, false)


        return ShiftViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int, model: itemShift) {
        holder.render(model)
        holder.itemView.setOnClickListener {
            val shift_id = snapshots.getSnapshot(position).id
            val bundle = bundleOf("shift" to model, "shift_id" to shift_id)
            it.findNavController().navigate(R.id.action_navigation_home_to_navigation_add_shift, bundle)
        }
    }

    override fun onDataChanged() {

    }

}
