package com.dayflow.hrms.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity representing employee payroll and salary compensation details.
 */
@Entity
@Table(name = "payroll")
public class Payroll {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "base_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary = BigDecimal.ZERO;

    @Column(name = "allowances", nullable = false, precision = 15, scale = 2)
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column(name = "deductions", nullable = false, precision = 15, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "net_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal netSalary = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Payroll() {
    }

    public Payroll(UUID id, Employee employee, BigDecimal baseSalary, BigDecimal allowances,
                   BigDecimal deductions, BigDecimal netSalary, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.employee = employee;
        this.baseSalary = baseSalary != null ? baseSalary : BigDecimal.ZERO;
        this.allowances = allowances != null ? allowances : BigDecimal.ZERO;
        this.deductions = deductions != null ? deductions : BigDecimal.ZERO;
        this.netSalary = netSalary != null ? netSalary : BigDecimal.ZERO;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        calculateNetSalary();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        calculateNetSalary();
    }

    /**
     * Calculates net salary server-side: Net = Base + Allowances - Deductions
     */
    public void calculateNetSalary() {
        BigDecimal base = this.baseSalary != null ? this.baseSalary : BigDecimal.ZERO;
        BigDecimal allow = this.allowances != null ? this.allowances : BigDecimal.ZERO;
        BigDecimal deduct = this.deductions != null ? this.deductions : BigDecimal.ZERO;
        this.netSalary = base.add(allow).subtract(deduct);
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary != null ? baseSalary : BigDecimal.ZERO;
        calculateNetSalary();
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public void setAllowances(BigDecimal allowances) {
        this.allowances = allowances != null ? allowances : BigDecimal.ZERO;
        calculateNetSalary();
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions != null ? deductions : BigDecimal.ZERO;
        calculateNetSalary();
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary != null ? netSalary : BigDecimal.ZERO;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payroll payroll = (Payroll) o;
        return Objects.equals(id, payroll.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private Employee employee;
        private BigDecimal baseSalary = BigDecimal.ZERO;
        private BigDecimal allowances = BigDecimal.ZERO;
        private BigDecimal deductions = BigDecimal.ZERO;
        private BigDecimal netSalary = BigDecimal.ZERO;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder employee(Employee employee) {
            this.employee = employee;
            return this;
        }

        public Builder baseSalary(BigDecimal baseSalary) {
            this.baseSalary = baseSalary;
            return this;
        }

        public Builder allowances(BigDecimal allowances) {
            this.allowances = allowances;
            return this;
        }

        public Builder deductions(BigDecimal deductions) {
            this.deductions = deductions;
            return this;
        }

        public Builder netSalary(BigDecimal netSalary) {
            this.netSalary = netSalary;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Payroll build() {
            Payroll payroll = new Payroll(id, employee, baseSalary, allowances, deductions, netSalary, createdAt, updatedAt);
            payroll.calculateNetSalary();
            return payroll;
        }
    }
}
