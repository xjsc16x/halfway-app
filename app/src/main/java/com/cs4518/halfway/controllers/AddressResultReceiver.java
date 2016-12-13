package com.cs4518.halfway.controllers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


public class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {}
    }
