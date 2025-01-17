//
// Created by julis.wang on 2022/2/11.
//
#include "func/MyGLRenderContext.h"
#include "jni.h"
#include "util/LogUtil.h"
#include "func/EGLRender.h"

#define NATIVE_RENDER_CLASS_NAME "julis/wang/learnopengl/opengl/MyNativeRender"
#define NATIVE_BG_RENDER_CLASS_NAME "julis/wang/learnopengl/opengl/egl/NativeEglRender"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL native_Init(JNIEnv *env, jobject instance) {
    MyGLRenderContext::GetInstance();
}

JNIEXPORT void JNICALL native_UnInit(JNIEnv *env, jobject instance) {
    MyGLRenderContext::DestroyInstance();
}

JNIEXPORT void JNICALL native_SetImageDataWithIndex
        (JNIEnv *env, jobject instance, jint index, jint format, jint width, jint height, jbyteArray imageData) {
    int len = env->GetArrayLength(imageData);
    auto *buf = new uint8_t[len];
    env->GetByteArrayRegion(imageData, 0, len, reinterpret_cast<jbyte *>(buf));
    MyGLRenderContext::GetInstance()->SetImageDataWithIndex(index, format, width, height, buf);
    delete[] buf;
    env->DeleteLocalRef(imageData);
}


JNIEXPORT void JNICALL native_SetImageData
        (JNIEnv *env, jobject instance, jint format, jint width, jint height, jbyteArray imageData) {
    int len = env->GetArrayLength(imageData);
    auto *buf = new uint8_t[len];
    env->GetByteArrayRegion(imageData, 0, len, reinterpret_cast<jbyte *>(buf));
    MyGLRenderContext::GetInstance()->SetImageData(format, width, height, buf);
    delete[] buf;
    env->DeleteLocalRef(imageData);
}

JNIEXPORT void JNICALL native_SetParamsInt
        (JNIEnv *env, jobject instance, jint paramType, jint value0, jint value1) {
    MyGLRenderContext::GetInstance()->SetParamsInt(paramType, value0, value1);
}

JNIEXPORT void JNICALL native_SetParamsFloat
        (JNIEnv *env, jobject instance, jint paramType, jfloat value0, jfloat value1) {
    MyGLRenderContext::GetInstance()->SetParamsFloat(paramType, value0, value1);
}

JNIEXPORT void JNICALL
native_UpdateTransformMatrix(JNIEnv *env, jobject instance, jfloat rotateX, jfloat rotateY, jfloat scaleX,
                             jfloat scaleY) {
    MyGLRenderContext::GetInstance()->UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
}

JNIEXPORT void JNICALL native_SetAudioData
        (JNIEnv *env, jobject instance, jshortArray data) {
    int len = env->GetArrayLength(data);
    auto *pShortBuf = new short[len];
    env->GetShortArrayRegion(data, 0, len, reinterpret_cast<jshort *>(pShortBuf));
    MyGLRenderContext::GetInstance()->SetParamsShortArr(pShortBuf, len);
    delete[] pShortBuf;
    env->DeleteLocalRef(data);
}

JNIEXPORT void JNICALL native_OnSurfaceCreated(JNIEnv *env, jobject instance) {
    MyGLRenderContext::GetInstance()->OnSurfaceCreated();
}


JNIEXPORT void JNICALL native_OnSurfaceChanged
        (JNIEnv *env, jobject instance, jint width, jint height) {
    MyGLRenderContext::GetInstance()->OnSurfaceChanged(width, height);
}

JNIEXPORT void JNICALL native_OnDrawFrame(JNIEnv *env, jobject instance) {
    MyGLRenderContext::GetInstance()->OnDrawFrame();
}

#ifdef __cplusplus
}
#endif

