#include "jni.h"
#include "jawt_md.h"
#include "windows.h"

static JNIEnv* global_env = NULL;
static jmethodID keyboardKeyUpHandlerFunction = NULL;
static jmethodID keyboardKeyDownHandlerFunction = NULL;
static jmethodID keyboardListChangedHandlerFunction = NULL;
static jclass utilsClass = NULL;
static jclass stringClass = NULL;

static HINSTANCE dllHandle = NULL;
static HANDLE* globalKeyboardHandles = NULL;
static char** globalKeybardIdentifiers = NULL;
static int globalKeyboardCount = 0;

BOOL APIENTRY DllMain(HINSTANCE _hInst, DWORD reason, LPVOID reserved) {
    if(reason == DLL_PROCESS_ATTACH) {
        dllHandle = _hInst;
    }
    return TRUE;
}

static void refreshGlobalKeyboards() {
    free(globalKeyboardHandles);

    for(int i = 0; i < globalKeyboardCount; ++i) {
        free(globalKeybardIdentifiers[i]);
    }

    UINT deviceCount;
    GetRawInputDeviceList(NULL, &deviceCount, sizeof(RAWINPUTDEVICELIST));

    PRAWINPUTDEVICELIST deviceList = _alloca(sizeof(RAWINPUTDEVICELIST) * deviceCount);
    GetRawInputDeviceList(deviceList, &deviceCount, sizeof(RAWINPUTDEVICELIST));

    int keyboardCount = 0;
    for(int i = 0; i < deviceCount; ++i) {
        if(deviceList[i].dwType == RIM_TYPEKEYBOARD) {
            ++keyboardCount;
        }
    }

    globalKeyboardHandles = malloc(sizeof(HANDLE) * keyboardCount);
    globalKeybardIdentifiers = malloc(sizeof(char*) * keyboardCount);
    globalKeyboardCount = keyboardCount;

    for(int i = 0, resultIndex = 0; i < deviceCount; ++i) {
        if(deviceList[i].dwType == RIM_TYPEKEYBOARD) {
            HANDLE deviceHandle = deviceList[i].hDevice;
            UINT nameLength;
            GetRawInputDeviceInfo(deviceHandle, RIDI_DEVICENAME, NULL, &nameLength);

            globalKeyboardHandles[resultIndex] = deviceHandle;
            globalKeybardIdentifiers[resultIndex] = malloc(nameLength);

            GetRawInputDeviceInfo(deviceHandle, RIDI_DEVICENAME, globalKeybardIdentifiers[resultIndex], &nameLength);

            ++resultIndex;
        }
    }
}

LRESULT CALLBACK handleWindowMessages(HWND hWndMain, UINT message, WPARAM wParam, LPARAM lParam) {
    switch(message) {
        case WM_INPUT: {
            RAWINPUT inputData;
            UINT rawInputSize = sizeof(inputData);

            GetRawInputData((HRAWINPUT) lParam, RID_INPUT, &inputData, &rawInputSize, sizeof(RAWINPUTHEADER));

            if(inputData.header.dwType == RIM_TYPEKEYBOARD) {
                jlong deviceHandle = (jlong)(uintptr_t) inputData.header.hDevice;
                UINT event = inputData.data.keyboard.Message;

                if(event == WM_KEYUP || event == WM_SYSKEYUP) {
                    (*global_env)->CallStaticVoidMethod(global_env, utilsClass, keyboardKeyUpHandlerFunction, inputData.data.keyboard.VKey, deviceHandle);
                }else if(event == WM_KEYDOWN || event == WM_SYSKEYDOWN) {
                    (*global_env)->CallStaticVoidMethod(global_env, utilsClass, keyboardKeyDownHandlerFunction, inputData.data.keyboard.VKey, deviceHandle);
                }
            }

            return FALSE;
        }

        case WM_INPUT_DEVICE_CHANGE: {
            refreshGlobalKeyboards();

            jobjectArray keyboardIdentifiers = (*global_env)->NewObjectArray(global_env, globalKeyboardCount, stringClass, NULL);
            for(int i = 0; i < globalKeyboardCount; ++i) {
                (*global_env)->SetObjectArrayElement(global_env, keyboardIdentifiers, i, (*global_env)->NewStringUTF(global_env, globalKeybardIdentifiers[i]));
            }

            (*global_env)->CallStaticVoidMethod(global_env, utilsClass, keyboardListChangedHandlerFunction, keyboardIdentifiers);
            return FALSE;
        }

        case WM_CLOSE: {
            PostQuitMessage(0);
            return FALSE;
        }

        default: return DefWindowProc(hWndMain, message, wParam, lParam);
    }
}

