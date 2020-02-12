package mass.model.job;

import com.fasterxml.jackson.annotation.JsonValue;
import fusion.json.CborSerializable;
import helloscala.common.data.IntEnum;

import java.util.Arrays;
import java.util.Optional;

public enum TriggerType implements IntEnum, CborSerializable {
    TRIGGER_UNKNOWN(0, "UNKNOWN"),
    CRON(1, "CRON"),
    SIMPLE(2, "SIMPLE"),
    EVENT(3, "EVENT"),
    ;

    @JsonValue
    private Integer value;
    private String name;

    TriggerType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static Optional<TriggerType> optionalFromName(String name) {
        return Arrays.stream(values()).filter(v -> v.name.equals(name)).findFirst();
    }

    public static TriggerType fromName(String name) {
        return optionalFromName(name).orElseThrow();
    }

    public static Optional<TriggerType> optionalFromValue(int value) {
        return Arrays.stream(values()).filter(v -> v.value.equals(value)).findFirst();
    }

    public static TriggerType fromValue(int value) {
        return optionalFromValue(value).orElseThrow();
    }
}