static JNINativeMethod g_RenderMethods[] = {
        {"nativeInit",                  "()V",       (void *) (native_Init)},
        {"nativeUnInit",                "()V",       (void *) (native_UnInit)},
        {"nativeSetImageData",          "(III[B)V",  (void *) (native_SetImageData)},
        {"nativeSetImageDataWithIndex", "(IIII[B)V", (void *) (native_SetImageDataWithIndex)},
        {"nativeSetParamsInt",          "(III)V",    (void *) (native_SetParamsInt)},
        {"nativeSetParamsFloat",        "(IFF)V",    (void *) (native_SetParamsFloat)},
        {"nativeSetAudioData",          "([S)V",     (void *) (native_SetAudioData)},
        {"nativeUpdateTransformMatrix", "(FFFF)V",   (void *) (native_UpdateTransformMatrix)},
        {"nativeOnSurfaceCreated",      "()V",       (void *) (native_OnSurfaceCreated)},
        {"nativeOnSurfaceChanged",      "(II)V",     (void *) (native_OnSurfaceChanged)},
        {"nativeOnDrawFrame",           "()V",       (void *) (native_OnDrawFrame)},
};



/*
 * Class:     com_byteflow_app_egl_NativeBgRender
 * Method:    native_EglRenderInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL native_EglRenderInit(JNIEnv *env, jobject instance) {
    EGLRender::GetInstance()->Init();

}


JNIEXPORT void JNICALL
native_EglRenderSetImageData(JNIEnv *env, jobject instance, jbyteArray data, jint width, jint height) {
    int len = env->GetArrayLength(data);
    uint8_t *buf = new uint8_t[len];
    env->GetByteArrayRegion(data, 0, len, reinterpret_cast<jbyte *>(buf));
    EGLRender::GetInstance()->SetImageData(buf, width, height);
    delete[] buf;
    env->DeleteLocalRef(data);


}

JNIEXPORT void JNICALL native_EglRenderSetIntParams(JNIEnv *env, jobject instance, jint type, jint param) {
    EGLRender::GetInstance()->SetIntParams(type, param);

}

JNIEXPORT void JNICALL native_EglRenderDraw(JNIEnv *env, jobject instance) {
    EGLRender::GetInstance()->Draw();
}

JNIEXPORT void JNICALL natuve_BgRenderUnInit(JNIEnv *env, jobject instance) {
    EGLRender::GetInstance()->UnInit();
}

static JNINativeMethod g_BgRenderMethods[] = {
        {"native_EglRenderInit",         "()V",     (void *) (native_EglRenderInit)},
        {"native_EglRenderSetImageData", "([BII)V", (void *) (native_EglRenderSetImageData)},
        {"native_EglRenderSetIntParams", "(II)V",   (void *) (native_EglRenderSetIntParams)},
        {"native_EglRenderDraw",         "()V",     (void *) (native_EglRenderDraw)},
        {"native_EglRenderUnInit",       "()V",     (void *) (natuve_BgRenderUnInit)},
};


static int RegisterNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *methods, int methodNum) {
    LOGCATE("RegisterNativeMethods");
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        LOGCATE("RegisterNativeMethods fail. clazz == nullptr");
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, methods, methodNum) < 0) {
        LOGCATE("RegisterNativeMethods fail");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static void UnregisterNativeMethods(JNIEnv *env, const char *className) {
    LOGCATE("UnregisterNativeMethods");
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        LOGCATE("UnregisterNativeMethods fail. clazz == nullptr");
        return;
    }
    env->UnregisterNatives(clazz);
}

// call this func when loading lib
extern "C" jint JNI_OnLoad(JavaVM *jvm, void *p) {
    LOGCATE("===== JNI_OnLoad =====");
    jint jniRet = JNI_ERR;
    JNIEnv *env = nullptr;
    if (jvm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return jniRet;
    }

    jint regRet = RegisterNativeMethods(env, NATIVE_RENDER_CLASS_NAME, g_RenderMethods,
                                        sizeof(g_RenderMethods) /
                                        sizeof(g_RenderMethods[0]));
    if (regRet != JNI_TRUE) {
        return JNI_ERR;
    }

    regRet = RegisterNativeMethods(env, NATIVE_BG_RENDER_CLASS_NAME, g_BgRenderMethods,
                                   sizeof(g_BgRenderMethods) /
                                   sizeof(g_BgRenderMethods[0]));
    if (regRet != JNI_TRUE) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}

extern "C" void JNI_OnUnload(JavaVM *jvm, void *p) {
    JNIEnv *env = nullptr;
    if (jvm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return;
    }

    UnregisterNativeMethods(env, NATIVE_RENDER_CLASS_NAME);
    UnregisterNativeMethods(env, NATIVE_BG_RENDER_CLASS_NAME);
}