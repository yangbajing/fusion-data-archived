/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.fasterxml.jackson.module.helloscala.HelloscalaModule;
import helloscala.common.util.TimeUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class ZonedDateTimeDeserializer extends JSR310DateTimeDeserializerBase<ZonedDateTime> {
    public ZonedDateTimeDeserializer() {
        super(ZonedDateTime.class, TimeUtils.formatterDateTime());
    }

//    public ZonedDateTimeDeserializer(DateTimeFormatter f) {
//        this(ZonedDateTime.class, f);
//    }

//    public ZonedDateTimeDeserializer(Class<ZonedDateTime> supportedType, DateTimeFormatter f) {
//        super(supportedType, f);
//    }

    @Override
    protected JsonDeserializer<ZonedDateTime> withDateFormat(DateTimeFormatter dtf) {
        return null;
    }

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return null;
            }

            try {
                return TimeUtils.toZonedDateTime(string);
            } catch (DateTimeException e) {
                _rethrowDateTimeException(parser, context, e, string);
            }
        }
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
                    && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final ZonedDateTime parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                ZonedDateTime result;

                int year = parser.getIntValue();
                int month = parser.nextIntValue(-1);
                int day = parser.nextIntValue(-1);
                int hour = parser.nextIntValue(-1);
                int minute = parser.nextIntValue(-1);

                t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    result = ZonedDateTime.of(year, month, day, hour, minute, 0, 0, TimeUtils.ZONE_CHINA_OFFSET());
                } else {
                    int second = parser.getIntValue();
                    t = parser.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        result = ZonedDateTime.of(year, month, day, hour, minute, second, 0, TimeUtils.ZONE_CHINA_OFFSET());
                    } else {
                        int partialSecond = parser.getIntValue();
                        if (partialSecond < 1_000 &&
                                !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS))
                            partialSecond *= 1_000_000; // value is milliseconds, convert it to nanoseconds
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY,
                                    "Expected array to end");
                        }
                        result = ZonedDateTime.of(year, month, day, hour, minute, second, partialSecond, TimeUtils.ZONE_CHINA_OFFSET());
                    }
                }
                return result;
            }
            context.reportInputMismatch(handledType(),
                    "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                    t);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (ZonedDateTime) parser.getEmbeddedObject();
        }
        throw context.wrongTokenException(parser, handledType(), JsonToken.VALUE_STRING,
                "Expected array or string.");
    }
}

/**
 * Jackson全局配置
 * Created by yangbajing(yangbajing@gmail.com) on 2017-03-14.
 */
public class Jackson {
    public static final ObjectMapper defaultObjectMapper = getObjectMapper();

    public static ObjectNode createObjectNode() {
        return defaultObjectMapper.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return defaultObjectMapper.createArrayNode();
    }

    private static ObjectMapper getObjectMapper() {
        JavaTimeModule jtm = new JavaTimeModule();
        jtm.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(TimeUtils.formatterDateTime()));
        jtm.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(TimeUtils.formatterDateTime()));
        jtm.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(TimeUtils.formatterDateTime()));
        jtm.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());

        return new ObjectMapper()
                .findAndRegisterModules()
                .registerModule(new HelloscalaModule())
                .registerModule(jtm)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
//                    .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}

