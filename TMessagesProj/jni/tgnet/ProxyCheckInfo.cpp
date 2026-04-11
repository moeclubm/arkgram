/*
 * This is the source code of tgnet library v. 1.1
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2015-2018.
 */

#include "ProxyCheckInfo.h"
#include "ConnectionsManager.h"
#include "FileLog.h"
#include <cstdlib>

#ifdef ANDROID
static JNIEnv *currentEnv() {
    JNIEnv *env = nullptr;
    if (javaVm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        if (javaVm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
            if (LOGS_ENABLED) DEBUG_E("can't attach current thread to jvm");
            exit(1);
        }
    }
    return env;
}
#endif

ProxyCheckInfo::~ProxyCheckInfo() {
#ifdef ANDROID
    if (ptr1 != nullptr) {
        DEBUG_DELREF("tgnet (2) request ptr1");
        currentEnv()->DeleteGlobalRef(ptr1);
        ptr1 = nullptr;
    }
#endif
}
