package vn.vifo.logging.controller.log;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.vifo.logging.dto.response.ErrorResponse;
import vn.vifo.logging.dto.response.PaginationResponse;
import vn.vifo.logging.dto.response.log.LogResponse;
import vn.vifo.logging.dto.response.log.LogsPaginationResponse;
import vn.vifo.logging.dto.response.user.UsersPaginationResponse;
import vn.vifo.logging.entity.LogRequest;
import vn.vifo.logging.entity.specification.criteria.LogCriteria;
import vn.vifo.logging.entity.specification.criteria.PaginationCriteria;
import vn.vifo.logging.service.LogService;
import vn.vifo.logging.service.MessageSourceService;
import vn.vifo.logging.controller.AbstractBaseController;

import java.net.UnknownHostException;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.UUID;

import static vn.vifo.logging.util.Constants.SECURITY_SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api")
public class LogController extends AbstractBaseController {
    private static final String[] SORT_COLUMNS = new String[]{"id", "typeLog", "environment", "time", "body", "source", "method", "header", "endpoint", "responseTime", "responseLog", "createdAt", "updatedAt"};
    private final MessageSourceService messageSourceService;

    private final LogService logService;

    @Operation(
            summary = "Users list endpoint",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success operation",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UsersPaginationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Full authentication is required to access this resource",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/search-logs")
    public ResponseEntity<LogsPaginationResponse> searchLogs(

            @Parameter(name = "timeStart", description = "Start time", example = "2024-11-07 16:31:15.741")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime createdAtStart,

            @Parameter(name = "timeEnd", description = "End time", example = "2024-11-07 16:31:15.741")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime createdAtEnd,

            @Parameter(name = "typeLog", description = "typeLog", example = "OUT")
            @RequestParam(required = false) final String typeLog,

            @Parameter(name = "environment", description = "environment", example = "dev")
            @RequestParam(required = false) final String environment,

            @Parameter(name = "time", description = "time", example = "2024-11-07T16:31:15.731")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime time,

            @Parameter(name = "body", description = "body", example = "test1")
            @RequestParam(required = false) final String body,

            @Parameter(name = "sourceName", description = "sourceName", example = "VIFO")
            @RequestParam(required = false) final String sourceName,

            @Parameter(name = "method", description = "method", example = "POST")
            @RequestParam(required = false) final String method,

            @Parameter(name = "header", description = "header", example = "\"host\":\"localhost:8080\"")
            @RequestParam(required = false) final String header,

            @Parameter(name = "endpoint", description = "endpoint", example = "/api/logs")
            @RequestParam(required = false) final String endpoint,

            @Parameter(name = "responseTime", description = "responseTime", example = "212ms")
            @RequestParam(required = false) final String responseTime,

            @Parameter(name = "responseLog", description = "responseLog", example = "Log saved successfully")
            @RequestParam(required = false) final String responseLog,

            @Parameter(name = "page", description = "Page number", example = "1")
            @RequestParam(defaultValue = "1", required = false) final Integer page,

            @Parameter(name = "size", description = "Page size", example = "20")
            @RequestParam(defaultValue = "${spring.data.web.pageable.default-page-size}", required = false) final Integer size,

            @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt",
                    schema = @Schema(type = "String", allowableValues = {"createdAt", "updatedAt"}))
            @RequestParam(defaultValue = "createdAt", required = false) final String sortBy,

            @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string",
                    allowableValues = {"asc", "desc"}, defaultValue = "asc"))
            @RequestParam(defaultValue = "asc", required = false) @Pattern(regexp = "asc|desc") final String sort
    ) {
        sortColumnCheck(messageSourceService, SORT_COLUMNS, sortBy);


        Page<LogRequest> logRequests = logService.findAll(
                LogCriteria.builder()
                        .createdAtStart(createdAtStart)
                        .createdAtEnd(createdAtEnd)
                        .typeLog(typeLog)
                        .environment(environment)
                        .time(time)
                        .body(body)
                        .method(method)
                        .header(header)
                        .endpoint(endpoint)
                        .responseLog(responseLog)
                        .responseTime(responseTime)
                        .sourceName(sourceName)
                        .build(),
                PaginationCriteria.builder()
                        .page(page)
                        .size(size)
                        .sortBy(sortBy)
                        .sort(sort)
                        .columns(SORT_COLUMNS)
                        .build()
        );

        return ResponseEntity.ok(new LogsPaginationResponse(
                logRequests, logRequests.stream()
                .map(LogResponse::fromEntity)
                .toList()
        ));
    }


    @PostMapping("/logs")
    public ResponseEntity<String> logRequest(@RequestHeader Map<String, String> headers, HttpServletRequest request, @RequestBody String body) throws UnknownHostException {
        return logService.logRequest(headers, request, body);
    }


    @PostMapping("/update-logs/{id}")
    public ResponseEntity<String> updateLogs(@PathVariable UUID id, @RequestBody LogRequest logRequest) {
        return logService.updateLog(id, logRequest);
    }

}
