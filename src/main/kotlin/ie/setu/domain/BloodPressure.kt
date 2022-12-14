package ie.setu.domain

import org.joda.time.DateTime

data class BloodPressure(var id:Int,
                         var pulse:Int,
                         var systolic:Int,
                         var diastolic: Int,
                         var userId:Int,
                         var createdAt:DateTime)
