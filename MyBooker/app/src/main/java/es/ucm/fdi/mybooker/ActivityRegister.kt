package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

open class ActivityRegister : AppCompatActivity() {

    // TODO: HAbrá q estructurar esto bien. Era puna prueba para el registro de usuarios

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var userMail: EditText
    private lateinit var userPass: EditText
    private lateinit var userName: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        userMail = findViewById<EditText>(R.id.editTextUserMail)
        userPass = findViewById<EditText>(R.id.editTextPassword)
        userName = findViewById<EditText>(R.id.editTextUserName)
        btnRegister = findViewById<Button>(R.id.btnRegisterUser)

        setUp()
    }

    private fun setUp()
    {
        title = "Register"

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("mail")
        userMail.setText(email ?: userMail.text.toString())
        val tipoUsuario = bundle?.getString("tipoUsuario")
        if  (tipoUsuario == "usuario") {
            setUpUsuario()
        }
        else if (tipoUsuario == "empresa") {
            setUpEmpresa()
        }
    }

    private fun setUpUsuario() {
        btnRegister.setOnClickListener {
            if (userName.text.isNotEmpty() && userPass.text.isNotEmpty() && userMail.text.isNotEmpty()) {

                mAuth.createUserWithEmailAndPassword(
                    userMail.text.toString(),
                    userPass.text.toString()
                ).addOnCompleteListener{
                    if (it.isSuccessful) {
                        val user_uid = mAuth.currentUser.uid
                        db.collection("users").document(user_uid).set(
                            mapOf(
                                "name" to userName.text.toString(),
                                "email" to userMail.text.toString(),
                                "tipoUsario" to "usuario"
                            )
                        )
                        showUserInfo(it.result?.user?.email ?: "", ProviderType.MAIL, userName.text.toString())
                    } else {
                        showAlert(it.exception!!);
                    }
                }
            }
        }
    }

    private fun setUpEmpresa() {
        btnRegister.setOnClickListener {
            if (userName.text.isNotEmpty() && userPass.text.isNotEmpty() && userMail.text.isNotEmpty()) {

                mAuth.createUserWithEmailAndPassword(
                    userMail.text.toString(),
                    userPass.text.toString()
                ).addOnCompleteListener{
                    if (it.isSuccessful) {
                        val user_uid = mAuth.currentUser.uid
                        db.collection("users").document(user_uid).set(
                            mapOf(
                                "name" to userName.text.toString(),
                                "email" to userMail.text.toString(),
                                "tipoUsario" to "empresa"
                            )
                        )
                        showUserInfo(it.result?.user?.email ?: "", ProviderType.MAIL, userName.text.toString())
                    } else {
                        showAlert(it.exception!!);
                    }
                }
            }
        }
    }

    private fun showAlert(e: Exception)
    {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(e.message.toString())
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showUserInfo(email: String, provider: ProviderType, userName: String)
    {
        // TODO: Nos vamos a ir a la info del usuario cndo haga login, o a la empresa que clique, pero eso hay q mirarlo bien
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", userName)
        }

        startActivity(homeIntent);
    }
}