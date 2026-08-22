import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import PayslipModal from "./PayslipModal";
import StatusBadge from "../common/StatusBadge";
import {
  CreditCard,
  FileText,
  Download,
  Eye,
  ShieldCheck,
  TrendingUp,
  Building,
  CheckCircle2,
  Lock
} from "lucide-react";
import { formatCurrency } from "../../utils/dateUtils";

export default function EmployeePayrollView() {
  const { currentUser, historicalPayslips } = useHRMS();
  const [selectedMonthSlip, setSelectedMonthSlip] = useState(null);

  const s = currentUser.salaryStructure || {};
  const grossPay = (s.basic || 0) + (s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0);
  const totalDeductions = (s.providentFund || 0) + (s.taxDeduction || 0) + (s.insurance || 0);
  const netInHand = grossPay - totalDeductions;

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
      {/* Top Banner */}
      <div
        className="glass-card"
        style={{
          padding: "1.5rem 2rem",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "1rem"
        }}
      >
        <div>
          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
            <CreditCard size={20} style={{ color: "var(--brand-primary)" }} />
            <h1 style={{ fontSize: "1.4rem", fontWeight: 800 }}>My Payroll & Payslips</h1>
          </div>
          <p style={{ fontSize: "0.85rem", color: "var(--text-secondary)", marginTop: "0.2rem" }}>
            Read-only breakdown of your compensation, tax withholdings, and monthly salary slips.
          </p>
        </div>

        <button onClick={() => setSelectedMonthSlip("August 2026")} className="btn btn-primary">
          <FileText size={16} /> Latest Payslip (Aug 2026)
        </button>
      </div>

      {/* Net Pay Overview Card */}
      <div
        className="glass-card"
        style={{
          padding: "1.75rem",
          background: "linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(99, 102, 241, 0.08) 100%)",
          border: "1px solid rgba(16, 185, 129, 0.3)",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "1.5rem"
        }}
      >
        <div>
          <span style={{ fontSize: "0.75rem", fontWeight: 700, color: "var(--text-muted)", textTransform: "uppercase" }}>
            Estimated Monthly Take-Home (Net In-Hand)
          </span>
          <div style={{ fontSize: "2.3rem", fontWeight: 800, color: "var(--color-success)", letterSpacing: "-0.03em" }}>
            {formatCurrency(netInHand)}
          </div>
          <div style={{ fontSize: "0.8rem", color: "var(--text-secondary)", marginTop: "0.2rem" }}>
            Direct deposit via ACH on last business day of every month.
          </div>
        </div>

        <div style={{ display: "flex", gap: "2rem" }}>
          <div>
            <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Gross Salary</span>
            <div style={{ fontSize: "1.3rem", fontWeight: 700, color: "var(--text-primary)" }}>
              {formatCurrency(grossPay)}
            </div>
            <span style={{ fontSize: "0.7rem", color: "var(--text-muted)" }}>Per month</span>
          </div>

          <div>
            <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Total Deductions</span>
            <div style={{ fontSize: "1.3rem", fontWeight: 700, color: "var(--color-danger)" }}>
              - {formatCurrency(totalDeductions)}
            </div>
            <span style={{ fontSize: "0.7rem", color: "var(--text-muted)" }}>Tax + PF + Insurance</span>
          </div>
        </div>
      </div>

      {/* Earnings vs Deductions Breakdown */}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1.5rem" }}>
        {/* Earnings Card */}
        <div className="glass-card" style={{ padding: "1.5rem" }}>
          <h3 style={{ fontSize: "1rem", fontWeight: 700, color: "var(--color-success)", marginBottom: "1rem" }}>
            Earnings & Allowances Breakdown
          </h3>
          <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem", fontSize: "0.875rem" }}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span style={{ color: "var(--text-secondary)" }}>Base Pay:</span>
              <strong style={{ fontFamily: "var(--font-mono)" }}>{formatCurrency(s.basic)}</strong>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span style={{ color: "var(--text-secondary)" }}>House Rent Allowance (HRA):</span>
              <strong style={{ fontFamily: "var(--font-mono)" }}>{formatCurrency(s.hra)}</strong>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span style={{ color: "var(--text-secondary)" }}>Special & Flexible Allowance:</span>
              <strong style={{ fontFamily: "var(--font-mono)" }}>{formatCurrency(s.specialAllowance)}</strong>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span style={{ color: "var(--text-secondary)" }}>Performance Bonus:</span>
              <strong style={{ fontFamily: "var(--font-mono)" }}>{formatCurrency(s.bonus)}</strong>
            </div>
            <div style={{ height: "1px", backgroundColor: "var(--border-color)", margin: "0.25rem 0" }} />
            <div style={{ display: "flex", justifyContent: "space-between", fontWeight: 800 }}>
              <span>Gross Earnings:</span>
              <span style={{ color: "var(--color-success)", fontFamily: "var(--font-mono)" }}>{formatCurrency(grossPay)}</span>
            </div>
          </div>
        </div>

        {/* Deductions Card */}
        <div className="glass-card" style={{ padding: "1.5rem" }}>
          <h3 style={{ fontSize: "1rem", fontWeight: 700, color: "var(--color-danger)", marginBottom: "1rem" }}>
            Statutory Deductions & Withholdings
          </h3>
          <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem", fontSize: "0.875rem" }}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span style={{ color: "var(--text-secondary)" }}>Provident Fund (PF):</span>
              <strong style={{ fontFamily: "var(--font-mono)", color: "var(--color-danger)" }}>{formatCurrency(s.providentFund)}</strong>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span style={{ color: "var(--text-secondary)" }}>Income Tax / TDS:</span>
              <strong style={{ fontFamily: "var(--font-mono)", color: "var(--color-danger)" }}>{formatCurrency(s.taxDeduction)}</strong>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span style={{ color: "var(--text-secondary)" }}>Health Insurance Premium:</span>
              <strong style={{ fontFamily: "var(--font-mono)", color: "var(--color-danger)" }}>{formatCurrency(s.insurance)}</strong>
            </div>
            <div style={{ height: "1px", backgroundColor: "var(--border-color)", margin: "0.25rem 0" }} />
            <div style={{ display: "flex", justifyContent: "space-between", fontWeight: 800 }}>
              <span>Total Deductions:</span>
              <span style={{ color: "var(--color-danger)", fontFamily: "var(--font-mono)" }}>- {formatCurrency(totalDeductions)}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Historical Payslips Archive (Section 3.6.1) */}
      <div className="glass-card" style={{ padding: "1.5rem" }}>
        <h3 style={{ fontSize: "1.1rem", fontWeight: 700, marginBottom: "1rem" }}>Historical Payslips Archive</h3>

        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Pay Period Month</th>
                <th>Payment Date</th>
                <th>Disbursement Ref</th>
                <th>Net In-Hand Amount</th>
                <th>Status</th>
                <th style={{ textAlign: "right" }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {historicalPayslips.map((slip, i) => (
                <tr key={i}>
                  <td>
                    <div style={{ fontWeight: 700, color: "var(--text-primary)" }}>{slip.month}</div>
                    <span style={{ fontSize: "0.725rem", color: "var(--text-muted)" }}>{slip.payPeriod}</span>
                  </td>
                  <td>
                    <span style={{ fontSize: "0.825rem", color: "var(--text-secondary)" }}>{slip.paymentDate}</span>
                  </td>
                  <td>
                    <span style={{ fontFamily: "var(--font-mono)", fontSize: "0.8rem", color: "var(--text-muted)" }}>
                      {slip.bankRef}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontFamily: "var(--font-mono)", fontWeight: 700, color: "var(--color-success)" }}>
                      {formatCurrency(netInHand)}
                    </span>
                  </td>
                  <td>
                    <StatusBadge status="Paid" />
                  </td>
                  <td style={{ textAlign: "right" }}>
                    <button
                      onClick={() => setSelectedMonthSlip(slip.month)}
                      className="btn btn-secondary btn-sm"
                      style={{ padding: "0.3rem 0.65rem" }}
                    >
                      <Eye size={13} /> View Slip
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Payslip Modal */}
      {selectedMonthSlip && (
        <PayslipModal
          isOpen={true}
          onClose={() => setSelectedMonthSlip(null)}
          employee={currentUser}
          month={selectedMonthSlip}
        />
      )}
    </div>
  );
}
