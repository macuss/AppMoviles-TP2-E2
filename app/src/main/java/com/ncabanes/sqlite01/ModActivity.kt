package com.ncabanes.sqlite01

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

class ModActivity : AppCompatActivity() {

    private lateinit var etCiudad: EditText
    private lateinit var etNewPobla: EditText
    private lateinit var botoncitoModificar: AppCompatButton
    private lateinit var dbHelper: MySQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod)

        dbHelper = MySQLiteHelper.getInstance(this)

        initComponents()
        initListeners()
    }

    private fun initComponents() {
        etCiudad = findViewById(R.id.etCiudad)
        etNewPobla = findViewById(R.id.etNewPobla)
        botoncitoModificar = findViewById(R.id.botoncitoModificar)
    }

    private fun initListeners() {
        botoncitoModificar.setOnClickListener {
            val nombreCiudad = etCiudad.text.toString()
            val nuevaPoblacion = etNewPobla.text.toString().toIntOrNull()
            if (nombreCiudad.isBlank() || nuevaPoblacion == null) {
                Toast.makeText(this, "Por favor, complete los campos de texto", Toast.LENGTH_SHORT)
                    .show()
            } else {
                modificarPoblacionCiudad(nombreCiudad, nuevaPoblacion)
                finish()
            }
            // Animación de escala sobre el botón
            val animatorSet =
                AnimatorInflater.loadAnimator(this, R.animator.scale_button) as AnimatorSet
            animatorSet.setTarget(botoncitoModificar)
            animatorSet.start()

        }
    }

    @SuppressLint("Recycle")
    private fun modificarPoblacionCiudad(nombreCiudad: String, nuevaPoblacion: Int) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val contentValues = ContentValues()
            contentValues.put("poblacion", nuevaPoblacion)
            val selection = "nombreCiudad = ?"
            val selectionArgs = arrayOf(nombreCiudad)
            val cursor = db.query("ciudades", null, selection, selectionArgs, null, null, null)
            if (cursor.moveToFirst()) {
                db.update("ciudades", contentValues, selection, selectionArgs)
                db.setTransactionSuccessful()
            } else {
                Toast.makeText(this, "La ciudad no existe en la base de datos", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error al modificar población de la ciudad", e)
        } finally {
            db.endTransaction()
            db.close()
        }
    }
}
