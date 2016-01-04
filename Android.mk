LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_PREBUILT_LIBS +=libCommand:libs/armeabi/libCommand.so
LOCAL_PREBUILT_LIBS +=libsmm:libs/armeabi/libsmm.so
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_SRC_FILES := $(call all-java-files-under,src)
LOCAL_SRC_FILES += $(call all-java-files-under,../google-play-services_lib/src)
LOCAL_SRC_FILES += \
        src/com/mtk/ipc/IControllerCallback.aidl \
        src/com/mtk/ipc/IControllerInterface.aidl
LOCAL_PACKAGE_NAME := Mediatek_SmartDevice 
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional
LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 commons-codec-1.5 signpost-commonshttp4-1.2.1.1 signpost-core-1.2.1.1 Wearable FotaVdmLib MREPermission GMSLib
LOCAL_JNI_SHARED_LIBRARIES := libCommand libsmm

LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res
LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/../google-play-services_lib/res
LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --extra-packages com.google.android.gms

#LOCAL_SDK_VERSION := current
LOCAL_MODULE_PATH := $(PRODUCT_OUT)

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)
droidcore all_modules : $(LOCAL_INSTALLED_MODULE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    commons-codec-1.5:libs/commons-codec-1.5.jar \
    signpost-commonshttp4-1.2.1.1:libs/signpost-commonshttp4-1.2.1.1.jar \
    signpost-core-1.2.1.1:libs/signpost-core-1.2.1.1.jar \
    FotaVdmLib:libs/FotaVdmLib.jar \
    android-support-v4:libs/android-support-v4.jar \
    MREPermission:libs/MREPermission.jar
include $(BUILD_MULTI_PREBUILT)
