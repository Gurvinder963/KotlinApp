package com.segunfamisa.kotlin.samples.retrofit.data.kotlin

import android.content.Context
import glucosetracker.com.bloodgluscosetracker.factory.BgTrackerRangeChecker
import glucosetracker.com.bloodgluscosetracker.factory.EnumTrackerType

class TrackerRangeFactory{
    companion object {
        fun getTrackerRangeChecker(context: Context, trackerType: EnumTrackerType, reading: String, type: String): TrackerRangeChecker? {

            when (trackerType) {

                EnumTrackerType.BGTRACKER -> return BgTrackerRangeChecker(context, reading, type)

            }

            return null
        }
    }


}