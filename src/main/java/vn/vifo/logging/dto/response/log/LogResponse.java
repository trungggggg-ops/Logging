package vn.vifo.logging.dto.response.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.vifo.logging.dto.response.AbstractBaseResponse;
import vn.vifo.logging.entity.LogRequest;
import vn.vifo.logging.entity.SourceRequest;


import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
public class LogResponse extends AbstractBaseResponse {

    @Schema(
            name = "id",
            description = "UUID of the log request",
            type = "String",
            example = "91b2999d-d327-4dc8-9956-2fadc0dc8778"
    )
    private UUID id;

    @Schema(
            name = "typeLog",
            description = "Type of the log",
            type = "String",
            example = "ERROR"
    )
    private String typeLog;

    @Schema(
            name = "environment",
            description = "Environment where the log was generated",
            type = "String",
            example = "production"
    )
    private String environment;

    @Schema(
            name = "time",
            description = "Timestamp of the log",
            type = "Timestamp",
            example = "2022-09-29T22:37:31"
    )
    private Timestamp time;

    @Schema(
            name = "body",
            description = "Content of the log body",
            type = "String",
            example = "Error details here..."
    )
    private String body;

    @Schema(
            name = "source_id",
            description = "Source of the log",
            type = "String",
            example = "91b2999d-d327-4dc8-9956-2fadc0dc8778"
    )
    private UUID sourceId;

    @Schema(
            name = "sourceName",
            description = "Source name of the log",
            type = "String",
            example = "VIFO"
    )
    private String sourceName;

    @Schema(
            name = "method",
            description = "HTTP method of the request",
            type = "String",
            example = "GET"
    )
    private String method;

    @Schema(
            name = "header",
            description = "Header information of the request",
            type = "String",
            example = "{\"content-type\":\"application/json\"}"
    )
    private String header;

    @Schema(
            name = "endpoint",
            description = "API endpoint accessed",
            type = "String",
            example = "/api/v1/user"
    )
    private String endpoint;

    @Schema(
            name = "responseTime",
            description = "Response time of the request in milliseconds",
            type = "String",
            example = "150ms"
    )
    private String responseTime;

    @Schema(
            name = "responseLog",
            description = "Response log details",
            type = "String",
            example = "Log saved successfully"
    )
    private String responseLog;

    @Schema(
            name = "createdAt",
            description = "Timestamp of log creation",
            type = "Timestamp",
            example = "2022-09-29T22:37:31"
    )
    private Timestamp createdAt;

    @Schema(
            name = "updatedAt",
            description = "Timestamp of the last update",
            type = "Timestamp",
            example = "2022-09-29T22:37:31"
    )
    private Timestamp updatedAt;

    public static LogResponse fromEntity(LogRequest logRequest) {
        SourceRequest source = logRequest.getSource();
        return LogResponse.builder()
                .id(logRequest.getId())
                .typeLog(logRequest.getTypeLog())
                .environment(logRequest.getEnvironment())
                .time(logRequest.getTime())
                .body(logRequest.getBody())
                .sourceId(logRequest.getSourceId())
                .sourceName(source.getSourceName())
                .header(logRequest.getHeader())
                .endpoint(logRequest.getEndpoint())
                .responseTime(logRequest.getResponseTime())
                .responseLog(logRequest.getResponseLog())
                .createdAt(logRequest.getCreatedAt())
                .updatedAt(logRequest.getUpdatedAt())
                .build();
    }
}
