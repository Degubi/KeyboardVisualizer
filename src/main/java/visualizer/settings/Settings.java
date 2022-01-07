package visualizer.settings;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class Settings {
    private static final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationFeature.INDENT_OUTPUT, true);
    private static final String KEYBOARDS_SETTING = "keyboards";

    public static final ArrayList<KeyboardView> keyboards;

    static {
        var settings = load();

        keyboards = getKeyboards(settings);
    }


    @SuppressWarnings("resource")
    private static ArrayList<KeyboardView> getKeyboards(JsonNode settings) {
        var profs = settings.get(KEYBOARDS_SETTING);

        if(profs == null) {
            return new ArrayList<>();
        }

        try {
            return json.readValue(json.treeAsTokens(profs), new TypeReference<ArrayList<KeyboardView>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
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
                           .set(KEYBOARDS_SETTING, json.convertValue(keyboards, JsonNode.class));
        try {
            Files.write(Path.of("./settings.json"), json.writeValueAsBytes(settings));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}