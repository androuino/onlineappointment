package com.intellisrc.josapedmoreno

import com.intellisrc.core.Config
import com.intellisrc.core.SysInfo
import com.intellisrc.core.SysService
import com.intellisrc.db.Database
import com.intellisrc.josapedmoreno.appointment.Appointment
import com.intellisrc.josapedmoreno.auth.AuthService
import com.intellisrc.josapedmoreno.ui.WebUIService
import com.intellisrc.web.WebService

class Main : SysService() {
    private val webService = WebService()

    override fun onStart() {
        /*
        The database uses a connection pool to recycle connections which
        improves performance. That pool will increase or decrease
        as needed.

        The next line will initialize the database object. Every time
        we call `DB db = Database.connect()` it will recycle a connection from
        the pool and `db.close()` will return it to the pool.
         */
        Database.init()
        Appointment.initDB()

        /*
        The web server is running over Spark Framework. This implementation
        allow the creation of services in an organized way. We can add
        as many services as we need.

        Web Resources are kept in the user directory: resources/
         */
        var port = Config.getInt("web.port", 7777)
        if (args.isNotEmpty())
            port = Integer.parseInt(args.poll())
        webService.port = port
        webService.setResources(SysInfo.getFile("resources", "public"))
        webService.addService(AuthService())
        webService.addService(WebUIService())
        webService.start(true)
    }

    override fun onStop() {
        super.onStop()
        webService.stop()
        Database.quit()
    }

    companion object {
        init {
            service = Main()
        }
    }
}