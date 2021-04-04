package es.ucm.fdi.mybooker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivityLogin : AppCompatActivity()
{

    // TODO: HAbrá q estructurar esto bien. Era puna prueba para el login de usuarios
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registerUser: TextView = findViewById<TextView>(R.id.btnRegisterUser)
        registerUser.setOnClickListener {
            goRegisterUser()
        }
        val register: TextView = findViewById<TextView>(R.id.btnRegisterEnt)
        registerUser.setOnClickListener {
            goRegisterEnt()
        }

        setUp()
    }

    private fun setUp()
    {
        title = "Login"

        val userMail: EditText = findViewById<EditText>(R.id.editTextUser)
        val userPass: EditText = findViewById<EditText>(R.id.editTextPassword)

        val login = findViewById<Button>(R.id.btnLogIn)
        login.setOnClickListener {
            if (userPass.text.isNotEmpty() && userMail.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    userMail.text.toString(),
                    userPass.text.toString()
                ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showUserInfo(it.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                }
            }
        }
    }

    private fun goRegisterUser()
    {

        val i = Intent(this, ActivityRegister::class.java).apply {
            putExtra("mail", findViewById<EditText>(R.id.editTextUser).text.toString())
        }
        startActivity(i)
    }

    private fun goRegisterEnt()
    {

        // TODO: Registrar empresa
    }

    private fun showAlert()
    {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("El usuario no existe")
        builder.setPositiveButton("Registrar usuario"){ _, _ -> goRegisterUser() }
        builder.setPositiveButton("Resgistrar empresa"){ _, _ -> goRegisterEnt() }
        builder.setNegativeButton("Reintentar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showUserInfo(email : String)
    {

        val name: String = ""
        db.collection("users").document(email).set(
            hashMapOf("name" to name)
        )

        // TODO: Nos vamos a ir a la info del usuario cndo haga login, o a la empresa que clique, pero eso hay q mirarlo bien
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("userName", name)
            putExtra("email", email)
        }
        startActivity(homeIntent);
    }
}