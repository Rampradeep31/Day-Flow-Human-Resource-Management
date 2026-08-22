import React from "react";
import { useHRMS } from "../../context/HRMSContext";
import { CalendarDays, HeartPulse, Sun, AlertCircle } from "lucide-react";

export default function LeaveBalancesView({ employee }) {
  const balances = employee?.leaveBalances || {
    paid: { total: 18, used: 4, available: 14 },
    sick: { total: 10, used: 1, available: 9 },
    casual: { total: 6, used: 1, available: 5 },
    unpaid: { total: 0, used: 0, available: 0 }
  };

  const cards = [
    {
      title: "Paid Annual Leave",
      available: balances.paid.available,
      used: balances.paid.used,
      total: balances.paid.total,
      icon: Sun,
      color: "var(--brand-primary)",
      bg: "var(--brand-primary-light)"
    },
    {
      title: "Sick & Medical Leave",
      available: balances.sick.available,
      used: balances.sick.used,
      total: balances.sick.total,
      icon: HeartPulse,
      color: "var(--color-success)",
      bg: "var(--color-success-bg)"
    },
    {
      title: "Casual Personal Leave",
      available: balances.casual.available,
      used: balances.casual.used,
      total: balances.casual.total,
      icon: CalendarDays,
      color: "var(--color-warning)",
      bg: "var(--color-warning-bg)"
    },
    {
      title: "Unpaid Sabbatical",
      available: 0,
      used: balances.unpaid.used,
      total: "Uncapped",
      icon: AlertCircle,
      color: "var(--text-secondary)",
      bg: "var(--bg-tertiary)"
    }
  ];

  return (
    <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: "1rem" }}>
      {cards.map((c) => {
        const Icon = c.icon;
        const percent = typeof c.total === "number" && c.total > 0 ? Math.round((c.available / c.total) * 100) : 0;

        return (
          <div
            key={c.title}
            className="glass-card"
            style={{
              padding: "1.25rem",
              display: "flex",
              flexDirection: "column",
              gap: "0.75rem"
            }}
          >
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
              <span style={{ fontSize: "0.8rem", fontWeight: 700, color: "var(--text-secondary)" }}>{c.title}</span>
              <div
                style={{
                  width: "32px",
                  height: "32px",
                  borderRadius: "var(--radius-sm)",
                  backgroundColor: c.bg,
                  color: c.color,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center"
                }}
              >
                <Icon size={16} />
              </div>
            </div>

            <div style={{ display: "flex", alignItems: "baseline", gap: "0.5rem" }}>
              <span style={{ fontSize: "1.75rem", fontWeight: 800, color: c.color, fontFamily: "var(--font-mono)" }}>
                {c.available}
              </span>
              <span style={{ fontSize: "0.775rem", color: "var(--text-muted)" }}>
                available of {c.total} {typeof c.total === "number" ? "days" : ""}
              </span>
            </div>

            {typeof c.total === "number" && (
              <div>
                <div style={{ width: "100%", height: "5px", backgroundColor: "var(--border-color)", borderRadius: "3px", overflow: "hidden" }}>
                  <div
                    style={{
                      height: "100%",
                      width: `${percent}%`,
                      backgroundColor: c.color,
                      borderRadius: "3px",
                      transition: "width 0.3s ease"
                    }}
                  />
                </div>
                <div style={{ display: "flex", justifyContent: "space-between", fontSize: "0.7rem", color: "var(--text-muted)", marginTop: "0.3rem" }}>
                  <span>{c.used} days used</span>
                  <span>{percent}% left</span>
                </div>
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}
