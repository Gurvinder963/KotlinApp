/*
package glucosetracker.com.bloodgluscosetracker.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import glucosetracker.com.bloodgluscosetracker.Utils.Constants

@Database(entities = [(Tracker::class)], version = 1)
abstract class TrackerDatabase : RoomDatabase()
{
    abstract fun getTrackerDao():TrackerDao


    companion object {

        */
/**
         * The only instance
         *//*

        private var sInstance: TrackerDatabase? = null

        */
/**
         * Gets the singleton instance of SampleDatabase.
         *
         * @param context The context.
         * @return The singleton instance of SampleDatabase.
         *//*

        @Synchronized
        fun getInstance(context: Context): TrackerDatabase {
            if (sInstance == null) {
                sInstance = Room
                        .databaseBuilder(context.applicationContext,TrackerDatabase::class.java,Constants.DB_NAME)
                        .build()
            }
            return sInstance!!
        }
    }



}*/
