package com.oogatta.wifisetup

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager

class WifiSetupService : IntentService(WifiSetupService::class.java.simpleName) {
    override fun onHandleIntent(intent: Intent?) {
        intent?.apply {
            when (action) {
                "WifiSetupService.Connect" -> connect(intent)
                "WifiSetupService.Remove" -> remove(intent)
            }
        }
    }

    /**
     * Connect
     *
     * $ am startservice \
     * -n com.oogatta.wifisetup/.WifiSetupService \
     * -a WifiSetupService.Connect
     * -e ssid network_ssid
     * -e passphrase network_pass
     *
     */
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

    /**
     * Remove
     *
     * $ am startservice \
     * -n com.oogatta.wifisetup/.WifiSetupService \
     * -a WifiSetupService.Remove
     * -e ssid network_ssid
     *
     */
    private fun remove(intent: Intent) {
        val ssid = intent.getStringExtra("ssid") ?: return

        (application.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply {
            configuredNetworks.find { it.SSID == String.format("\"%s\"", ssid) }?.networkId?.also {
                removeNetwork(it)
            }
        }

    }
}