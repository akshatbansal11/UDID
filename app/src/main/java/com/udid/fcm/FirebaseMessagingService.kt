package com.udid.fcm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.udid.utilities.AppConstants
import com.udid.utilities.Utility
import org.json.JSONException

class FirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MessagingService"
//    private var viewModel: ViewModel? = null

    override fun onNewToken(refreshedToken: String) {
        if(refreshedToken!="")
            Utility.savePreferencesString(this@FirebaseMessagingService, AppConstants.FIREBASE_DEVICE_TOKEN, refreshedToken)
        Log.e(TAG, "New token: $refreshedToken")
//        updateToken(refreshedToken)
    }

//    private fun updateToken(token: String) {
//        viewModel = ViewModel()
//        viewModel?.init()
//        viewModel?.getPushNotification(this,
//            GetPushNotification(token, Utility.getPreferenceInt(this,AppConstants.USER_ID)),false
//        )
//    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.e("notify", "Data:" + Gson().toJson(remoteMessage))
//        Log.e(TAG, "Data Notification: " + remoteMessage.notification.toString())
        if (remoteMessage.data.isNotEmpty()) {
//            Log.e(TAG, "Data: " + remoteMessage.data.toString())
//            Log.e(TAG, "Data: $remoteMessage")
//             Log.e(TAG, "Data Notification: " + remoteMessage.notification.toString())
            val data: Map<String?, String?> = remoteMessage.data

            var title = remoteMessage.data["title"]
            var content = remoteMessage.data["body"]
            var userType = remoteMessage.data["usertype"]


            Log.d(TAG, "data1 : ${remoteMessage.data["title"]}")
            Log.d(TAG, "data2 : ${remoteMessage.data["body"]}")
            Log.d(TAG, "data3 : ${remoteMessage.data["usertype"]}")
//            val jData=convertToJSON(data.toString())
//            Log.d("jdata",jData)
//            val json = JSONObject(jData)
            if (data.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {
//                        Logger.e("Permsision", "User accepted the notifications!")
                        sendPushNotification(title, content,userType)
                    } else {
//                        Logger.e("Permission", "User not accepted the notifications!")
                    }
                } else {
                    sendPushNotification(title, content,userType)
                }
            }
        }
    }

    private fun sendPushNotification(title: String?,
                                     body: String?,userType:String?) {
        //optionally we can display the json into log
//        Log.e(TAG, "Notification JSON $json")
//      Log.e(TAG, "Notification JSON ${json.get("type")}")
        var intent: Intent?= null

        try {
//            val value = json.getJSONObject("value")
//            val type = value.getString("type").toString()
//            val title = title
//            val message = body
//          val notificationType = json.getInt("noti_type").toString()
//             if (userType=="Employee")
//             {
//                 val mNotificationManager = NotificationManager(applicationContext)
//                 intent = Intent(applicationContext, HomeActivityEmployee::class.java)
//                 intent.putExtra(AppConstants.NOTIFICATION_DATA, "notification")
//                 intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                 mNotificationManager.showSmallNotification(title, body, intent)
//             }
//            else{
//                 val mNotificationManager = NotificationManager(applicationContext)
//                 intent = Intent(applicationContext, HomeActivity::class.java)
//                 intent.putExtra(AppConstants.NOTIFICATION_DATA, "notification")
//                 intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                 mNotificationManager.showSmallNotification(title, body, intent)
//            }
        } catch (e: JSONException){
              e.printStackTrace()
        }
    }
}