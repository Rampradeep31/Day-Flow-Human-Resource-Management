import React from "react";
import { Clock, MapPin, Laptop, Coffee, CheckCircle, AlertCircle } from "lucide-react";
import StatusBadge from "../common/StatusBadge";

export default function DailyTimeline({ records, employeeName }) {
  const todayStr = new Date().toISOString().split("T")[0];
  const todayRecord = records.find((r) => r.date === todayStr);

  const hours = [
    "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
    "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM", "06:00 PM", "07:00 PM"
  ];

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
      <div
        className="glass-card"
        style={{
          padding: "1.5rem",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "1rem"
        }}
      >
        <div>
          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
            <h3 style={{ fontSize: "1.1rem", fontWeight: 700 }}>Today's Timeline Log</h3>
            <span style={{ fontSize: "0.8rem", color: "var(--text-muted)", fontFamily: "var(--font-mono)" }}>
              ({todayStr})
            </span>
          </div>
          <p style={{ fontSize: "0.8rem", color: "var(--text-secondary)", marginTop: "2px" }}>
            Recorded punches and work intervals for {employeeName}
          </p>
        </div>

        <div>
          {todayRecord ? (
            <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
              <StatusBadge status={todayRecord.status} />
              <span
                style={{
                  fontSize: "0.8rem",
                  fontWeight: 600,
                  color: "var(--text-primary)",
                  backgroundColor: "var(--bg-tertiary)",
                  padding: "0.3rem 0.6rem",
                  borderRadius: "var(--radius-sm)"
                }}
              >
                Duration: {todayRecord.workHours}
              </span>
            </div>
          ) : (
            <span style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}>No punch recorded for today</span>
          )}
        </div>
      </div>

      {/* Visual Day Schedule Bar */}
      <div className="glass-card" style={{ padding: "1.5rem" }}>
        <div style={{ fontSize: "0.85rem", fontWeight: 700, marginBottom: "1rem", color: "var(--text-secondary)" }}>
          9-Hour Daily Activity Spread
        </div>

        <div style={{ display: "grid", gridTemplateColumns: "repeat(12, 1fr)", gap: "4px", marginBottom: "0.5rem" }}>
          {hours.map((h, i) => {
            // Simulated active slots from 9am to 5pm
            const isActive = i >= 1 && i <= 8;
            const isBreak = i === 4; // 12pm lunch

            return (
              <div
                key={h}
                style={{
                  height: "44px",
                  borderRadius: "4px",
                  backgroundColor: isBreak
                    ? "var(--color-warning-bg)"
                    : isActive
                    ? "rgba(99, 102, 241, 0.25)"
                    : "var(--bg-tertiary)",
                  border: `1px solid ${
                    isBreak
                      ? "var(--color-warning-border)"
                      : isActive
                      ? "rgba(99, 102, 241, 0.4)"
                      : "var(--border-subtle)"
                  }`,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontSize: "0.65rem",
                  fontWeight: 700,
                  color: isBreak ? "var(--color-warning)" : isActive ? "var(--brand-primary)" : "var(--text-muted)"
                }}
                title={`${h} - ${isBreak ? "Break" : isActive ? "Active Work" : "Offline"}`}
              >
                {isBreak ? "Break" : isActive ? "Work" : "—"}
              </div>
            );
          })}
        </div>

        <div style={{ display: "flex", justifyContent: "space-between", fontSize: "0.7rem", color: "var(--text-muted)", fontFamily: "var(--font-mono)" }}>
          <span>8:00 AM</span>
          <span>12:00 PM</span>
          <span>4:00 PM</span>
          <span>7:00 PM</span>
        </div>
      </div>

      {/* Punch Events Log */}
      <div className="glass-card" style={{ padding: "1.5rem" }}>
        <h4 style={{ fontSize: "0.95rem", fontWeight: 700, marginBottom: "1rem" }}>Today's Punch Audit Trail</h4>

        {todayRecord ? (
          <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
            <div style={{ display: "flex", alignItems: "center", gap: "0.75rem", padding: "0.75rem", backgroundColor: "var(--bg-secondary)", borderRadius: "var(--radius-md)" }}>
              <div style={{ width: "32px", height: "32px", borderRadius: "50%", backgroundColor: "var(--color-success-bg)", color: "var(--color-success)", display: "flex", alignItems: "center", justifyContent: "center" }}>
                <CheckCircle size={16} />
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 700, fontSize: "0.85rem" }}>Clocked In (Shift Start)</div>
                <div style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>
                  Time: {todayRecord.checkIn} • Mode: {todayRecord.workMode} • Notes: "{todayRecord.notes || "Standard check-in"}"
                </div>
              </div>
            </div>

            {todayRecord.checkOut && (
              <div style={{ display: "flex", alignItems: "center", gap: "0.75rem", padding: "0.75rem", backgroundColor: "var(--bg-secondary)", borderRadius: "var(--radius-md)" }}>
                <div style={{ width: "32px", height: "32px", borderRadius: "50%", backgroundColor: "var(--brand-primary-light)", color: "var(--brand-primary)", display: "flex", alignItems: "center", justifyContent: "center" }}>
                  <Clock size={16} />
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontWeight: 700, fontSize: "0.85rem" }}>Clocked Out (Shift End)</div>
                  <div style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>
                    Time: {todayRecord.checkOut} • Total Duration: {todayRecord.workHours}
                  </div>
                </div>
              </div>
            )}
          </div>
        ) : (
          <div style={{ padding: "1.5rem", textAlign: "center", color: "var(--text-muted)", fontSize: "0.85rem" }}>
            No punch recorded for today. Clock in using the timer on top!
          </div>
        )}
      </div>
    </div>
  );
}
