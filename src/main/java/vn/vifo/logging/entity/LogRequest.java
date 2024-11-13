package vn.vifo.logging.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "log_requests")
@Getter
@Setter
public class LogRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(nullable = false)
    private String typeLog;

    @Column(nullable = false)
    private String environment;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp time;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "source_id")
    private UUID sourceId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SourceRequest source;

    @Size(max = 10)
    @NotNull
    @ColumnDefault("'GET'")
    @Column(name = "method", nullable = false, length = 10)
    private String method;

    @Column(name = "header", length = Integer.MAX_VALUE)
    private String header;

    @Size(max = 255)
    @Column(name = "endpoint")
    private String endpoint;

    @Size(max = 10)
    @ColumnDefault("'0'")
    @Column(name = "response_time", length = 10)
    private String responseTime;

    @Size(max = 50)
    @ColumnDefault("''")
    @Column(name = "response_log", length = 50)
    private String responseLog;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;



    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
