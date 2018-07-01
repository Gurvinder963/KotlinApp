package glucosetracker.com.bloodgluscosetracker.Utils

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.support.v4.content.ContextCompat
import glucosetracker.com.bloodgluscosetracker.R
import glucosetracker.com.bloodgluscosetracker.model.BgListItem

import java.util.*
 class PlotOnGraph {
companion object {
    fun plot_array_list_bg(roudedmax:Double, roundedmin:Double, itemIndex: Int, activity: Activity, this_g: Canvas, mfilteredList: ArrayList<BgListItem>, this_title: String, only_this_idx: Int, drawSizes: IntArray, x_axis_distance_px: Int): Boolean {

        var circleDP = 0

        val filteredList = ArrayList(mfilteredList)

        if (filteredList.size > 5) {

            if (itemIndex > 0) {
                for (i in 0 until itemIndex) {
                    filteredList.removeAt(filteredList.size - 1)
                }


            }

            if (filteredList.size > 5) {
                filteredList.subList(0, filteredList.size - 5).clear()
            }
        }


        val densityPx = activity.resources.displayMetrics.density

        if (densityPx.toDouble() == 1.0) {
            circleDP = 8
        } else if (densityPx.toDouble() == 1.5) {

            circleDP = 10
        } else if (densityPx == 2f) {
            circleDP = 16
        } else if (densityPx > 2) {

            circleDP = 20
        }

        val circleBorderDP = circleDP + 2


        var lRow: Int
        val nParms: Int

        var prev_x: Float
        var prev_y = 0f

        var cur_x: Float
        var cur_y: Float

        var cur_point: Point? = Point()
        cur_point!!.set(0, 0)

        var cur_maxX: Double
        var cur_minX: Double=0.0
        var cur_maxY: Double
        var cur_minY: Double
        var cur_start_x: Int=0
        var cur_points_2_plot: Int

        val POINTS_TO_CHANGE = filteredList.size + 1
        var cur_OBD_val: Double

        var curElt: String

        var curElt2: Any
        var cur_elt_array2: Array<String>

        val paint = Paint()

        try {

            run {
                cur_start_x = 0
                cur_minX = 0.0
            }


            paint.style = Paint.Style.STROKE

            nParms = only_this_idx
            run {

              //  curElt2 = these_labels.elementAt(nParms)
                //cur_elt_array2 = curElt2 as Array<String>


                cur_maxY = roudedmax
                cur_minY = roundedmin


                cur_points_2_plot = filteredList.size
                cur_maxX = cur_points_2_plot.toDouble()

                val item1 = filteredList[0]

                curElt = item1.list?.get(item1.list?.size!! - 1)!!.readingValue
                cur_OBD_val = java.lang.Double.parseDouble(curElt)

                cur_point = scale_point(0, cur_OBD_val, cur_point!!,
                        drawSizes[0], drawSizes[1], drawSizes[2], drawSizes[3],
                        cur_maxX, cur_minX, cur_maxY, cur_minY)

                cur_x = cur_point!!.x.toFloat()
                cur_y = cur_point!!.y.toFloat()


                paint.strokeWidth = 3f

                if (cur_points_2_plot < POINTS_TO_CHANGE) {
                    val formattedTime1 = item1.list?.get(item1.list?.size!! - 1)!!.time
                    val timeArray = formattedTime1.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

                    val hour = java.lang.Float.parseFloat(timeArray[0])
                    val min = java.lang.Float.parseFloat(timeArray[1])

                    val time = hour + min / 60

                    cur_x = x_axis_distance_px / 6 * time + 50

                    paint.isAntiAlias = true
                    paint.color = -0xc03f4b
                    paint.style = Paint.Style.FILL


                    paint.style = Paint.Style.STROKE
                    paint.color = Color.WHITE


                }
                prev_x = cur_x
                prev_y = cur_y


                lRow = cur_start_x
                while (lRow < cur_start_x + cur_points_2_plot) {

                    if (lRow == 0) {
                        for (i in filteredList[lRow].list?.size!! - 1 downTo 0) {

                            if (i != filteredList[lRow].list?.size!! - 1) {
                                val formattedTime1 = filteredList[lRow].list?.get(i)?.time
                                val timeArray = formattedTime1?.split(":".toRegex())?.dropLastWhile({ it.isEmpty() })?.toTypedArray()

                                val hour = java.lang.Float.parseFloat(timeArray!![0])
                                val min = java.lang.Float.parseFloat(timeArray[1])

                                val time = hour + min / 60

                                cur_x = x_axis_distance_px / 6 * (time + 24 * lRow) + 50


                                curElt = filteredList[lRow].list?.get(i)?.readingValue!!
                                cur_OBD_val = java.lang.Double.parseDouble(curElt)

                                cur_point = scale_point(0, cur_OBD_val, cur_point!!,
                                        drawSizes[0], drawSizes[1], drawSizes[2], drawSizes[3],
                                        cur_maxX, cur_minX, cur_maxY, cur_minY)

                                cur_y = cur_point!!.y.toFloat()

                                this_g.drawLine(prev_x, prev_y, cur_x, cur_y, paint)
                                prev_x = cur_x
                                prev_y = cur_y
                            }
                        }
                    } else {

                        for (i in filteredList[lRow].list?.size!! - 1 downTo 0) {

                            val formattedTime1 = filteredList[lRow].list?.get(i)?.time
                            val timeArray = formattedTime1?.split(":".toRegex())?.dropLastWhile({ it.isEmpty() })?.toTypedArray()

                            val hour = java.lang.Float.parseFloat(timeArray!![0])
                            val min = java.lang.Float.parseFloat(timeArray[1])

                            val time = hour + min / 60

                            cur_x = x_axis_distance_px / 6 * (time + 24 * lRow) + 50

                            curElt = filteredList[lRow].list?.get(i)?.readingValue!!
                            cur_OBD_val = java.lang.Double.parseDouble(curElt)

                            cur_point = scale_point(0, cur_OBD_val, cur_point!!,
                                    drawSizes[0], drawSizes[1], drawSizes[2], drawSizes[3],
                                    cur_maxX, cur_minX, cur_maxY, cur_minY)

                            cur_y = cur_point!!.y.toFloat()
                            this_g.drawLine(prev_x, prev_y, cur_x, cur_y, paint)
                            prev_x = cur_x
                            prev_y = cur_y

                        }
                    }
                    lRow++
                }

                lRow = cur_start_x
                while (lRow < cur_start_x + cur_points_2_plot) {


                    for (i in filteredList[lRow].list?.size!! - 1 downTo 0) {

                        val formattedTime1 = filteredList[lRow].list?.get(i)?.time
                        val timeArray = formattedTime1?.split(":".toRegex())?.dropLastWhile({ it.isEmpty() })?.toTypedArray()

                        val hour = java.lang.Float.parseFloat(timeArray!![0])
                        val min = java.lang.Float.parseFloat(timeArray[1])

                        val time = hour + min / 60

                        cur_x = x_axis_distance_px / 6 * (time + 24 * lRow) + 50


                        paint.isAntiAlias = true

                        paint.color = -0xc03f4b
                        paint.style = Paint.Style.FILL

                        curElt = filteredList[lRow].list?.get(i)?.readingValue!!
                        cur_OBD_val = java.lang.Double.parseDouble(curElt)

                        cur_point = scale_point(0, cur_OBD_val, cur_point!!,
                                drawSizes[0], drawSizes[1], drawSizes[2], drawSizes[3],
                                cur_maxX, cur_minX, cur_maxY, cur_minY)

                        cur_y = cur_point!!.y.toFloat()


                        setGlucoseRangeColor(activity, java.lang.Double.parseDouble(filteredList[lRow].list?.get(i)?.readingValue), filteredList[lRow].list?.get(i)?.readingType!!, paint)

                        this_g.drawCircle(cur_x, cur_y, circleDP.toFloat(), paint)

                        paint.style = Paint.Style.STROKE
                        paint.color = Color.WHITE
                        this_g.drawCircle(cur_x, cur_y, circleBorderDP.toFloat(), paint)


                    }
                    lRow++

                }


            }


            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false

        }

    }

    private fun scale_point(this_x: Int, this_y: Double, drawPoint: Point,
                            scr_x: Int, scr_y: Int, scr_width: Int, src_height: Int,
                            maxX: Double, minX: Double, maxY: Double, minY: Double): Point? {
        var drawPoint = drawPoint
        val temp_x: Int
        val temp_y: Int
        val temp = Point()

        if (maxY == minY)
        //skip bad data
            return null

        //don't touch it if is nothing
        try {
            temp_x = scr_x + ((this_x.toDouble() - minX) * (scr_width.toDouble() / (maxX - minX))).toInt()
            temp_y = scr_y + ((maxY - this_y) * (src_height.toDouble() / (maxY - minY))).toInt()

            temp.x = temp_x
            temp.y = temp_y
            drawPoint = temp


        } catch (e: Exception) {

            return null
        }

        return temp

    }

    private fun setGlucoseRangeColor(activity: Activity, glucose: Double, readingType: String, paint: Paint) {


        if (readingType.equals("Fasting", ignoreCase = true)) {


            if (glucose < 70) {
                paint.color = ContextCompat.getColor(activity, R.color.color_18c1e0)

            } else if (glucose >= 70 && glucose <= 100) {
                paint.color = ContextCompat.getColor(activity, R.color.color_2cc056)

            } else if (glucose >= 101 && glucose <= 120) {
                paint.color = ContextCompat.getColor(activity, R.color.color_fd7601)

            } else if (glucose > 120) {
                paint.color = ContextCompat.getColor(activity, R.color.color_fe2727)

            }
        } else {

            if (glucose < 70) {
                paint.color = ContextCompat.getColor(activity, R.color.color_18c1e0)

            } else if (glucose >= 70 && glucose <= 140) {
                paint.color = ContextCompat.getColor(activity, R.color.color_2cc056)

            } else if (glucose > 140) {
                paint.color = ContextCompat.getColor(activity, R.color.color_fe2727)

            }
        }


    }
}


}