#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_icehong_gnugo_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Hello from C");
}