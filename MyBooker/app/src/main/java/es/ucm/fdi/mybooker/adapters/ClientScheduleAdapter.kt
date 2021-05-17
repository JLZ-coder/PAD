package es.ucm.fdi.mybooker.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.fragment.ScheduleFragment
import es.ucm.fdi.mybooker.objects.itemClientSchedule

class ClientScheduleAdapter(private val element: List<itemClientSchedule>) : RecyclerView.Adapter<ClientScheduleAdapter.ScheduleHolder>()
{

    private var db = FirebaseFirestore.getInstance()

    override fun getItemCount(): Int = element.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder
    {

        val layoutInflater = LayoutInflater.from(parent.context)
        return ScheduleHolder(layoutInflater.inflate(R.layout.item_client_schedule, parent, false))
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int)
    {

        holder.itemView.setOnClickListener {
            alertDialog(it.context, element[position].dateId, element[position].userMail)
        }

        holder.render(element[position])
    }

    private fun alertDialog(context: Context, id: String, userMail: String)
    {

        val builder = this?.let { androidx.appcompat.app.AlertDialog.Builder(context) }
        builder.setTitle("¿Qué desea hacer?")
        builder.setPositiveButton("Eliminar cita"){ _, _ -> borrarCita(id, userMail, context) }
        builder.setNegativeButton("Cerrar", null)
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        dialog.show()
    }

    private fun borrarCita(id: String, userMail: String, c: Context)
    {

        db.collection("usersSchedule").document(userMail).collection("appointmentDetails")
            .document(id).delete().addOnSuccessListener {
                Toast.makeText(c, "Cita eliminada con exito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(c, "Error eliminando cita", Toast.LENGTH_SHORT).show()
                FirebaseCrashlytics.getInstance().recordException(Exception("No se ha podido borrar la cita $id del usuario $userMail"))
            }
    }

    class ScheduleHolder(val view: View): RecyclerView.ViewHolder(view)
    {

        private val entName = view.findViewById(R.id.entName) as TextView
        private val appointmentDate = view.findViewById(R.id.scheduleTime) as TextView
        private val entAddress = view.findViewById(R.id.location) as TextView

        fun render(enterprise: itemClientSchedule) {
            entName.text = enterprise.enterprise_name
            appointmentDate.text = enterprise.date.toString()
            entAddress.text = enterprise.address
        }
    }
}