import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import WorkdayWidget from "../dashboard/WorkdayWidget";
import DailyTimeline from "./DailyTimeline";
import WeeklyGrid from "./WeeklyGrid";
import MonthlyCalendar from "./MonthlyCalendar";
import AdminCompanyAttendance from "./AdminCompanyAttendance";
import {
  Clock,
  CalendarDays,
  Calendar,
  Layers,
  Building,
  UserCheck,
  Plus
} from "lucide-react";

export default function AttendanceView() {
  const { currentUser, attendanceRecords } = useHRMS();
  const isAdmin = currentUser.role === "admin";

  const [activeSubView, setActiveSubView] = useState(isAdmin ? "company" : "weekly");

  // Employee only gets their own records (Section 3.4.2 requirement)
  const myRecords = attendanceRecords.filter((r) => r.employeeId === currentUser.id);

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
            <Clock size={20} style={{ color: "var(--brand-primary)" }} />
            <h1 style={{ fontSize: "1.4rem", fontWeight: 800 }}>Attendance & Workday Logs</h1>
          </div>
          <p style={{ fontSize: "0.85rem", color: "var(--text-secondary)", marginTop: "0.2rem" }}>
            {isAdmin
              ? "Monitor company-wide time records, approve overrides, and review punctuality metrics."
              : "Track your daily punches, weekly shifts, and monthly attendance calendar."}
          </p>
        </div>

        {/* Segmented View Switcher */}
        <div
          style={{
            display: "flex",
            backgroundColor: "var(--bg-tertiary)",
            padding: "0.25rem",
            borderRadius: "var(--radius-md)",
            gap: "0.25rem"
          }}
        >
          {isAdmin && (
            <button
              onClick={() => setActiveSubView("company")}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "0.4rem",
                padding: "0.5rem 0.85rem",
                borderRadius: "var(--radius-sm)",
                border: "none",
                fontSize: "0.8rem",
                fontWeight: 700,
                cursor: "pointer",
                backgroundColor: activeSubView === "company" ? "var(--bg-elevated)" : "transparent",
                color: activeSubView === "company" ? "var(--text-primary)" : "var(--text-muted)",
                boxShadow: activeSubView === "company" ? "var(--shadow-sm)" : "none",
                transition: "all var(--transition-fast)"
              }}
            >
              <Building size={14} /> Company Sheet
            </button>
          )}

          <button
            onClick={() => setActiveSubView("daily")}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "0.4rem",
              padding: "0.5rem 0.85rem",
              borderRadius: "var(--radius-sm)",
              border: "none",
              fontSize: "0.8rem",
              fontWeight: 700,
              cursor: "pointer",
              backgroundColor: activeSubView === "daily" ? "var(--bg-elevated)" : "transparent",
              color: activeSubView === "daily" ? "var(--text-primary)" : "var(--text-muted)",
              boxShadow: activeSubView === "daily" ? "var(--shadow-sm)" : "none",
              transition: "all var(--transition-fast)"
            }}
          >
            <Clock size={14} /> Daily Timeline
          </button>

          <button
            onClick={() => setActiveSubView("weekly")}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "0.4rem",
              padding: "0.5rem 0.85rem",
              borderRadius: "var(--radius-sm)",
              border: "none",
              fontSize: "0.8rem",
              fontWeight: 700,
              cursor: "pointer",
              backgroundColor: activeSubView === "weekly" ? "var(--bg-elevated)" : "transparent",
              color: activeSubView === "weekly" ? "var(--text-primary)" : "var(--text-muted)",
              boxShadow: activeSubView === "weekly" ? "var(--shadow-sm)" : "none",
              transition: "all var(--transition-fast)"
            }}
          >
            <Layers size={14} /> Weekly View
          </button>

          <button
            onClick={() => setActiveSubView("monthly")}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "0.4rem",
              padding: "0.5rem 0.85rem",
              borderRadius: "var(--radius-sm)",
              border: "none",
              fontSize: "0.8rem",
              fontWeight: 700,
              cursor: "pointer",
              backgroundColor: activeSubView === "monthly" ? "var(--bg-elevated)" : "transparent",
              color: activeSubView === "monthly" ? "var(--text-primary)" : "var(--text-muted)",
              boxShadow: activeSubView === "monthly" ? "var(--shadow-sm)" : "none",
              transition: "all var(--transition-fast)"
            }}
          >
            <CalendarDays size={14} /> Monthly Calendar
          </button>
        </div>
      </div>

      {/* Top Workday Stopwatch (Always available for employees or admins wanting to punch) */}
      {!isAdmin && <WorkdayWidget />}

      {/* Dynamic Sub-View Render */}
      {activeSubView === "company" && isAdmin && <AdminCompanyAttendance />}
      {activeSubView === "daily" && <DailyTimeline records={isAdmin ? attendanceRecords : myRecords} employeeName={currentUser.name} />}
      {activeSubView === "weekly" && <WeeklyGrid records={isAdmin ? attendanceRecords : myRecords} />}
      {activeSubView === "monthly" && <MonthlyCalendar records={isAdmin ? attendanceRecords : myRecords} />}
    </div>
  );
}
