import React from "react";

export default function MetricCard({ title, value, subtitle, icon: Icon, change, trend = "up", color = "primary" }) {
  const getColorStyles = () => {
    switch (color) {
      case "success":
        return { bg: "var(--color-success-bg)", text: "var(--color-success)", border: "var(--color-success-border)" };
      case "warning":
        return { bg: "var(--color-warning-bg)", text: "var(--color-warning)", border: "var(--color-warning-border)" };
      case "danger":
        return { bg: "var(--color-danger-bg)", text: "var(--color-danger)", border: "var(--color-danger-border)" };
      case "purple":
        return { bg: "var(--color-purple-bg)", text: "var(--color-purple)", border: "var(--color-purple-border)" };
      default:
        return { bg: "var(--brand-primary-light)", text: "var(--brand-primary)", border: "rgba(99, 102, 241, 0.25)" };
    }
  };

  const style = getColorStyles();

  return (
    <div className={`glass-card metric-card metric-card-${color}`} style={{ padding: "1.25rem 1.5rem", display: "flex", flexDirection: "column", gap: "0.75rem" }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
        <span style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-secondary)" }}>{title}</span>
        {Icon && (
          <div
            className="metric-card-icon"
            style={{
              width: "36px",
              height: "36px",
              borderRadius: "var(--radius-md)",
              backgroundColor: style.bg,
              color: style.text,
              border: `1px solid ${style.border}`,
              display: "flex",
              alignItems: "center",
              justifyContent: "center"
            }}
          >
            <Icon size={18} />
          </div>
        )}
      </div>

      <div style={{ display: "flex", alignItems: "baseline", gap: "0.75rem" }}>
        <div className="metric-card-value" style={{ fontSize: "1.75rem", fontWeight: 800, color: "var(--text-primary)", letterSpacing: "-0.02em" }}>
          {value}
        </div>
        {change && (
          <span
            style={{
              fontSize: "0.75rem",
              fontWeight: 700,
              color: trend === "up" ? "var(--color-success)" : "var(--color-danger)",
              display: "inline-flex",
              alignItems: "center"
            }}
          >
            {trend === "up" ? "↑" : "↓"} {change}
          </span>
        )}
      </div>

      {subtitle && (
        <div style={{ fontSize: "0.775rem", color: "var(--text-muted)", marginTop: "auto" }}>
          {subtitle}
        </div>
      )}
    </div>
  );
}
