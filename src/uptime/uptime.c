#include <jni.h>
#include <linux/unistd.h>
#include <linux/kernel.h>
#include <sys/sysinfo.h>
#include <segfault_hydrolog_nativelib_Uptime.h>

JNIEXPORT jlong JNICALL
Java_segfault_hydrolog_nativelib_Uptime_uptime(JNIEnv *env, jobject obj)
{
    struct sysinfo s_info;
    int error = sysinfo(&s_info);
    if(error != 0)
    {
        return -1;
    }
    return s_info.uptime;
}
