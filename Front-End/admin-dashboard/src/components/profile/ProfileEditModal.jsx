import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import Modal from "../common/Modal";
import { Lock, ShieldAlert, Check } from "lucide-react";

export default function ProfileEditModal({ isOpen, onClose, targetEmployee }) {
  const { currentUser, updateEmployeeProfile } = useHRMS();
  const isHRAdmin = currentUser.role === "admin";

  const [formData, setFormData] = useState({
    name: targetEmployee?.name || "",
    phone: targetEmployee?.phone || "",
    address: targetEmployee?.address || "",
    bio: targetEmployee?.bio || "",
    emergencyName: targetEmployee?.emergencyContact?.name || "",
    emergencyPhone: targetEmployee?.emergencyContact?.phone || "",
    emergencyRelation: targetEmployee?.emergencyContact?.relation || "",
    // Admin only fields
    designation: targetEmployee?.designation || "",
    department: targetEmployee?.department || "Engineering",
    workLocation: targetEmployee?.workLocation || "",
    employmentType: targetEmployee?.employmentType || "Full-time",
    reportingManager: targetEmployee?.reportingManager || "",
    status: targetEmployee?.status || "Active",
    joiningDate: targetEmployee?.joiningDate || ""
  });

  const handleSubmit = (e) => {
    e.preventDefault();

    const payload = {
      phone: formData.phone,
      address: formData.address,
      bio: formData.bio,
      emergencyContact: {
        name: formData.emergencyName,
        phone: formData.emergencyPhone,
        relation: formData.emergencyRelation
      }
    };

    if (isHRAdmin) {
      payload.name = formData.name;
      payload.designation = formData.designation;
      payload.department = formData.department;
      payload.workLocation = formData.workLocation;
      payload.employmentType = formData.employmentType;
      payload.reportingManager = formData.reportingManager;
      payload.status = formData.status;
      payload.joiningDate = formData.joiningDate;
    }

    updateEmployeeProfile(targetEmployee.id, payload, isHRAdmin);
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Edit Profile (${targetEmployee?.name})`}
      maxWidth="650px"
    >
      <form onSubmit={handleSubmit}>
        <div className="modal-body" style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          {!isHRAdmin && (
            <div
              style={{
                padding: "0.75rem",
                borderRadius: "var(--radius-md)",
                backgroundColor: "var(--bg-tertiary)",
                border: "1px solid var(--border-color)",
                fontSize: "0.8rem",
                color: "var(--text-secondary)",
                display: "flex",
                alignItems: "center",
                gap: "0.5rem"
              }}
            >
              <Lock size={15} style={{ color: "var(--brand-primary)" }} />
              <span>
                <strong>Employee Mode:</strong> You can edit your contact info, address, and emergency details. Job and salary data are managed by HR.
              </span>
            </div>
          )}

          {/* Contact Fields (Editable for both) */}
          <div style={{ fontSize: "0.85rem", fontWeight: 700, color: "var(--brand-primary)", textTransform: "uppercase", letterSpacing: "0.05em" }}>
            Personal & Contact Information
          </div>

          {isHRAdmin && (
            <div className="input-group">
              <label className="input-label">Full Name</label>
              <input
                type="text"
                required
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="input-control"
              />
            </div>
          )}

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">Phone Number</label>
              <input
                type="text"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Residential Address</label>
              <input
                type="text"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                className="input-control"
              />
            </div>
          </div>

          {/* Emergency Contact */}
          <div style={{ fontSize: "0.85rem", fontWeight: 700, color: "var(--brand-primary)", textTransform: "uppercase", letterSpacing: "0.05em" }}>
            Emergency Contact Details
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "0.75rem" }}>
            <div className="input-group">
              <label className="input-label">Contact Name</label>
              <input
                type="text"
                value={formData.emergencyName}
                onChange={(e) => setFormData({ ...formData, emergencyName: e.target.value })}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Relationship</label>
              <input
                type="text"
                value={formData.emergencyRelation}
                onChange={(e) => setFormData({ ...formData, emergencyRelation: e.target.value })}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Emergency Phone</label>
              <input
                type="text"
                value={formData.emergencyPhone}
                onChange={(e) => setFormData({ ...formData, emergencyPhone: e.target.value })}
                className="input-control"
              />
            </div>
          </div>

          <div className="input-group">
            <label className="input-label">Professional Bio / About</label>
            <textarea
              rows={2}
              value={formData.bio}
              onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
              className="input-control"
            />
          </div>

          {/* Admin Managed Fields (Section 3.3.2) */}
          {isHRAdmin && (
            <>
              <div style={{ fontSize: "0.85rem", fontWeight: 700, color: "var(--brand-primary)", textTransform: "uppercase", letterSpacing: "0.05em", marginTop: "0.5rem" }}>
                Job & Organizational Details (Admin Controls)
              </div>

              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
                <div className="input-group">
                  <label className="input-label">Job Title / Designation</label>
                  <input
                    type="text"
                    value={formData.designation}
                    onChange={(e) => setFormData({ ...formData, designation: e.target.value })}
                    className="input-control"
                  />
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
                  <label className="input-label">Work Location</label>
                  <input
                    type="text"
                    value={formData.workLocation}
                    onChange={(e) => setFormData({ ...formData, workLocation: e.target.value })}
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

              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
                <div className="input-group">
                  <label className="input-label">Employment Status</label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                    className="input-control"
                  >
                    <option value="Active">Active</option>
                    <option value="On Leave">On Leave</option>
                    <option value="Inactive">Inactive</option>
                  </select>
                </div>

                <div className="input-group">
                  <label className="input-label">Joining Date</label>
                  <input
                    type="date"
                    value={formData.joiningDate}
                    onChange={(e) => setFormData({ ...formData, joiningDate: e.target.value })}
                    className="input-control"
                  />
                </div>
              </div>
            </>
          )}
        </div>

        <div className="modal-footer">
          <button type="button" onClick={onClose} className="btn btn-secondary">
            Cancel
          </button>
          <button type="submit" className="btn btn-primary">
            Save Profile Changes
          </button>
        </div>
      </form>
    </Modal>
  );
}
