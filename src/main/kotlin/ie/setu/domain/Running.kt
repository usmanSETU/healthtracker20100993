package ie.setu.domain

import org.joda.time.DateTime

data class Running(var id:Int,
                   var distance:Int,
                   var calories:Int,
                   var userId:Int,
                   var createdAt:DateTime)