JNIEXPORT jlong JNICALL Java_visualizer_utils_NativeUtils_getKeyboardHandleFromIdentifier(JNIEnv* env, jclass clazz, jstring identifier) {
    char* identifierChars = (*env)->GetStringUTFChars(env, identifier, JNI_FALSE);

    for(int i = 0; i < globalKeyboardCount; ++i) {
        if(strcmp(identifierChars, globalKeybardIdentifiers[i]) == 0) {
            return globalKeyboardHandles[i];
        }
    }

    return -1;
}

JNIEXPORT jstring JNICALL Java_visualizer_utils_NativeUtils_getKeyboardIdentifierFromHandle(JNIEnv* env, jclass clazz, jlong handle) {
    for(int i = 0; i < globalKeyboardCount; ++i) {
        if(globalKeyboardHandles[i] == handle) {
            return (*env)->NewStringUTF(env, globalKeybardIdentifiers[i]);
        }
    }

    return NULL;
}

JNIEXPORT void JNICALL Java_visualizer_utils_NativeUtils_initializeNativeUtils(JNIEnv* env, jclass clazz) {
    JavaVM* jvm;
    (*env)->GetJavaVM(env, &jvm);
    (*jvm)->AttachCurrentThread(jvm, &global_env, NULL);

    keyboardKeyUpHandlerFunction = (*env)->GetStaticMethodID(env, clazz, "onKeyboardKeyUp", "(IJ)V");
    keyboardKeyDownHandlerFunction = (*env)->GetStaticMethodID(env, clazz, "onKeyboardKeyDown", "(IJ)V");
    keyboardListChangedHandlerFunction = (*env)->GetStaticMethodID(env, clazz, "onKeyboardListChange", "([Ljava/lang/String;)V");
    stringClass = (*env)->FindClass(env, "java/lang/String");
    utilsClass = clazz;

    char windowName[] = "nativeutils-window";
    WNDCLASS class = { .lpfnWndProc = handleWindowMessages, .hInstance = dllHandle, .lpszClassName = windowName };
    RegisterClass(&class);

    HWND window = CreateWindow(windowName, NULL, 0, 0, 0, 0, 0, HWND_MESSAGE, NULL, dllHandle, NULL);
    RAWINPUTDEVICE keyboardRawDevice = { .usUsagePage = 1, .usUsage = 6, .hwndTarget = window, .dwFlags = RIDEV_INPUTSINK | RIDEV_DEVNOTIFY };

    RegisterRawInputDevices(&keyboardRawDevice, 1, sizeof(keyboardRawDevice));

    MSG message;
    while(GetMessage(&message, NULL, 0, 0)) {
        TranslateMessage(&message);
        DispatchMessage(&message);
    }

    if(window != NULL) {
        DestroyWindow(window);
    }
}

JNIEXPORT void JNICALL Java_visualizer_utils_NativeUtils_makeJFrameBehindClickable(JNIEnv* env, jclass clazz, jobject frameObject) {
    JAWT awt = { .version = JAWT_VERSION_9 };
    JAWT_GetAWT(env, &awt);

    JAWT_DrawingSurface* surface = awt.GetDrawingSurface(env, frameObject);
    jint lock = surface->Lock(surface);
    JAWT_DrawingSurfaceInfo* surfaceInfo = surface->GetDrawingSurfaceInfo(surface);
    HWND nativeWindowHandle = ((JAWT_Win32DrawingSurfaceInfo*) surfaceInfo->platformInfo)->hwnd;

    SetWindowLong(nativeWindowHandle, GWL_EXSTYLE, GetWindowLong(nativeWindowHandle, GWL_EXSTYLE) | WS_EX_LAYERED | WS_EX_TRANSPARENT);

    surface->Unlock(surface);
    awt.FreeDrawingSurface(surface);
}