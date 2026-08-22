import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import StatusBadge from "../common/StatusBadge";
import Modal from "../common/Modal";
import {
  CheckSquare,
  CheckCircle2,
  XCircle,
  Search,
  Filter,
  Calendar,
  MessageSquare,
  FileText,
  Building,
  User
} from "lucide-react";
import { formatDate } from "../../utils/dateUtils";

export default function AdminLeaveApprovalQueue() {
  const { leaveRequests, approveLeave, rejectLeave, employees } = useHRMS();

  const [statusFilter, setStatusFilter] = useState("all");
  const [departmentFilter, setDepartmentFilter] = useState("all");
  const [searchQuery, setSearchQuery] = useState("");

  // Review modal state
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [actionType, setActionType] = useState("approve"); // "approve" | "reject"
  const [hrRemarks, setHrRemarks] = useState("");

  const departments = ["Engineering", "Product Design", "Marketing", "Human Resources", "Finance"];

  const filtered = leaveRequests.filter((req) => {
    const matchesSearch =
      req.employeeName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      req.reason.toLowerCase().includes(searchQuery.toLowerCase()) ||
      req.id.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesStatus = statusFilter === "all" || req.status.toLowerCase() === statusFilter.toLowerCase();
    const matchesDept = departmentFilter === "all" || req.department === departmentFilter;

    return matchesSearch && matchesStatus && matchesDept;
  });

  const handleOpenAction = (req, type) => {
    setSelectedRequest(req);
    setActionType(type);
    setHrRemarks(type === "approve" ? "Approved by HR Operations." : "Declined due to scheduling constraints.");
  };

  const handleConfirmAction = (e) => {
    e.preventDefault();
    if (!selectedRequest) return;
    if (actionType === "approve") {
      approveLeave(selectedRequest.id, hrRemarks);
    } else {
      rejectLeave(selectedRequest.id, hrRemarks);
    }
    setSelectedRequest(null);
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
      {/* Action Header */}
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
          <div style={{ position: "relative", minWidth: "220px" }}>
            <input
              type="text"
              placeholder="Search employee or request..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="input-control"
              style={{ paddingLeft: "2.2rem" }}
            />
            <Search size={15} style={{ position: "absolute", left: "0.75rem", top: "50%", transform: "translateY(-50%)", color: "var(--text-muted)" }} />
          </div>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="input-control"
            style={{ width: "auto" }}
          >
            <option value="all">All Approval Statuses</option>
            <option value="pending">Pending Review Only</option>
            <option value="approved">Approved Requests</option>
            <option value="rejected">Rejected Requests</option>
          </select>

          <select
            value={departmentFilter}
            onChange={(e) => setDepartmentFilter(e.target.value)}
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

        <span style={{ fontSize: "0.8rem", color: "var(--text-muted)", fontWeight: 600 }}>
          {filtered.length} Requests Found
        </span>
      </div>

      {/* Requests Grid / Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Employee Details</th>
              <th>Department</th>
              <th>Leave Type</th>
              <th>Requested Dates</th>
              <th>Duration</th>
              <th>Reason & Supporting Docs</th>
              <th>Status</th>
              <th>HR Audit Log</th>
              <th style={{ textAlign: "right" }}>Decision Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan={9} style={{ textAlign: "center", padding: "3rem", color: "var(--text-muted)" }}>
                  No leave requests found matching selected filters.
                </td>
              </tr>
            ) : (
              filtered.map((req) => (
                <tr key={req.id}>
                  <td>
                    <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
                      <img
                        src={req.employeeAvatar}
                        alt=""
                        style={{ width: "32px", height: "32px", borderRadius: "50%", objectFit: "cover" }}
                      />
                      <div>
                        <div style={{ fontWeight: 700, color: "var(--text-primary)" }}>{req.employeeName}</div>
                        <div style={{ fontSize: "0.7rem", color: "var(--text-muted)", fontFamily: "var(--font-mono)" }}>
                          {req.id} • {req.employeeId}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span style={{ fontSize: "0.825rem", color: "var(--text-secondary)" }}>{req.department}</span>
                  </td>
                  <td>
                    <span style={{ fontWeight: 700 }}>{req.type} Leave</span>
                    {req.halfDay && (
                      <span style={{ display: "block", fontSize: "0.7rem", color: "var(--color-warning)" }}>
                        {req.halfDay}
                      </span>
                    )}
                  </td>
                  <td>
                    <div style={{ fontWeight: 600, fontSize: "0.825rem" }}>
                      {formatDate(req.startDate)} {req.startDate !== req.endDate ? `→ ${formatDate(req.endDate)}` : ""}
                    </div>
                    <span style={{ fontSize: "0.7rem", color: "var(--text-muted)" }}>
                      Applied {formatDate(req.appliedOn)}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontWeight: 700, color: "var(--brand-primary)" }}>
                      {req.daysCount} {req.daysCount === 1 ? "day" : "days"}
                    </span>
                  </td>
                  <td style={{ maxWidth: "240px" }}>
                    <div style={{ fontSize: "0.8rem", color: "var(--text-secondary)" }}>{req.reason}</div>
                    {req.attachment && (
                      <div style={{ display: "flex", alignItems: "center", gap: "0.3rem", fontSize: "0.7rem", color: "var(--brand-primary)", marginTop: "2px" }}>
                        <FileText size={12} /> {req.attachment}
                      </div>
                    )}
                  </td>
                  <td>
                    <StatusBadge status={req.status} />
                  </td>
                  <td>
                    {req.adminComment ? (
                      <div style={{ fontSize: "0.75rem", color: "var(--text-secondary)", maxWidth: "180px" }}>
                        <strong>{req.reviewedBy}:</strong> "{req.adminComment}"
                      </div>
                    ) : (
                      <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Awaiting Review</span>
                    )}
                  </td>
                  <td style={{ textAlign: "right" }}>
                    {req.status === "Pending" ? (
                      <div style={{ display: "flex", justifyContent: "flex-end", gap: "0.4rem" }}>
                        <button
                          onClick={() => handleOpenAction(req, "reject")}
                          className="btn btn-danger btn-sm"
                          title="Decline request"
                        >
                          <XCircle size={14} /> Reject
                        </button>
                        <button
                          onClick={() => handleOpenAction(req, "approve")}
                          className="btn btn-success btn-sm"
                          title="Approve request"
                        >
                          <CheckCircle2 size={14} /> Approve
                        </button>
                      </div>
                    ) : (
                      <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", fontStyle: "italic" }}>
                        Decision Finalized
                      </span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Decision Review Remarks Modal */}
      {selectedRequest && (
        <Modal
          isOpen={true}
          onClose={() => setSelectedRequest(null)}
          title={`${actionType === "approve" ? "Approve" : "Reject"} Leave (${selectedRequest.id})`}
        >
          <form onSubmit={handleConfirmAction}>
            <div className="modal-body">
              <div style={{ padding: "0.85rem", backgroundColor: "var(--bg-tertiary)", borderRadius: "var(--radius-md)", marginBottom: "1rem" }}>
                <div style={{ fontWeight: 700 }}>{selectedRequest.employeeName} ({selectedRequest.department})</div>
                <div style={{ fontSize: "0.8rem", color: "var(--text-secondary)", marginTop: "2px" }}>
                  {selectedRequest.type} Leave ({selectedRequest.daysCount} days): {formatDate(selectedRequest.startDate)} to {formatDate(selectedRequest.endDate)}
                </div>
                <div style={{ fontSize: "0.775rem", color: "var(--text-muted)", marginTop: "4px" }}>
                  Reason: "{selectedRequest.reason}"
                </div>
              </div>

              <div className="input-group">
                <label className="input-label">HR Feedback / Remarks</label>
                <textarea
                  required
                  rows={3}
                  value={hrRemarks}
                  onChange={(e) => setHrRemarks(e.target.value)}
                  className="input-control"
                  placeholder="Enter remarks for the applicant..."
                />
              </div>
            </div>

            <div className="modal-footer">
              <button type="button" onClick={() => setSelectedRequest(null)} className="btn btn-secondary">
                Cancel
              </button>
              <button
                type="submit"
                className={`btn ${actionType === "approve" ? "btn-success" : "btn-danger"}`}
              >
                {actionType === "approve" ? "Confirm & Approve" : "Confirm & Reject"}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
