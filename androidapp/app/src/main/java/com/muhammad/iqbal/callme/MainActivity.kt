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
import android.content.SharedPreferences
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener

class MainActivity : AppCompatActivity(), RewardedVideoAdListener  {
    var name : String? = null
    var phoneNumber : String? = null
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    val MY_PERMISSIONS_REQUEST_CALL_PHONE = 1

    private var mDb: ContactDataBase? = null
    private lateinit var mName: TextView
    private lateinit var mPhoneNumber: TextView
    private lateinit var mEditName : EditText
    private lateinit var mEditPhoneNumber : EditText
    private lateinit var mDbWorkerThread: DbWorkerThread
    private val mUiHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()
        mName = findViewById(R.id.tvName)
        mPhoneNumber = findViewById(R.id.tvPhoneNumber)
        mEditName = findViewById(R.id.editName)
        mEditPhoneNumber = findViewById(R.id.editPhoneNumber)
        mDb = ContactDataBase.getInstance(this)

        MobileAds.initialize(this, AdmobKey.getAppId())
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this

        loadRewardedVideoAd()

        // Get the support action bar
        val actionBar = supportActionBar

        // Set the action bar title, subtitle and elevation
        actionBar!!.title = "Call Me"
        actionBar.subtitle = "Call Your loved ones"
        actionBar.elevation = 4.0F

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val sharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                "name" -> {
                    name = sharedPreferences.getString("name", "me")
                    btnCallMe.text = "Call " + name
                }
                "phoneNumber" -> {
                    phoneNumber = sharedPreferences.getString("phoneNumber", "9703035627")
                }
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)

        name = prefs.getString("name", "me")
        phoneNumber = prefs.getString("phoneNumber", "9703035627")
        btnCallMe.text = "Call " + name
        btnCallMe.setOnClickListener {
            if (mRewardedVideoAd.isLoaded) {
                mRewardedVideoAd.show()
            }
        }

        btnSave.setOnClickListener{
            insertContactDataInDb(contactData = ContactData(null, mEditName.text.toString(), mEditPhoneNumber.text.toString()))
        }
        fetchContactDataFromDb()
    }

    private fun bindDataWithUi(contactData: ContactData?) {
        mName.text = contactData?.name
        mPhoneNumber.text = contactData?.phoneNumber
    }

    private fun fetchContactDataFromDb() {
        val task = Runnable {
            val contactData =
                    mDb?.contactDataDao()?.getAll()
            mUiHandler.post({
                if (contactData == null || contactData?.size == 0) {
                    showToast("No data in cache..!!", Toast.LENGTH_SHORT)
                } else {
                    bindDataWithUi(contactData = contactData?.get(0))
                }
            })
        }
        mDbWorkerThread.postTask(task)
    }

    private fun insertContactDataInDb(contactData: ContactData) {
        val task = Runnable { mDb?.contactDataDao()?.insert(contactData) }
        mDbWorkerThread.postTask(task)
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
            R.id.contacts -> {
                val intent = Intent(this,ListActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadRewardedVideoAd() {
        mRewardedVideoAd?.loadAd(AdmobKey.getRewardedVideoAdUnit(),
                AdRequest.Builder().build())
    }

    private fun makeCall() {
        val phoneIntent : Intent = Intent(Intent.ACTION_CALL)
        phoneIntent.data = Uri.parse("tel:" + phoneNumber)
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(phoneIntent)
        }

    }

    override fun onRewarded(reward: RewardItem) {
        Toast.makeText(this, "onRewarded! currency: ${reward.type} amount: ${reward.amount}",
        Toast.LENGTH_SHORT).show()
        // Reward the user.

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            makeCall()
        }
        // Here, thisActivity is the current activity
        else {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                            Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        MY_PERMISSIONS_REQUEST_CALL_PHONE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        //loadRewardedVideoAd()
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CALL_PHONE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    makeCall()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    btnCallMe.setEnabled(false)
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show()
    }
    override fun onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        mRewardedVideoAd.pause(this)
    }

    override fun onResume() {
        super.onResume()
        mRewardedVideoAd.resume(this)
    }

    override fun onDestroy() {
        mRewardedVideoAd.destroy(this)
        ContactDataBase.destroyInstance()
        mDbWorkerThread.quit()
        super.onDestroy()

    }
}

