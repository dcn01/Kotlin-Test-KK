package com.example.kotlintestkk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.json.JSONObject
import java.io.IOException
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startServer()
        println("server started")
    }

    private var mHttpServer: HttpServer? = null

    private fun startServer() {
        // adapted from https://medium.com/hacktive-devs/creating-a-local-http-server-on-android-49831fbad9ca
        try {
            mHttpServer = HttpServer.create(InetSocketAddress(5000), 0)
            mHttpServer!!.executor = Executors.newCachedThreadPool()

            mHttpServer!!.createContext("/", HttpHandler { exchange ->
                run {
                    // Get request method
                    when (exchange!!.requestMethod) {
                        "GET" -> {
                            val responseText = "Welcome to my server"
                            exchange.sendResponseHeaders(200, responseText.length.toLong())
                            val os = exchange.responseBody
                            os.write(responseText.toByteArray())
                            os.close()
                        }
                        "POST" -> {
                            val inputStream = exchange.requestBody
                            val s = Scanner(inputStream).useDelimiter("\\A")
                            val requestBody = if (s.hasNext()) s.next() else ""
                            val jsonBody = JSONObject(requestBody)
                            val responseText = jsonBody.toString()
                            exchange.sendResponseHeaders(200, responseText.length.toLong())
                            exchange.responseBody.write(jsonBody.toString().toByteArray())
                            exchange.responseBody.close()
                        }
                    }
                }
            }
            )
            mHttpServer!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}
