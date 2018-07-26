package com.muhammad.iqbal.callme

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.view.Menu
import android.view.MenuItem


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the support action bar
        val actionBar = supportActionBar

        // Set the action bar title, subtitle and elevation
        actionBar!!.title = "Call Me"
        actionBar.subtitle = "Call Your loved ones"
        actionBar.elevation = 4.0F

        btnCallMe.setOnClickListener {
            val phoneIntent : Intent = Intent(Intent.ACTION_CALL)
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val phoneNumber = prefs.getString("phoneNumber", "9703035627")

            phoneIntent.data = Uri.parse("tel:" + phoneNumber)

            if (ActivityCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(phoneIntent)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this,SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
