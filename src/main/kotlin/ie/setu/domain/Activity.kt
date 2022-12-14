package ie.setu.domain

import org.joda.time.DateTime

data class Activity (var id:Int,
                     var calories:String,
                     var activityName:String,
                     var userId:Int,
                     var createdAt:DateTime)