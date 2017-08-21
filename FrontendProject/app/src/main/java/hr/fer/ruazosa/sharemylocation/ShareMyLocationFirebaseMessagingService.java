package hr.fer.ruazosa.sharemylocation;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Alen on 10.7.2017..
 */

public class ShareMyLocationFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Notiffication received", remoteMessage.getNotification().getBody());
    }
}
