package vn.vifo.logging.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.vifo.logging.entity.LogRequest;

import vn.vifo.logging.entity.SourceRequest;
import vn.vifo.logging.entity.specification.LogFilterSpecification;
import vn.vifo.logging.entity.specification.criteria.LogCriteria;
import vn.vifo.logging.entity.specification.criteria.PaginationCriteria;
import vn.vifo.logging.repository.LogRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import vn.vifo.logging.repository.SourceRequestRepository;
import vn.vifo.logging.util.PageRequestBuilder;

import vn.vifo.logging.util.ParseJson;


import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService {

    @Value("${app.dev:default_dev_value}")
    private String environment;

    @Autowired
    private LogRequestRepository logRequestRepository;

    @Autowired
    private SourceRequestRepository sourceRequestRepository;


    public Page<LogRequest> findAll(LogCriteria criteria, PaginationCriteria paginationCriteria) {
        return logRequestRepository.findAll(new LogFilterSpecification(criteria),
                PageRequestBuilder.build(paginationCriteria));
    }

    public ResponseEntity<String> logRequest(@RequestHeader Map<String, String> headers, HttpServletRequest request, @RequestBody String body)  {
        long startTime = System.currentTimeMillis();
        LogRequest logRequest = createLogRequest(headers, request, body);

        try {
            UUID sourceId = validateOrCreateSource(body, request.getRemoteAddr());
            saveLogRequest(logRequest, sourceId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        logRequest.setResponseTime((endTime - startTime) + "ms");
        logRequest.setResponseLog("Log saved successfully");

        try {
            logRequestRepository.save(logRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving log: " + e.getMessage());
        }
        return ResponseEntity.ok("Log saved successfully");
    }

    public ResponseEntity<String> updateLog(@PathVariable UUID id, @RequestBody LogRequest logRequest) {
        LogRequest logRequestSaved = logRequestRepository.findById(id).orElse(null);
        if (logRequestSaved == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cant find id");
        } else {
            logRequestSaved.setTypeLog(logRequest.getTypeLog());
            logRequestSaved.setEnvironment(logRequest.getEnvironment());
            logRequestSaved.setMethod(logRequest.getMethod());
            logRequestSaved.setEndpoint(logRequest.getEndpoint());
            logRequestSaved.setHeader(logRequest.getHeader());
            logRequestSaved.setBody(logRequest.getBody());
            logRequestRepository.save(logRequestSaved);
        }
        return ResponseEntity.ok("Log updated successfully");
    }


    private LogRequest createLogRequest(Map<String, String> headers, HttpServletRequest request, String body){
        LogRequest logRequest = new LogRequest();
        logRequest.setTime(new Timestamp(System.currentTimeMillis()));
        logRequest.setTypeLog("IN");
        logRequest.setEnvironment(environment);
        logRequest.setMethod(request.getMethod());
        logRequest.setEndpoint(request.getRequestURI());
        logRequest.setHeader(ParseJson.Stringify(headers));
        logRequest.setBody(ParseJson.Stringify(body));
        return logRequest;
    }


    private UUID validateOrCreateSource(@RequestBody String body, String requestIp) throws IllegalArgumentException {
        if (!body.contains("sourceName")) {
            throw new IllegalArgumentException("sourceName is missing");
        }

        SourceRequest source = ParseJson.parseJsonToObject(body, SourceRequest.class);

        if (source == null || source.getSourceName() == null || source.getSourceName().isEmpty()) {
            throw new IllegalArgumentException("Invalid source name");
        }
        Optional<SourceRequest> existingSource = sourceRequestRepository.findBySourceName(source.getSourceName());
        if (existingSource.isPresent()) {
            return existingSource.get().getId();
        } else {
            SourceRequest newSource = new SourceRequest();
            newSource.setSourceName(source.getSourceName());
            newSource.setSourceIp(requestIp);
            sourceRequestRepository.save(newSource);
            return newSource.getId();
        }
    }

    private void saveLogRequest(LogRequest logRequest, UUID sourceId) throws Exception {
        try {
            logRequest.setSourceId(sourceId);
            logRequestRepository.save(logRequest);
        } catch (Exception e) {
            throw new Exception("Error saving log: " + e.getMessage(), e);
        }
    }


}
