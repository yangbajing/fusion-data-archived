package mass.model.job;

import com.fasterxml.jackson.annotation.JsonValue;
import fusion.json.CborSerializable;
import helloscala.common.data.IntEnum;

import java.util.Arrays;
import java.util.Optional;

public enum Program implements IntEnum, CborSerializable {
    UNKOWN(0, "UNKOWN"),
    SCALA(1, "SCALA"),
    JAVA(2, "JAVA"),
    PYTHON(3, "PYTHON"),
    SH(4, "SH"),
    SQL(5, "SQL"),
    NODE_JS(6, "NODE_JS"),
    ;

    @JsonValue
    private Integer value;
    private String name;

    Program(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static Optional<Program> optionalFromName(String name) {
        return Arrays.stream(values()).filter(v -> v.name.equals(name)).findFirst();
    }

    public static Program fromName(String name) {
        return optionalFromName(name).orElseThrow();
    }

    public static Optional<Program> optionalFromValue(int value) {
        return Arrays.stream(values()).filter(v -> v.value.equals(value)).findFirst();
    }

    public static Program fromValue(int value) {
        return optionalFromValue(value).orElseThrow();
    }
}
