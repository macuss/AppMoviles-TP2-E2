package com.ncabanes.sqlite01

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class BorrarCP : AppCompatActivity() {
    private lateinit var etnPais: EditText
    private lateinit var btcBorrarCP: Button
    private lateinit var dbHelper: MySQLiteHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrar_cp)
        dbHelper = MySQLiteHelper.getInstance(this)
        db = dbHelper.writableDatabase
        initComponents()
        initListeners()
    }

    private fun initComponents() {
        etnPais = findViewById(R.id.etnPais)
        btcBorrarCP = findViewById(R.id.btcBorrarCP)
    }

    private fun initListeners() {
        btcBorrarCP.setOnClickListener {
            val nombrePais = etnPais.text.toString()
            borrarCiudadesPorPais(nombrePais)
        }
    }

    private fun borrarCiudadesPorPais(nombrePais: String) {
        db.beginTransaction()
        try {
            val paisIdCursor =
                db.rawQuery("SELECT _id FROM paises WHERE nombrePais=?", arrayOf(nombrePais))
            if (paisIdCursor.moveToFirst()) {
                val paisId = paisIdCursor.getLong(0)
                val selection = "idpais = ?"
                val selectionArgs = arrayOf(paisId.toString())
                db.delete("ciudades", selection, selectionArgs)
                db.setTransactionSuccessful()
                Toast.makeText(this, "Ciudades eliminadas correctamente", Toast.LENGTH_SHORT).show()
            }
            paisIdCursor.close()
        } catch (e: Exception) {
            Log.e("TAG", "Error al borrar ciudades por país", e)
        } finally {
            db.endTransaction()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}
