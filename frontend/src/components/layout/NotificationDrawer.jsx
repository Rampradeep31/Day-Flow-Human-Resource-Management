import React from "react";
import { useHRMS } from "../../context/HRMSContext";
import { X, Bell, CheckCheck, Trash2, Calendar, FileText, Award, AlertCircle } from "lucide-react";

export default function NotificationDrawer() {
  const {
    isNotificationDrawerOpen,
    setIsNotificationDrawerOpen,
    notifications,
    markNotificationAsRead,
    markAllNotificationsAsRead,
    dismissNotification,
    setActiveTab,
    currentUser
  } = useHRMS();

  if (!isNotificationDrawerOpen) return null;

  // Filter notifications relevant to current user role
  const relevantNotifications = notifications.filter(
    (n) => n.targetRole === "all" || n.targetRole === currentUser.role
  );

  const getIcon = (type) => {
    switch (type) {
      case "leave":
        return <Calendar size={16} style={{ color: "var(--brand-primary)" }} />;
      case "approval":
        return <Award size={16} style={{ color: "var(--color-success)" }} />;
      case "payroll":
        return <FileText size={16} style={{ color: "var(--color-purple)" }} />;
      default:
        return <AlertCircle size={16} style={{ color: "var(--color-info)" }} />;
    }
  };

  const handleNotificationClick = (notif) => {
    markNotificationAsRead(notif.id);
    if (notif.linkTab) {
      setActiveTab(notif.linkTab);
      setIsNotificationDrawerOpen(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={() => setIsNotificationDrawerOpen(false)}>
      <div
        style={{
          position: "fixed",
          top: 0,
          right: 0,
          bottom: 0,
          width: "100%",
          maxWidth: "400px",
          backgroundColor: "var(--bg-elevated)",
          borderLeft: "1px solid var(--border-color)",
          boxShadow: "var(--shadow-xl)",
          display: "flex",
          flexDirection: "column",
          zIndex: 1100,
          animation: "slideInRight 0.25s cubic-bezier(0.16, 1, 0.3, 1)"
        }}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div
          style={{
            padding: "1.25rem 1.5rem",
            borderBottom: "1px solid var(--border-color)",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between"
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
            <Bell size={20} style={{ color: "var(--brand-primary)" }} />
            <h3 style={{ fontSize: "1.1rem", fontWeight: 700, color: "var(--text-primary)" }}>Notifications</h3>
          </div>
          <button
            onClick={() => setIsNotificationDrawerOpen(false)}
            className="btn-ghost"
            style={{ padding: "0.35rem", borderRadius: "var(--radius-sm)" }}
          >
            <X size={18} />
          </button>
        </div>

        {/* Action bar */}
        <div
          style={{
            padding: "0.75rem 1.5rem",
            backgroundColor: "var(--bg-secondary)",
            borderBottom: "1px solid var(--border-subtle)",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between"
          }}
        >
          <span style={{ fontSize: "0.775rem", color: "var(--text-muted)", fontWeight: 600 }}>
            {relevantNotifications.filter((n) => !n.read).length} Unread Alerts
          </span>
          <button
            onClick={markAllNotificationsAsRead}
            className="btn-ghost btn-sm"
            style={{ fontSize: "0.75rem", color: "var(--brand-primary)", display: "flex", alignItems: "center", gap: "0.25rem", padding: "0.2rem 0.5rem" }}
          >
            <CheckCheck size={14} /> Mark all read
          </button>
        </div>

        {/* Notifications List */}
        <div style={{ flex: 1, overflowY: "auto", padding: "1rem", display: "flex", flexDirection: "column", gap: "0.75rem" }}>
          {relevantNotifications.length === 0 ? (
            <div style={{ padding: "3rem 1rem", textAlign: "center", color: "var(--text-muted)" }}>
              <Bell size={36} style={{ margin: "0 auto 1rem auto", opacity: 0.3 }} />
              <p style={{ fontWeight: 600, fontSize: "0.95rem", color: "var(--text-secondary)" }}>No notifications yet</p>
              <p style={{ fontSize: "0.8rem", marginTop: "0.25rem" }}>You are all caught up with your workday alerts!</p>
            </div>
          ) : (
            relevantNotifications.map((notif) => (
              <div
                key={notif.id}
                onClick={() => handleNotificationClick(notif)}
                style={{
                  padding: "0.85rem",
                  borderRadius: "var(--radius-md)",
                  border: `1px solid ${notif.read ? "var(--border-subtle)" : "rgba(99, 102, 241, 0.3)"}`,
                  backgroundColor: notif.read ? "var(--bg-secondary)" : "rgba(99, 102, 241, 0.04)",
                  cursor: "pointer",
                  display: "flex",
                  gap: "0.75rem",
                  transition: "all var(--transition-fast)",
                  position: "relative"
                }}
                onMouseEnter={(e) => (e.currentTarget.style.borderColor = "var(--brand-primary)")}
                onMouseLeave={(e) =>
                  (e.currentTarget.style.borderColor = notif.read ? "var(--border-subtle)" : "rgba(99, 102, 241, 0.3)")
                }
              >
                <div
                  style={{
                    width: "32px",
                    height: "32px",
                    borderRadius: "var(--radius-sm)",
                    backgroundColor: "var(--bg-tertiary)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    flexShrink: 0,
                    marginTop: "2px"
                  }}
                >
                  {getIcon(notif.type)}
                </div>

                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "0.5rem" }}>
                    <h4 style={{ fontSize: "0.85rem", fontWeight: 700, color: "var(--text-primary)" }}>
                      {notif.title}
                    </h4>
                    {!notif.read && (
                      <span
                        style={{
                          width: "8px",
                          height: "8px",
                          borderRadius: "50%",
                          backgroundColor: "var(--brand-primary)",
                          flexShrink: 0
                        }}
                      />
                    )}
                  </div>
                  <p style={{ fontSize: "0.775rem", color: "var(--text-secondary)", marginTop: "0.25rem", lineHeight: "1.4" }}>
                    {notif.message}
                  </p>
                  <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginTop: "0.5rem" }}>
                    <span style={{ fontSize: "0.7rem", color: "var(--text-muted)" }}>{notif.timestamp}</span>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        dismissNotification(notif.id);
                      }}
                      className="btn-ghost"
                      style={{ padding: "0.15rem", color: "var(--text-muted)" }}
                      title="Delete notification"
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
