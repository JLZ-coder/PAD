package es.ucm.fdi.mybooker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.fragment.EnterpriseFragment
import es.ucm.fdi.mybooker.objects.ItemHours

class HoursAdapter(var hours: List<ItemHours>, var inter: onClickListener): RecyclerView.Adapter<HoursAdapter.HoursViewHolder>() {
    // Devolvemos el tamaño de la lista de horas. Forma simplificada
    override fun getItemCount(): Int = hours.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder
    {

        val layoutInflater = LayoutInflater.from(parent.context)
        return HoursViewHolder(layoutInflater.inflate(R.layout.item_hours, parent, false))
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int)
    {
        holder.render(hours[position], inter)
    }

    interface onClickListener {
        fun openItemClick(hours: ItemHours, position: Int)
    }
    inner class HoursViewHolder (val view: View): RecyclerView.ViewHolder(view) {
        private var checkbox = view.findViewById<CheckBox>(R.id.checkbox)
        private var timeS = view.findViewById<TextView>(R.id.horaS)
        private var timeE = view.findViewById<TextView>(R.id.horaE)
        private var state = view.findViewById<TextView>(R.id.status)
        fun render(hour: ItemHours, action: onClickListener) {
            timeS.text = hour.start
            timeE.text = hour.end
            state.text = hour.state
        }
    }
}