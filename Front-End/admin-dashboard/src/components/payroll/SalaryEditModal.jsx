import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import Modal from "../common/Modal";
import { formatCurrency } from "../../utils/dateUtils";

export default function SalaryEditModal({ isOpen, onClose, employee }) {
  const { updateSalaryStructure } = useHRMS();

  const currentSalary = employee?.salaryStructure || {
    basic: 7500,
    hra: 3000,
    specialAllowance: 1500,
    bonus: 1000,
    providentFund: 900,
    taxDeduction: 1500,
    insurance: 200
  };

  const [basic, setBasic] = useState(currentSalary.basic);
  const [hra, setHra] = useState(currentSalary.hra);
  const [specialAllowance, setSpecialAllowance] = useState(currentSalary.specialAllowance);
  const [bonus, setBonus] = useState(currentSalary.bonus);
  const [providentFund, setProvidentFund] = useState(currentSalary.providentFund);
  const [taxDeduction, setTaxDeduction] = useState(currentSalary.taxDeduction);
  const [insurance, setInsurance] = useState(currentSalary.insurance);

  const gross = Number(basic) + Number(hra) + Number(specialAllowance) + Number(bonus);
  const deductions = Number(providentFund) + Number(taxDeduction) + Number(insurance);
  const net = gross - deductions;

  const handleSubmit = (e) => {
    e.preventDefault();
    updateSalaryStructure(employee.id, {
      basic: Number(basic),
      hra: Number(hra),
      specialAllowance: Number(specialAllowance),
      bonus: Number(bonus),
      providentFund: Number(providentFund),
      taxDeduction: Number(taxDeduction),
      insurance: Number(insurance)
    });
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Update Salary Structure (${employee?.name})`}
      maxWidth="620px"
    >
      <form onSubmit={handleSubmit}>
        <div className="modal-body" style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          {/* Real-time calculated summary */}
          <div
            style={{
              padding: "1rem 1.25rem",
              borderRadius: "var(--radius-md)",
              backgroundColor: "var(--bg-tertiary)",
              border: "1px solid var(--border-color)",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center"
            }}
          >
            <div>
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Computed Net In-Hand</span>
              <div style={{ fontSize: "1.4rem", fontWeight: 800, color: "var(--color-success)" }}>
                {formatCurrency(net)}
              </div>
            </div>
            <div style={{ textAlign: "right", fontSize: "0.8rem", color: "var(--text-secondary)" }}>
              <div>Gross: <strong>{formatCurrency(gross)}</strong></div>
              <div>Deductions: <strong style={{ color: "var(--color-danger)" }}>- {formatCurrency(deductions)}</strong></div>
            </div>
          </div>

          <div style={{ fontSize: "0.85rem", fontWeight: 700, color: "var(--brand-primary)", textTransform: "uppercase", letterSpacing: "0.05em" }}>
            Monthly Earnings Components ($)
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">Basic Pay</label>
              <input
                type="number"
                required
                value={basic}
                onChange={(e) => setBasic(e.target.value)}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">House Rent Allowance (HRA)</label>
              <input
                type="number"
                required
                value={hra}
                onChange={(e) => setHra(e.target.value)}
                className="input-control"
              />
            </div>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">Special Allowance</label>
              <input
                type="number"
                required
                value={specialAllowance}
                onChange={(e) => setSpecialAllowance(e.target.value)}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Performance Bonus</label>
              <input
                type="number"
                required
                value={bonus}
                onChange={(e) => setBonus(e.target.value)}
                className="input-control"
              />
            </div>
          </div>

          <div style={{ fontSize: "0.85rem", fontWeight: 700, color: "var(--color-danger)", textTransform: "uppercase", letterSpacing: "0.05em", marginTop: "0.5rem" }}>
            Monthly Deductions Components ($)
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "0.75rem" }}>
            <div className="input-group">
              <label className="input-label">Provident Fund (PF)</label>
              <input
                type="number"
                required
                value={providentFund}
                onChange={(e) => setProvidentFund(e.target.value)}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Tax / TDS</label>
              <input
                type="number"
                required
                value={taxDeduction}
                onChange={(e) => setTaxDeduction(e.target.value)}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Insurance</label>
              <input
                type="number"
                required
                value={insurance}
                onChange={(e) => setInsurance(e.target.value)}
                className="input-control"
              />
            </div>
          </div>
        </div>

        <div className="modal-footer">
          <button type="button" onClick={onClose} className="btn btn-secondary">
            Cancel
          </button>
          <button type="submit" className="btn btn-primary">
            Save Salary Structure
          </button>
        </div>
      </form>
    </Modal>
  );
}
