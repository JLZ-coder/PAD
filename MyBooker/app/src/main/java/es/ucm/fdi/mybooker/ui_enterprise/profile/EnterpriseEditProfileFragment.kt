package es.ucm.fdi.mybooker.ui_enterprise.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
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
  private lateinit var loading: ProgressBar
  private lateinit var profile_pic: ImageView
  private lateinit var change_profilepic_btn: Button
  private val mStorage: StorageReference = FirebaseStorage.getInstance().reference
  private var image_src: String? = null
  private val GALLERY_INTENT = 1

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
    loading = root.findViewById(R.id.progress_bar_enterprise_editprofile)
    profile_pic = root.findViewById(R.id.imageView_enterprise_editprofile)
    change_profilepic_btn = root.findViewById(R.id.button_enterprise_editprofile_changeimg)

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
        image_src = aux_enterprise.profileImg
        name_editText.setText(aux_enterprise.name)
        cp_editText.setText(aux_enterprise.cp.toString())
        location_editText.setText(aux_enterprise.location)
        var spinnerPosition = mArrayAdapter.getPosition(aux_enterprise.category)
        if (spinnerPosition == -1) spinnerPosition = 1
        spinner.setSelection(spinnerPosition)
        if (image_src != "") Picasso.with(view?.context).load(image_src).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(profile_pic)
      }
    }

    confirm_btn.setOnClickListener {view ->
      setUploading(view)
      var ok = true
      val f_name = name_editText.text.toString()
      val f_cp = cp_editText.text.toString()
      val f_location = location_editText.text.toString()
      val f_category = spinner.selectedItem.toString()
      val f_image = image_src

      new_enterprise = itemEnterprise_2(userId, f_image, f_name, f_name.toLowerCase(), aux_enterprise.email, f_category, f_location, f_cp.toInt())
      db.collection("enterprises").document(userId).set(new_enterprise)
        .addOnSuccessListener {
          Log.d("EnterpriseEditProfileFragment", "DocumentSnapshot written with ID: $userId")
          view.findNavController().navigate(R.id.action_enterpriseEditProfileFragment_to_navigation_notifications)
        }
        .addOnFailureListener { e->
          Log.w("EnterpriseEditProfileFragment", "Error adding document", e)
          Toast.makeText(activity?.baseContext, "Hubo un problema al guardar los datos", Toast.LENGTH_LONG)
          setUpdisloading(view)
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
              FirebaseAuth.getInstance().currentUser!!.delete()
                .addOnSuccessListener {
                  db.collection("enterprises").whereEqualTo("userId", userId).limit(1).get()
                    .addOnSuccessListener {documents->
                      for (document in documents) {
                        document.reference.delete()
                      }
                      db.collection("shifts").whereEqualTo("id_enterprise", userId).get()
                        .addOnSuccessListener {documents->
                          for (document in documents) {
                            document.reference.delete()
                          }
                          db.collection("reserves").whereEqualTo("id_enterprise", userId).get()
                            .addOnCompleteListener { task ->
                              if (task.isSuccessful) {
                                val documents = task.result
                                if (documents != null) {
                                  for (document in documents) {
                                    document.reference.delete()
                                  }
                                  val i = Intent(activity, ActivityLogin::class.java)
                                  startActivity(i)
                                }
                              } else {
                                showAlert("Hubo un problema al eliminar al usuario")
                              }
                            }
                        }
                        .addOnFailureListener {
                          showAlert("Hubo un problema al eliminar al usuario")
                        }
                    }
                    .addOnFailureListener {
                      showAlert("Hubo un problema al eliminar al usuario")
                    }
                }
                .addOnFailureListener {
                  showAlert("Hubo un problema al eliminar al usuario")
                }
            }
            .addOnFailureListener {
              showAlert("Hubo un problema al eliminar al usuario")
            }
          if (dialog != null) {
            dialog.dismiss()
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

    change_profilepic_btn.setOnClickListener { view->
      val i: Intent = Intent(Intent.ACTION_PICK)
      i.type = "image/*"
      startActivityForResult(i, GALLERY_INTENT)
    }

    return root
  }

  private fun showAlert(message: String?)
  {
    val builder = AlertDialog.Builder(activity)
    builder.setTitle("ERROR")
    builder.setMessage(message.toString())
    builder.setPositiveButton("Continuar", null)
    builder.show()
  }

  private fun setUploading(view: View) {
    view.isEnabled = false
    loading.visibility =  View.VISIBLE
  }

  private fun setUpdisloading(view: View) {
    view.isEnabled = true
    loading.visibility =  View.GONE
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
  {

    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_INTENT){

      val uri: Uri? = data?.data
      if (uri != null) {
        val filePath: StorageReference = mStorage.child("user_images").child(uri?.lastPathSegment.toString())

        filePath.putFile(uri).addOnSuccessListener {

          mStorage.child(filePath.path).downloadUrl.addOnSuccessListener {
            db.collection("users").document(userId).update(
              mapOf(
                "profileImg" to it.toString()
              )
            )

            Picasso.with(view?.context).load(it.toString()).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(profile_pic)
          }.addOnFailureListener {
            showAlert(it.message)
          }
        }.addOnFailureListener{
          showAlert(it.message)
        }
      }
    }
  }
}