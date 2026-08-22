import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import StatusBadge from "../common/StatusBadge";
import { formatDate } from "../../utils/dateUtils";
import { Search, Filter, Trash2, FileText, CheckCircle2, XCircle, MessageSquare } from "lucide-react";

export default function EmployeeLeaveHistory({ requests, isEmployeeView = true }) {
  const { cancelLeave } = useHRMS();

  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [typeFilter, setTypeFilter] = useState("all");

  const filtered = requests.filter((req) => {
    const matchesSearch =
      req.reason.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (req.employeeName && req.employeeName.toLowerCase().includes(searchQuery.toLowerCase())) ||
      req.id.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesStatus = statusFilter === "all" || req.status.toLowerCase() === statusFilter.toLowerCase();
    const matchesType = typeFilter === "all" || req.type.toLowerCase() === typeFilter.toLowerCase();

    return matchesSearch && matchesStatus && matchesType;
  });

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
      {/* Search & Filter Bar */}
      <div
        className="glass-card"
        style={{
          padding: "1rem 1.5rem",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "0.75rem"
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "0.75rem", flexWrap: "wrap" }}>
          <div style={{ position: "relative", minWidth: "220px" }}>
            <input
              type="text"
              placeholder="Search reason or ref ID..."
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
            <option value="all">All Statuses</option>
            <option value="pending">Pending</option>
            <option value="approved">Approved</option>
            <option value="rejected">Rejected</option>
          </select>

          <select
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
            className="input-control"
            style={{ width: "auto" }}
          >
            <option value="all">All Leave Types</option>
            <option value="paid">Paid</option>
            <option value="sick">Sick</option>
            <option value="casual">Casual</option>
            <option value="unpaid">Unpaid</option>
          </select>
        </div>

        <span style={{ fontSize: "0.8rem", color: "var(--text-muted)", fontWeight: 600 }}>
          Showing {filtered.length} requests
        </span>
      </div>

      {/* Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              {!isEmployeeView && <th>Employee</th>}
              <th>Ref ID</th>
              <th>Type</th>
              <th>Dates & Duration</th>
              <th>Reason / Purpose</th>
              <th>Applied On</th>
              <th>Status</th>
              <th>HR Feedback / Notes</th>
              {isEmployeeView && <th style={{ textAlign: "right" }}>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan={isEmployeeView ? 8 : 8} style={{ textAlign: "center", padding: "2.5rem", color: "var(--text-muted)" }}>
                  No leave requests found matching filters.
                </td>
              </tr>
            ) : (
              filtered.map((req) => (
                <tr key={req.id}>
                  {!isEmployeeView && (
                    <td>
                      <div style={{ display: "flex", alignItems: "center", gap: "0.6rem" }}>
                        <img
                          src={req.employeeAvatar}
                          alt=""
                          style={{ width: "28px", height: "28px", borderRadius: "50%", objectFit: "cover" }}
                        />
                        <span style={{ fontWeight: 700 }}>{req.employeeName}</span>
                      </div>
                    </td>
                  )}
                  <td>
                    <span style={{ fontFamily: "var(--font-mono)", fontSize: "0.8rem", fontWeight: 700, color: "var(--brand-primary)" }}>
                      {req.id}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontWeight: 600, fontSize: "0.85rem" }}>{req.type} Leave</span>
                    {req.halfDay && (
                      <span style={{ display: "block", fontSize: "0.7rem", color: "var(--color-warning)" }}>
                        {req.halfDay}
                      </span>
                    )}
                  </td>
                  <td>
                    <div style={{ fontWeight: 700, color: "var(--text-primary)" }}>
                      {formatDate(req.startDate)} {req.startDate !== req.endDate ? `→ ${formatDate(req.endDate)}` : ""}
                    </div>
                    <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>
                      {req.daysCount} working {req.daysCount === 1 ? "day" : "days"}
                    </span>
                  </td>
                  <td style={{ maxWidth: "260px" }}>
                    <div style={{ fontSize: "0.825rem", color: "var(--text-secondary)" }}>{req.reason}</div>
                    {req.attachment && (
                      <div style={{ display: "flex", alignItems: "center", gap: "0.3rem", fontSize: "0.7rem", color: "var(--brand-primary)", marginTop: "2px" }}>
                        <FileText size={12} /> {req.attachment}
                      </div>
                    )}
                  </td>
                  <td>
                    <span style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}>{formatDate(req.appliedOn)}</span>
                  </td>
                  <td>
                    <StatusBadge status={req.status} />
                  </td>
                  <td>
                    {req.adminComment ? (
                      <div style={{ fontSize: "0.775rem", color: "var(--text-secondary)" }}>
                        <strong>{req.reviewedBy || "HR"}:</strong> "{req.adminComment}"
                      </div>
                    ) : (
                      <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>—</span>
                    )}
                  </td>
                  {isEmployeeView && (
                    <td style={{ textAlign: "right" }}>
                      {req.status === "Pending" && (
                        <button
                          onClick={() => cancelLeave(req.id)}
                          className="btn-ghost btn-sm"
                          style={{ color: "var(--color-danger)", padding: "0.25rem 0.5rem" }}
                          title="Withdraw / Cancel request"
                        >
                          <Trash2 size={14} /> Cancel
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
