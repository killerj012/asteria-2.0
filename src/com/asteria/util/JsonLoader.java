package com.asteria.util;

import java.io.FileReader;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A utility class that provides functions for parsing <code>.json</code> files.
 * 
 * @author lare96
 */
public abstract class JsonLoader {

    /** Used for parsing the actual file. */
    private JsonParser parser;

    /** Holds all of the elements of the parsed file. */
    private JsonArray array;

    /** Used to extract the data from the parsed elements. */
    private Gson builder;

    /**
     * Create a new {@link JsonLoader}.
     * 
     * @throws Exception
     *             if any exceptions occur while creating the loader.
     */
    public JsonLoader() throws Exception {
        this.parser = new JsonParser();
        this.array = (JsonArray) parser.parse(new FileReader(Paths.get(
                filePath()).toFile()));
        this.builder = new GsonBuilder().create();
    }

    /**
     * Allows the user to read and/or modify the parsed data.
     * 
     * @param reader
     *            the reader instance.
     * @param builder
     *            the builder instance.
     */
    public abstract void load(JsonObject reader, Gson builder);

    /**
     * Returns the path to the <code>.json</code> file that will be parsed.
     * 
     * @return the path to the file.
     */
    public abstract String filePath();

    /**
     * Loads the parsed data. How the data is loaded is defined by
     * <code>load(JsonObject j, Gson g)</code>.
     * 
     * @return the loader instance, for chaining.
     * @throws Exception
     *             if any exception occur while loading the parsed data.
     */
    public JsonLoader load() throws Exception {
        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);
            load(reader, builder);
        }
        return this;
    }
}
