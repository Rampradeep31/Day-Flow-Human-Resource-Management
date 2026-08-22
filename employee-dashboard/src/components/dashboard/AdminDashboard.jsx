import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import MetricCard from "../common/MetricCard";
import StatusBadge from "../common/StatusBadge";
import Modal from "../common/Modal";
import {
  Users,
  CalendarCheck,
  CalendarDays,
  CreditCard,
  UserCheck,
  CheckCircle2,
  XCircle,
  Plus,
  ArrowRight,
  ShieldCheck,
  Search,
  Filter,
  TrendingUp,
  MessageSquare,
  Clock,
  Sparkles
} from "lucide-react";
import { formatDate, formatCurrency } from "../../utils/dateUtils";

export default function AdminDashboard() {
  const {
    employees,
    attendanceRecords,
    leaveRequests,
    approveLeave,
    rejectLeave,
    setActiveTab,
    setSelectedEmployeeId,
    switchUser,
    setIsAddEmployeeOpen,
    setIsPayslipModalOpen
  } = useHRMS();

  // Review modal state for leave approval / rejection
  const [reviewLeaveItem, setReviewLeaveItem] = useState(null);
  const [reviewAction, setReviewAction] = useState("approve"); // "approve" | "reject"
  const [hrRemarks, setHrRemarks] = useState("");

  // Search filter for employee quick switch list
  const [employeeSearch, setEmployeeSearch] = useState("");

  const pendingLeaves = leaveRequests.filter((l) => l.status === "Pending");
  const todayStr = new Date().toISOString().split("T")[0];
  const todayAttendance = attendanceRecords.filter((r) => r.date === todayStr);

  const presentCount = todayAttendance.filter((r) => r.status === "Present").length;
  const presentRate = Math.round((presentCount / Math.max(1, employees.length)) * 100);

  // Total payroll expense calculation
  const totalPayrollMonthly = employees.reduce((sum, emp) => {
    const s = emp.salaryStructure || {};
    return sum + (s.basic || 0) + (s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0);
  }, 0);

  const filteredEmployees = employees.filter(
    (e) =>
      e.name.toLowerCase().includes(employeeSearch.toLowerCase()) ||
      e.department.toLowerCase().includes(employeeSearch.toLowerCase()) ||
      e.designation.toLowerCase().includes(employeeSearch.toLowerCase())
  );

  const handleOpenReview = (leave, action) => {
    setReviewLeaveItem(leave);
    setReviewAction(action);
    setHrRemarks(action === "approve" ? "Approved by HR Operations." : "Declined due to scheduling conflicts.");
  };

  const handleConfirmReview = (e) => {
    e.preventDefault();
    if (!reviewLeaveItem) return;
    if (reviewAction === "approve") {
      approveLeave(reviewLeaveItem.id, hrRemarks);
    } else {
      rejectLeave(reviewLeaveItem.id, hrRemarks);
    }
    setReviewLeaveItem(null);
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.75rem" }}>
      {/* Top Admin Welcome & Header */}
      <div
        className="glass-card"
        style={{
          padding: "1.75rem 2rem",
          background: "linear-gradient(135deg, rgba(99, 102, 241, 0.12) 0%, rgba(139, 92, 246, 0.08) 50%, rgba(217, 70, 239, 0.04) 100%)",
          border: "1px solid rgba(99, 102, 241, 0.3)",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "1rem"
        }}
      >
        <div>
          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
            <span
              style={{
                display: "inline-flex",
                alignItems: "center",
                gap: "0.3rem",
                padding: "0.25rem 0.6rem",
                borderRadius: "var(--radius-full)",
                backgroundColor: "var(--brand-primary-light)",
                color: "var(--brand-primary)",
                fontWeight: 700,
                fontSize: "0.75rem"
              }}
            >
              <ShieldCheck size={14} /> HR Operations Control Center
            </span>
          </div>
          <h1 style={{ fontSize: "1.6rem", fontWeight: 800, marginTop: "0.4rem", color: "var(--text-primary)" }}>
            Workforce Overview & Approvals
          </h1>
          <p style={{ fontSize: "0.875rem", color: "var(--text-secondary)", marginTop: "0.2rem" }}>
            Manage {employees.length} active employees across 5 departments. {pendingLeaves.length} leave requests awaiting decision.
          </p>
        </div>

        <div style={{ display: "flex", gap: "0.75rem" }}>
          <button onClick={() => setIsAddEmployeeOpen(true)} className="btn btn-primary">
            <Plus size={16} /> Onboard New Employee
          </button>
          <button onClick={() => setActiveTab("analytics")} className="btn btn-secondary">
            <TrendingUp size={16} /> Analytics & Reports
          </button>
        </div>
      </div>

      {/* Top 4 KPI Cards */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(230px, 1fr))", gap: "1.25rem" }}>
        <MetricCard
          title="Total Workforce"
          value={employees.length}
          subtitle="100% Active Contracts"
          icon={Users}
          change="+1 this month"
          trend="up"
          color="primary"
        />

        <MetricCard
          title="Present Today"
          value={`${presentRate}%`}
          subtitle={`${presentCount} of ${employees.length} staff checked in`}
          icon={CalendarCheck}
          change="Normal attendance"
          trend="up"
          color="success"
        />

        <MetricCard
          title="Pending Approvals"
          value={pendingLeaves.length}
          subtitle="Action required by HR"
          icon={CalendarDays}
          change={pendingLeaves.length > 0 ? "Requires review" : "Queue clear"}
          trend={pendingLeaves.length > 0 ? "down" : "up"}
          color="warning"
        />

        <MetricCard
          title="Monthly Payroll Expense"
          value={formatCurrency(totalPayrollMonthly)}
          subtitle="Processed with zero variance"
          icon={CreditCard}
          change="Fidelity 100%"
          trend="up"
          color="purple"
        />
      </div>

      {/* Main Grid: Pending Approvals Queue & Switch Between Employees */}
      <div style={{ display: "grid", gridTemplateColumns: "1.3fr 0.9fr", gap: "1.5rem" }}>
        {/* Left: Pending Leave Approvals (Section 3.2.2 & 3.5.2) */}
        <div className="glass-card" style={{ padding: "1.5rem", display: "flex", flexDirection: "column" }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1.25rem" }}>
            <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
              <CalendarDays size={18} style={{ color: "var(--brand-primary)" }} />
              <h3 style={{ fontSize: "1.1rem", fontWeight: 700 }}>Leave Approval Queue</h3>
              {pendingLeaves.length > 0 && (
                <span
                  style={{
                    fontSize: "0.75rem",
                    fontWeight: 700,
                    padding: "0.15rem 0.55rem",
                    borderRadius: "var(--radius-full)",
                    backgroundColor: "var(--color-warning)",
                    color: "#ffffff"
                  }}
                >
                  {pendingLeaves.length} Pending
                </span>
              )}
            </div>
            <button onClick={() => setActiveTab("approvals")} className="btn-ghost btn-sm" style={{ color: "var(--brand-primary)" }}>
              View All Approvals <ArrowRight size={14} />
            </button>
          </div>

          {pendingLeaves.length === 0 ? (
            <div style={{ padding: "3rem 1rem", textAlign: "center", color: "var(--text-muted)", margin: "auto" }}>
              <CheckCircle2 size={40} style={{ margin: "0 auto 0.75rem auto", color: "var(--color-success)", opacity: 0.8 }} />
              <h4 style={{ fontWeight: 700, color: "var(--text-primary)" }}>All Caught Up!</h4>
              <p style={{ fontSize: "0.825rem", marginTop: "0.25rem" }}>No pending leave requests in the queue right now.</p>
            </div>
          ) : (
            <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
              {pendingLeaves.map((req) => (
                <div
                  key={req.id}
                  style={{
                    padding: "1rem",
                    borderRadius: "var(--radius-md)",
                    backgroundColor: "var(--bg-secondary)",
                    border: "1px solid var(--border-color)",
                    display: "flex",
                    flexDirection: "column",
                    gap: "0.75rem"
                  }}
                >
                  <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
                      <img
                        src={req.employeeAvatar}
                        alt={req.employeeName}
                        style={{ width: "38px", height: "38px", borderRadius: "50%", objectFit: "cover" }}
                      />
                      <div>
                        <div style={{ fontWeight: 700, fontSize: "0.9rem", color: "var(--text-primary)" }}>
                          {req.employeeName}
                        </div>
                        <div style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>
                          {req.department} • Applied on {formatDate(req.appliedOn)}
                        </div>
                      </div>
                    </div>

                    <StatusBadge status={req.type} />
                  </div>

                  <div
                    style={{
                      padding: "0.6rem 0.85rem",
                      borderRadius: "var(--radius-sm)",
                      backgroundColor: "var(--bg-tertiary)",
                      fontSize: "0.8rem",
                      color: "var(--text-secondary)",
                      display: "flex",
                      flexDirection: "column",
                      gap: "0.25rem"
                    }}
                  >
                    <div>
                      <strong>Dates:</strong> {formatDate(req.startDate)} → {formatDate(req.endDate)} ({req.daysCount} days)
                    </div>
                    <div>
                      <strong>Reason:</strong> "{req.reason}"
                    </div>
                  </div>

                  <div style={{ display: "flex", justifyContent: "flex-end", gap: "0.5rem" }}>
                    <button
                      onClick={() => handleOpenReview(req, "reject")}
                      className="btn btn-danger btn-sm"
                    >
                      <XCircle size={14} /> Reject
                    </button>
                    <button
                      onClick={() => handleOpenReview(req, "approve")}
                      className="btn btn-success btn-sm"
                    >
                      <CheckCircle2 size={14} /> Approve Request
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Right: Employee Switcher & Roster Shortcut (Section 3.2.2 Requirement) */}
        <div className="glass-card" style={{ padding: "1.5rem", display: "flex", flexDirection: "column" }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1rem" }}>
            <div>
              <h3 style={{ fontSize: "1.1rem", fontWeight: 700 }}>Employee Switcher</h3>
              <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginTop: "2px" }}>
                Switch view to inspect any employee profile
              </p>
            </div>
            <button onClick={() => setActiveTab("directory")} className="btn-ghost btn-sm" style={{ color: "var(--brand-primary)" }}>
              Directory <ArrowRight size={14} />
            </button>
          </div>

          {/* Quick Search */}
          <div style={{ position: "relative", marginBottom: "1rem" }}>
            <input
              type="text"
              placeholder="Filter employee by name or dept..."
              value={employeeSearch}
              onChange={(e) => setEmployeeSearch(e.target.value)}
              className="input-control"
              style={{ paddingLeft: "2.2rem", fontSize: "0.825rem" }}
            />
            <Search size={15} style={{ position: "absolute", left: "0.75rem", top: "50%", transform: "translateY(-50%)", color: "var(--text-muted)" }} />
          </div>

          <div style={{ flex: 1, overflowY: "auto", display: "flex", flexDirection: "column", gap: "0.5rem", maxHeight: "380px" }}>
            {filteredEmployees.map((emp) => (
              <div
                key={emp.id}
                style={{
                  padding: "0.65rem 0.85rem",
                  borderRadius: "var(--radius-md)",
                  backgroundColor: "var(--bg-secondary)",
                  border: "1px solid var(--border-subtle)",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  transition: "all var(--transition-fast)"
                }}
              >
                <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
                  <img
                    src={emp.avatar}
                    alt={emp.name}
                    style={{ width: "32px", height: "32px", borderRadius: "50%", objectFit: "cover" }}
                  />
                  <div>
                    <div style={{ fontSize: "0.85rem", fontWeight: 700, color: "var(--text-primary)" }}>
                      {emp.name}
                    </div>
                    <div style={{ fontSize: "0.725rem", color: "var(--text-muted)" }}>
                      {emp.designation} • {emp.department}
                    </div>
                  </div>
                </div>

                <div style={{ display: "flex", gap: "0.35rem" }}>
                  <button
                    onClick={() => {
                      setSelectedEmployeeId(emp.id);
                      setActiveTab("profile");
                    }}
                    className="btn btn-secondary btn-sm"
                    style={{ padding: "0.25rem 0.5rem", fontSize: "0.725rem" }}
                    title="Inspect Profile"
                  >
                    Profile
                  </button>
                  <button
                    onClick={() => switchUser(emp.id)}
                    className="btn btn-primary btn-sm"
                    style={{ padding: "0.25rem 0.5rem", fontSize: "0.725rem" }}
                    title="Impersonate / Act as user"
                  >
                    Act As
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Review Remarks Modal */}
      {reviewLeaveItem && (
        <Modal
          isOpen={true}
          onClose={() => setReviewLeaveItem(null)}
          title={`${reviewAction === "approve" ? "Approve" : "Reject"} Leave Request (${reviewLeaveItem.id})`}
        >
          <form onSubmit={handleConfirmReview}>
            <div className="modal-body">
              <div style={{ padding: "0.85rem", backgroundColor: "var(--bg-tertiary)", borderRadius: "var(--radius-md)", marginBottom: "1rem" }}>
                <div style={{ fontSize: "0.875rem", fontWeight: 700 }}>{reviewLeaveItem.employeeName}</div>
                <div style={{ fontSize: "0.8rem", color: "var(--text-secondary)", marginTop: "2px" }}>
                  {reviewLeaveItem.type} Leave ({reviewLeaveItem.daysCount} days): {formatDate(reviewLeaveItem.startDate)} to {formatDate(reviewLeaveItem.endDate)}
                </div>
                <div style={{ fontSize: "0.775rem", color: "var(--text-muted)", marginTop: "4px" }}>
                  Reason: "{reviewLeaveItem.reason}"
                </div>
              </div>

              <div className="input-group">
                <label className="input-label">HR Remarks / Feedback Note</label>
                <textarea
                  required
                  rows={3}
                  value={hrRemarks}
                  onChange={(e) => setHrRemarks(e.target.value)}
                  className="input-control"
                  placeholder="Enter comments that will be recorded and notified to the employee..."
                />
              </div>
            </div>

            <div className="modal-footer">
              <button type="button" onClick={() => setReviewLeaveItem(null)} className="btn btn-secondary">
                Cancel
              </button>
              <button
                type="submit"
                className={`btn ${reviewAction === "approve" ? "btn-success" : "btn-danger"}`}
              >
                {reviewAction === "approve" ? "Confirm Approval" : "Confirm Rejection"}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
