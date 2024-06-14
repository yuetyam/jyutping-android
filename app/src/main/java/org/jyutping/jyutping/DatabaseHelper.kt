package org.jyutping.jyutping

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context, databaseName: String) : SQLiteOpenHelper(context, databaseName, null, 3) {

        override fun onCreate(db: SQLiteDatabase) { }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }

        override fun onOpen(db: SQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("PRAGMA foreign_keys=ON;")
        }

        fun listTables(): Cursor? {
                val command: String = "SELECT name FROM sqlite_schema WHERE type='table';"
                val db = this.readableDatabase
                return db.rawQuery(command, null)
        }
        fun fetchRomanizations(text: String): Cursor? {
                val command: String = "SELECT romanization FROM jyutpingtable WHERE word = '$text';"
                val db = this.readableDatabase
                return db.rawQuery(command, null)
        }
        fun fetchWords(text: String): Cursor? {
                val command: String = "SELECT word FROM jyutpingtable WHERE romanization = '$text';"
                val db = this.readableDatabase
                return db.rawQuery(command, null)
        }
}
