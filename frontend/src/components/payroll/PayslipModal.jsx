import React, { useRef } from "react";
import { useHRMS } from "../../context/HRMSContext";
import Modal from "../common/Modal";
import { Printer, Download, Layers, ShieldCheck, CheckCircle2 } from "lucide-react";
import { formatDate, formatCurrency } from "../../utils/dateUtils";

export default function PayslipModal({ isOpen, onClose, employee, month = "August 2026" }) {
  const { employees, currentUser } = useHRMS();
  const targetEmp = employee || currentUser;
  const s = targetEmp.salaryStructure || {};

  const grossEarnings = (s.basic || 0) + (s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0);
  const totalDeductions = (s.providentFund || 0) + (s.taxDeduction || 0) + (s.insurance || 0);
  const netInHand = grossEarnings - totalDeductions;

  const handlePrint = () => {
    window.print();
  };

  const handleDownload = () => {
    alert(`Downloading verified digital payslip for ${targetEmp.name} - ${month}`);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Salary Payslip — ${month}`} maxWidth="750px">
      <div className="modal-body" style={{ padding: "1.5rem" }}>
        {/* Printable Document Sheet Container */}
        <div
          id="payslip-print-sheet"
          style={{
            padding: "2rem",
            backgroundColor: "#ffffff",
            color: "#0f172a",
            borderRadius: "var(--radius-md)",
            border: "1px solid #cbd5e1",
            fontFamily: "var(--font-sans)",
            display: "flex",
            flexDirection: "column",
            gap: "1.25rem"
          }}
        >
          {/* Header */}
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "flex-start",
              borderBottom: "2px solid #6366f1",
              paddingBottom: "1.25rem"
            }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
              <div
                style={{
                  width: "40px",
                  height: "40px",
                  borderRadius: "8px",
                  backgroundColor: "#6366f1",
                  color: "#ffffff",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center"
                }}
              >
                <Layers size={22} />
              </div>
              <div>
                <div style={{ fontSize: "1.4rem", fontWeight: 800, color: "#0f172a", letterSpacing: "-0.02em" }}>
                  DAYFLOW TECHNOLOGIES INC.
                </div>
                <div style={{ fontSize: "0.75rem", color: "#64748b" }}>
                  452 Silicon Boulevard, Suite 800, San Francisco, CA 94107
                </div>
              </div>
            </div>

            <div style={{ textAlign: "right" }}>
              <div style={{ fontSize: "1.1rem", fontWeight: 800, color: "#6366f1", textTransform: "uppercase" }}>
                SALARY PAYSLIP
              </div>
              <div style={{ fontSize: "0.85rem", fontWeight: 700, color: "#334155" }}>
                Pay Period: {month}
              </div>
              <div style={{ fontSize: "0.75rem", color: "#64748b" }}>
                Status: <strong style={{ color: "#10b981" }}>PAID / DISBURSED</strong>
              </div>
            </div>
          </div>

          {/* Employee & Bank Info Matrix */}
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: "1rem",
              padding: "1rem",
              backgroundColor: "#f8fafc",
              borderRadius: "6px",
              fontSize: "0.8rem"
            }}
          >
            <div style={{ display: "flex", flexDirection: "column", gap: "0.4rem" }}>
              <div><strong>Employee Name:</strong> {targetEmp.name}</div>
              <div><strong>Employee ID:</strong> {targetEmp.id}</div>
              <div><strong>Designation:</strong> {targetEmp.designation}</div>
              <div><strong>Department:</strong> {targetEmp.department}</div>
            </div>

            <div style={{ display: "flex", flexDirection: "column", gap: "0.4rem" }}>
              <div><strong>Joining Date:</strong> {formatDate(targetEmp.joiningDate)}</div>
              <div><strong>Work Location:</strong> {targetEmp.workLocation}</div>
              <div><strong>Bank Account:</strong> •••• •••• 9012 (Direct ACH)</div>
              <div><strong>Tax / SSN Status:</strong> Verified Active</div>
            </div>
          </div>

          {/* Detailed Earnings vs Deductions Table */}
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1.5rem" }}>
            {/* Earnings */}
            <div>
              <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.825rem" }}>
                <thead>
                  <tr style={{ borderBottom: "2px solid #e2e8f0", textAlign: "left" }}>
                    <th style={{ padding: "0.5rem 0", color: "#475569" }}>Earnings Description</th>
                    <th style={{ padding: "0.5rem 0", textAlign: "right", color: "#475569" }}>Amount</th>
                  </tr>
                </thead>
                <tbody>
                  <tr style={{ borderBottom: "1px solid #f1f5f9" }}>
                    <td style={{ padding: "0.4rem 0" }}>Basic Salary</td>
                    <td style={{ textAlign: "right", fontFamily: "var(--font-mono)" }}>{formatCurrency(s.basic)}</td>
                  </tr>
                  <tr style={{ borderBottom: "1px solid #f1f5f9" }}>
                    <td style={{ padding: "0.4rem 0" }}>House Rent Allowance (HRA)</td>
                    <td style={{ textAlign: "right", fontFamily: "var(--font-mono)" }}>{formatCurrency(s.hra)}</td>
                  </tr>
                  <tr style={{ borderBottom: "1px solid #f1f5f9" }}>
                    <td style={{ padding: "0.4rem 0" }}>Special & Flexi Allowance</td>
                    <td style={{ textAlign: "right", fontFamily: "var(--font-mono)" }}>{formatCurrency(s.specialAllowance)}</td>
                  </tr>
                  <tr style={{ borderBottom: "1px solid #f1f5f9" }}>
                    <td style={{ padding: "0.4rem 0" }}>Performance Incentive</td>
                    <td style={{ textAlign: "right", fontFamily: "var(--font-mono)" }}>{formatCurrency(s.bonus)}</td>
                  </tr>
                  <tr style={{ borderTop: "2px solid #cbd5e1", fontWeight: 800 }}>
                    <td style={{ padding: "0.6rem 0" }}>Gross Earnings (A)</td>
                    <td style={{ textAlign: "right", color: "#10b981", fontFamily: "var(--font-mono)" }}>
                      {formatCurrency(grossEarnings)}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            {/* Deductions */}
            <div>
              <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.825rem" }}>
                <thead>
                  <tr style={{ borderBottom: "2px solid #e2e8f0", textAlign: "left" }}>
                    <th style={{ padding: "0.5rem 0", color: "#475569" }}>Deductions Item</th>
                    <th style={{ padding: "0.5rem 0", textAlign: "right", color: "#475569" }}>Amount</th>
                  </tr>
                </thead>
                <tbody>
                  <tr style={{ borderBottom: "1px solid #f1f5f9" }}>
                    <td style={{ padding: "0.4rem 0" }}>Provident Fund (PF @ 12%)</td>
                    <td style={{ textAlign: "right", fontFamily: "var(--font-mono)", color: "#f43f5e" }}>{formatCurrency(s.providentFund)}</td>
                  </tr>
                  <tr style={{ borderBottom: "1px solid #f1f5f9" }}>
                    <td style={{ padding: "0.4rem 0" }}>Income Tax / TDS Withholding</td>
                    <td style={{ textAlign: "right", fontFamily: "var(--font-mono)", color: "#f43f5e" }}>{formatCurrency(s.taxDeduction)}</td>
                  </tr>
                  <tr style={{ borderBottom: "1px solid #f1f5f9" }}>
                    <td style={{ padding: "0.4rem 0" }}>Health & Life Insurance</td>
                    <td style={{ textAlign: "right", fontFamily: "var(--font-mono)", color: "#f43f5e" }}>{formatCurrency(s.insurance)}</td>
                  </tr>
                  <tr style={{ borderTop: "2px solid #cbd5e1", fontWeight: 800 }}>
                    <td style={{ padding: "0.6rem 0" }}>Total Deductions (B)</td>
                    <td style={{ textAlign: "right", color: "#f43f5e", fontFamily: "var(--font-mono)" }}>
                      {formatCurrency(totalDeductions)}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          {/* Net Salary Payable Box */}
          <div
            style={{
              padding: "1rem 1.25rem",
              backgroundColor: "#f0fdf4",
              border: "1px solid #bbf7d0",
              borderRadius: "6px",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center"
            }}
          >
            <div>
              <div style={{ fontSize: "0.75rem", fontWeight: 700, color: "#166534", textTransform: "uppercase" }}>
                Net Salary Payable (A - B)
              </div>
              <div style={{ fontSize: "0.75rem", color: "#15803d", fontStyle: "italic" }}>
                Transferred to employee registered bank account
              </div>
            </div>
            <div style={{ fontSize: "1.75rem", fontWeight: 900, color: "#166534", fontFamily: "var(--font-mono)" }}>
              {formatCurrency(netInHand)}
            </div>
          </div>

          {/* Footer & Signature */}
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "flex-end",
              paddingTop: "1.5rem",
              borderTop: "1px dashed #cbd5e1",
              fontSize: "0.75rem",
              color: "#64748b"
            }}
          >
            <div>
              <div>This is a computer-generated official document. No physical signature required.</div>
              <div>Generated by Dayflow HRMS Platform on {formatDate(new Date().toISOString())}.</div>
            </div>
            <div style={{ textAlign: "center" }}>
              <div style={{ fontFamily: "cursive", fontSize: "1.1rem", color: "#4338ca", marginBottom: "0.2rem" }}>
                Sarah Connor
              </div>
              <div style={{ borderTop: "1px solid #94a3b8", paddingTop: "0.2rem", fontWeight: 700, color: "#334155" }}>
                Authorized HR Officer
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="modal-footer no-print">
        <button type="button" onClick={onClose} className="btn btn-secondary">
          Close
        </button>
        <button type="button" onClick={handleDownload} className="btn btn-secondary">
          <Download size={14} /> Download PDF
        </button>
        <button type="button" onClick={handlePrint} className="btn btn-primary">
          <Printer size={14} /> Print Payslip
        </button>
      </div>
    </Modal>
  );
}
