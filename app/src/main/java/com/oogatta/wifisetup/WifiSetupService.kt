package com.oogatta.wifisetup

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager

class WifiSetupService : IntentService(WifiSetupService::class.java.simpleName) {
    /**
     * $ am startservice \
     * -n com.google.wifisetup/.WifiSetupService \
     * -a WifiSetupService.Connect
     * -e ssid network_ssid
     * -e passphrase network_pass
     *
     */
    override fun onHandleIntent(intent: Intent?) {
        intent?.apply {
            when (action) {
                "WifiSetupService.Connect" -> connect(intent)
            }
        }
    }

    private fun connect(intent: Intent) {
        val ssid = intent.getStringExtra("ssid") ?: return
        val passPhrase = intent.getStringExtra("passphrase") ?: return

        val wifiConf = WifiConfiguration().apply {
            SSID = String.format("\"%s\"", ssid)
            status = WifiConfiguration.Status.ENABLED
            preSharedKey = String.format("\"%s\"", passPhrase)
        }

        (application.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply {
            val netId = addNetwork(wifiConf)
            disconnect()
            enableNetwork(netId, false)
            reconnect()
        }

        println(ssid)
        println(passPhrase)

    }
}