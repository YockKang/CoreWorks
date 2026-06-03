package com.main.CoreWorks;

import com.badlogic.gdx.files.*;
import com.badlogic.gdx.utils.*;

public class JsonProcessor {
    public static JsonValue read(FileHandle file) {
        JsonReader reader = new JsonReader();
        return reader.parse(file);
    }
}
