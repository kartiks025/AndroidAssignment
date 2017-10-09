#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_in_ac_iitb_cse_kartiks_a150050025_126_LoginActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
