package com.example.flutter_payment_integration_native;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import co.tamara.sdk.PaymentResult;
import co.tamara.sdk.TamaraPayment;
import co.tamara.sdk.TamaraPaymentHelper;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;


public class MainJavaActivity extends FlutterActivity {
    private static final String CHANNEL = "flutter/native/payment_integration";

    private static MethodCall methodCaller = null;
    private static MethodChannel.Result methodResult = null;

    private static final String API_URL = "https://api-staging.tamara.co/";
    private static final String AUTH_TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhY2NvdW50SWQiOiI0NWQwMzAzOC1kM2I2LTQ1ODctYWY2Ny1hNDNlY2FlYjFiZDMiLCJ0eXBlIjoibWVyY2hhbnQiLCJzYWx0IjoiZjlmNWI3ZjUzMjY3NzM0NWZjNmNmNzk4ZWZlMDRiZGYiLCJpYXQiOjE1OTkzNzQ0MzksImlzcyI6IlRhbWFyYSJ9.OVeDMF5k_JCybrIGSnhscL0AUguCa0ZgUbvz3uyPcbhhXAH9ZDpXYSwtSkWXDuYNLda4_l9YcVg8oPz3RXhSxw348nNtDAbKhsdwPqauI-I_2OXX2ngyBIATdOExvv07TX1xmsyM9ftDFNSfhZ6q8acZy89I3YHUbxWiT-i_5oJsBv0XRCEwUuzoFjN6YW_1Y2lBgLpFP1xeEwFPX3ve9w5GP8j73D1O5UvrHYOoX-mQZ0qVjjDdgv6YL_IxBjBfoI9rLwXzsOpz0F_KtaEoIIqYrOW4k8VEmETyxgxyJ8PrBmqoD31O6Vkvs63nialYRApPcFxAvXyIj3CqnMydzw";
    private static final String NOTIFICATION_WEB_HOOK_URL = "https://tamara.co/pushnotification";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TamaraPayment.Companion.initialize(AUTH_TOKEN, API_URL, NOTIFICATION_WEB_HOOK_URL);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            // Note: this method is invoked on the main thread.
                            // TODO
                            methodCaller = call; //Assigning call to methodCaller
                            methodResult = result; //Assigning result to methodResult
                            //now can use methodCaller abd methodResult anywhere within the class

                            if (methodCaller.method.equals("getPaymentStatus")) {
                                startPayment();
                            }
                        }
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (TamaraPaymentHelper.Companion.shouldHandleActivityResult(requestCode, resultCode, data)) {
            if (data != null) {
                PaymentResult result = TamaraPaymentHelper.Companion.getData(data);
                if (result != null) {
                    if (PaymentResult.STATUS_CANCEL == result.getStatus()) {
                        Toast.makeText(this, "Cancel", Toast.LENGTH_LONG).show();
                        methodResult.success("Cancel");
                    } else if (PaymentResult.STATUS_FAILURE == result.getStatus()) {
                        Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
                        methodResult.success("Failure");
                    } else if (PaymentResult.STATUS_SUCCESS == result.getStatus()) {
                        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                        methodResult.success("Success");
                    }
                }
            }
        }
    }

    public void startPayment() {
        //Testing
        TamaraPayment.Companion.createOrder("123", "Shoe");
        TamaraPayment.Companion.setCustomerInfo("Hi", "Hello", "8129812981", "", true);
        TamaraPayment.Companion.addItem("shoe", "123", "123", 500, 200, 20, 1);
        TamaraPayment.Companion.setShippingAddress("", "", "", "", "", "", "", "");
        TamaraPayment.Companion.setBillingAddress("", "", "", "", "", "", "", "");
        TamaraPayment.Companion.setShippingAmount(500);
        TamaraPayment.Companion.setDiscount(20, "discount");
        TamaraPayment.Companion.startPayment(this);
    }
}
