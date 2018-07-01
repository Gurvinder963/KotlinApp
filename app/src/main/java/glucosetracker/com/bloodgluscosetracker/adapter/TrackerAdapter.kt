package glucosetracker.com.bloodgluscosetracker.adapter

import android.app.Activity
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.segunfamisa.kotlin.samples.retrofit.data.kotlin.TrackerRangeChecker
import com.segunfamisa.kotlin.samples.retrofit.data.kotlin.TrackerRangeFactory
import glucosetracker.com.bloodgluscosetracker.R
import glucosetracker.com.bloodgluscosetracker.factory.EnumTrackerType

import org.zakariya.stickyheaders.SectioningAdapter
import java.text.ParseException
import java.text.SimpleDateFormat
import glucosetracker.com.bloodgluscosetracker.model.BgListItem
import java.util.*

class TrackerAdapter(var arrList: ArrayList<BgListItem>?, var mActivity: Activity, var topbarheight: Int, var graphHeight: Int, var mTrackerType: String, var mCurrentType: String) : SectioningAdapter() {
    var sectionHeight: Int = 0
    var itemHeight: Int = 0
    private var recycleViewHeight: Int = 0

    init {

        val display = mActivity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val deviceHeight = size.y

        mCurrentType = mCurrentType
        recycleViewHeight = deviceHeight - (graphHeight + topbarheight)
    }

    override fun getNumberOfSections(): Int {
        return arrList!!.size
    }

    override fun getNumberOfItemsInSection(sectionIndex: Int): Int {
        return arrList!!.get(sectionIndex).list?.size!!
    }

    override fun doesSectionHaveHeader(sectionIndex: Int): Boolean {
        return true
    }

    override fun doesSectionHaveFooter(sectionIndex: Int): Boolean {
        return false
    }

    fun sectionHeight(): Int {
        return sectionHeight
    }

    fun itemHeight(): Int {
        return itemHeight
    }
    override fun onCreateItemViewHolder(parent: ViewGroup?, itemType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent!!.context)
        val v = inflater.inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(v)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup?, headerType: Int): HeaderViewHolder {
        val inflater = LayoutInflater.from(parent!!.context)
        val v = inflater.inflate(R.layout.list_section, parent, false)
        return HeaderViewHolder(v)
    }

    override fun onBindHeaderViewHolder(viewHolder: SectioningAdapter.HeaderViewHolder?, sectionIndex: Int, headerType: Int) {
        val s = arrList?.get(sectionIndex)
        val hvh = viewHolder as HeaderViewHolder
        sectionHeight=hvh.llSection.layoutParams.height
        hvh.titleTextView.setText(formatDate("MMM dd,yyyy", "EEEE MMM dd,yyyy", s!!.date!!))
        hvh.titleTextView.tag = sectionIndex

    }

    override fun onBindItemViewHolder(viewHolder: SectioningAdapter.ItemViewHolder?, sectionIndex: Int, itemIndex: Int, itemType: Int) {
        val ivh = viewHolder as ItemViewHolder
        itemHeight = ivh.rlItem.layoutParams.height
        if (itemIndex == 0) {
            ivh.rlv2.setVisibility(View.VISIBLE)
            ivh.rlv2.bringToFront()
        } else {
            ivh.rlv2.setVisibility(View.INVISIBLE)
        }

        val s = arrList?.get(sectionIndex)?.list
        ivh.tvTime.setText(s?.get(itemIndex)?.time)
        ivh.tvTime.setTag(itemIndex)

        var rangeValue = ""
        var trackerRangeChecker: TrackerRangeChecker? = null
        if (mTrackerType.equals("Blood Glucose", ignoreCase = true)) {
            if (s?.get(itemIndex)?.readingType.equals("Fasting", ignoreCase = true))
                rangeValue = s?.get(itemIndex)?.readingValue + " (fasting)"
            else
                rangeValue = s?.get(itemIndex)?.readingValue + " (" + s?.get(itemIndex)?.readingType?.toLowerCase() + ")"

            trackerRangeChecker = TrackerRangeFactory.getTrackerRangeChecker(mActivity, EnumTrackerType.BGTRACKER, s?.get(itemIndex)?.readingValue!!, s?.get(itemIndex)?.readingType)
        }
        ivh.tvRange.setText(rangeValue)
        ivh.tvMaxMin.setText(trackerRangeChecker!!.getMaxMin() + "")
        val bg = viewHolder.tvColor.getBackground().mutate() as GradientDrawable
        bg.setColor(ContextCompat.getColor(mActivity, trackerRangeChecker!!.getRangeColor()))

        if (sectionIndex == arrList!!.size - 1 && arrList!!.size != 1 && itemIndex == s!!.size - 1) {
            ivh.rlItem.getLayoutParams().height = recycleViewHeight - (sectionHeight + itemHeight)
        } else {
            ivh.rlItem.getLayoutParams().height = itemHeight
        }


    }

    class ItemViewHolder(itemView: View) : SectioningAdapter.ItemViewHolder(itemView) {
        var tvTime: TextView
        var tvRange: TextView
        var tvColor: TextView
        var tvMaxMin: TextView
        var rlItem: RelativeLayout
        var rlv2: TextView
        var imgDelete: ImageView


        init {
            rlv2 = itemView.findViewById(R.id.rlv2) as TextView
            rlItem = itemView.findViewById(R.id.rlItem) as RelativeLayout
            tvTime = itemView.findViewById(R.id.itemTitle) as TextView
            tvRange = itemView.findViewById(R.id.tvRange) as TextView
            tvMaxMin = itemView.findViewById(R.id.tvMaxMin) as TextView
            imgDelete = itemView.findViewById(R.id.imgDelete) as ImageView
            tvColor = itemView.findViewById(R.id.tvColor) as TextView
            //itemHeight = rlItem.layoutParams.height
        }

    }

    class HeaderViewHolder(itemView: View) : SectioningAdapter.HeaderViewHolder(itemView) {


        var titleTextView: TextView
        var llSection: LinearLayout

        init {
            titleTextView = itemView.findViewById(R.id.sectionTitle) as TextView
            llSection = itemView.findViewById(R.id.llSection) as LinearLayout
            // sectionHeight = llSection.layoutParams.height
        }
    }

    fun formatDate(formatSrc: String, formatDest: String, date: String): String {
        val str: String
        val sdf = SimpleDateFormat(formatSrc, Locale.US)
        try {
            val myDate = sdf.parse(date)
            str = SimpleDateFormat(formatDest, Locale.US).format(myDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

        return str
    }
}