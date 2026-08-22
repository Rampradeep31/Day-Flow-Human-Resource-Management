import React from "react";
import { useHRMS } from "../../context/HRMSContext";
import WorkdayWidget from "./WorkdayWidget";
import MetricCard from "../common/MetricCard";
import StatusBadge from "../common/StatusBadge";
import {
  UserCheck,
  CalendarCheck,
  CalendarDays,
  FileText,
  CreditCard,
  Bell,
  Clock,
  Sparkles,
  ArrowRight,
  TrendingUp,
  Calendar,
  AlertCircle,
  Plus
} from "lucide-react";
import { formatDate } from "../../utils/dateUtils";

export default function EmployeeDashboard() {
  const {
    currentUser,
    setActiveTab,
    setIsApplyLeaveOpen,
    setIsPayslipModalOpen,
    leaveRequests,
    companyAnnouncements,
    companyHolidays
  } = useHRMS();

  // My Leave Requests
  const myLeaves = leaveRequests.filter((l) => l.employeeId === currentUser.id);
  const pendingLeaves = myLeaves.filter((l) => l.status === "Pending");
  const approvedLeaves = myLeaves.filter((l) => l.status === "Approved");

  const balances = currentUser.leaveBalances || {
    paid: { total: 18, used: 4, available: 14 },
    sick: { total: 10, used: 1, available: 9 },
    casual: { total: 6, used: 1, available: 5 },
    unpaid: { total: 0, used: 0, available: 0 }
  };

  return (
    <div className="dashboard-shell" style={{ display: "flex", flexDirection: "column", gap: "1.75rem" }}>
      {/* Welcome Banner */}
      <div
        className="glass-card dashboard-hero employee-hero"
        style={{
          padding: "1.75rem 2rem",
          background: "linear-gradient(135deg, rgba(99, 102, 241, 0.12) 0%, rgba(139, 92, 246, 0.08) 50%, rgba(217, 70, 239, 0.04) 100%)",
          border: "1px solid rgba(99, 102, 241, 0.25)",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "1rem"
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "1.25rem" }}>
          <img
            src={currentUser.avatar}
            alt={currentUser.name}
            style={{ width: "58px", height: "58px", borderRadius: "50%", objectFit: "cover", border: "2px solid var(--brand-primary)" }}
          />
          <div>
            <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
              <h1 style={{ fontSize: "1.5rem", fontWeight: 800, color: "var(--text-primary)" }}>
                Good day, {currentUser.name}! 👋
              </h1>
              <span className="badge badge-role-employee">{currentUser.designation}</span>
            </div>
            <p style={{ fontSize: "0.875rem", color: "var(--text-secondary)", marginTop: "0.2rem" }}>
              Department: <strong>{currentUser.department}</strong> • ID: <span style={{ fontFamily: "var(--font-mono)" }}>{currentUser.id}</span> • {currentUser.workLocation}
            </p>
          </div>
        </div>

        <div style={{ display: "flex", gap: "0.75rem" }}>
          <button onClick={() => setIsApplyLeaveOpen(true)} className="btn btn-primary">
            <Plus size={16} /> Apply for Leave
          </button>
          <button onClick={() => setIsPayslipModalOpen(true)} className="btn btn-secondary">
            <FileText size={16} /> View Payslip
          </button>
        </div>
      </div>

      {/* Quick-Access Section (3.2.1 Requirements) */}
      <div>
        <h2 style={{ fontSize: "1.05rem", fontWeight: 700, marginBottom: "0.85rem", color: "var(--text-primary)" }}>
          Quick-Access Cards
        </h2>
        <div className="quick-access-grid" style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: "1rem" }}>
          {/* Profile Card */}
          <div
            onClick={() => setActiveTab("profile")}
            className="glass-card"
            style={{ padding: "1.2rem", cursor: "pointer", display: "flex", alignItems: "center", gap: "1rem" }}
          >
            <div
              style={{
                width: "42px",
                height: "42px",
                borderRadius: "var(--radius-md)",
                backgroundColor: "var(--brand-primary-light)",
                color: "var(--brand-primary)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                flexShrink: 0
              }}
            >
              <UserCheck size={20} />
            </div>
            <div>
              <h3 style={{ fontSize: "0.9rem", fontWeight: 700 }}>My Profile</h3>
              <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginTop: "2px" }}>Personal, job & docs</p>
            </div>
          </div>

          {/* Attendance Card */}
          <div
            onClick={() => setActiveTab("attendance")}
            className="glass-card"
            style={{ padding: "1.2rem", cursor: "pointer", display: "flex", alignItems: "center", gap: "1rem" }}
          >
            <div
              style={{
                width: "42px",
                height: "42px",
                borderRadius: "var(--radius-md)",
                backgroundColor: "var(--color-success-bg)",
                color: "var(--color-success)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                flexShrink: 0
              }}
            >
              <CalendarCheck size={20} />
            </div>
            <div>
              <h3 style={{ fontSize: "0.9rem", fontWeight: 700 }}>Attendance</h3>
              <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginTop: "2px" }}>Daily & weekly tracker</p>
            </div>
          </div>

          {/* Leave Requests Card */}
          <div
            onClick={() => setActiveTab("leaves")}
            className="glass-card"
            style={{ padding: "1.2rem", cursor: "pointer", display: "flex", alignItems: "center", gap: "1rem" }}
          >
            <div
              style={{
                width: "42px",
                height: "42px",
                borderRadius: "var(--radius-md)",
                backgroundColor: "var(--color-warning-bg)",
                color: "var(--color-warning)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                flexShrink: 0
              }}
            >
              <CalendarDays size={20} />
            </div>
            <div>
              <h3 style={{ fontSize: "0.9rem", fontWeight: 700 }}>Leave Requests</h3>
              <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginTop: "2px" }}>
                {pendingLeaves.length} pending approval
              </p>
            </div>
          </div>

          {/* Payroll Card */}
          <div
            onClick={() => setActiveTab("payroll")}
            className="glass-card"
            style={{ padding: "1.2rem", cursor: "pointer", display: "flex", alignItems: "center", gap: "1rem" }}
          >
            <div
              style={{
                width: "42px",
                height: "42px",
                borderRadius: "var(--radius-md)",
                backgroundColor: "var(--color-purple-bg)",
                color: "var(--color-purple)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                flexShrink: 0
              }}
            >
              <CreditCard size={20} />
            </div>
            <div>
              <h3 style={{ fontSize: "0.9rem", fontWeight: 700 }}>Salary Details</h3>
              <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginTop: "2px" }}>Structure & payslips</p>
            </div>
          </div>
        </div>
      </div>

      {/* Main Grid: Workday Widget + Leave Balances */}
      <div className="dashboard-main-grid employee-main-grid" style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1.5rem" }}>
        {/* Left: Workday Widget */}
        <WorkdayWidget />

        {/* Right: Leave Quota & Balances */}
        <div className="glass-card" style={{ padding: "1.5rem", display: "flex", flexDirection: "column", justifyContent: "space-between" }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1rem" }}>
            <div>
              <h3 style={{ fontSize: "1.05rem", fontWeight: 700 }}>Time-Off Balances</h3>
              <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginTop: "2px" }}>
                Allocated annual leave allowances
              </p>
            </div>
            <button onClick={() => setIsApplyLeaveOpen(true)} className="btn btn-secondary btn-sm">
              <Plus size={14} /> Request Leave
            </button>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.85rem" }}>
            <div style={{ padding: "0.85rem", backgroundColor: "var(--bg-tertiary)", borderRadius: "var(--radius-md)" }}>
              <div style={{ fontSize: "0.75rem", color: "var(--text-muted)", fontWeight: 600 }}>Paid Leave</div>
              <div style={{ fontSize: "1.35rem", fontWeight: 800, color: "var(--brand-primary)", marginTop: "2px" }}>
                {balances.paid.available}{" "}
                <span style={{ fontSize: "0.75rem", fontWeight: 500, color: "var(--text-muted)" }}>/ {balances.paid.total} days</span>
              </div>
            </div>

            <div style={{ padding: "0.85rem", backgroundColor: "var(--bg-tertiary)", borderRadius: "var(--radius-md)" }}>
              <div style={{ fontSize: "0.75rem", color: "var(--text-muted)", fontWeight: 600 }}>Sick Leave</div>
              <div style={{ fontSize: "1.35rem", fontWeight: 800, color: "var(--color-success)", marginTop: "2px" }}>
                {balances.sick.available}{" "}
                <span style={{ fontSize: "0.75rem", fontWeight: 500, color: "var(--text-muted)" }}>/ {balances.sick.total} days</span>
              </div>
            </div>

            <div style={{ padding: "0.85rem", backgroundColor: "var(--bg-tertiary)", borderRadius: "var(--radius-md)" }}>
              <div style={{ fontSize: "0.75rem", color: "var(--text-muted)", fontWeight: 600 }}>Casual Leave</div>
              <div style={{ fontSize: "1.35rem", fontWeight: 800, color: "var(--color-warning)", marginTop: "2px" }}>
                {balances.casual.available}{" "}
                <span style={{ fontSize: "0.75rem", fontWeight: 500, color: "var(--text-muted)" }}>/ {balances.casual.total} days</span>
              </div>
            </div>

            <div style={{ padding: "0.85rem", backgroundColor: "var(--bg-tertiary)", borderRadius: "var(--radius-md)" }}>
              <div style={{ fontSize: "0.75rem", color: "var(--text-muted)", fontWeight: 600 }}>Unpaid Leave</div>
              <div style={{ fontSize: "1.35rem", fontWeight: 800, color: "var(--text-secondary)", marginTop: "2px" }}>
                {balances.unpaid.used} <span style={{ fontSize: "0.75rem", fontWeight: 500, color: "var(--text-muted)" }}>days taken</span>
              </div>
            </div>
          </div>

          <div style={{ marginTop: "1rem", paddingTop: "0.75rem", borderTop: "1px solid var(--border-subtle)", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>
              {pendingLeaves.length} request(s) awaiting approval
            </span>
            <button onClick={() => setActiveTab("leaves")} className="btn-ghost btn-sm" style={{ color: "var(--brand-primary)", padding: "0.2rem 0.5rem" }}>
              View History <ArrowRight size={13} />
            </button>
          </div>
        </div>
      </div>

      {/* Bottom Grid: Recent Activity / Leaves + Announcements & Holidays */}
      <div className="dashboard-main-grid employee-bottom-grid" style={{ display: "grid", gridTemplateColumns: "1.2fr 0.8fr", gap: "1.5rem" }}>
        {/* Recent Leave Requests & Activity */}
        <div className="glass-card" style={{ padding: "1.5rem" }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1.25rem" }}>
            <h3 style={{ fontSize: "1.05rem", fontWeight: 700 }}>My Recent Leave Applications</h3>
            <button onClick={() => setActiveTab("leaves")} className="btn-ghost btn-sm" style={{ color: "var(--brand-primary)" }}>
              See All <ArrowRight size={14} />
            </button>
          </div>

          {myLeaves.length === 0 ? (
            <div style={{ padding: "2rem", textAlign: "center", color: "var(--text-muted)", fontSize: "0.875rem" }}>
              No leave requests applied yet.
            </div>
          ) : (
            <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
              {myLeaves.slice(0, 3).map((leave) => (
                <div
                  key={leave.id}
                  style={{
                    padding: "0.85rem",
                    borderRadius: "var(--radius-md)",
                    backgroundColor: "var(--bg-secondary)",
                    border: "1px solid var(--border-subtle)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between"
                  }}
                >
                  <div style={{ display: "flex", alignItems: "center", gap: "0.85rem" }}>
                    <div
                      style={{
                        width: "36px",
                        height: "36px",
                        borderRadius: "var(--radius-sm)",
                        backgroundColor: "var(--bg-tertiary)",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        color: "var(--brand-primary)"
                      }}
                    >
                      <CalendarDays size={18} />
                    </div>
                    <div>
                      <div style={{ fontSize: "0.875rem", fontWeight: 700, color: "var(--text-primary)" }}>
                        {leave.type} Leave ({leave.daysCount} {leave.daysCount === 1 ? "day" : "days"})
                      </div>
                      <div style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>
                        {formatDate(leave.startDate)} → {formatDate(leave.endDate)}
                      </div>
                    </div>
                  </div>

                  <div style={{ textAlign: "right" }}>
                    <StatusBadge status={leave.status} />
                    {leave.adminComment && (
                      <div style={{ fontSize: "0.7rem", color: "var(--text-muted)", marginTop: "4px", maxWidth: "160px", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                        HR: "{leave.adminComment}"
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Company Announcements & Holidays */}
        <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
          {/* Announcements Card */}
          <div className="glass-card" style={{ padding: "1.25rem" }}>
            <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", marginBottom: "0.85rem" }}>
              <Bell size={16} style={{ color: "var(--brand-primary)" }} />
              <h3 style={{ fontSize: "0.95rem", fontWeight: 700 }}>Company Bulletins</h3>
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: "0.6rem" }}>
              {companyAnnouncements.slice(0, 2).map((ann) => (
                <div key={ann.id} style={{ padding: "0.65rem 0.75rem", borderRadius: "var(--radius-sm)", backgroundColor: "var(--bg-tertiary)" }}>
                  <div style={{ fontSize: "0.825rem", fontWeight: 700, color: "var(--text-primary)" }}>{ann.title}</div>
                  <div style={{ fontSize: "0.725rem", color: "var(--text-secondary)", marginTop: "2px", lineHeight: "1.35" }}>
                    {ann.content}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Upcoming Holidays */}
          <div className="glass-card" style={{ padding: "1.25rem" }}>
            <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", marginBottom: "0.85rem" }}>
              <Calendar size={16} style={{ color: "var(--color-success)" }} />
              <h3 style={{ fontSize: "0.95rem", fontWeight: 700 }}>Upcoming Holidays</h3>
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: "0.5rem" }}>
              {companyHolidays.slice(0, 2).map((h, i) => (
                <div key={i} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", fontSize: "0.8rem" }}>
                  <span style={{ fontWeight: 600, color: "var(--text-primary)" }}>{h.name}</span>
                  <span style={{ color: "var(--text-muted)", fontSize: "0.75rem" }}>{formatDate(h.date)}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
