package mass.model;

import com.fasterxml.jackson.annotation.JsonValue;
import fusion.json.CborSerializable;
import helloscala.common.data.IntEnum;

import java.util.Arrays;
import java.util.Optional;

public enum CommonStatus implements IntEnum, CborSerializable {
    DISABLE(0, "DISABLE"),
    ENABLE(1, "ENABLE"),

    Continue(100, "Continue"),
    SwitchingProtocols(101, "SwitchingProtocols"),
    Processing(102, "Processing"),

    OK(200, "OK"),
    Created(201, "Created"),
    Accepted(202, "Accepted"),
    NonAuthoritativeInformation(203, "NonAuthoritativeInformation"),
    NoContent(204, "NoContent"),
    ResetContent(205, "ResetContent"),
    PartialContent(206, "PartialContent"),
    MultiStatus(207, "MultiStatus"),
    AlreadyReported(208, "AlreadyReported"),
    IMUsed(226, "IMUsed"),

    MultipleChoices(300, "MultipleChoices"),
    MovedPermanently(301, "MovedPermanently"),
    Found(302, "Found"),
    SeeOther(303, "SeeOther"),
    NotModified(304, "NotModified"),
    UseProxy(305, "UseProxy"),
    TemporaryRedirect(307, "TemporaryRedirect"),
    PermanentRedirect(308, "PermanentRedirect"),

    BadRequest(400, "BadRequest"),
    Unauthorized(401, "Unauthorized"),
    PaymentRequired(402, "PaymentRequired"),
    Forbidden(403, "Forbidden"),
    NotFound(404, "NotFound"),
    MethodNotAllowed(405, "MethodNotAllowed"),
    NotAcceptable(406, "NotAcceptable"),
    ProxyAuthenticationRequired(407, "ProxyAuthenticationRequired"),
    RequestTimeout(408, "RequestTimeout"),
    Conflict(409, "Conflict"),
    Gone(410, "Gone"),

    InternalServerError(500, "InternalServerError"),
    NotImplemented(501, "NotImplemented"),
    BadGateway(502, "BadGateway"),
    ServiceUnavailable(503, "ServiceUnavailable"),
    GatewayTimeout(504, "GatewayTimeout"),
    HTTPVersionNotSupported(505, "HTTPVersionNotSupported"),
    VariantAlsoNegotiates(506, "VariantAlsoNegotiates"),
    ;

    @JsonValue
    private Integer value;
    private String name;

    CommonStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    public static Optional<CommonStatus> optionalFromValue(int value) {
        return Arrays.stream(values()).filter(v -> v.value.equals(value)).findFirst();
    }

    public static CommonStatus fromValue(int value) {
        return optionalFromValue(value).orElseThrow();
    }
}
