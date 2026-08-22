package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.*;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.exception.DuplicateResourceException;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.RoleRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.repository.UserRoleRepository;
import com.dayflow.hrms.security.SecurityUtils;
import com.dayflow.hrms.security.UserPrincipal;
import com.dayflow.hrms.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Implementation of EmployeeService managing employee operations, profiles, and ownership enforcement.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getCurrentUserProfile() {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserIdWithDetails(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user: " + currentUserId));

        return EmployeeResponse.fromEntity(employee);
    }

    @Override
    public EmployeeResponse updateCurrentUserProfile(UpdateProfileRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user: " + currentUserId));

        if (request.getPhone() != null) {
            employee.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            employee.setAddress(request.getAddress());
        }
        if (request.getProfilePictureUrl() != null) {
            employee.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        Employee saved = employeeRepository.save(employee);
        log.info("Updated personal profile for employee: {}", saved.getEmployeeCode());
        return EmployeeResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getEmployees(String search, String department, EmploymentStatus status, Pageable pageable) {
        String cleanSearch = StringUtils.hasText(search) ? search.trim() : null;
        String cleanDepartment = StringUtils.hasText(department) ? department.trim() : null;

        Page<Employee> employeePage = employeeRepository.findWithFilters(cleanSearch, cleanDepartment, status, pageable);
        Page<EmployeeResponse> dtoPage = employeePage.map(EmployeeResponse::fromEntity);

        return PageResponse.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(UUID employeeId) {
        Employee employee = employeeRepository.findByIdWithDetails(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        // Enforce role-based access / ownership
        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        boolean isHrOrAdmin = principal.getRoleNames().contains("HR") || principal.getRoleNames().contains("ADMIN");
        boolean isOwner = employee.getUser() != null && employee.getUser().getId().equals(principal.getId());

        if (!isHrOrAdmin && !isOwner) {
            log.warn("Access denied: User {} attempted to view employee {}", principal.getEmail(), employee.getEmployeeCode());
            throw new AccessDeniedException("You do not have permission to view other employee profiles");
        }

        return EmployeeResponse.fromEntity(employee);
    }

    @Override
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new DuplicateResourceException("Employee code already exists: " + request.getEmployeeCode());
        }

        User user;
        if (request.getUserId() != null) {
            user = userRepository.findByIdWithRoles(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
        } else if (StringUtils.hasText(request.getEmail())) {
            String email = request.getEmail().trim().toLowerCase();
            user = userRepository.findByEmailWithRoles(email)
                    .orElseGet(() -> {
                        User newUser = userRepository.save(User.builder()
                                .email(email)
                                .status(UserStatus.ACTIVE)
                                .build());

                        Role employeeRole = roleRepository.findByName("EMPLOYEE")
                                .orElseThrow(() -> new ResourceNotFoundException("Default EMPLOYEE role not found"));
                        userRoleRepository.save(new UserRole(newUser, employeeRole));
                        return userRepository.findByIdWithRoles(newUser.getId()).orElse(newUser);
                    });
        } else {
            throw new BadRequestException("Either userId or email must be provided to create an employee");
        }

        if (employeeRepository.existsByUserId(user.getId())) {
            throw new DuplicateResourceException("Employee profile already exists for user ID: " + user.getId());
        }

        Employee employee = Employee.builder()
                .user(user)
                .employeeCode(request.getEmployeeCode().trim())
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .phone(request.getPhone())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .profilePictureUrl(request.getProfilePictureUrl())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .joiningDate(request.getJoiningDate())
                .employmentStatus(request.getEmploymentStatus() != null ? request.getEmploymentStatus() : EmploymentStatus.ACTIVE)
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("Created new employee record: {} ({})", saved.getFullName(), saved.getEmployeeCode());
        return EmployeeResponse.fromEntity(saved);
    }

    @Override
    public EmployeeResponse updateEmployee(UUID employeeId, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        if (request.getFirstName() != null && StringUtils.hasText(request.getFirstName())) {
            employee.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && StringUtils.hasText(request.getLastName())) {
            employee.setLastName(request.getLastName().trim());
        }
        if (request.getPhone() != null) {
            employee.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            employee.setAddress(request.getAddress());
        }
        if (request.getDateOfBirth() != null) {
            employee.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            employee.setGender(request.getGender());
        }
        if (request.getProfilePictureUrl() != null) {
            employee.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getDepartment() != null) {
            employee.setDepartment(request.getDepartment());
        }
        if (request.getDesignation() != null) {
            employee.setDesignation(request.getDesignation());
        }
        if (request.getJoiningDate() != null) {
            employee.setJoiningDate(request.getJoiningDate());
        }
        if (request.getEmploymentStatus() != null) {
            employee.setEmploymentStatus(request.getEmploymentStatus());
            syncUserStatus(employee.getUser(), request.getEmploymentStatus());
        }

        Employee saved = employeeRepository.save(employee);
        log.info("Updated employee ID: {}", employeeId);
        return EmployeeResponse.fromEntity(saved);
    }

    @Override
    public EmployeeResponse changeEmployeeStatus(UUID employeeId, ChangeEmployeeStatusRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        employee.setEmploymentStatus(request.getEmploymentStatus());
        syncUserStatus(employee.getUser(), request.getEmploymentStatus());

        Employee saved = employeeRepository.save(employee);
        log.info("Changed employee {} status to {}", employee.getEmployeeCode(), request.getEmploymentStatus());
        return EmployeeResponse.fromEntity(saved);
    }

    private void syncUserStatus(User user, EmploymentStatus employmentStatus) {
        if (user != null) {
            if (employmentStatus == EmploymentStatus.TERMINATED ||
                employmentStatus == EmploymentStatus.INACTIVE ||
                employmentStatus == EmploymentStatus.RESIGNED) {
                user.setStatus(UserStatus.INACTIVE);
            } else if (employmentStatus == EmploymentStatus.ACTIVE || employmentStatus == EmploymentStatus.PROBATION) {
                user.setStatus(UserStatus.ACTIVE);
            }
            userRepository.save(user);
        }
    }
}
