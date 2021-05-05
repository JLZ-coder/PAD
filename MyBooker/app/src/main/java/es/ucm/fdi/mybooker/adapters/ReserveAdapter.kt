package es.ucm.fdi.mybooker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.itemReserve

class ReserveAdapter(private val dataSet: List<itemReserve>) : RecyclerView.Adapter<ReserveAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        val hora: TextView = view.findViewById(R.id.reserva_hora)
        val nombre: TextView = view.findViewById(R.id.reserva_nombre)
        val personas: TextView = view.findViewById(R.id.reserva_personas)

        fun render(reserva : itemReserve) {
            hora.text = reserva.hora
            nombre.text = reserva.nombre
            personas.text = reserva.personas.toString()
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.reserve_row, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.textView.text = dataSet[position].enterpriseImg
        viewHolder.render(dataSet[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
