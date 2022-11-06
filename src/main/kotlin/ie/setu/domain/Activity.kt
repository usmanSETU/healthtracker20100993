package ie.setu.domain

data class ActivityDTO(val user: Activity? = null)
data class Activity (var id:Int,
                     var calories:String,
                     var activityName:String);