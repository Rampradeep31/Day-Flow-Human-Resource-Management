import React from "react";
import StatusBadge from "../common/StatusBadge";
import { formatDate } from "../../utils/dateUtils";
import { Calendar, Clock, MapPin, Laptop } from "lucide-react";

export default function WeeklyGrid({ records }) {
  // Generate 7 days for the current week (Aug 16 - Aug 22, 2026)
  const weekDays = [
    { date: "2026-08-16", name: "Sun", isWeekend: true },
    { date: "2026-08-17", name: "Mon", isWeekend: false },
    { date: "2026-08-18", name: "Tue", isWeekend: false },
    { date: "2026-08-19", name: "Wed", isWeekend: false },
    { date: "2026-08-20", name: "Thu", isWeekend: false },
    { date: "2026-08-21", name: "Fri", isWeekend: false },
    { date: "2026-08-22", name: "Sat (Today)", isWeekend: false }
  ];

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <div>
          <h3 style={{ fontSize: "1.1rem", fontWeight: 700 }}>Weekly Work Log (Past 7 Days)</h3>
          <p style={{ fontSize: "0.8rem", color: "var(--text-secondary)", marginTop: "2px" }}>
            Summary of hours worked, punches, and presence
          </p>
        </div>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))", gap: "1rem" }}>
        {weekDays.map((day) => {
          const rec = records.find((r) => r.date === day.date);
          const status = rec ? rec.status : day.isWeekend ? "Holiday" : "Absent";

          return (
            <div
              key={day.date}
              className="glass-card"
              style={{
                padding: "1.25rem",
                display: "flex",
                flexDirection: "column",
                gap: "0.75rem",
                position: "relative"
              }}
            >
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <div>
                  <span style={{ fontSize: "0.75rem", fontWeight: 700, color: "var(--brand-primary)", textTransform: "uppercase" }}>
                    {day.name}
                  </span>
                  <div style={{ fontSize: "0.85rem", fontWeight: 800, color: "var(--text-primary)" }}>
                    {formatDate(day.date)}
                  </div>
                </div>
                <StatusBadge status={status} />
              </div>

              <div
                style={{
                  padding: "0.75rem",
                  borderRadius: "var(--radius-sm)",
                  backgroundColor: "var(--bg-tertiary)",
                  display: "flex",
                  flexDirection: "column",
                  gap: "0.35rem",
                  fontSize: "0.75rem"
                }}
              >
                <div style={{ display: "flex", justifyContent: "space-between", color: "var(--text-secondary)" }}>
                  <span>Check In:</span>
                  <strong style={{ color: "var(--text-primary)" }}>{rec?.checkIn || "—"}</strong>
                </div>
                <div style={{ display: "flex", justifyContent: "space-between", color: "var(--text-secondary)" }}>
                  <span>Check Out:</span>
                  <strong style={{ color: "var(--text-primary)" }}>{rec?.checkOut || (rec?.checkIn ? "Active" : "—")}</strong>
                </div>
                <div style={{ display: "flex", justifyContent: "space-between", color: "var(--text-secondary)" }}>
                  <span>Hours:</span>
                  <strong style={{ color: "var(--brand-primary)" }}>{rec?.workHours || "0.0 hrs"}</strong>
                </div>
              </div>

              {rec?.workMode && (
                <div style={{ fontSize: "0.7rem", color: "var(--text-muted)", display: "flex", alignItems: "center", gap: "0.25rem" }}>
                  {rec.workMode === "Office" ? <MapPin size={12} /> : <Laptop size={12} />}
                  <span>{rec.workMode}</span>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
