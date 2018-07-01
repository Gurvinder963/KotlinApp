package glucosetracker.com.bloodgluscosetracker.response
import glucosetracker.com.bloodgluscosetracker.model.DataBGTrackerItem
data class BGTrackersResponse(

var status:Int=0,

var data:List<DataBGTrackerItem>

)