import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import LeaveBalancesView from "./LeaveBalancesView";
import EmployeeLeaveHistory from "./EmployeeLeaveHistory";
import AdminLeaveApprovalQueue from "./AdminLeaveApprovalQueue";
import ApplyLeaveModal from "./ApplyLeaveModal";
import { CalendarDays, Plus, CheckSquare, History, Sparkles } from "lucide-react";

export default function LeaveMasterView() {
  const { currentUser, leaveRequests, isApplyLeaveOpen, setIsApplyLeaveOpen } = useHRMS();
  const isAdmin = currentUser.role === "admin";

  const [activeTab, setActiveTab] = useState(isAdmin ? "approvals" : "history");

  const myLeaves = leaveRequests.filter((l) => l.employeeId === currentUser.id);

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
      {/* Header Banner */}
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
            <CalendarDays size={20} style={{ color: "var(--brand-primary)" }} />
            <h1 style={{ fontSize: "1.4rem", fontWeight: 800 }}>Leave & Time-Off Management</h1>
          </div>
          <p style={{ fontSize: "0.85rem", color: "var(--text-secondary)", marginTop: "0.2rem" }}>
            {isAdmin
              ? "Review time-off requests, authorize leaves, and ensure team resource coverage."
              : "Check your available leave balances, apply for time off, and track approval status."}
          </p>
        </div>

        <div style={{ display: "flex", gap: "0.75rem" }}>
          <button onClick={() => setIsApplyLeaveOpen(true)} className="btn btn-primary">
            <Plus size={16} /> Apply for Leave
          </button>
        </div>
      </div>

      {/* Leave Balances Quota Progress */}
      <LeaveBalancesView employee={currentUser} />

      {/* Tabs Switcher for Admin (Approvals vs My Own Leaves) */}
      {isAdmin && (
        <div style={{ display: "flex", gap: "0.5rem", borderBottom: "1px solid var(--border-color)", paddingBottom: "0.5rem" }}>
          <button
            onClick={() => setActiveTab("approvals")}
            className={`btn btn-sm ${activeTab === "approvals" ? "btn-primary" : "btn-secondary"}`}
          >
            <CheckSquare size={14} /> Company Approval Queue ({leaveRequests.filter((l) => l.status === "Pending").length} pending)
          </button>
          <button
            onClick={() => setActiveTab("history")}
            className={`btn btn-sm ${activeTab === "history" ? "btn-primary" : "btn-secondary"}`}
          >
            <History size={14} /> My Personal Leave Applications
          </button>
        </div>
      )}

      {/* Render View */}
      {isAdmin && activeTab === "approvals" ? (
        <AdminLeaveApprovalQueue />
      ) : (
        <EmployeeLeaveHistory requests={myLeaves} isEmployeeView={true} />
      )}

      {/* Apply Leave Modal */}
      {isApplyLeaveOpen && (
        <ApplyLeaveModal
          isOpen={isApplyLeaveOpen}
          onClose={() => setIsApplyLeaveOpen(false)}
        />
      )}
    </div>
  );
}
