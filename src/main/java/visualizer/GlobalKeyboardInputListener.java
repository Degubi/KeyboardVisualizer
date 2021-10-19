package visualizer;

@FunctionalInterface
public interface GlobalKeyboardInputListener {
    void keyStateChanged(int keyCode, long deviceHandle);
}