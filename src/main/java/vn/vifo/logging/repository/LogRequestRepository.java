package vn.vifo.logging.repository;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.vifo.logging.entity.LogRequest;

import java.util.UUID;

public interface LogRequestRepository extends JpaRepository<LogRequest, UUID>, JpaSpecificationExecutor<LogRequest>{
    @Modifying
    @Query("DELETE FROM LogRequest b WHERE b.id=:id")
    void deleteById(@Param("id") UUID id);


}
