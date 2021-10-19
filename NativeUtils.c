#include "jni.h"
#include "jawt_md.h"
#include "windows.h"

static JNIEnv* global_env = NULL;
static jmethodID keyboardKeyUpHandlerFunction = NULL;
static jmethodID keyboardKeyDownHandlerFunction = NULL;
static jclass utilsClass = NULL;

static HINSTANCE dllHandle = NULL;

BOOL APIENTRY DllMain(HINSTANCE _hInst, DWORD reason, LPVOID reserved) {
    if(reason == DLL_PROCESS_ATTACH) {
        dllHandle = _hInst;
    }
    return TRUE;
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

        case WM_CLOSE: {
            PostQuitMessage(0);
            return FALSE;
        }

        default: return DefWindowProc(hWndMain, message, wParam, lParam);
    }
}

JNIEXPORT jlongArray JNICALL Java_visualizer_NativeUtils_listAllKeyboardHandles(JNIEnv* env, jclass clazz) {
    unsigned int deviceCount;
    GetRawInputDeviceList(NULL, &deviceCount, sizeof(RAWINPUTDEVICELIST));

    PRAWINPUTDEVICELIST deviceList = malloc(sizeof(RAWINPUTDEVICELIST) * deviceCount);
    GetRawInputDeviceList(deviceList, &deviceCount, sizeof(RAWINPUTDEVICELIST));

    int keyboardCount = 0;
    for(int i = 0; i < deviceCount; ++i) {
        if(deviceList[i].dwType == RIM_TYPEKEYBOARD) {
            ++keyboardCount;
        }
    }

    HANDLE* handles = malloc(sizeof(long) * deviceCount);
    int handleIndex = 0;
    for(int i = 0; i < deviceCount; ++i) {
        if(deviceList[i].dwType == RIM_TYPEKEYBOARD) {
            handles[handleIndex++] = deviceList[i].hDevice;
        }
    }

    jlongArray result = (*env)->NewLongArray(env, keyboardCount);
    (*env)->SetLongArrayRegion(env, result, 0, keyboardCount, handles);

    free(deviceList);
    free(handles);

    return result;
}

JNIEXPORT void JNICALL Java_visualizer_NativeUtils_initializeNativeUtils(JNIEnv* env, jclass clazz) {
    JavaVM* jvm;
    (*env)->GetJavaVM(env, &jvm);
    (*jvm)->AttachCurrentThread(jvm, &global_env, NULL);

    keyboardKeyUpHandlerFunction = (*env)->GetStaticMethodID(env, clazz, "onKeyboardKeyUp", "(IJ)V");
    keyboardKeyDownHandlerFunction = (*env)->GetStaticMethodID(env, clazz, "onKeyboardKeyDown", "(IJ)V");
    utilsClass = clazz;

    char windowName[] = "nativeutils-window";
    WNDCLASS class = { .lpfnWndProc = handleWindowMessages, .hInstance = dllHandle, .lpszClassName = windowName };
    RegisterClass(&class);

    HWND window = CreateWindow(windowName, NULL, 0, 0, 0, 0, 0, HWND_MESSAGE, NULL, dllHandle, NULL);
    RAWINPUTDEVICE keyboardRawDevice = { .usUsagePage = 1, .usUsage = 6, .hwndTarget = window, .dwFlags = RIDEV_INPUTSINK };
    RAWINPUTDEVICE mouseRawDevice = { .usUsagePage = 1, .usUsage = 2, .hwndTarget = window, .dwFlags = RIDEV_INPUTSINK };

    RegisterRawInputDevices(&keyboardRawDevice, 1, sizeof(keyboardRawDevice));
    RegisterRawInputDevices(&mouseRawDevice, 1, sizeof(mouseRawDevice));

    MSG message;
    while(GetMessage(&message, NULL, 0, 0)) {
        TranslateMessage(&message);
        DispatchMessage(&message);
    }

    if(window != NULL) {
        DestroyWindow(window);
    }
}

JNIEXPORT void JNICALL Java_visualizer_NativeUtils_makeJFrameBehindClickable(JNIEnv* env, jclass clazz, jobject frameObject) {
    JAWT awt = { .version = JAWT_VERSION_9 };
    JAWT_GetAWT(env, &awt);

    JAWT_DrawingSurface* surface = awt.GetDrawingSurface(env, frameObject);
    jint lock = surface->Lock(surface);
    JAWT_DrawingSurfaceInfo* surfaceInfo = surface->GetDrawingSurfaceInfo(surface);
    HWND nativeWindowHandle = ((JAWT_Win32DrawingSurfaceInfo*) surfaceInfo->platformInfo)->hwnd;
    int originalFlags = GetWindowLong(nativeWindowHandle, GWL_EXSTYLE);

    SetWindowLong(nativeWindowHandle, GWL_EXSTYLE, originalFlags | WS_EX_LAYERED | WS_EX_TRANSPARENT);

    surface->Unlock(surface);
    awt.FreeDrawingSurface(surface);
}