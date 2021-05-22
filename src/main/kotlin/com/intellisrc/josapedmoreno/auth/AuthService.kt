package com.intellisrc.josapedmoreno.auth

import com.intellisrc.core.Log
import com.intellisrc.web.ServiciableAuth
import com.intellisrc.web.JSON
import com.intellisrc.crypt.hash.PasswordHash
import com.intellisrc.db.Database
import com.intellisrc.web.Service.Allow
import spark.Request
import spark.Response
import java.lang.Exception

/**
 * This class is used to authenticate users into the service and
 * authorize other services.
 * It uses a session object for authorization.
 *
 *
 * NOTE: Using session for authorization may not be ideal for a public API
 * as there may be clients which don't support it. For this particular
 * project we can control the client, so this is the simplest solution.
 *
 *
 * Access: Public
 *
 * @since 1/18/20.
 */
class AuthService : ServiciableAuth {
    internal enum class Level {
        GUEST, ADMIN
    }

    /**
     * Returns the service path
     *
     * @return path as string
     */
    override fun getPath(): String {
        return "/auth"
    }

    override fun getLoginPath(): String {
        return "/login"
    }

    override fun getLogoutPath(): String {
        return "/logout"
    }

    /**
     * Provides the login logic
     * @param request HTTP request
     * @param response HTTP response
     * @return a map which becomes the session
     */
    override fun onLogin(request: Request, response: Response): Map<String, Any> {
        var level = Level.GUEST
        val json = JSON.decode(request.body()).toMap()
        val user = json["user"].toString()
        val pass = json["pass"].toString().toCharArray()
        if (pass.isNotEmpty()) {
            val db = Database.connect()
            val hash = db.table(authTable).field("pass").key("user")[user].toString()
            db.close()
            if (hash.isNotEmpty()) {
                val ph = PasswordHash()
                ph.setPassword(*pass)
                val login = ph.verify(hash)
                if (login) {
                    level = Level.ADMIN
                    Log.s("[%s] logged in as %s", request.ip(), user)
                } else {
                    Log.s("[%s] Provided password is incorrect. Hash: [%s]", request.ip(), ph.BCryptNoHeader())
                }
            } else {
                Log.s("[%s] User %s not found.", request.ip(), user)
            }
        } else {
            Log.s("[%s] Password was empty", request.ip(), user)
        }
        if (level == Level.GUEST) {
            response.status(401)
        }
        val map = HashMap<String, Any>()
        map["level"] = level
        map["ip"] = request.ip()
        return map
    }

    override fun getAllowOrigin(): String {
        return ""
    }

    /**
     * Logout
     * @param request HTTP request
     * @param response HTTP response
     * @return boolean on success.
     */
    override fun onLogout(request: Request, response: Response): Boolean {
        var ok = false
        if (request.session() != null) {
            request.session().invalidate()
            ok = true
        } else {
            Log.s("[%s] Session was empty", request.ip())
        }
        Log.s("[%s] logged out", request.ip())
        return ok
    }

    companion object {
        private const val authTable = "auth"

        /**
         * Evaluates if we should allow user
         * This is used by private services
         *
         * It will verify that the IP address and user level are correct
         *
         * @return Allow interface which is used to evaluate the request
         */
        fun allowAdmin(): Allow {
            return Allow { request ->
                if (request.session() != null) {
                    try {
                        Log.i("Request from ${request?.ip()}")
                        request?.ip() == request?.session()?.attribute("ip") && Level.valueOf(
                            request?.session()?.attribute<Any>("level")?.toString()?.toUpperCase()!!
                        ) == Level.ADMIN
                    } catch (ex: Exception) {
                        Log.e("Error during authorization", ex)
                        return@Allow false
                    }
                } else {
                    return@Allow false
                }
            }
        }
    }
}