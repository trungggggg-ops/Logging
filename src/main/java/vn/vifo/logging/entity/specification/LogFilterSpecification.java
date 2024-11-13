package vn.vifo.logging.entity.specification;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import vn.vifo.logging.entity.LogRequest;
import vn.vifo.logging.entity.SourceRequest;
import vn.vifo.logging.entity.specification.criteria.LogCriteria;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class LogFilterSpecification implements Specification<LogRequest> {
    private final LogCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull final Root<LogRequest> root,
                                 @NonNull final CriteriaQuery<?> query,
                                 @NonNull final CriteriaBuilder builder) {

        if (criteria == null) {
            return null;
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getCreatedAtStart() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtStart()));
        }
        if (criteria.getCreatedAtEnd() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtEnd()));
        }

        if (criteria.getMethod() != null && !criteria.getMethod().isEmpty()) {
            predicates.add(builder.equal(root.get("method"), criteria.getMethod()));
        }

        if (criteria.getEndpoint() != null && !criteria.getEndpoint().isEmpty()) {
            predicates.add(builder.equal(root.get("endpoint"), criteria.getEndpoint()));
        }

        if (criteria.getHeader() != null && !criteria.getHeader().isEmpty()) {
            predicates.add(builder.like(root.get("header"), "%" + criteria.getHeader() + "%"));
        }

        if (criteria.getBody() != null && !criteria.getBody().isEmpty()) {
            predicates.add(builder.like(root.get("body"), "%" + criteria.getBody() + "%"));
        }

        if (criteria.getEnvironment() != null && !criteria.getEnvironment().isEmpty()) {
            predicates.add(builder.equal(root.get("environment"), criteria.getEnvironment()));
        }

        if (criteria.getTypeLog() != null && !criteria.getTypeLog().isEmpty()) {
            predicates.add(builder.equal(root.get("typeLog"), criteria.getTypeLog()));
        }

        if (criteria.getSourceName() != null && !criteria.getSourceName().isEmpty()) {
            Join<LogRequest, SourceRequest> sourceJoin = root.join("source", JoinType.INNER);
            predicates.add(builder.equal(sourceJoin.get("sourceName"), criteria.getSourceName()));
        }

        if (criteria.getTime() != null) {
            predicates.add(builder.equal(root.get("time"), criteria.getTime()));
        }
        if (criteria.getResponseLog() != null) {
            predicates.add(builder.equal(root.get("responseLog"), criteria.getResponseLog()));
        }
        if (criteria.getResponseTime() != null) {
            predicates.add(builder.equal(root.get("responseTime"), criteria.getResponseTime()));
        }



        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return query.distinct(true).getRestriction();
    }
}
