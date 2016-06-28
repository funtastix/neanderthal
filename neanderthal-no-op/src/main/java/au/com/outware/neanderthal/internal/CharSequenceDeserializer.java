package au.com.outware.neanderthal.internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @author timmutton
 */
public class CharSequenceDeserializer implements JsonDeserializer<CharSequence> {
    @Override
    public CharSequence deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json != null) {
            return json.toString();
        }
        return null;
    }
}