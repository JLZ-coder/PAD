package es.ucm.fdi.mybooker.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        val lunes: TextView = view.findViewById(R.id.lunes)
        val martes: TextView = view.findViewById(R.id.martes)
        val miercoles: TextView = view.findViewById(R.id.miercoles)
        val jueves: TextView = view.findViewById(R.id.jueves)
        val viernes: TextView = view.findViewById(R.id.viernes)
        val sabado: TextView = view.findViewById(R.id.sabado)
        val domingo: TextView = view.findViewById(R.id.domingo)
        val list_of_days = listOf<TextView>(lunes, martes, miercoles, jueves, viernes, sabado, domingo)

        fun render(turno : itemShift) {
            val start_hours = turno.start?.hours.toString()
            val start_minutes = turno.start?.minutes.toString()
            val end_hours = turno.end?.hours.toString()
            val end_minutes = turno.end?.minutes.toString()

            start_end.text = "$start_hours:$start_minutes-$end_hours:$end_minutes"
            period.text = "Cada ${turno.period} min"
            turno.days?.forEach {
                list_of_days[it].setBackgroundColor(Color.YELLOW)
            }
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
    }

    override fun onDataChanged() {

    }

}
