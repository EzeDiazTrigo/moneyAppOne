package com.souckan.moneyappone.data.database.utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.souckan.moneyappone.data.database.TotalDatabase
import com.souckan.moneyappone.di.RoomModule
import com.souckan.moneyappone.di.RoomModule.provideDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object DatabaseUtils {

    private const val DATABASE_NAME = "total_database"
    private const val BACKUP_FOLDER = "MoneyAppBackups"

    // Exportar base de datos
    fun exportDatabase(context: Context, uri: Uri, db: TotalDatabase) {
        try {
            // Forzar volcado del Write-Ahead Log (si se está usando WAL)
            db.openHelper.writableDatabase.execSQL("PRAGMA wal_checkpoint(FULL)")
            db.close() // Esto también asegura que se escriba

            val dbFile = context.getDatabasePath("total_database")
            context.contentResolver.openOutputStream(uri)?.use { output ->
                FileInputStream(dbFile).use { input ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(context, "Base de datos exportada con éxito", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("ExportDB", "Error exportando la base de datos", e)
            Toast.makeText(context, "Error al exportar la base de datos", Toast.LENGTH_SHORT).show()
        }
    }



    // Seleccionar archivo de base de datos para importar
    fun selectDatabaseFile(activity: Activity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
        }
        activity.startActivityForResult(Intent.createChooser(intent, "Selecciona la base de datos"), 1001)
    }



    // Manejar el resultado del selector de archivos en la Activity
    fun importDatabase(context: Context, uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputFile = File(context.getDatabasePath("total_database").absolutePath)
            inputStream?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("ImportDB", "Importación completada: ${outputFile.absolutePath}")
            Toast.makeText(context, "Importación exitosa", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ImportDB", "Error al importar la base de datos", e)
            Toast.makeText(context, "Error al importar base de datos", Toast.LENGTH_LONG).show()
        }
    }


    // Importar base de datos
    fun handleImportResult(requestCode: Int, resultCode: Int, data: Intent?, context: Context) {

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                importDatabase(context, uri)
            }
        }
    }


    fun copyDatabaseFiles(context: Context, uri: Uri) {
        val dbName = "total_database"
        val dbPath = context.getDatabasePath(dbName).absolutePath
        val walPath = "$dbPath-wal"
        val shmPath = "$dbPath-shm"

        context.contentResolver.openOutputStream(uri)?.use { output ->
            ZipOutputStream(output).use { zipOut ->
                listOf(
                    dbPath to dbName,
                    walPath to "$dbName-wal",
                    shmPath to "$dbName-shm"
                ).forEach { (path, name) ->
                    val file = File(path)
                    if (file.exists()) {
                        FileInputStream(file).use { input ->
                            zipOut.putNextEntry(ZipEntry(name))
                            input.copyTo(zipOut)
                            zipOut.closeEntry()
                        }
                    }
                }
            }
        }
    }






}
