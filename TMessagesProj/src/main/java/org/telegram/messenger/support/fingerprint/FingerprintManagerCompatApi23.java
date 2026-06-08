/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.telegram.messenger.support.fingerprint;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;

import androidx.biometric.BiometricManager;

import org.telegram.messenger.FileLog;

import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.Mac;

@TargetApi(23)
public final class FingerprintManagerCompatApi23 {

    public static boolean hasEnrolledFingerprints(Context context) {
        try {
            return BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    public static boolean isHardwareDetected(Context context) {
        try {
            int result = BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
            return result == BiometricManager.BIOMETRIC_SUCCESS || result == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    public static void authenticate(Context context, CryptoObject crypto, int flags, Object cancel,
                                    AuthenticationCallback callback, Handler handler) {
        callback.onAuthenticationError(BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED, "FingerprintManager API is unavailable");
    }

    public static class CryptoObject {

        private final Signature mSignature;
        private final Cipher mCipher;
        private final Mac mMac;

        public CryptoObject(Signature signature) {
            mSignature = signature;
            mCipher = null;
            mMac = null;
        }

        public CryptoObject(Cipher cipher) {
            mCipher = cipher;
            mSignature = null;
            mMac = null;
        }

        public CryptoObject(Mac mac) {
            mMac = mac;
            mCipher = null;
            mSignature = null;
        }

        public Signature getSignature() {
            return mSignature;
        }

        public Cipher getCipher() {
            return mCipher;
        }

        public Mac getMac() {
            return mMac;
        }
    }

    public static final class AuthenticationResultInternal {
        private CryptoObject mCryptoObject;

        public AuthenticationResultInternal(CryptoObject crypto) {
            mCryptoObject = crypto;
        }

        public CryptoObject getCryptoObject() {
            return mCryptoObject;
        }
    }

    public static abstract class AuthenticationCallback {

        public void onAuthenticationError(int errMsgId, CharSequence errString) {
        }

        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        }

        public void onAuthenticationSucceeded(AuthenticationResultInternal result) {
        }

        public void onAuthenticationFailed() {
        }
    }
}
