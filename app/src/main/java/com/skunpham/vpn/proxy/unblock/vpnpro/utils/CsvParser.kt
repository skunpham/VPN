package com.skunpham.vpn.proxy.unblock.vpnpro.utils

import android.util.Base64
import com.skunpham.vpn.proxy.unblock.vpnpro.model.Server
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object CsvParser {
    private const val HOST_NAME = 0
    private const val IP_ADDRESS = 1
    private const val SCORE = 2
    private const val PING = 3
    private const val SPEED = 4
    private const val COUNTRY_LONG = 5
    private const val COUNTRY_SHORT = 6
    private const val VPN_SESSION = 7
    private const val UPTIME = 8
    private const val TOTAL_USERS = 9
    private const val TOTAL_TRAFFIC = 10
    private const val LOG_TYPE = 11
    private const val OPERATOR = 12
    private const val MESSAGE = 13
    private const val OVPN_CONFIG_DATA = 14
    private const val PORT_INDEX = 2
    private const val PROTOCOL_INDEX = 1

    fun parse(response: ResponseBody): ArrayList<Server> {
        var listServerFree = ArrayList<Server>()
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null

        try {
            inputStream = response.byteStream()
            reader = BufferedReader(InputStreamReader(inputStream))
            var line: String
            while (reader.readLine().also { line = it } != null) {
                if (!line.startsWith("*") && !line.startsWith("#")) {
                    val serverFree = stringToServer(line)
                    if (serverFree.countryShort != "US") {
                        listServerFree.add(serverFree)
                    }
                }
            }
        } catch (e: Exception) {

        } finally {
            try {
                reader?.close()
                inputStream?.close()
            } catch (ignored: IOException) {
            }
        }
        return listServerFree
    }

    private fun stringToServer(line: String): Server {
        val vpns = line.split(",")
        val ovpnConfigData = String(Base64.decode(vpns[OVPN_CONFIG_DATA], Base64.DEFAULT))
        val lines = ovpnConfigData.split("[\\r\\n]+").toTypedArray()

        return Server(
            vpns[HOST_NAME],
            vpns[IP_ADDRESS],
            vpns[SCORE].toInt(),
            vpns[PING],
            vpns[SPEED].toLong(),
            vpns[COUNTRY_LONG],
            vpns[COUNTRY_SHORT],
            null,
            "",
            vpns[VPN_SESSION].toLong(),
            vpns[UPTIME].toLong(),
            vpns[TOTAL_USERS].toLong(),
            vpns[TOTAL_TRAFFIC],
            vpns[LOG_TYPE],
            vpns[OPERATOR],
            vpns[MESSAGE],
            ovpnConfigData,
            getPort(lines),
            getProtocol(lines),
            isStarred = false,
            isVip = false
        )
    }

    private fun getPort(lines: Array<String>): Int {
        var port = 0
        for (line in lines) {
            if (!line.startsWith("#")) {
                if (line.startsWith("remote")) {
                    port = line.split(" ").toTypedArray()[PORT_INDEX].toInt()
                    break
                }
            }
        }
        return port
    }

    private fun getProtocol(lines: Array<String>): String {
        var protocol = ""
        for (line in lines) {
            if (!line.startsWith("#")) {
                if (line.startsWith("proto")) {
                    protocol = line.split(" ").toTypedArray()[PROTOCOL_INDEX]
                    break
                }
            }
        }
        return protocol
    }
}