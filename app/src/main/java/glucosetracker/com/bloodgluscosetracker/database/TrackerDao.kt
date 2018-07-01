/*
package glucosetracker.com.bloodgluscosetracker.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import glucosetracker.com.bloodgluscosetracker.Utils.Constants

@Dao
interface TrackerDao {

    @Insert
    fun insert(tracker: Tracker)

    @get:Query("SELECT * FROM "+Constants.TABLE_NAME_TRACKER)
    val all: List<Tracker>


}*/
