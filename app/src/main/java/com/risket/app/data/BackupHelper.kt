package com.risket.app.data

import android.content.Context
import android.net.Uri
import java.io.File

object BackupHelper {

    private const val DB_NAME = "risket.db"

    fun dbFile(context: Context): File = context.getDatabasePath(DB_NAME)

    /** Forces a WAL checkpoint so all pending writes land in the main db file before copying. */
    fun checkpoint(context: Context) {
        val db = RisketDatabase.getInstance(context)
        db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").close()
    }

    fun exportTo(context: Context, destination: Uri) {
        checkpoint(context)
        context.contentResolver.openOutputStream(destination)?.use { out ->
            dbFile(context).inputStream().use { input ->
                input.copyTo(out)
            }
        }
    }

    fun importFrom(context: Context, source: Uri) {
        // Close the current database connection before overwriting the file.
        RisketDatabase.closeInstance()
        context.contentResolver.openInputStream(source)?.use { input ->
            dbFile(context).outputStream().use { out ->
                input.copyTo(out)
            }
        }
    }
}
