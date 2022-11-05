package ie.setu.controllers

import io.javalin.http.Context

object MainController {
    fun ping(ctx: Context){
        ctx.json("{success:true}")
    }
}