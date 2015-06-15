package iriemo.bangaloreweather.service;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by iriemo on 12/6/15.
 */
public class MyInstanceIdListenerService extends InstanceIDListenerService {


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
// [START refresh_token]
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
    }
}
