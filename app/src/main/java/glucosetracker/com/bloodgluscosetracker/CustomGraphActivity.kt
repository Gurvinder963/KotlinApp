package glucosetracker.com.bloodgluscosetracker

import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.google.gson.Gson
import glucosetracker.com.bloodgluscosetracker.Utils.PlotOnGraph
import glucosetracker.com.bloodgluscosetracker.adapter.TrackerAdapter
//import glucosetracker.com.bloodgluscosetracker.database.TrackerDatabase
import glucosetracker.com.bloodgluscosetracker.model.BgListItem
import glucosetracker.com.bloodgluscosetracker.model.DataBGTrackerItem
import glucosetracker.com.bloodgluscosetracker.model.ItemBean
import glucosetracker.com.bloodgluscosetracker.response.BGTrackersResponse

import org.zakariya.stickyheaders.StickyHeaderLayoutManager
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CustomGraphActivity : AppCompatActivity() {
    private var mGesture: GestureDetector? = null
    private var trackerAdapter: TrackerAdapter? = null
    private lateinit var canvas: Canvas
    private var emptyBitmap: Bitmap? = null
    private var paint: Paint? = null
    private var rect: Rect? = null

    private var currentType = ""
    private var chartBitmap: Bitmap? = null
    lateinit var recyclerView: RecyclerView
    lateinit var hsvw: HorizontalScrollView
    lateinit var graphBg: ImageView
    lateinit var rootView: RelativeLayout
    lateinit var rootLayout: LinearLayout
    private val draw_only_this_idx = -1
    var deviceWidth: Int = 0
    var deviceHeight: Int = 0
    var graphHeight: Int = 0
    private var drawSizes: IntArray? = null
    private var listScroll = -1
    private var totalFrame = -1
    private var itemIndex = 0
    private var lastSectionPos = 0
    var x_axis_distance_px: Int = 0
    var bgTrackerItems: ArrayList<DataBGTrackerItem>? = null
    var bgListItems: ArrayList<BgListItem>? = null
    var filteredList: ArrayList<BgListItem>? = null

    var trackerDetail: String = "Blood Glucose"
    var topBarHeight: Int = 0
    var rounded_max = 0.0
    var rounded_min = 0.0
    lateinit var layoutManager: StickyHeaderLayoutManager

    private val mOnGesture = object : GestureDetector.SimpleOnGestureListener() {


        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (listScroll == 2) {

                val dx = distanceX
                Log.d("onscrollX", dx.toString() + "")
                val holder = layoutManager.getFirstVisibleHeaderViewHolder(true)
                val view = holder!!.itemView
                val sectionText = view.findViewById(R.id.sectionTitle) as TextView

                val sectionPos = Integer.parseInt(sectionText.tag.toString())
                val itemCount = trackerAdapter!!.getNumberOfItemsInSection(sectionPos)
                val sectionHeight = trackerAdapter!!.sectionHeight()
                val itemHeight = trackerAdapter!!.itemHeight()

                val total_count = itemCount * itemHeight + sectionHeight
                val a = total_count.toFloat() / deviceWidth.toFloat()
                val scrollY = Math.round(a * dx)
                recyclerView.scrollBy(0, (-scrollY).toInt())
                if (bgListItems!!.size > 5) {
                    if (sectionPos < bgListItems!!.size - 2) {
                        if (sectionPos > 1 && sectionPos != lastSectionPos) {

                            itemIndex = sectionPos - 3 + 1


                            redrawGraph(itemIndex)

                            lastSectionPos = sectionPos
                        }
                    }
                }
            }

            return false
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            Log.d("onFlingX", (e1.x - e2.x).toString() + "")
            return true

        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_custom_graph)
        recyclerView = findViewById(R.id.allReadingRecyclerView) as RecyclerView
        hsvw = findViewById(R.id.hsvw) as HorizontalScrollView
        graphBg = findViewById(R.id.graphBg) as ImageView
        rootView = findViewById(R.id.rootView) as RelativeLayout
        rootLayout = findViewById(R.id.rootLayout) as LinearLayout

        var mTopToolbar = findViewById(R.id.toolbar) as Toolbar
        var tvToolbarTitle = findViewById(R.id.tbTitle) as TextView
        setSupportActionBar(mTopToolbar)

        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
            getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        }

        tvToolbarTitle.setText("")
        mTopToolbar.setTitle("Blood Glucose Tracker")
        mTopToolbar.setSubtitle("")
        mTopToolbar.setTitleTextColor(resources.getColor(R.color.color_ffffff))
        assert(getSupportActionBar() != null)
        getSupportActionBar()!!.show()
        topBarHeight = mTopToolbar.layoutParams.height


        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        deviceWidth = size.x
        deviceHeight = size.y

        graphHeight = deviceHeight / 4
        x_axis_distance_px = deviceWidth / 4 - 10

        bgTrackerItems = ArrayList<DataBGTrackerItem>()
        bgListItems = ArrayList<BgListItem>()
        filteredList = ArrayList<BgListItem>()
        graphBg.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        hsvw.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        graphBg.isDrawingCacheEnabled = false
        hsvw.isDrawingCacheEnabled = false

        mGesture = GestureDetector(this, mOnGesture)

        layoutManager = StickyHeaderLayoutManager()

        layoutManager.headerPositionChangedCallback = StickyHeaderLayoutManager.HeaderPositionChangedCallback { sectionIndex, header, oldPosition, newPosition ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val elevated = newPosition == StickyHeaderLayoutManager.HeaderPosition.STICKY
                header.elevation = (if (elevated) 8 else 0).toFloat()
            }
        }

        recyclerView.setLayoutManager(layoutManager)


        recyclerView.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            listScroll = 1
            false
        })
        hsvw.setOnTouchListener { view, motionEvent ->
            listScroll = 2
            mGesture!!.onTouchEvent(motionEvent)
            false
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (listScroll == 1) {

                    val holder = layoutManager.getFirstVisibleHeaderViewHolder(true)
                    val view = holder!!.itemView
                    val sectionText = view.findViewById(R.id.sectionTitle) as TextView
                    val sectionPos = Integer.parseInt(sectionText.tag.toString())
                    val itemCount = trackerAdapter!!.getNumberOfItemsInSection(sectionPos)

                    val sectionHeight = trackerAdapter!!.sectionHeight()
                    val itemHeight = trackerAdapter!!.itemHeight()

                    val total_count = itemCount * itemHeight + sectionHeight
                    val a = deviceWidth.toFloat() / total_count.toFloat()
                    val scrollX = Math.round(a * dy)
                    hsvw.scrollBy((-scrollX).toInt(), 0)

                    if (bgListItems!!.size > 5) {
                        if (sectionPos < bgListItems!!.size - 2) {
                            if (sectionPos > 1 && sectionPos != lastSectionPos) {

                                itemIndex = sectionPos - 3 + 1


                                redrawGraph(itemIndex)

                                lastSectionPos = sectionPos
                            }
                        }
                    }
                }
            }

        })
        getDataForBg()
    }

    var maxY: Int = 0

    private fun getDataForBg() {

        // BGTrackersResponse
        var response = Gson().fromJson(loadJSONFromAsset(), BGTrackersResponse::class.java)
       /* val dbData = TrackerDatabase.getInstance(this).getTrackerDao().all
        for (i in 0 until dbData.size) {
            val databgItem: DataBGTrackerItem?=null
            databgItem!!.SugarID = dbData[i].SugarID
            databgItem!!.DateTime = dbData[i].DateTime
            databgItem!!.Reading = dbData[i].Reading
            databgItem!!.ReadingType = dbData[i].ReadingType
            bgTrackerItems?.add(databgItem)
        }*/

       bgTrackerItems?.addAll(response.data)
        Collections.sort(bgTrackerItems) { lhs, rhs ->
            val a = getDate(truncateMilliseconds(lhs.DateTime), "yyyy-MM-dd'T'HH:mm:ss")
            val b = getDate(truncateMilliseconds(rhs.DateTime), "yyyy-MM-dd'T'HH:mm:ss")

            b!!.compareTo(a)
        }

        var max = Integer.MIN_VALUE
        for (i in bgTrackerItems!!.indices) {
            if (bgTrackerItems!!.get(i).Reading > max) {
                max = (bgTrackerItems!!.get(i).Reading as Double).toInt()
            }
        }
        maxY = max + 1
        var lastDate = ""
        for (i in bgTrackerItems!!.indices) {
            val reading = bgTrackerItems!!.get(i).Reading
            val dateTime = bgTrackerItems!!.get(i).DateTime
            val formattedDate = appServerFormatDate(dateTime)
            val formattedTime = getTime(dateTime)

            val arrItemBean = ArrayList<ItemBean>()

            val itemBean = ItemBean(0, "", "", "", "", "")
            itemBean?.readingValue = reading.toString()
            itemBean?.time = formattedTime
            itemBean?.readingType = (bgTrackerItems!!.get(i).ReadingType!!)
            itemBean?.note = (bgTrackerItems!!.get(i).Note!!)
            itemBean?.itemId = (bgTrackerItems!!.get(i).SugarID)
            itemBean?.dateTime = (bgTrackerItems!!.get(i).DateTime!!)

            arrItemBean.add(itemBean)


            val bgListItem = BgListItem("", null)
            bgListItem?.date = formattedDate
            bgListItem?.list = arrItemBean

            if (!lastDate.equals(formattedDate, ignoreCase = true)) {

                bgListItems?.add(bgListItem)

            } else {
                val item = bgListItems!!.get(bgListItems!!.size - 1)
                val list = item.list
                if (itemBean != null) {
                    list?.add(itemBean)
                }
                item.list = list
                bgListItems!!.set(bgListItems!!.size - 1, item)

            }

            lastDate = formattedDate

        }
        for (j in bgListItems!!.indices.reversed()) {
            filteredList?.add(bgListItems!!.get(j))
        }
        drawGraph()

    }

    fun getTime(date: String?): String {
        return if (date != null && date.length > 0) {
            formatDate("yyyy-MM-dd'T'HH:mm:ss", "HH:mm", truncateMilliseconds(date))
        } else {
            ""
        }
    }

    fun appServerFormatDate(date: String?): String {
        return if (date != null && date.length > 0) {
            formatDate("yyyy-MM-dd'T'HH:mm:ss", "MMM dd, yyyy", truncateMilliseconds(date))
        } else {
            ""
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

    fun getDate(inputDate: String, formatSrc: String): Date? {
        var myDate: Date? = null
        try {
            val sdf = SimpleDateFormat(formatSrc, Locale.US)
            myDate = sdf.parse(inputDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return myDate


    }

    fun truncateMilliseconds(date: String?): String {
        val truncatedDate: String
        truncatedDate = if (date!!.contains(".")) date.substring(0, date.indexOf(".")) else date
        return truncatedDate

    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val `is` = assets.open("bg_reading.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val charset = Charsets.UTF_8
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    private fun drawGraph() {

        if (bgListItems?.size!! > 5) {
            totalFrame = 5
        } else {

            totalFrame = bgListItems?.size!!
        }

        setAdapterAndDecor(recyclerView)

        runOnUiThread(Runnable {
            emptyBitmap = Bitmap.createBitmap(totalFrame * deviceWidth,
                    graphHeight, Bitmap.Config.ARGB_8888)


            chartBitmap = drawXYGraph(emptyBitmap!!, bgListItems!!, itemIndex)

            graphBg.setImageBitmap(chartBitmap)
        })

        hsvw.postDelayed({ hsvw.fullScroll(HorizontalScrollView.FOCUS_RIGHT) }, 5L)


    }


    private fun setAdapterAndDecor(rv: RecyclerView) {
        //Collections.reverse(arrList);
        trackerAdapter = TrackerAdapter(bgListItems, this, topBarHeight, graphHeight, trackerDetail, currentType)

        rv.adapter = trackerAdapter


    }

    private fun drawXYGraph(bitmap: Bitmap, bgListItems: ArrayList<BgListItem>, itemIndex: Int): Bitmap {

        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888)

        canvas = Canvas(output)

        val color = 0x000B0B91
        paint = Paint()
        rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = 18f

        paint!!.setAntiAlias(true)
        canvas!!.drawARGB(0, 0, 0, 0)
        paint!!.setColor(color)

        canvas!!.drawRoundRect(rectF, roundPx, roundPx, paint)

        val cur_elt_array = arrayOfNulls<String>(4)
        cur_elt_array[0] = "Voltage"
        cur_elt_array[1] = "volts"
        cur_elt_array[2] = maxY.toString() + ""  // max
        cur_elt_array[3] = "0"    //min

        // labels = Vector()
        // labels?.add(cur_elt_array)

        draw_the_grid(canvas!!)

        drawBgGraph()


        canvas!!.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    private fun draw_the_grid(this_g: Canvas) {


        val curElt: Any

        val left_margin_d: Int


        val cur_elt_array = arrayOfNulls<String>(4)
        cur_elt_array[0] = "Voltage"
        cur_elt_array[1] = "volts"
        cur_elt_array[2] = maxY.toString() + ""  // max
        cur_elt_array[3] = "0"    //min


        rounded_max = java.lang.Double.parseDouble(cur_elt_array[2])
        rounded_min = java.lang.Double.parseDouble(cur_elt_array[3])


        val paint = Paint()
        paint.textSize = 22f

        left_margin_d = getCurTextLengthInPixels(paint, java.lang.Double.toString(rounded_max))

        var p_height = 0

        p_height = graphHeight - 100

        val p_width = totalFrame * deviceWidth
        val tmp_draw_sizes = intArrayOf(2 + left_margin_d, 25, p_width - 2 - left_margin_d, p_height - 25 - 5)
        drawSizes = tmp_draw_sizes

        paint.style = Paint.Style.FILL

        paint.style = Paint.Style.STROKE

        print_axis_values_4_grid(this_g, java.lang.Double.toString(rounded_max), java.lang.Double.toString(rounded_min), 2, 0)

    }

    private fun print_axis_values_4_grid(thisDrawingArea: Canvas, cur_max: String, cur_min: String, x_guide: Int, this_idx: Int) {

        val this_str: String
        val delta = (java.lang.Double.valueOf(cur_max) - java.lang.Double.valueOf(cur_min)) / 5
        val paint = Paint()

        paint.color = Color.WHITE
        //  paint.setTypeface(Typeface.SANS_SERIF);
        // val tf = Typeface.createFromAsset(getAssets(), "fonts/Cabin-Regular.ttf")
        //   paint.typeface = tf


        var textSize = 0
        val densityPx = getResources().getDisplayMetrics().density

        if (densityPx.toDouble() == 1.0) {
            textSize = 14
        } else if (densityPx.toDouble() == 1.5) {

            textSize = 20
        } else if (densityPx == 2f) {
            textSize = 28
        } else if (densityPx > 2) {

            textSize = 36
        }

        paint.textSize = textSize.toFloat()

        thisDrawingArea.drawText("00:00", 0f, (graphHeight - 20).toFloat(), paint)
        thisDrawingArea.drawText("06:00", x_axis_distance_px.toFloat(), (graphHeight - 20).toFloat(), paint)
        thisDrawingArea.drawText("12:00", (x_axis_distance_px * 2).toFloat(), (graphHeight - 20).toFloat(), paint)
        thisDrawingArea.drawText("18:00", (x_axis_distance_px * 3).toFloat(), (graphHeight - 20).toFloat(), paint)


        if (totalFrame == 1) {
            thisDrawingArea.drawText("00:00", (x_axis_distance_px * 4).toFloat(), (graphHeight - 20).toFloat(), paint)
        }


        var lastX4 = x_axis_distance_px * 3


        for (i in 0 until totalFrame - 1) {

            thisDrawingArea.drawText("00:00", (lastX4 + x_axis_distance_px).toFloat(), (graphHeight - 20).toFloat(), paint)


            thisDrawingArea.drawText("06:00", (lastX4 + x_axis_distance_px * 2).toFloat(), (graphHeight - 20).toFloat(), paint)
            thisDrawingArea.drawText("12:00", (lastX4 + x_axis_distance_px * 3).toFloat(), (graphHeight - 20).toFloat(), paint)
            thisDrawingArea.drawText("18:00", (lastX4 + x_axis_distance_px * 4).toFloat(), (graphHeight - 20).toFloat(), paint)

            if (i == totalFrame - 2) {
                thisDrawingArea.drawText("00:00", (lastX4 + x_axis_distance_px * 5).toFloat(), (graphHeight - 20).toFloat(), paint)
            }


            lastX4 = lastX4 + x_axis_distance_px * 4


        }
        paint.pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 5f)
        thisDrawingArea.drawLine(0f, (graphHeight - 70).toFloat(), (totalFrame * deviceWidth).toFloat(), (graphHeight - 70).toFloat(), paint)

    }

    private fun drawBgGraph() {
        PlotOnGraph.plot_array_list_bg(rounded_max, rounded_min, itemIndex, this, this!!.canvas!!, this!!.filteredList!!, "the title", 0, this!!.drawSizes!!, x_axis_distance_px)
        // PlotOnGraph.plot_array_list_bg(itemIndex, this, canvas, filteredList, labels, "the title", 0, drawSizes, x_axis_distance_px)
    }

    private fun getCurTextLengthInPixels(this_paint: Paint, this_text: String): Int {
        val tp = this_paint.fontMetrics
        val rect = Rect()
        this_paint.getTextBounds(this_text, 0, this_text.length, rect)
        return rect.width()
    }

    private fun redrawGraph(itemIndex: Int) {

        chartBitmap!!.eraseColor(Color.TRANSPARENT)

        graphBg.invalidate()

        draw_the_grid(canvas)

        if (trackerDetail.equals("Blood Glucose", ignoreCase = true)) {
            drawBgGraph()
        }

        hsvw.scrollTo(totalFrame * deviceWidth - deviceWidth * 3, 0)

    }
}