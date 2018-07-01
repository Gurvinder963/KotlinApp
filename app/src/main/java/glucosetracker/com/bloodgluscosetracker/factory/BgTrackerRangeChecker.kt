package glucosetracker.com.bloodgluscosetracker.factory

import android.content.Context
import com.segunfamisa.kotlin.samples.retrofit.data.kotlin.TrackerRangeChecker
import glucosetracker.com.bloodgluscosetracker.R


class BgTrackerRangeChecker(var context: Context, var reading: String, var type: String) : TrackerRangeChecker() {

    private var maxMin: String = ""
    private var rangeColor: Int = 0

    init {
        setValuesAccordingRange()
    }


    override fun getMaxMin(): String {
        return maxMin
    }

    override fun getRangeColor(): Int {
        return rangeColor
    }

    fun setValuesAccordingRange() {
        if (type.equals("Fasting", true)) {
            if (java.lang.Float.parseFloat(reading) < 70) run {
                maxMin = "Low Sugar"
                rangeColor = R.color.color_18c1e0
            } else if (java.lang.Float.parseFloat(reading) >= 70 && java.lang.Float.parseFloat(reading) <= 100) {
                maxMin = "Normal"
                rangeColor = R.color.color_2cc056
            } else if (java.lang.Float.parseFloat(reading) >= 101 && java.lang.Float.parseFloat(reading) <= 120) {
                maxMin = "Pre diabetic"
                rangeColor = R.color.color_fd7601
            } else if (java.lang.Float.parseFloat(reading) > 120) {
                maxMin = "High Sugar"
                rangeColor = R.color.color_fe2727
            }


        } else run {
            if (java.lang.Float.parseFloat(reading) < 70) {
                maxMin = "Low Sugar"
                rangeColor = R.color.color_18c1e0
            } else if (java.lang.Float.parseFloat(reading) >= 70 && java.lang.Float.parseFloat(reading) <= 140) {
                maxMin = "Normal"
                rangeColor = R.color.color_2cc056
            } else if (java.lang.Float.parseFloat(reading) > 140) {
                maxMin = "High Sugar"
                rangeColor = R.color.color_fe2727
            }
        }


    }

}