package visualizer.model;

import com.fasterxml.jackson.annotation.*;
import java.util.*;
import javax.swing.*;
import visualizer.gui.*;
import visualizer.utils.*;

public final class KeyboardView {

    public long handle;
    public final String name;
    public int visualizerFrameXPosition;
    public int visualizerFrameYPosition;
    public int visualizerFrameWidth;
    public int visualizerFrameHeight;

    public transient JFrame visualizerFrame;
    public transient KeyboardVisualizerScreen visualizerScreen;
    public transient GlobalKeyboardInputListener keyDownListener;
    public transient GlobalKeyboardInputListener keyUpListener;
    public final transient ArrayList<String> heldKeys = new ArrayList<>();

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