package glucosetracker.com.bloodgluscosetracker

import android.app.Application
//import glucosetracker.com.bloodgluscosetracker.database.Tracker
//import glucosetracker.com.bloodgluscosetracker.database.TrackerDatabase
import org.jetbrains.anko.doAsync

class
CustomGraphApp : Application() {

    override fun onCreate() {
        super.onCreate()
      /*  doAsync {

            val database = TrackerDatabase.getInstance(context = this@CustomGraphApp)
            val trackerList = database.getTrackerDao().all
            if (trackerList.isEmpty()) {
                val item1 = Tracker(11066, "2018-06-08T13:35:00", 77.00, "Fasting", "");
                database.getTrackerDao().insert(item1)

                val item2 = Tracker(11067, "2018-06-05T13:35:00", 48.00, "Random", "");
                database.getTrackerDao().insert(item2)

                val item3 = Tracker(11068, "2018-06-03T13:36:00", 195.00, "Random", "");
                database.getTrackerDao().insert(item3)

            }
        }*/
    }
}