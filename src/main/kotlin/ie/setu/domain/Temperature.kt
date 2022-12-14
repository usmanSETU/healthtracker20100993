package ie.setu.domain

import org.joda.time.DateTime

data class Temperature(var id:Int,var temperature:Float, var userId:Int, var createdAt:DateTime)
