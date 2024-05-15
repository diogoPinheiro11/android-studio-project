package com.example.android_studio_project.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.View
import androidx.activity.enableEdgeToEdge
import android.content.SharedPreferences

import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.AuthService

class LoginActivity : AppCompatActivity() {
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (isLoggedIn()) {
            navigateToDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        authService = AuthService()

        val email: EditText = findViewById(R.id.editTextEmail)
        val password: EditText = findViewById(R.id.editTextPassword)
        val btnLogin: Button = findViewById(R.id.buttonLogin)

        btnLogin.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                authService.verifyUser(emailText, passwordText, onResponse = { user ->
                    if (user != null) {
                        runOnUiThread {
                            Toast.makeText(this, "Login bem-sucedido", Toast.LENGTH_SHORT).show()
                        }
                        saveLoginState(true)
                        navigateToDashboard()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Login mal-sucedido", Toast.LENGTH_SHORT).show()
                        }
                    }
                }, onFailure = { error ->
                    runOnUiThread {
                        Toast.makeText(this, "Erro na autenticação: ${error.message}", Toast.LENGTH_SHORT).show()

                    }
                })
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun openRegister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)}

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java);
        startActivity(intent)
        finish()
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("UserLoggedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("UserLoggedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}
