package com.ncabanes.sqlite01

import android.content.ContentValues

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.content.Context


class MySQLiteHelper(context: Context) : SQLiteOpenHelper(
    context, "capitales.db", null, 1
) {
    companion object {
        private const val DATABASE_NAME = "CapitalesDB.db"
        private const val DATABASE_VERSION = 1

        private lateinit var dbHelper: MySQLiteHelper

        fun getInstance(context: Context): MySQLiteHelper {
            if (!::dbHelper.isInitialized || dbHelper.readableDatabase.isOpen) {
                dbHelper = MySQLiteHelper(context.applicationContext)
            }
            return dbHelper
        }
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val ordenCreacion1 = "CREATE TABLE paises " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombrePais varchar(25))"
        val ordenCreacion2 = "CREATE TABLE ciudades " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idpais INTEGER references paises(_id)," +
                "nombreCiudad varchar(25),poblacion INTEGER)"

        db!!.execSQL(ordenCreacion1)
        db.execSQL(ordenCreacion2)
    }


    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val ordenBorrado1 = "DROP TABLE IF EXISTS paises"
        val ordenBorrado2 = "DROP TABLE IF EXISTS ciudades"
        db!!.execSQL(ordenBorrado1)
        db.execSQL(ordenBorrado2)
        onCreate(db)
    }

    fun anadirDatos(nombreP: String, nombreC: String, poblacion: Int) {
        agregarPais(nombreP)
        anadirDatosCiudad(nombreC, poblacion, nombreP)
    }

    fun anadirDatosPais(nombreP: String) {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM paises WHERE nombrePais = ?", arrayOf(nombreP))
        if (cursor.count == 0) {
            val datosp = ContentValues()
            datosp.put("nombrePais", nombreP)
            db.insert("paises", null, datosp)
            Log.d("MyApp", "País $nombreP agregado correctamente")
        } else {
            Log.d("MyApp", "El país $nombreP ya existe")
        }
        cursor.close()
        db.close()
    }

    fun agregarPais(nombreP: String) {
        Log.d("MyApp", "Agregando país " + nombreP)
        anadirDatosPais(nombreP)
    }

    fun anadirDatosCiudad(nombreC: String, poblacion: Int, nombreP: String) {
        val datosc = ContentValues()
        val idp: Int? = buscarPais(nombreP)
        if (idp != null) {
            datosc.put("idpais", idp)
            datosc.put("nombreCiudad", nombreC)
            datosc.put("poblacion", poblacion)
            val db: SQLiteDatabase = this.writableDatabase
            db.insert("ciudades", null, datosc)
            db.beginTransaction()
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        } else {
            Log.d("MyApp", "No se pudo encontrar el país $nombreP")
        }

    }

    fun buscarPais(nombreP: String): Int? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM paises WHERE nombrePais=?", arrayOf(nombreP))
        var idPais: Int? = null
        Log.d("MyApp", "Número de filas en el cursor: " + cursor.count)
        if (cursor.moveToFirst()) {
            idPais = cursor.getInt(cursor.getColumnIndex("_id"))
            val nombre = cursor.getString(cursor.getColumnIndex("nombrePais"))
            Log.d("MyApp", "País encontrado: ID=$idPais, nombre=$nombre")
        } else {
            Log.d("MyApp", "No se encontró ningún país con el nombre $nombreP")
        }
        cursor.close()
        return idPais
    }
}