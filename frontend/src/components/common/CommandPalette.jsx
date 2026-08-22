import React, { useState, useEffect } from "react";
import { useHRMS } from "../../context/HRMSContext";
import {
  Search,
  LayoutDashboard,
  CalendarCheck,
  CalendarDays,
  Users,
  UserCheck,
  CreditCard,
  BarChart3,
  Clock,
  LogOut,
  Moon,
  Sun,
  User,
  CheckCircle2,
  FileText
} from "lucide-react";

export default function CommandPalette() {
  const {
    isCommandPaletteOpen,
    setIsCommandPaletteOpen,
    setActiveTab,
    employees,
    switchUser,
    isCheckedIn,
    punchIn,
    punchOut,
    setIsApplyLeaveOpen,
    setIsPayslipModalOpen,
    toggleTheme,
    theme,
    logout
  } = useHRMS();

  const [query, setQuery] = useState("");

  useEffect(() => {
    const handleKeyDown = (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === "k") {
        e.preventDefault();
        setIsCommandPaletteOpen((prev) => !prev);
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [setIsCommandPaletteOpen]);

  if (!isCommandPaletteOpen) return null;

  const actions = [
    { id: "dash", title: "Go to Dashboard", icon: LayoutDashboard, category: "Navigation", run: () => setActiveTab("dashboard") },
    { id: "att", title: "Go to Attendance & Time Tracker", icon: CalendarCheck, category: "Navigation", run: () => setActiveTab("attendance") },
    { id: "leave", title: "Go to Leave & Time-Off Management", icon: CalendarDays, category: "Navigation", run: () => setActiveTab("leaves") },
    { id: "dir", title: "Go to Employee Directory", icon: Users, category: "Navigation", run: () => setActiveTab("directory") },
    { id: "prof", title: "Go to My Profile", icon: UserCheck, category: "Navigation", run: () => setActiveTab("profile") },
    { id: "pay", title: "Go to Payroll & Compensation", icon: CreditCard, category: "Navigation", run: () => setActiveTab("payroll") },
    { id: "anal", title: "Go to Reports & Analytics", icon: BarChart3, category: "Navigation", run: () => setActiveTab("analytics") },

    // Fast actions
    {
      id: "punch",
      title: isCheckedIn ? "Clock Out (End Workday)" : "Clock In (Start Workday)",
      icon: Clock,
      category: "Workday Actions",
      run: () => (isCheckedIn ? punchOut() : punchIn("Office"))
    },
    {
      id: "apply-leave",
      title: "Apply for Leave Request",
      icon: CalendarDays,
      category: "Workday Actions",
      run: () => setIsApplyLeaveOpen(true)
    },
    {
      id: "view-slip",
      title: "View & Download Latest Payslip",
      icon: FileText,
      category: "Workday Actions",
      run: () => setIsPayslipModalOpen(true)
    },
    {
      id: "theme",
      title: `Switch to ${theme === "dark" ? "Light" : "Dark"} Mode`,
      icon: theme === "dark" ? Sun : Moon,
      category: "Preferences",
      run: () => toggleTheme()
    },
    {
      id: "logout",
      title: "Sign Out",
      icon: LogOut,
      category: "Preferences",
      run: () => logout()
    }
  ];

  // Also include employee switch options
  const employeeActions = employees.map((emp) => ({
    id: `user-${emp.id}`,
    title: `Switch to ${emp.name} (${emp.role === "admin" ? "Admin / HR" : emp.department})`,
    icon: User,
    category: "Switch Active Persona",
    run: () => switchUser(emp.id)
  }));

  const allItems = [...actions, ...employeeActions];
  const filtered = allItems.filter(
    (item) =>
      item.title.toLowerCase().includes(query.toLowerCase()) ||
      item.category.toLowerCase().includes(query.toLowerCase())
  );

  const handleSelect = (item) => {
    setIsCommandPaletteOpen(false);
    setQuery("");
    item.run();
  };

  return (
    <div className="modal-backdrop" onClick={() => setIsCommandPaletteOpen(false)}>
      <div
        className="modal-content"
        style={{ maxWidth: "560px", padding: 0, overflow: "hidden" }}
        onClick={(e) => e.stopPropagation()}
      >
        <div
          style={{
            padding: "0.85rem 1.25rem",
            borderBottom: "1px solid var(--border-color)",
            display: "flex",
            alignItems: "center",
            gap: "0.75rem",
            background: "var(--bg-secondary)"
          }}
        >
          <Search size={18} style={{ color: "var(--brand-primary)" }} />
          <input
            type="text"
            placeholder="Type a command, navigate, or search employees... (ESC to close)"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            autoFocus
            style={{
              flex: 1,
              border: "none",
              background: "transparent",
              outline: "none",
              color: "var(--text-primary)",
              fontSize: "0.95rem"
            }}
          />
          <span
            style={{
              fontSize: "0.7rem",
              padding: "0.2rem 0.45rem",
              background: "var(--bg-tertiary)",
              border: "1px solid var(--border-color)",
              borderRadius: "4px",
              color: "var(--text-muted)",
              fontFamily: "var(--font-mono)"
            }}
          >
            ESC
          </span>
        </div>

        <div style={{ maxHeight: "360px", overflowY: "auto", padding: "0.5rem" }}>
          {filtered.length === 0 ? (
            <div style={{ padding: "2rem", textAlign: "center", color: "var(--text-muted)", fontSize: "0.875rem" }}>
              No commands found for "{query}"
            </div>
          ) : (
            filtered.map((item) => {
              const Icon = item.icon;
              return (
                <div
                  key={item.id}
                  onClick={() => handleSelect(item)}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "0.75rem",
                    padding: "0.65rem 0.85rem",
                    borderRadius: "var(--radius-md)",
                    cursor: "pointer",
                    transition: "background var(--transition-fast)"
                  }}
                  onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = "var(--bg-card-hover)")}
                  onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = "transparent")}
                >
                  <div
                    style={{
                      width: "32px",
                      height: "32px",
                      borderRadius: "var(--radius-sm)",
                      background: "var(--bg-tertiary)",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "var(--brand-primary)",
                      flexShrink: 0
                    }}
                  >
                    <Icon size={16} />
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: "0.875rem", fontWeight: 600, color: "var(--text-primary)" }}>
                      {item.title}
                    </div>
                  </div>
                  <span
                    style={{
                      fontSize: "0.7rem",
                      color: "var(--text-muted)",
                      background: "var(--bg-tertiary)",
                      padding: "0.15rem 0.45rem",
                      borderRadius: "var(--radius-sm)"
                    }}
                  >
                    {item.category}
                  </span>
                </div>
              );
            })
          )}
        </div>
      </div>
    </div>
  );
}
