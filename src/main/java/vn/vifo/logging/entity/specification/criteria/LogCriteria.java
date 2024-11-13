package vn.vifo.logging.entity.specification.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Range;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogCriteria {
    private String method;
    private String endpoint;
    private String header;
    private String body;
    private String environment;
    private String typeLog;
    private String sourceName;
    private String responseTime;
    private String responseLog;
    private LocalDateTime time;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;
}
