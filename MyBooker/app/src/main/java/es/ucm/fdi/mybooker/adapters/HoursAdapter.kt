package es.ucm.fdi.mybooker.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
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
        holder.render(hours[position], inter,position)
    }

    interface onClickListener {
        fun openItemClick(hours: ItemHours, position: Int, checked :Boolean )
    }
    inner class HoursViewHolder (val view: View): RecyclerView.ViewHolder(view) {
        private var checkbox = view.findViewById<CheckBox>(R.id.checkbox)
        private var timeS = view.findViewById<TextView>(R.id.horaS)
        private var timeE = view.findViewById<TextView>(R.id.horaE)
        private var state = view.findViewById<TextView>(R.id.status)
        private var card = view.findViewById<CardView>(R.id.cardView)
        private var linear = view.findViewById<LinearLayout>(R.id.linear)

        fun render(hour: ItemHours, action: onClickListener, position: Int) {

            if(hour.state == "ocupado"){
                checkbox.isEnabled = false
                linear.setBackgroundColor(Color.RED)
            }
            timeS.text = hour.start
            timeE.text = hour.end
            state.text = hour.state

            checkbox.setOnCheckedChangeListener(){ buttonView, isChecked: Boolean ->
                //Si es seleccionado
                if(isChecked){
                    action.openItemClick(hour, position,true)
                }else{ //Si es deseleccionado se elimina de la lista
                    action.openItemClick(hour, position,false)
                }
            }

        }
    }
}

