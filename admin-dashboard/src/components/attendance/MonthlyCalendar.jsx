import React, { useState } from "react";
import StatusBadge from "../common/StatusBadge";
import { getMonthDays, formatDate } from "../../utils/dateUtils";
import { ChevronLeft, ChevronRight, Calendar as CalendarIcon, Clock, CheckCircle } from "lucide-react";

export default function MonthlyCalendar({ records }) {
  const [currentYear] = useState(2026);
  const [currentMonth] = useState(7); // August (0-indexed: 7)
  const [selectedDayRecord, setSelectedDayRecord] = useState(null);

  const days = getMonthDays(currentYear, currentMonth);

  const weekHeaders = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

  const handleDayClick = (dayObj) => {
    if (!dayObj.isCurrentMonth) return;
    const rec = records.find((r) => r.date === dayObj.dateString);
    setSelectedDayRecord({
      date: dayObj.dateString,
      record: rec,
      isWeekend: dayObj.isWeekend
    });
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
      {/* Calendar Header */}
      <div
        className="glass-card"
        style={{
          padding: "1.25rem 1.5rem",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between"
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
          <CalendarIcon size={20} style={{ color: "var(--brand-primary)" }} />
          <div>
            <h3 style={{ fontSize: "1.1rem", fontWeight: 800 }}>August 2026</h3>
            <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Monthly Attendance Record Sheet</span>
          </div>
        </div>

        {/* Legend */}
        <div style={{ display: "flex", alignItems: "center", gap: "0.6rem", flexWrap: "wrap" }}>
          <StatusBadge status="Present" />
          <StatusBadge status="Half-day" />
          <StatusBadge status="Absent" />
          <StatusBadge status="Leave" />
          <StatusBadge status="Holiday" />
        </div>
      </div>

      {/* Main Month Grid */}
      <div className="glass-card" style={{ padding: "1.25rem" }}>
        {/* Day of Week Headers */}
        <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: "6px", marginBottom: "6px" }}>
          {weekHeaders.map((header) => (
            <div
              key={header}
              style={{
                textAlign: "center",
                padding: "0.5rem 0",
                fontSize: "0.75rem",
                fontWeight: 700,
                color: "var(--text-secondary)",
                textTransform: "uppercase"
              }}
            >
              {header}
            </div>
          ))}
        </div>

        {/* Calendar Cells */}
        <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: "6px" }}>
          {days.map((dayObj, index) => {
            if (!dayObj.isCurrentMonth) {
              return (
                <div
                  key={index}
                  style={{
                    height: "85px",
                    backgroundColor: "var(--bg-tertiary)",
                    opacity: 0.3,
                    borderRadius: "var(--radius-sm)"
                  }}
                />
              );
            }

            const rec = records.find((r) => r.date === dayObj.dateString);
            const status = rec ? rec.status : dayObj.isWeekend ? "Holiday" : dayObj.day > 22 ? null : "Absent";
            const isToday = dayObj.dateString === "2026-08-22";

            return (
              <div
                key={dayObj.dateString}
                onClick={() => handleDayClick(dayObj)}
                style={{
                  minHeight: "85px",
                  padding: "0.45rem",
                  borderRadius: "var(--radius-sm)",
                  backgroundColor: isToday ? "rgba(99, 102, 241, 0.08)" : "var(--bg-secondary)",
                  border: `1px solid ${isToday ? "var(--brand-primary)" : "var(--border-subtle)"}`,
                  cursor: "pointer",
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "space-between",
                  transition: "all var(--transition-fast)"
                }}
                onMouseEnter={(e) => (e.currentTarget.style.borderColor = "var(--brand-primary)")}
                onMouseLeave={(e) =>
                  (e.currentTarget.style.borderColor = isToday ? "var(--brand-primary)" : "var(--border-subtle)")
                }
              >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <span
                    style={{
                      fontSize: "0.8rem",
                      fontWeight: isToday ? 800 : 600,
                      color: isToday ? "var(--brand-primary)" : "var(--text-primary)"
                    }}
                  >
                    {dayObj.day}
                  </span>
                  {isToday && (
                    <span
                      style={{
                        fontSize: "0.6rem",
                        padding: "0.05rem 0.35rem",
                        borderRadius: "var(--radius-full)",
                        backgroundColor: "var(--brand-primary)",
                        color: "#fff",
                        fontWeight: 700
                      }}
                    >
                      Today
                    </span>
                  )}
                </div>

                {status ? (
                  <div>
                    <StatusBadge status={status} />
                    {rec?.workHours && (
                      <div style={{ fontSize: "0.65rem", color: "var(--text-muted)", marginTop: "2px", fontFamily: "var(--font-mono)" }}>
                        {rec.workHours}
                      </div>
                    )}
                  </div>
                ) : (
                  <span style={{ fontSize: "0.65rem", color: "var(--text-muted)" }}>Upcoming</span>
                )}
              </div>
            );
          })}
        </div>
      </div>

      {/* Selected Day Detail Drawer / Card */}
      {selectedDayRecord && (
        <div className="glass-card" style={{ padding: "1.25rem 1.5rem", borderLeft: "4px solid var(--brand-primary)" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "0.5rem" }}>
            <h4 style={{ fontSize: "1rem", fontWeight: 700 }}>
              Attendance Details for {formatDate(selectedDayRecord.date)}
            </h4>
            <button onClick={() => setSelectedDayRecord(null)} className="btn-ghost btn-sm">
              Close
            </button>
          </div>

          {selectedDayRecord.record ? (
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))", gap: "1rem", marginTop: "0.75rem" }}>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Status</span>
                <div>
                  <StatusBadge status={selectedDayRecord.record.status} />
                </div>
              </div>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Check-In Time</span>
                <div style={{ fontWeight: 700, fontSize: "0.9rem" }}>{selectedDayRecord.record.checkIn || "N/A"}</div>
              </div>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Check-Out Time</span>
                <div style={{ fontWeight: 700, fontSize: "0.9rem" }}>{selectedDayRecord.record.checkOut || "Active / In Progress"}</div>
              </div>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Total Hours</span>
                <div style={{ fontWeight: 700, fontSize: "0.9rem", color: "var(--brand-primary)" }}>
                  {selectedDayRecord.record.workHours}
                </div>
              </div>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Notes</span>
                <div style={{ fontSize: "0.825rem", color: "var(--text-secondary)" }}>
                  {selectedDayRecord.record.notes || "No extra notes logged."}
                </div>
              </div>
            </div>
          ) : (
            <div style={{ color: "var(--text-muted)", fontSize: "0.85rem" }}>
              {selectedDayRecord.isWeekend ? "Standard weekend holiday." : "No attendance activity was logged on this date."}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
