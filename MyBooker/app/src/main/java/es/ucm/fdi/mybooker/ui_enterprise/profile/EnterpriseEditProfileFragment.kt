package es.ucm.fdi.mybooker.ui_enterprise.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.ActivityLogin
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.itemEnterprise_2


class EnterpriseEditProfileFragment : Fragment() {

  private var db = FirebaseFirestore.getInstance()
  private var mAuth = FirebaseAuth.getInstance()
  private val userId = mAuth.currentUser.uid

  private lateinit var new_enterprise: itemEnterprise_2
  private lateinit var mArrayAdapter: ArrayAdapter<CharSequence>
  private lateinit var aux_enterprise: itemEnterprise_2

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    val root = inflater.inflate(R.layout.fragment_enterprise_edit_profile, container, false)
    val name_editText: EditText = root.findViewById(R.id.editText_enterprise_editprofile_name)
    val cp_editText: EditText = root.findViewById(R.id.editText_enterprise_editprofile_cp)
    val location_editText: EditText = root.findViewById(R.id.editText_enterprise_editprofile_location)
    val spinner: Spinner = root.findViewById(R.id.spinner_enterprise_editprofile_category)
    val confirm_btn: Button = root.findViewById(R.id.button_enterprise_editprofile_confirm)
    val delete_btn: Button = root.findViewById(R.id.button_enterprise_editprofile_delete)

    // Create an ArrayAdapter using the string array and a default spinner layout
    activity?.baseContext?.let {
      ArrayAdapter.createFromResource(
        it,
        R.array.categorias,
        android.R.layout.simple_spinner_item
      ).also { adapter ->
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner.adapter = adapter
        mArrayAdapter = adapter
      }
    }

    if (arguments != null) {
      if (requireArguments().containsKey("enterprise")) {
        aux_enterprise = requireArguments().getSerializable("enterprise") as itemEnterprise_2
        name_editText.setText(aux_enterprise.name)
        cp_editText.setText(aux_enterprise.cp.toString())
        location_editText.setText(aux_enterprise.location)
        var spinnerPosition = mArrayAdapter.getPosition(aux_enterprise.category)
        if (spinnerPosition == -1) spinnerPosition = 1
        spinner.setSelection(spinnerPosition)
      }
    }

    confirm_btn.setOnClickListener {view ->
      var ok = true
      val f_name = name_editText.text.toString()
      val f_cp = cp_editText.text.toString()
      val f_location = location_editText.text.toString()
      val f_category = spinner.selectedItem.toString()

      new_enterprise = itemEnterprise_2(userId, f_name, f_name.toLowerCase(), aux_enterprise.email, f_category, f_location, f_cp.toInt())
      db.collection("enterprises").document(userId).set(new_enterprise)
        .addOnSuccessListener {
          Log.d("EnterpriseEditProfileFragment", "DocumentSnapshot written with ID: $userId")
          view.findNavController().navigate(R.id.action_enterpriseEditProfileFragment_to_navigation_notifications)
        }
        .addOnFailureListener { e->
          Log.w("EnterpriseEditProfileFragment", "Error adding document", e)
          Toast.makeText(activity?.baseContext, "Hubo un problema al guardar los datos", Toast.LENGTH_LONG)
        }
    }

    delete_btn.setOnClickListener { view ->
      val alert = AlertDialog.Builder(activity)
      alert.setTitle("Eliminar cuenta")
      alert.setMessage("¿Estas seguro?")
      alert.setPositiveButton("SI, estoy seguro", object: DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
          db.collection("users").document(userId).delete()
            .addOnSuccessListener {
              FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener {task->
                if(task.isSuccessful) {
                  val i = Intent(activity, ActivityLogin::class.java)
                  startActivity(i)
                }
                else {
                  Toast.makeText(activity?.baseContext, "Hubo un problema al eliminar al usuario", Toast.LENGTH_LONG)
                  if (dialog != null) {
                    dialog.dismiss()
                  }
                }
              }
            }
        }
      })
      alert.setNegativeButton("Cancelar", object: DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
          if (dialog != null) {
            dialog.dismiss()
          }
        }
      })

      alert.show()
    }

    return root
  }
}