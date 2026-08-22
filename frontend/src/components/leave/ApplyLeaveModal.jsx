import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import Modal from "../common/Modal";
import { Calendar, FileText, Upload, Sparkles, AlertCircle } from "lucide-react";
import { calculateWorkingDays } from "../../utils/dateUtils";

export default function ApplyLeaveModal({ isOpen, onClose }) {
  const { applyLeave, currentUser } = useHRMS();

  const [leaveType, setLeaveType] = useState("Paid");
  const [startDate, setStartDate] = useState("2026-08-28");
  const [endDate, setEndDate] = useState("2026-08-30");
  const [isHalfDay, setIsHalfDay] = useState(false);
  const [halfDayType, setHalfDayType] = useState("First Half");
  const [reason, setReason] = useState("");
  const [attachmentName, setAttachmentName] = useState("");

  const workingDays = calculateWorkingDays(startDate, endDate);
  const totalDays = isHalfDay ? 0.5 : workingDays;

  const quickReasonTemplates = [
    "Attending personal family commitment",
    "Medical appointment & recovery",
    "Scheduled vacation & travel downtime",
    "Home maintenance and emergency repairs",
    "Attending professional tech conference"
  ];

  const handleSimulatedFileUpload = (e) => {
    if (e.target.files && e.target.files[0]) {
      setAttachmentName(e.target.files[0].name);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!reason.trim()) {
      alert("Please provide a reason for the leave request.");
      return;
    }

    applyLeave({
      type: leaveType,
      startDate,
      endDate: isHalfDay ? startDate : endDate,
      halfDay: isHalfDay ? halfDayType : null,
      reason,
      attachment: attachmentName ? { name: attachmentName } : null
    });

    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Apply for Time-Off / Leave">
      <form onSubmit={handleSubmit}>
        <div className="modal-body" style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          {/* Leave Type Selector */}
          <div className="input-group">
            <label className="input-label">Leave Type</label>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: "0.5rem" }}>
              {["Paid", "Sick", "Casual", "Unpaid"].map((type) => (
                <button
                  key={type}
                  type="button"
                  onClick={() => setLeaveType(type)}
                  style={{
                    padding: "0.6rem 0.4rem",
                    borderRadius: "var(--radius-md)",
                    border: `1px solid ${leaveType === type ? "var(--brand-primary)" : "var(--border-color)"}`,
                    backgroundColor: leaveType === type ? "var(--brand-primary-light)" : "var(--bg-secondary)",
                    color: leaveType === type ? "var(--brand-primary)" : "var(--text-primary)",
                    fontWeight: 700,
                    fontSize: "0.8rem",
                    cursor: "pointer",
                    transition: "all var(--transition-fast)"
                  }}
                >
                  {type}
                </button>
              ))}
            </div>
          </div>

          {/* Half-Day Toggle */}
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "0.75rem", backgroundColor: "var(--bg-tertiary)", borderRadius: "var(--radius-md)" }}>
            <span style={{ fontSize: "0.825rem", fontWeight: 600, color: "var(--text-primary)" }}>
              Applying for Half-Day only?
            </span>
            <label style={{ display: "flex", alignItems: "center", gap: "0.5rem", cursor: "pointer" }}>
              <input
                type="checkbox"
                checked={isHalfDay}
                onChange={(e) => setIsHalfDay(e.target.checked)}
                style={{ width: "16px", height: "16px" }}
              />
              <span style={{ fontSize: "0.8rem", color: "var(--text-secondary)" }}>Half-day</span>
            </label>
          </div>

          {isHalfDay && (
            <div className="input-group">
              <label className="input-label">Half-Day Session</label>
              <select
                value={halfDayType}
                onChange={(e) => setHalfDayType(e.target.value)}
                className="input-control"
              >
                <option value="First Half">First Half (Morning to 1:30 PM)</option>
                <option value="Second Half">Second Half (1:30 PM to Evening)</option>
              </select>
            </div>
          )}

          {/* Date Range Picker */}
          <div style={{ display: "grid", gridTemplateColumns: isHalfDay ? "1fr" : "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">{isHalfDay ? "Date of Leave" : "Start Date"}</label>
              <input
                type="date"
                required
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="input-control"
              />
            </div>

            {!isHalfDay && (
              <div className="input-group">
                <label className="input-label">End Date</label>
                <input
                  type="date"
                  required
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  className="input-control"
                />
              </div>
            )}
          </div>

          {/* Computed Duration Badge */}
          <div
            style={{
              padding: "0.6rem 0.85rem",
              borderRadius: "var(--radius-sm)",
              backgroundColor: "rgba(99, 102, 241, 0.08)",
              border: "1px solid rgba(99, 102, 241, 0.2)",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              fontSize: "0.8rem"
            }}
          >
            <span style={{ color: "var(--text-secondary)" }}>Working Days Calculated:</span>
            <strong style={{ color: "var(--brand-primary)", fontSize: "0.95rem" }}>
              {totalDays} {totalDays === 1 ? "day" : "days"}
            </strong>
          </div>

          {/* Quick Reason Presets */}
          <div>
            <div style={{ fontSize: "0.75rem", fontWeight: 700, color: "var(--text-muted)", marginBottom: "0.4rem", textTransform: "uppercase" }}>
              Quick Templates:
            </div>
            <div style={{ display: "flex", flexWrap: "wrap", gap: "0.35rem" }}>
              {quickReasonTemplates.map((template, idx) => (
                <button
                  key={idx}
                  type="button"
                  onClick={() => setReason(template)}
                  style={{
                    fontSize: "0.7rem",
                    padding: "0.25rem 0.5rem",
                    borderRadius: "var(--radius-full)",
                    backgroundColor: "var(--bg-tertiary)",
                    border: "1px solid var(--border-color)",
                    color: "var(--text-secondary)",
                    cursor: "pointer"
                  }}
                >
                  + {template}
                </button>
              ))}
            </div>
          </div>

          {/* Remarks Textarea */}
          <div className="input-group">
            <label className="input-label">Reason / Remarks</label>
            <textarea
              required
              rows={3}
              placeholder="Provide context or instructions for your team and HR..."
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              className="input-control"
            />
          </div>

          {/* Attachment Upload Simulation */}
          <div className="input-group">
            <label className="input-label">Supporting Document (Optional)</label>
            <div
              style={{
                border: "1px dashed var(--border-color)",
                padding: "0.85rem",
                borderRadius: "var(--radius-md)",
                textAlign: "center",
                cursor: "pointer",
                backgroundColor: "var(--bg-secondary)"
              }}
              onClick={() => document.getElementById("leave-file-input")?.click()}
            >
              <Upload size={18} style={{ color: "var(--brand-primary)", margin: "0 auto 0.25rem auto" }} />
              <div style={{ fontSize: "0.8rem", fontWeight: 600 }}>
                {attachmentName ? `Attached: ${attachmentName}` : "Click to attach doctor's note or tickets"}
              </div>
              <span style={{ fontSize: "0.7rem", color: "var(--text-muted)" }}>PDF, JPG or PNG up to 5MB</span>
              <input
                id="leave-file-input"
                type="file"
                style={{ display: "none" }}
                onChange={handleSimulatedFileUpload}
              />
            </div>
          </div>
        </div>

        <div className="modal-footer">
          <button type="button" onClick={onClose} className="btn btn-secondary">
            Cancel
          </button>
          <button type="submit" className="btn btn-primary">
            Submit Application
          </button>
        </div>
      </form>
    </Modal>
  );
}
