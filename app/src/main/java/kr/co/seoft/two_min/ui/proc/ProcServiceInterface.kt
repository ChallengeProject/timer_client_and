package kr.co.seoft.two_min.ui.proc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class ProcServiceInterface(val ctx: Context) {

    var connection: ServiceConnection?
    var service: ProcService? = null

    init {
        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
                service = (iBinder as ProcService.ProcServiceBinder).service
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                connection = null
                service = null
            }
        }
        val intent = Intent(ctx, ProcService::class.java)
        ctx.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        connection?.let {
            ctx.unbindService(connection)
        }
    }

    fun updateActViewNow() {
        service?.updateActViewNow()
    }

    fun restart() {
        service?.restart(ProcService.StartType.RESTART)
    }

    fun pause() {
        service?.pause()
    }

    fun stop() {
        service?.stop(true)
    }

    fun addMin() {
        service?.addMin()
    }

    fun move(pos: Int) {
        service?.move(pos)
    }

    fun turnRepeat(isOn: Boolean) {
        service?.turnRepeat(isOn)
    }


    fun getRepeat() {
        service?.brdIsRepeat()
    }

    fun stopSound() {
        service?.stopSound()
    }


}