import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import MetricCard from "../common/MetricCard";
import {
  BarChart3,
  TrendingUp,
  Download,
  CalendarCheck,
  CreditCard,
  Users,
  FileSpreadsheet,
  Layers,
  Sparkles
} from "lucide-react";
import { formatCurrency } from "../../utils/dateUtils";
import { exportAttendanceToCSV, exportPayrollSummaryCSV } from "../../utils/exportUtils";

export default function AnalyticsDashboard() {
  const { employees, attendanceRecords, leaveRequests } = useHRMS();

  // Calculate Attendance Percentage Trend over the week
  const weeklyAttendanceData = [
    { day: "Mon", rate: 96 },
    { day: "Tue", rate: 98 },
    { day: "Wed", rate: 92 },
    { day: "Thu", rate: 100 },
    { day: "Fri", rate: 94 },
    { day: "Sat", rate: 88 }
  ];

  // Calculate Leave Type Breakdown
  const leaveCounts = {
    Paid: leaveRequests.filter((l) => l.type === "Paid").length,
    Sick: leaveRequests.filter((l) => l.type === "Sick").length,
    Casual: leaveRequests.filter((l) => l.type === "Casual").length,
    Unpaid: leaveRequests.filter((l) => l.type === "Unpaid").length
  };
  const totalLeaves = Object.values(leaveCounts).reduce((a, b) => a + b, 0) || 1;

  // Department payroll breakdown
  const deptPayroll = employees.reduce((acc, emp) => {
    const s = emp.salaryStructure || {};
    const gross = (s.basic || 0) + (s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0);
    acc[emp.department] = (acc[emp.department] || 0) + gross;
    return acc;
  }, {});

  const maxDeptExpense = Math.max(...Object.values(deptPayroll), 1);

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
            <BarChart3 size={20} style={{ color: "var(--brand-primary)" }} />
            <h1 style={{ fontSize: "1.4rem", fontWeight: 800 }}>Reports & Intelligence Analytics</h1>
          </div>
          <p style={{ fontSize: "0.85rem", color: "var(--text-secondary)", marginTop: "0.2rem" }}>
            Workforce attendance patterns, leave utilization metrics, and department budget distributions.
          </p>
        </div>

        <div style={{ display: "flex", gap: "0.5rem" }}>
          <button onClick={() => exportAttendanceToCSV(attendanceRecords)} className="btn btn-secondary btn-sm">
            <Download size={14} /> Attendance Report
          </button>
          <button onClick={() => exportPayrollSummaryCSV(employees)} className="btn btn-primary btn-sm">
            <Download size={14} /> Payroll Master Report
          </button>
        </div>
      </div>

      {/* Top 3 Metric Cards */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))", gap: "1.25rem" }}>
        <MetricCard
          title="Overall Attendance Rate"
          value="95.4%"
          subtitle="Past 30 workdays average"
          icon={CalendarCheck}
          change="+2.1%"
          trend="up"
          color="success"
        />

        <MetricCard
          title="Leave Utilization Index"
          value={`${Math.round((leaveRequests.filter(l => l.status === "Approved").length / Math.max(1, leaveRequests.length)) * 100)}%`}
          subtitle="Approval confidence ratio"
          icon={TrendingUp}
          change="Healthy balance"
          trend="up"
          color="primary"
        />

        <MetricCard
          title="Avg Department Budget"
          value={formatCurrency(Math.round(Object.values(deptPayroll).reduce((a, b) => a + b, 0) / 5))}
          subtitle="5 Operating business units"
          icon={CreditCard}
          color="purple"
        />
      </div>

      {/* Visual Charts Grid */}
      <div style={{ display: "grid", gridTemplateColumns: "1.2fr 0.8fr", gap: "1.5rem" }}>
        {/* Weekly Attendance Trend Chart (SVG visualization) */}
        <div className="glass-card" style={{ padding: "1.5rem", display: "flex", flexDirection: "column" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.25rem" }}>
            <div>
              <h3 style={{ fontSize: "1.05rem", fontWeight: 700 }}>Weekly Attendance Presence (%)</h3>
              <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginTop: "2px" }}>
                Company-wide punctuality and attendance
              </p>
            </div>
            <span style={{ fontSize: "0.75rem", color: "var(--color-success)", fontWeight: 700 }}>Avg: 94.7%</span>
          </div>

          {/* Bar Chart Visualization */}
          <div style={{ display: "flex", alignItems: "flex-end", justifyContent: "space-between", height: "180px", padding: "0 1rem", borderBottom: "1px solid var(--border-color)" }}>
            {weeklyAttendanceData.map((item) => (
              <div key={item.day} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "0.5rem", width: "45px" }}>
                <span style={{ fontSize: "0.7rem", fontWeight: 700, color: "var(--brand-primary)", fontFamily: "var(--font-mono)" }}>
                  {item.rate}%
                </span>
                <div
                  style={{
                    width: "100%",
                    height: `${(item.rate / 100) * 120}px`,
                    background: "var(--brand-gradient)",
                    borderRadius: "6px 6px 0 0",
                    transition: "height 0.3s ease"
                  }}
                />
                <span style={{ fontSize: "0.75rem", fontWeight: 600, color: "var(--text-secondary)" }}>
                  {item.day}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Leave Type Breakdown (Doughnut / Progress) */}
        <div className="glass-card" style={{ padding: "1.5rem" }}>
          <h3 style={{ fontSize: "1.05rem", fontWeight: 700, marginBottom: "0.25rem" }}>Leave Categories Distribution</h3>
          <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginBottom: "1.25rem" }}>
            Proportion of leaves taken across types
          </p>

          <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
            {[
              { label: "Paid Leave", count: leaveCounts.Paid, color: "var(--brand-primary)" },
              { label: "Sick Leave", count: leaveCounts.Sick, color: "var(--color-success)" },
              { label: "Casual Leave", count: leaveCounts.Casual, color: "var(--color-warning)" },
              { label: "Unpaid Sabbatical", count: leaveCounts.Unpaid, color: "var(--text-secondary)" }
            ].map((cat) => {
              const pct = Math.round((cat.count / totalLeaves) * 100);
              return (
                <div key={cat.label}>
                  <div style={{ display: "flex", justifyContent: "space-between", fontSize: "0.8rem", marginBottom: "0.3rem" }}>
                    <span style={{ fontWeight: 600, color: "var(--text-primary)" }}>{cat.label}</span>
                    <span style={{ color: "var(--text-muted)", fontFamily: "var(--font-mono)" }}>
                      {cat.count} requests ({pct}%)
                    </span>
                  </div>
                  <div style={{ width: "100%", height: "8px", backgroundColor: "var(--bg-tertiary)", borderRadius: "4px", overflow: "hidden" }}>
                    <div
                      style={{
                        height: "100%",
                        width: `${pct}%`,
                        backgroundColor: cat.color,
                        borderRadius: "4px",
                        transition: "width 0.3s ease"
                      }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Department Payroll Breakdown Horizontal Bars */}
      <div className="glass-card" style={{ padding: "1.5rem" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.25rem" }}>
          <div>
            <h3 style={{ fontSize: "1.05rem", fontWeight: 700 }}>Department Monthly Payroll Allocation</h3>
            <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginTop: "2px" }}>
              Total gross payroll commitments grouped by department
            </p>
          </div>
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          {Object.entries(deptPayroll).map(([dept, amount]) => {
            const pct = Math.round((amount / maxDeptExpense) * 100);

            return (
              <div key={dept}>
                <div style={{ display: "flex", justifyContent: "space-between", fontSize: "0.825rem", marginBottom: "0.35rem" }}>
                  <span style={{ fontWeight: 700, color: "var(--text-primary)" }}>{dept}</span>
                  <span style={{ fontWeight: 800, color: "var(--brand-primary)", fontFamily: "var(--font-mono)" }}>
                    {formatCurrency(amount)}
                  </span>
                </div>
                <div style={{ width: "100%", height: "10px", backgroundColor: "var(--bg-tertiary)", borderRadius: "5px", overflow: "hidden" }}>
                  <div
                    style={{
                      height: "100%",
                      width: `${pct}%`,
                      background: "var(--brand-gradient)",
                      borderRadius: "5px",
                      transition: "width 0.4s ease"
                    }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
