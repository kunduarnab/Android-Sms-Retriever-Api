package in.arnabkundu.androidsmsretrieverapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime. Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
public class MySMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            if (status != null) {
                switch(status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get SMS message contents
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        // Extract one-time code from the message and complete verification
                        // by sending the code back to your server.
                        if(message!=null)
                        {
                            //Get OTP from message string
                            String otp = getVerificationCode(message);
                            //Create a LocalBroadcastManager with own Intent Action
                            Intent smsIntent = new Intent("MyCustomIntentAction");
                            smsIntent.putExtra("otp",otp);
                            //Broadcast the otp
                            LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Waiting for SMS timed out (5 minutes)
                        // Handle the error ...
                        break;
                }
            }
        }
    }

    /**
     * Message Structure: <#> Your verification code is: <OTP> <APP_HASH>
     * OTP - Any Random Generated String (In my case 4 digit number)
     * APP_HASH - You have to generate application hash by calling the getAppSignatures()
     *            from AppSignatureHelper.java
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf("is:");
        if (index != -1) {
            int start = index + 4;
            int length = 4;
            code = message.substring(start, start + length);
            return code;
        }
        return code;
    }
}
