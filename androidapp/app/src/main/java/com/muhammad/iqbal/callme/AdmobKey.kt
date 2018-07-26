package com.muhammad.iqbal.callme

class AdmobKey {
    companion object {
        var test = true
        val mAppId = "ca-app-pub-7893979407683638~6215204540"
        val mTestAppId = "ca-app-pub-3940256099942544~3347511713"
        val mRewardedVideoAdUnit = "ca-app-pub-7893979407683638/2917698189"
        val mTestRewardedVideoAdUnit = "ca-app-pub-3940256099942544/5224354917"
        fun getAppId() = if(test) mTestAppId else mAppId
        fun getRewardedVideoAdUnit() =  if(test) mTestRewardedVideoAdUnit else mRewardedVideoAdUnit
    }
}