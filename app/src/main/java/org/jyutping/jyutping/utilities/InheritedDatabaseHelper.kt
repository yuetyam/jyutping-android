package org.jyutping.jyutping.utilities

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class InheritedDatabaseHelper(context: Context, name: String) : SQLiteOpenHelper(context, name, null, DATABASE_VERSION) {
        override fun onConfigure(db: SQLiteDatabase?) {
                super.onConfigure(db)
                db?.disableWriteAheadLogging()
        }
        override fun onCreate(db: SQLiteDatabase?) {}
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
        override fun onOpen(db: SQLiteDatabase?) {
                super.onOpen(db)
                if (db?.isReadOnly == false) {
                        db.execSQL("PRAGMA query_only = ON;")
                }
        }
        companion object {
                private const val DATABASE_VERSION = 1
        }
}
