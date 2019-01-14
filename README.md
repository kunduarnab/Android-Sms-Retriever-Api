# Android Sms Retriever Api
Android phone number verification using Google's Sms Retriever Api. This method does not required any SMS Permissions.
- Simple and Clean Coding
- Used Google Sms Retriever Api
- Used LocalBroadcastManager to receive the OTP
- Can generate application hash from AppSignatureHelper.java
- Does not require any SMS_PERMISSION

## Setup
Add this dependency  to app level Build Gradle
```groovy
	dependencies {
      implementation 'com.google.android.gms:play-services-base:16.0.1'
      implementation 'com.google.android.gms:play-services-identity:16.0.0'
      implementation 'com.google.android.gms:play-services-auth:16.0.1'
      implementation 'com.google.android.gms:play-services-auth-api-phone:16.0.0'
	}
```

## Getting user phone number
```java
// Construct a request for phone numbers and show the picker
    private void getUserPhoneNumber() {
        try {
            HintRequest hintRequest = new HintRequest.Builder()
                    .setPhoneNumberIdentifierSupported(true)
                    .build();
            GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.CREDENTIALS_API)
                    .build();
            PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest);
            startIntentSenderForResult(intent.getIntentSender(), REQUEST_CODE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

// Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                String phone_number = credential.getId();
                //Send the phone_number to backend server for sending otp
                Toast.makeText(this, phone_number, Toast.LENGTH_SHORT).show();
            }
        }
    }
```

## Listening for the OTP from the Local BroadCasting 
```java
private void startSMSListener() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                txtView.setText("Waiting for the OTP");
                //Toast.makeText(MainActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                txtView.setText("Cannot Start SMS Retriever");
                //Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }
```
