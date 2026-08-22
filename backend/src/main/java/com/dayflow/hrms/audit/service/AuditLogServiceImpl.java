package com.dayflow.hrms.audit.service;

import com.dayflow.hrms.audit.dto.AuditLogResponse;
import com.dayflow.hrms.audit.entity.AuditLog;
import com.dayflow.hrms.audit.enums.AuditAction;
import com.dayflow.hrms.audit.enums.AuditResourceType;
import com.dayflow.hrms.audit.enums.AuditStatus;
import com.dayflow.hrms.audit.repository.AuditLogRepository;
import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.security.SecurityUtils;
import com.dayflow.hrms.security.UserPrincipal;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_USER_AGENT_LENGTH = 500;
    private static final Pattern SENSITIVE_MARKERS = Pattern.compile(
            "(?i)(password|authorization|bearer|jwt|refresh[ _-]?token|service[ _-]?role|database password|api[ _-]?key|signed url)");
    private static final Map<String, String> SAFE_SORTS = Map.of(
            "createdAt", "createdAt", "action", "action", "resourceType", "resourceType", "status", "status");

    private final AuditLogRepository auditLogRepository;
    private final EmployeeRepository employeeRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, EmployeeRepository employeeRepository) {
        this.auditLogRepository = auditLogRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void log(AuditAction action, AuditResourceType resourceType, UUID resourceId,
                    String description, AuditStatus status) {
        Objects.requireNonNull(action, "Audit action is required");
        Objects.requireNonNull(resourceType, "Audit resource type is required");
        Objects.requireNonNull(status, "Audit status is required");
        String safeDescription = validateDescription(description);

        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal().orElse(null);
        UUID actorUserId = principal != null ? principal.getId() : null;
        Employee actorEmployee = actorUserId != null ? employeeRepository.findByUserId(actorUserId).orElse(null) : null;
        UUID actorEmployeeId = actorEmployee != null ? actorEmployee.getId() : null;
        String actorName = actorEmployee != null ? truncate(actorEmployee.getFullName(), 201) : null;

        HttpServletRequest request = currentRequest();
        String ipAddress = request != null ? truncate(request.getRemoteAddr(), 45) : null;
        String userAgent = request != null ? truncate(request.getHeader("User-Agent"), MAX_USER_AGENT_LENGTH) : null;

        auditLogRepository.save(new AuditLog(actorUserId, actorEmployeeId, actorName, action,
                resourceType, resourceId, safeDescription, status, ipAddress, userAgent));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> getAuditLogs(UUID actorUserId, UUID actorEmployeeId,
            AuditAction action, AuditResourceType resourceType, UUID resourceId, AuditStatus status,
            LocalDate from, LocalDate to, int page, int size, String sort, String direction) {
        validateDateRange(from, to);
        Pageable pageable = pageable(page, size, sort, direction);

        Specification<AuditLog> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (actorUserId != null) predicates.add(cb.equal(root.get("actorUserId"), actorUserId));
            if (actorEmployeeId != null) predicates.add(cb.equal(root.get("actorEmployeeId"), actorEmployeeId));
            if (action != null) predicates.add(cb.equal(root.get("action"), action));
            if (resourceType != null) predicates.add(cb.equal(root.get("resourceType"), resourceType));
            if (resourceId != null) predicates.add(cb.equal(root.get("resourceId"), resourceId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay().toInstant(ZoneOffset.UTC)));
            if (to != null) predicates.add(cb.lessThan(root.get("createdAt"), to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        return PageResponse.of(auditLogRepository.findAll(specification, pageable).map(AuditLogResponse::fromEntity));
    }

    private static Pageable pageable(int page, int size, String sort, String direction) {
        if (page < 0) throw new BadRequestException("Page must be zero or greater");
        if (size < 1 || size > MAX_PAGE_SIZE) throw new BadRequestException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        String property = SAFE_SORTS.get(sort);
        if (property == null) throw new BadRequestException("Unsupported sort field: " + sort);
        try {
            return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), property));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Sort direction must be 'asc' or 'desc'");
        }
    }

    private static void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException("'from' date cannot be after 'to' date");
        }
    }

    private static String validateDescription(String description) {
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Audit description is required");
        String normalized = description.replace('\r', ' ').replace('\n', ' ').trim();
        if (SENSITIVE_MARKERS.matcher(normalized).find()) {
            throw new IllegalArgumentException("Audit description contains a prohibited sensitive-data marker");
        }
        return truncate(normalized, MAX_DESCRIPTION_LENGTH);
    }

    private static HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }

    private static String truncate(String value, int length) {
        if (value == null || value.isBlank()) return null;
        String normalized = value.replace('\r', ' ').replace('\n', ' ').trim();
        return normalized.length() <= length ? normalized : normalized.substring(0, length);
    }
}
