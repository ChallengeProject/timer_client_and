package com.timer.proc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class ProcExceedServiceInterface(val ctx: Context) {

    var connection : ServiceConnection?
    var service : ProcExceedService? = null

    init {
        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
                service = (iBinder as ProcExceedService.ProcExceedServiceBinder).service
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                connection = null
                service = null
            }
        }
        val intent = Intent(ctx,ProcExceedService::class.java)
        ctx.bindService(intent,connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(){
        connection?.let {
            ctx.unbindService(connection)
        }
    }

    fun updateActViewNow(){
        service?.updateActViewNow()
    }

    fun restart(){
        service?.restart()
    }

    fun pause(){
        service?.pause()
    }

    fun stop(){
        service?.stop(true)
    }



}