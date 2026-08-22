import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import Modal from "../common/Modal";

export default function AddEmployeeModal({ isOpen, onClose }) {
  const { addNewEmployee } = useHRMS();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    role: "employee",
    department: "Engineering",
    designation: "Software Engineer",
    workLocation: "San Francisco HQ (Hybrid)",
    reportingManager: "Sarah Connor (VP of People Operations)",
    phone: "+1 (555) 345-0019",
    basicSalary: 7500,
    hra: 3000,
    allowances: 1500,
    bonus: 1000
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.name || !formData.email) {
      alert("Name and email are required.");
      return;
    }

    addNewEmployee({
      name: formData.name,
      email: formData.email,
      role: formData.role,
      department: formData.department,
      designation: formData.designation,
      workLocation: formData.workLocation,
      reportingManager: formData.reportingManager,
      phone: formData.phone,
      salaryStructure: {
        basic: Number(formData.basicSalary),
        hra: Number(formData.hra),
        specialAllowance: Number(formData.allowances),
        bonus: Number(formData.bonus),
        providentFund: Math.round(Number(formData.basicSalary) * 0.12),
        taxDeduction: Math.round(Number(formData.basicSalary) * 0.2),
        insurance: 220
      }
    });

    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Onboard New Employee" maxWidth="650px">
      <form onSubmit={handleSubmit}>
        <div className="modal-body" style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">Full Name</label>
              <input
                type="text"
                required
                placeholder="e.g. Rachel Adams"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Work Email</label>
              <input
                type="email"
                required
                placeholder="rachel.adams@dayflow.io"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="input-control"
              />
            </div>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">Role</label>
              <select
                value={formData.role}
                onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                className="input-control"
              >
                <option value="employee">Employee</option>
                <option value="admin">Admin / HR Officer</option>
              </select>
            </div>

            <div className="input-group">
              <label className="input-label">Department</label>
              <select
                value={formData.department}
                onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                className="input-control"
              >
                <option value="Engineering">Engineering</option>
                <option value="Product Design">Product Design</option>
                <option value="Marketing">Marketing</option>
                <option value="Human Resources">Human Resources</option>
                <option value="Finance">Finance</option>
              </select>
            </div>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">Designation / Title</label>
              <input
                type="text"
                value={formData.designation}
                onChange={(e) => setFormData({ ...formData, designation: e.target.value })}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Reporting Manager</label>
              <input
                type="text"
                value={formData.reportingManager}
                onChange={(e) => setFormData({ ...formData, reportingManager: e.target.value })}
                className="input-control"
              />
            </div>
          </div>

          {/* Initial Salary Package Configuration */}
          <div style={{ fontSize: "0.85rem", fontWeight: 700, color: "var(--brand-primary)", textTransform: "uppercase", letterSpacing: "0.05em", marginTop: "0.5rem" }}>
            Initial Monthly Compensation Package ($)
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr 1fr", gap: "0.75rem" }}>
            <div className="input-group">
              <label className="input-label">Basic Salary</label>
              <input
                type="number"
                value={formData.basicSalary}
                onChange={(e) => setFormData({ ...formData, basicSalary: e.target.value })}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">HRA</label>
              <input
                type="number"
                value={formData.hra}
                onChange={(e) => setFormData({ ...formData, hra: e.target.value })}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Allowances</label>
              <input
                type="number"
                value={formData.allowances}
                onChange={(e) => setFormData({ ...formData, allowances: e.target.value })}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Bonus</label>
              <input
                type="number"
                value={formData.bonus}
                onChange={(e) => setFormData({ ...formData, bonus: e.target.value })}
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
            Complete Onboarding
          </button>
        </div>
      </form>
    </Modal>
  );
}
