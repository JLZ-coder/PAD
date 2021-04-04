package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

open class ActivityRegister : AppCompatActivity() {

    // TODO: HAbrá q estructurar esto bien. Era puna prueba para el registro de usuarios

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setUp()
    }

    private fun setUp()
    {
        title = "Register"

        val userMail: EditText = findViewById<EditText>(R.id.editTextUserMail)
        val userPass: EditText = findViewById<EditText>(R.id.editTextPassword)
        val userName: EditText = findViewById<EditText>(R.id.editTextUserName)

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("mail")
        userMail.setText(email ?: userMail.text.toString())

        val btnRegister = findViewById<Button>(R.id.btnRegisterUser)
        btnRegister.setOnClickListener {
            if (userName.text.isNotEmpty() && userPass.text.isNotEmpty() && userMail.text.isNotEmpty()) {

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    userMail.text.toString(),
                    userPass.text.toString()
                ).addOnCompleteListener{
                    if (it.isSuccessful) {
                        showUserInfo(it.result?.user?.email ?: "", ProviderType.MAIL, userName.text.toString());
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

        db.collection("users").document(email).set(
            hashMapOf("name" to userName)
        )

        // TODO: Nos vamos a ir a la info del usuario cndo haga login, o a la empresa que clique, pero eso hay q mirarlo bien
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", userName)
        }

        startActivity(homeIntent);
    }
}