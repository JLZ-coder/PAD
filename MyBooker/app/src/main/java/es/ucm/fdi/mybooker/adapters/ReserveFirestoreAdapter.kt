package es.ucm.fdi.mybooker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.itemReserve
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReserveFirestoreAdapter(options: FirestoreRecyclerOptions<itemReserve>) : FirestoreRecyclerAdapter<itemReserve, ReserveFirestoreAdapter.ReserveViewHolder>(options) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ReserveViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        val hora: TextView = view.findViewById(R.id.reserva_hora)
        val nombre: TextView = view.findViewById(R.id.reserva_nombre)
        val personas: TextView = view.findViewById(R.id.reserva_personas)

        fun render(reserva : itemReserve) {
            val millis = reserva?.hora?.time
            val formato = DateTimeFormatter.ofPattern("HH:mm")
            val hora_local = millis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC+2")).toLocalDateTime() }
            if (hora_local != null) {
                hora.text = hora_local.format(formato)
            }
            nombre.text = "A nombre de " + reserva.nombre_cliente.toString()
            personas.text = reserva.personas.toString() + " persona(s)"
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ReserveViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.reserve_row, viewGroup, false)

        return ReserveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReserveViewHolder, position: Int, model: itemReserve) {
        holder.render(model)
    }

    override fun onDataChanged() {
        itemCount
    }

}
