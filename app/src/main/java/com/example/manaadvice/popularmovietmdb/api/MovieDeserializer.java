package com.example.manaadvice.popularmovietmdb.api;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class MovieDeserializer<T> implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement results = json.getAsJsonObject().get("results");
        return new Gson().fromJson(results, typeOfT);
    }
}
