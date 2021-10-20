package visualizer.model;

import com.fasterxml.jackson.annotation.*;
import javax.swing.*;
import visualizer.*;
import visualizer.gui.*;

public final class KeyboardView {

    public long handle;
    public final String name;
    public int visualizerFrameXPosition;
    public int visualizerFrameYPosition;
    public int visualizerFrameWidth;
    public int visualizerFrameHeight;

    public transient JFrame helperFrame;
    public transient KeyboardHelperScreen helperScreen;
    public transient GlobalKeyboardInputListener keyDownListener;
    public transient GlobalKeyboardInputListener keyUpListener;

    @JsonCreator
    public KeyboardView(@JsonProperty("handle") long handle,
                        @JsonProperty("name") String name,
                        @JsonProperty("visualizerFrameXPosition") int visualizerFrameXPosition,
                        @JsonProperty("visualizerFrameYPosition") int visualizerFrameYPosition,
                        @JsonProperty("visualizerFrameWidth") int visualizerFrameWidth,
                        @JsonProperty("visualizerFrameHeight") int visualizerFrameHeight) {

        this.handle = handle;
        this.name = name;
        this.visualizerFrameXPosition = visualizerFrameXPosition;
        this.visualizerFrameYPosition = visualizerFrameYPosition;
        this.visualizerFrameWidth = visualizerFrameWidth;
        this.visualizerFrameHeight = visualizerFrameHeight;
    }
}