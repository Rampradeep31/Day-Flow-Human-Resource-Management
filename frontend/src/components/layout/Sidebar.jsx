import React from "react";
import { useHRMS } from "../../context/HRMSContext";
import {
  LayoutDashboard,
  CalendarCheck,
  CalendarDays,
  CheckSquare,
  Users,
  UserCheck,
  CreditCard,
  BarChart3,
  Sparkles,
  ShieldCheck,
  ChevronLeft,
  ChevronRight,
  Clock,
  Layers
} from "lucide-react";

export default function Sidebar({ collapsed, setCollapsed }) {
  const { activeTab, setActiveTab, currentUser, leaveRequests, isCheckedIn } = useHRMS();

  const pendingLeavesCount = leaveRequests.filter((l) => l.status === "Pending").length;
  const isAdmin = currentUser.role === "admin";

  const navItems = [
    {
      id: "dashboard",
      label: "Dashboard",
      icon: LayoutDashboard,
      roles: ["admin", "employee"]
    },
    {
      id: "attendance",
      label: "Attendance & Time",
      icon: CalendarCheck,
      badge: isCheckedIn ? "Active" : null,
      badgeColor: "success",
      roles: ["admin", "employee"]
    },
    {
      id: "leaves",
      label: "Leave & Time-Off",
      icon: CalendarDays,
      roles: ["admin", "employee"]
    },
    ...(isAdmin
      ? [
          {
            id: "approvals",
            label: "Approval Workflows",
            icon: CheckSquare,
            badge: pendingLeavesCount > 0 ? pendingLeavesCount : null,
            badgeColor: "warning",
            roles: ["admin"]
          }
        ]
      : []),
    {
      id: "directory",
      label: "Employee Directory",
      icon: Users,
      roles: ["admin", "employee"]
    },
    {
      id: "profile",
      label: "My Profile",
      icon: UserCheck,
      roles: ["admin", "employee"]
    },
    {
      id: "payroll",
      label: "Payroll & Salary",
      icon: CreditCard,
      roles: ["admin", "employee"]
    },
    {
      id: "analytics",
      label: "Reports & Analytics",
      icon: BarChart3,
      roles: ["admin", "employee"]
    }
  ];

  return (
    <aside
      style={{
        width: collapsed ? "80px" : "270px",
        backgroundColor: "var(--bg-sidebar)",
        borderRight: "1px solid var(--border-color)",
        display: "flex",
        flexDirection: "column",
        transition: "width 0.25s cubic-bezier(0.4, 0, 0.2, 1)",
        position: "relative",
        zIndex: 100,
        flexShrink: 0
      }}
    >
      {/* Brand Header */}
      <div
        style={{
          padding: collapsed ? "1.5rem 0.5rem" : "1.5rem 1.5rem 1.25rem 1.5rem",
          display: "flex",
          alignItems: "center",
          gap: "0.85rem",
          borderBottom: "1px solid rgba(255, 255, 255, 0.08)",
          justifyContent: collapsed ? "center" : "flex-start"
        }}
      >
        <div
          style={{
            width: "40px",
            height: "40px",
            borderRadius: "12px",
            background: "var(--brand-gradient)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            color: "#ffffff",
            boxShadow: "0 4px 14px rgba(99, 102, 241, 0.4)",
            flexShrink: 0
          }}
        >
          <Layers size={22} />
        </div>

        {!collapsed && (
          <div style={{ display: "flex", flexDirection: "column", minWidth: 0 }}>
            <div style={{ display: "flex", alignItems: "center", gap: "0.4rem" }}>
              <span
                style={{
                  fontSize: "1.2rem",
                  fontWeight: 800,
                  color: "#ffffff",
                  letterSpacing: "-0.03em",
                  fontFamily: "var(--font-sans)"
                }}
              >
                dayflow
              </span>
              <span
                style={{
                  fontSize: "0.65rem",
                  fontWeight: 700,
                  background: "rgba(99, 102, 241, 0.3)",
                  color: "#818cf8",
                  padding: "0.1rem 0.4rem",
                  borderRadius: "4px",
                  textTransform: "uppercase"
                }}
              >
                HRMS
              </span>
            </div>
            <span
              style={{
                fontSize: "0.685rem",
                color: "#94a3b8",
                whiteSpace: "nowrap",
                letterSpacing: "0.01em",
                fontWeight: 500
              }}
            >
              Every workday, perfectly aligned.
            </span>
          </div>
        )}
      </div>

      {/* Role Badge Indicator */}
      {!collapsed && (
        <div style={{ padding: "0.85rem 1.5rem 0.4rem 1.5rem" }}>
          <div
            style={{
              padding: "0.5rem 0.75rem",
              borderRadius: "var(--radius-md)",
              backgroundColor: isAdmin ? "rgba(99, 102, 241, 0.15)" : "rgba(255, 255, 255, 0.05)",
              border: `1px solid ${isAdmin ? "rgba(99, 102, 241, 0.3)" : "rgba(255, 255, 255, 0.08)"}`,
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between"
            }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: "0.45rem" }}>
              {isAdmin ? (
                <ShieldCheck size={16} style={{ color: "#818cf8" }} />
              ) : (
                <UserCheck size={16} style={{ color: "#94a3b8" }} />
              )}
              <span style={{ fontSize: "0.75rem", fontWeight: 700, color: "#ffffff" }}>
                {isAdmin ? "Admin / HR Officer" : "Employee Portal"}
              </span>
            </div>
            <span
              style={{
                fontSize: "0.65rem",
                padding: "0.1rem 0.35rem",
                borderRadius: "var(--radius-full)",
                backgroundColor: isAdmin ? "#6366f1" : "rgba(255, 255, 255, 0.15)",
                color: "#ffffff",
                fontWeight: 700
              }}
            >
              {isAdmin ? "Full Control" : "Self-Service"}
            </span>
          </div>
        </div>
      )}

      {/* Navigation List */}
      <nav
        style={{
          flex: 1,
          padding: collapsed ? "1rem 0.5rem" : "0.75rem 1rem",
          display: "flex",
          flexDirection: "column",
          gap: "0.35rem",
          overflowY: "auto"
        }}
      >
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;

          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              style={{
                width: "100%",
                display: "flex",
                alignItems: "center",
                gap: "0.85rem",
                padding: collapsed ? "0.75rem 0" : "0.7rem 0.95rem",
                justifyContent: collapsed ? "center" : "flex-start",
                borderRadius: "var(--radius-md)",
                border: "none",
                backgroundColor: isActive ? "var(--sidebar-active-bg)" : "transparent",
                color: isActive ? "#ffffff" : "#94a3b8",
                fontWeight: isActive ? 700 : 500,
                fontSize: "0.875rem",
                cursor: "pointer",
                transition: "all var(--transition-fast)",
                position: "relative",
                textAlign: "left"
              }}
              onMouseEnter={(e) => {
                if (!isActive) {
                  e.currentTarget.style.backgroundColor = "rgba(255, 255, 255, 0.06)";
                  e.currentTarget.style.color = "#f1f5f9";
                }
              }}
              onMouseLeave={(e) => {
                if (!isActive) {
                  e.currentTarget.style.backgroundColor = "transparent";
                  e.currentTarget.style.color = "#94a3b8";
                }
              }}
              title={collapsed ? item.label : ""}
            >
              {isActive && (
                <span
                  style={{
                    position: "absolute",
                    left: 0,
                    top: "15%",
                    bottom: "15%",
                    width: "3px",
                    borderRadius: "0 4px 4px 0",
                    backgroundColor: "var(--brand-primary)"
                  }}
                />
              )}

              <Icon
                size={19}
                style={{
                  color: isActive ? "var(--brand-primary)" : "inherit",
                  flexShrink: 0
                }}
              />

              {!collapsed && <span style={{ flex: 1, whiteSpace: "nowrap" }}>{item.label}</span>}

              {!collapsed && item.badge && (
                <span
                  style={{
                    fontSize: "0.7rem",
                    fontWeight: 700,
                    padding: "0.15rem 0.5rem",
                    borderRadius: "var(--radius-full)",
                    backgroundColor:
                      item.badgeColor === "warning" ? "var(--color-warning)" : "var(--color-success)",
                    color: "#ffffff"
                  }}
                >
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      {/* Collapse Toggle */}
      <div
        style={{
          padding: "0.75rem 1rem",
          borderTop: "1px solid rgba(255, 255, 255, 0.08)",
          display: "flex",
          justifyContent: collapsed ? "center" : "flex-end"
        }}
      >
        <button
          onClick={() => setCollapsed(!collapsed)}
          className="btn-ghost"
          style={{
            padding: "0.4rem",
            color: "#94a3b8",
            borderRadius: "var(--radius-sm)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center"
          }}
          title={collapsed ? "Expand sidebar" : "Collapse sidebar"}
        >
          {collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
        </button>
      </div>

      {/* User Footer Card */}
      {!collapsed && (
        <div
          style={{
            padding: "1rem",
            margin: "0 0.75rem 1rem 0.75rem",
            backgroundColor: "rgba(255, 255, 255, 0.04)",
            border: "1px solid rgba(255, 255, 255, 0.08)",
            borderRadius: "var(--radius-lg)",
            display: "flex",
            alignItems: "center",
            gap: "0.75rem"
          }}
        >
          <img
            src={currentUser.avatar}
            alt={currentUser.name}
            style={{ width: "36px", height: "36px", borderRadius: "50%", objectFit: "cover" }}
          />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: "0.825rem", fontWeight: 700, color: "#ffffff", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
              {currentUser.name}
            </div>
            <div style={{ fontSize: "0.7rem", color: "#94a3b8", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
              {currentUser.email}
            </div>
          </div>
        </div>
      )}
    </aside>
  );
}
