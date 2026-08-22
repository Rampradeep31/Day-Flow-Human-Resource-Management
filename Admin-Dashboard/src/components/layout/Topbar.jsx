import React, { useState, useEffect } from "react";
import { useHRMS } from "../../context/HRMSContext";
import QuickUserSwitcher from "./QuickUserSwitcher";
import {
  Search,
  Bell,
  Sun,
  Moon,
  Volume2,
  VolumeX,
  Clock,
  LogOut,
  ChevronRight,
  ShieldCheck,
  User,
  Sparkles
} from "lucide-react";
import { formatTime } from "../../utils/dateUtils";

export default function Topbar({ onToggleSidebarMobile }) {
  const {
    activeTab,
    theme,
    toggleTheme,
    soundEnabled,
    setSoundEnabled,
    currentUser,
    notifications,
    setIsNotificationDrawerOpen,
    setIsCommandPaletteOpen,
    isCheckedIn,
    logout
  } = useHRMS();

  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const unreadCount = notifications.filter(
    (n) => (!n.read && (n.targetRole === "all" || n.targetRole === currentUser.role))
  ).length;

  const getBreadcrumbTitle = (tab) => {
    switch (tab) {
      case "dashboard":
        return "Dashboard Overview";
      case "attendance":
        return "Attendance & Workday Tracker";
      case "leaves":
        return "Leave & Time-Off Management";
      case "approvals":
        return "HR Approval Center";
      case "directory":
        return "Employee Directory";
      case "profile":
        return "Employee Profile File";
      case "payroll":
        return "Payroll & Compensation";
      case "analytics":
        return "Analytics & Company Reports";
      default:
        return "Overview";
    }
  };

  return (
    <header
      className="topbar"
      style={{
        height: "70px",
        backgroundColor: "var(--bg-secondary)",
        borderBottom: "1px solid var(--border-color)",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        padding: "0 2rem",
        position: "sticky",
        top: 0,
        zIndex: 90,
        backdropFilter: "blur(12px)",
        WebkitBackdropFilter: "blur(12px)"
      }}
    >
      {/* Left: Breadcrumbs & Live Time */}
      <div style={{ display: "flex", alignItems: "center", gap: "1rem" }}>
        <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
          <span style={{ fontSize: "0.825rem", color: "var(--text-muted)", fontWeight: 500 }}>Dayflow</span>
          <ChevronRight size={14} style={{ color: "var(--text-muted)" }} />
          <span style={{ fontSize: "0.925rem", fontWeight: 700, color: "var(--text-primary)" }}>
            {getBreadcrumbTitle(activeTab)}
          </span>
        </div>

        {/* Live Clock Tag */}
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: "0.4rem",
            padding: "0.3rem 0.65rem",
            background: "var(--bg-tertiary)",
            borderRadius: "var(--radius-full)",
            border: "1px solid var(--border-subtle)",
            fontSize: "0.75rem",
            fontWeight: 600,
            color: "var(--text-secondary)",
            fontFamily: "var(--font-mono)"
          }}
        >
          <Clock size={13} style={{ color: isCheckedIn ? "var(--color-success)" : "var(--text-muted)" }} />
          <span>{formatTime(currentTime)}</span>
          <span
            style={{
              width: "6px",
              height: "6px",
              borderRadius: "50%",
              backgroundColor: isCheckedIn ? "var(--color-success)" : "var(--text-muted)"
            }}
          />
        </div>
      </div>

      {/* Right: Actions, Search, Persona Switcher, Profile */}
      <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
        {/* Command Search Bar Shortcut */}
        <button
          onClick={() => setIsCommandPaletteOpen(true)}
          style={{
            display: "flex",
            alignItems: "center",
            gap: "0.6rem",
            padding: "0.45rem 0.9rem",
            background: "var(--bg-tertiary)",
            border: "1px solid var(--border-color)",
            borderRadius: "var(--radius-md)",
            cursor: "pointer",
            color: "var(--text-muted)",
            fontSize: "0.825rem",
            transition: "all var(--transition-fast)"
          }}
          onMouseEnter={(e) => (e.currentTarget.style.borderColor = "var(--brand-primary)")}
          onMouseLeave={(e) => (e.currentTarget.style.borderColor = "var(--border-color)")}
        >
          <Search size={15} />
          <span style={{ display: "inline-block" }}>Search or jump to...</span>
          <kbd
            style={{
              fontSize: "0.675rem",
              padding: "0.15rem 0.4rem",
              background: "var(--bg-secondary)",
              border: "1px solid var(--border-color)",
              borderRadius: "4px",
              fontFamily: "var(--font-mono)"
            }}
          >
            Ctrl+K
          </kbd>
        </button>

        {/* Quick User Persona Switcher */}
        <QuickUserSwitcher />

        {/* Theme Toggle */}
        <button
          onClick={toggleTheme}
          className="btn-ghost"
          style={{
            width: "38px",
            height: "38px",
            borderRadius: "var(--radius-md)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: 0,
            color: "var(--text-secondary)"
          }}
          title={`Switch to ${theme === "dark" ? "Light" : "Dark"} mode`}
        >
          {theme === "dark" ? <Sun size={18} /> : <Moon size={18} />}
        </button>

        {/* Sound Toggle */}
        <button
          onClick={() => setSoundEnabled(!soundEnabled)}
          className="btn-ghost"
          style={{
            width: "38px",
            height: "38px",
            borderRadius: "var(--radius-md)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: 0,
            color: soundEnabled ? "var(--brand-primary)" : "var(--text-muted)"
          }}
          title={soundEnabled ? "Mute audio cues" : "Unmute audio cues"}
        >
          {soundEnabled ? <Volume2 size={18} /> : <VolumeX size={18} />}
        </button>

        {/* Notifications Bell */}
        <button
          onClick={() => setIsNotificationDrawerOpen(true)}
          className="btn-ghost"
          style={{
            width: "38px",
            height: "38px",
            borderRadius: "var(--radius-md)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: 0,
            position: "relative",
            color: "var(--text-secondary)"
          }}
          title="Notifications"
        >
          <Bell size={18} />
          {unreadCount > 0 && (
            <span
              style={{
                position: "absolute",
                top: "6px",
                right: "6px",
                width: "8px",
                height: "8px",
                borderRadius: "50%",
                backgroundColor: "var(--color-danger)",
                boxShadow: "0 0 0 2px var(--bg-secondary)"
              }}
            />
          )}
        </button>

        {/* Sign Out Shortcut */}
        <button
          onClick={logout}
          className="btn-ghost"
          style={{
            width: "38px",
            height: "38px",
            borderRadius: "var(--radius-md)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: 0,
            color: "var(--color-danger)"
          }}
          title="Sign Out"
        >
          <LogOut size={17} />
        </button>
      </div>
    </header>
  );
}
