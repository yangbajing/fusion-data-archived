package mass.model.job;

import com.fasterxml.jackson.annotation.JsonValue;
import fusion.json.CborSerializable;
import helloscala.common.data.IntEnum;

import java.util.Arrays;
import java.util.Optional;

public enum RunStatus implements IntEnum, CborSerializable {
    JOB_NORMAL(0, "NORMAL"),
    JOB_ENABLE(1, "ENABLE"),
    JOB_RUNNING(100, "RUNNING"),
    JOB_OK(200, "OK"),
    JOB_FAILURE(500, "FAILURE"),
    ;

    @JsonValue
    private Integer value;
    private String name;

    RunStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static Optional<RunStatus> optionalFromValue(int value) {
        return Arrays.stream(values()).filter(v -> v.value.equals(value)).findFirst();
    }

    public static RunStatus fromValue(int value) {
        return optionalFromValue(value).orElseThrow();
    }
}
