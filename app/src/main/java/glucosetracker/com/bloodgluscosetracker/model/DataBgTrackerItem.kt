package glucosetracker.com.bloodgluscosetracker.model

data class DataBGTrackerItem(

         var MemberID: Int = 0,
         var DateTime: String? = null,
         var SugarID: Int = 0,
         var Reading: Double = 0.toDouble(),
         var ReadingType: String? = null,
         var Note: String? = null
)

