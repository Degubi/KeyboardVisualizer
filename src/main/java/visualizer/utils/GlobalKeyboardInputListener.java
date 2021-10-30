package visualizer.utils;

@FunctionalInterface
public interface GlobalKeyboardInputListener {
    void keyStateChanged(int keyCode, long deviceHandle);
}