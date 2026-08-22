import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import MetricCard from "../common/MetricCard";
import SalaryEditModal from "./SalaryEditModal";
import PayslipModal from "./PayslipModal";
import {
  CreditCard,
  DollarSign,
  TrendingUp,
  Download,
  Play,
  Edit2,
  FileText,
  Search,
  CheckCircle2,
  Sparkles
} from "lucide-react";
import { formatCurrency } from "../../utils/dateUtils";
import { exportPayrollSummaryCSV } from "../../utils/exportUtils";

export default function AdminPayrollManager() {
  const { employees, processMonthlyPayroll } = useHRMS();

  const [searchQuery, setSearchQuery] = useState("");
  const [selectedDept, setSelectedDept] = useState("all");

  const [selectedEmpForSalaryEdit, setSelectedEmpForSalaryEdit] = useState(null);
  const [selectedEmpForPayslip, setSelectedEmpForPayslip] = useState(null);

  const departments = ["Engineering", "Product Design", "Marketing", "Human Resources", "Finance"];

  const filtered = employees.filter((emp) => {
    const matchesSearch =
      emp.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      emp.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
      emp.department.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesDept = selectedDept === "all" || emp.department === selectedDept;
    return matchesSearch && matchesDept;
  });

  const totalGrossExpense = employees.reduce((sum, emp) => {
    const s = emp.salaryStructure || {};
    return sum + (s.basic || 0) + (s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0);
  }, 0);

  const totalNetDisbursal = employees.reduce((sum, emp) => {
    const s = emp.salaryStructure || {};
    const gross = (s.basic || 0) + (s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0);
    const deductions = (s.providentFund || 0) + (s.taxDeduction || 0) + (s.insurance || 0);
    return sum + (gross - deductions);
  }, 0);

  const avgSalary = Math.round(totalGrossExpense / Math.max(1, employees.length));

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
      {/* Metric summary */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))", gap: "1.25rem" }}>
        <MetricCard
          title="Total Monthly Payroll Expense"
          value={formatCurrency(totalGrossExpense)}
          subtitle="Monthly gross workforce commitments"
          icon={DollarSign}
          color="primary"
        />

        <MetricCard
          title="Total Net In-Hand Disbursed"
          value={formatCurrency(totalNetDisbursal)}
          subtitle="Direct ACH bank transfers"
          icon={CreditCard}
          color="success"
        />

        <MetricCard
          title="Average Employee Compensation"
          value={formatCurrency(avgSalary)}
          subtitle="Competitive tech benchmark"
          icon={TrendingUp}
          color="purple"
        />
      </div>

      {/* Action Toolbar */}
      <div
        className="glass-card"
        style={{
          padding: "1.25rem 1.5rem",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "1rem"
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "0.75rem", flexWrap: "wrap" }}>
          <div style={{ position: "relative", minWidth: "240px" }}>
            <input
              type="text"
              placeholder="Search employee or ID..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="input-control"
              style={{ paddingLeft: "2.2rem" }}
            />
            <Search size={15} style={{ position: "absolute", left: "0.75rem", top: "50%", transform: "translateY(-50%)", color: "var(--text-muted)" }} />
          </div>

          <select
            value={selectedDept}
            onChange={(e) => setSelectedDept(e.target.value)}
            className="input-control"
            style={{ width: "auto" }}
          >
            <option value="all">All Departments</option>
            {departments.map((d) => (
              <option key={d} value={d}>
                {d}
              </option>
            ))}
          </select>
        </div>

        <div style={{ display: "flex", gap: "0.5rem" }}>
          <button onClick={() => exportPayrollSummaryCSV(employees)} className="btn btn-secondary btn-sm">
            <Download size={14} /> Export Payroll CSV
          </button>
          <button onClick={() => processMonthlyPayroll("August 2026")} className="btn btn-primary btn-sm">
            <Play size={14} /> Run Monthly Payroll Batch
          </button>
        </div>
      </div>

      {/* Enterprise Payroll Master Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Employee Details</th>
              <th>Department</th>
              <th>Basic Pay</th>
              <th>Allowances</th>
              <th>Gross Earnings</th>
              <th>Deductions</th>
              <th>Net In-Hand</th>
              <th style={{ textAlign: "right" }}>Payroll Controls</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((emp) => {
              const s = emp.salaryStructure || {};
              const gross = (s.basic || 0) + (s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0);
              const deductions = (s.providentFund || 0) + (s.taxDeduction || 0) + (s.insurance || 0);
              const net = gross - deductions;

              return (
                <tr key={emp.id}>
                  <td>
                    <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
                      <img
                        src={emp.avatar}
                        alt=""
                        style={{ width: "32px", height: "32px", borderRadius: "50%", objectFit: "cover" }}
                      />
                      <div>
                        <div style={{ fontWeight: 700, color: "var(--text-primary)" }}>{emp.name}</div>
                        <div style={{ fontSize: "0.725rem", color: "var(--text-muted)", fontFamily: "var(--font-mono)" }}>
                          {emp.id}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span style={{ fontSize: "0.8rem", color: "var(--text-secondary)" }}>{emp.department}</span>
                  </td>
                  <td>
                    <span style={{ fontFamily: "var(--font-mono)", fontWeight: 600 }}>{formatCurrency(s.basic)}</span>
                  </td>
                  <td>
                    <span style={{ fontFamily: "var(--font-mono)", color: "var(--text-secondary)" }}>
                      {formatCurrency((s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0))}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontFamily: "var(--font-mono)", fontWeight: 700, color: "var(--color-success)" }}>
                      {formatCurrency(gross)}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontFamily: "var(--font-mono)", color: "var(--color-danger)" }}>
                      - {formatCurrency(deductions)}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontFamily: "var(--font-mono)", fontWeight: 800, color: "var(--brand-primary)" }}>
                      {formatCurrency(net)}
                    </span>
                  </td>
                  <td style={{ textAlign: "right" }}>
                    <div style={{ display: "flex", justifyContent: "flex-end", gap: "0.35rem" }}>
                      <button
                        onClick={() => setSelectedEmpForSalaryEdit(emp)}
                        className="btn btn-secondary btn-sm"
                        style={{ padding: "0.25rem 0.5rem", fontSize: "0.725rem" }}
                        title="Edit Salary Structure"
                      >
                        <Edit2 size={13} /> Edit
                      </button>
                      <button
                        onClick={() => setSelectedEmpForPayslip(emp)}
                        className="btn btn-primary btn-sm"
                        style={{ padding: "0.25rem 0.5rem", fontSize: "0.725rem" }}
                        title="Generate Payslip"
                      >
                        <FileText size={13} /> Slip
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Salary Edit Modal */}
      {selectedEmpForSalaryEdit && (
        <SalaryEditModal
          isOpen={true}
          onClose={() => setSelectedEmpForSalaryEdit(null)}
          employee={selectedEmpForSalaryEdit}
        />
      )}

      {/* Payslip Modal */}
      {selectedEmpForPayslip && (
        <PayslipModal
          isOpen={true}
          onClose={() => setSelectedEmpForPayslip(null)}
          employee={selectedEmpForPayslip}
        />
      )}
    </div>
  );
}
