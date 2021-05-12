package es.ucm.fdi.mybooker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.itemClientSchedule

class ClientScheduleAdapter(private val element: List<itemClientSchedule>) : RecyclerView.Adapter<ClientScheduleAdapter.ScheduleHolder>()
{

    override fun getItemCount(): Int = element.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder
    {

        val layoutInflater = LayoutInflater.from(parent.context)
        return ScheduleHolder(layoutInflater.inflate(R.layout.item_client_schedule, parent, false))
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int)
    {
        holder.render(element[position])
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