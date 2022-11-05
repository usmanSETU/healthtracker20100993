package ie.setu.controllers

import io.javalin.http.Context

object Main {
    fun ping(ctx: Context){
        ctx.json("{success:true}")
    }
}