package visualizer;

import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.nio.file.*;

public final class Settings {
    private static final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static int keyboardHelperXPosition;
    public static int keyboardHelperYPosition;
    public static int keyboardHelperWidth;
    public static int keyboardHelperHeight;

    static {
        var settings = load();

        keyboardHelperXPosition = getOrDefaultInt("keyboardHelperXPosition", 0, settings);
        keyboardHelperYPosition = getOrDefaultInt("keyboardHelperYPosition", 0, settings);
        keyboardHelperWidth = getOrDefaultInt("keyboardHelperWidth", 600, settings);
        keyboardHelperHeight = getOrDefaultInt("keyboardHelperHeight", 400, settings);
    }


    private static int getOrDefaultInt(String propName, int defaultValue, JsonNode settings) {
        return settings.has(propName) ? settings.get(propName).asInt() : defaultValue;
    }

    private static JsonNode load() {
        var settingsPath = Path.of("./settings.json");

        try {
            if(!Files.exists(settingsPath)) {
                Files.writeString(settingsPath, "{}");
            }

            return json.readValue(Files.readAllBytes(settingsPath), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Can' generate settings.json!");
        }
    }

    public static void save() {
        var settings = json.createObjectNode()
                           .put("keyboardHelperXPosition", keyboardHelperXPosition)
                           .put("keyboardHelperYPosition", keyboardHelperYPosition)
                           .put("keyboardHelperWidth", keyboardHelperWidth)
                           .put("keyboardHelperHeight", keyboardHelperHeight);
        try {
            Files.write(Path.of("./settings.json"), json.writeValueAsBytes(settings));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}