package org.shsts.tinycorelib.content.meta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.neoforge.common.conditions.AndCondition;
import net.neoforged.neoforge.common.conditions.FalseCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.OrCondition;
import net.neoforged.neoforge.common.conditions.TrueCondition;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MetaConditionDecoder {
    private static final String TYPE_KEY = "type";

    private MetaConditionDecoder() {}

    public static boolean conditionsMatch(JsonElement element) {
        if (!element.isJsonArray()) {
            throw new JsonParseException("neoforge:conditions must be an array");
        }

        var conditions = new ArrayList<ICondition>();
        for (var child : element.getAsJsonArray()) {
            conditions.add(decodeCondition(child));
        }
        return conditions.stream().allMatch(condition -> condition.test(ICondition.IContext.EMPTY));
    }

    private static ICondition decodeCondition(JsonElement element) {
        if (!element.isJsonObject()) {
            throw new JsonParseException("condition must be an object");
        }

        var object = element.getAsJsonObject();
        var typeElement = object.get(TYPE_KEY);
        if (typeElement == null || !typeElement.isJsonPrimitive() ||
            !typeElement.getAsJsonPrimitive().isString()) {
            throw new JsonParseException("condition type must be a string");
        }

        return switch (typeElement.getAsString()) {
            case "neoforge:true" -> TrueCondition.INSTANCE;
            case "neoforge:false" -> FalseCondition.INSTANCE;
            case "neoforge:mod_loaded" -> new ModLoadedCondition(requireString(object, "modid"));
            case "neoforge:not" -> new NotCondition(decodeCondition(requireField(object, "value")));
            case "neoforge:and" -> new AndCondition(decodeChildren(requireArray(object, "values")));
            case "neoforge:or" -> new OrCondition(decodeChildren(requireArray(object, "values")));
            default -> throw new JsonParseException(
                "unsupported condition type " + typeElement.getAsString());
        };
    }

    private static List<ICondition> decodeChildren(JsonArray array) {
        var children = new ArrayList<ICondition>();
        for (var child : array) {
            children.add(decodeCondition(child));
        }
        return List.copyOf(children);
    }

    private static JsonElement requireField(JsonObject object, String key) {
        var value = object.get(key);
        if (value == null) {
            throw new JsonParseException("condition is missing field '" + key + "'");
        }
        return value;
    }

    private static String requireString(JsonObject object, String key) {
        var value = requireField(object, key);
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
            throw new JsonParseException("condition field '" + key + "' must be a string");
        }
        return value.getAsString();
    }

    private static JsonArray requireArray(JsonObject object, String key) {
        var value = requireField(object, key);
        if (!value.isJsonArray()) {
            throw new JsonParseException("condition field '" + key + "' must be an array");
        }
        return value.getAsJsonArray();
    }
}
