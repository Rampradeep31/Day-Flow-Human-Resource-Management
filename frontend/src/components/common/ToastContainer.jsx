import React from "react";
import { useHRMS } from "../../context/HRMSContext";
import { CheckCircle2, AlertCircle, Info, AlertTriangle, X } from "lucide-react";

export default function ToastContainer() {
  const { toasts, dismissToast } = useHRMS();

  if (!toasts || toasts.length === 0) return null;

  const getIcon = (type) => {
    switch (type) {
      case "success":
        return <CheckCircle2 size={20} className="text-emerald-500" style={{ color: "var(--color-success)" }} />;
      case "error":
        return <AlertCircle size={20} className="text-rose-500" style={{ color: "var(--color-danger)" }} />;
      case "warning":
        return <AlertTriangle size={20} className="text-amber-500" style={{ color: "var(--color-warning)" }} />;
      default:
        return <Info size={20} className="text-sky-500" style={{ color: "var(--color-info)" }} />;
    }
  };

  return (
    <div className="toast-container" aria-live="polite">
      {toasts.map((toast) => (
        <div key={toast.id} className={`toast-item ${toast.type || "info"}`}>
          <div style={{ flexShrink: 0, marginTop: "2px" }}>{getIcon(toast.type)}</div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontWeight: 600, fontSize: "0.875rem", color: "var(--text-primary)" }}>{toast.title}</div>
            {toast.message && (
              <div style={{ fontSize: "0.8rem", color: "var(--text-secondary)", marginTop: "2px", lineHeight: "1.35" }}>
                {toast.message}
              </div>
            )}
          </div>
          <button
            onClick={() => dismissToast(toast.id)}
            style={{
              background: "transparent",
              border: "none",
              cursor: "pointer",
              color: "var(--text-muted)",
              padding: "2px",
              display: "flex",
              alignItems: "center"
            }}
            title="Dismiss"
          >
            <X size={16} />
          </button>
        </div>
      ))}
    </div>
  );
}
