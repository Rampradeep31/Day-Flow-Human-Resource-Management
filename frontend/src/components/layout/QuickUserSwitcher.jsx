import React, { useState, useRef, useEffect } from "react";
import { useHRMS } from "../../context/HRMSContext";
import { UserCheck, ShieldCheck, User, ChevronDown, Check } from "lucide-react";

export default function QuickUserSwitcher() {
  const { currentUser, employees, switchUser } = useHRMS();
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div style={{ position: "relative" }} ref={dropdownRef}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        style={{
          display: "flex",
          alignItems: "center",
          gap: "0.6rem",
          padding: "0.4rem 0.75rem",
          background: currentUser.role === "admin" ? "rgba(99, 102, 241, 0.12)" : "var(--bg-secondary)",
          border: `1px solid ${currentUser.role === "admin" ? "rgba(99, 102, 241, 0.35)" : "var(--border-color)"}`,
          borderRadius: "var(--radius-full)",
          cursor: "pointer",
          color: "var(--text-primary)",
          transition: "all var(--transition-fast)"
        }}
        title="Switch Persona / Role Demo"
      >
        <img
          src={currentUser.avatar}
          alt={currentUser.name}
          style={{ width: "24px", height: "24px", borderRadius: "50%", objectFit: "cover" }}
        />
        <div style={{ display: "flex", flexDirection: "column", textAlign: "left", lineHeight: "1.1" }}>
          <span style={{ fontSize: "0.775rem", fontWeight: 700, color: "var(--text-primary)" }}>
            {currentUser.name}
          </span>
          <span
            style={{
              fontSize: "0.65rem",
              fontWeight: 600,
              color: currentUser.role === "admin" ? "var(--brand-primary)" : "var(--text-muted)"
            }}
          >
            {currentUser.role === "admin" ? "HR Admin" : "Employee"}
          </span>
        </div>
        <ChevronDown size={14} style={{ color: "var(--text-muted)", marginLeft: "2px" }} />
      </button>

      {isOpen && (
        <div
          style={{
            position: "absolute",
            top: "calc(100% + 6px)",
            right: 0,
            width: "280px",
            background: "var(--bg-elevated)",
            border: "1px solid var(--border-color)",
            borderRadius: "var(--radius-lg)",
            boxShadow: "var(--shadow-xl)",
            padding: "0.5rem",
            zIndex: 1100,
            animation: "fadeIn 0.15s ease"
          }}
        >
          <div
            style={{
              padding: "0.4rem 0.6rem 0.5rem 0.6rem",
              borderBottom: "1px solid var(--border-subtle)",
              marginBottom: "0.4rem"
            }}
          >
            <div style={{ fontSize: "0.75rem", fontWeight: 700, color: "var(--text-muted)", textTransform: "uppercase", letterSpacing: "0.05em" }}>
              Quick Persona Switcher
            </div>
            <div style={{ fontSize: "0.7rem", color: "var(--text-secondary)", marginTop: "2px" }}>
              Test role-based access for Admin vs Employee
            </div>
          </div>

          <div style={{ maxHeight: "240px", overflowY: "auto", display: "flex", flexDirection: "column", gap: "2px" }}>
            {employees.map((emp) => {
              const isSelected = emp.id === currentUser.id;
              const isAdmin = emp.role === "admin";

              return (
                <div
                  key={emp.id}
                  onClick={() => {
                    switchUser(emp.id);
                    setIsOpen(false);
                  }}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "0.6rem",
                    padding: "0.5rem 0.6rem",
                    borderRadius: "var(--radius-md)",
                    cursor: "pointer",
                    background: isSelected ? "var(--bg-tertiary)" : "transparent",
                    transition: "background var(--transition-fast)"
                  }}
                  onMouseEnter={(e) => !isSelected && (e.currentTarget.style.backgroundColor = "var(--bg-card-hover)")}
                  onMouseLeave={(e) => !isSelected && (e.currentTarget.style.backgroundColor = "transparent")}
                >
                  <img
                    src={emp.avatar}
                    alt={emp.name}
                    style={{ width: "28px", height: "28px", borderRadius: "50%", objectFit: "cover" }}
                  />
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "0.35rem" }}>
                      <span style={{ fontSize: "0.825rem", fontWeight: 600, color: "var(--text-primary)" }}>
                        {emp.name}
                      </span>
                      {isAdmin ? (
                        <span
                          style={{
                            fontSize: "0.6rem",
                            padding: "0.1rem 0.35rem",
                            borderRadius: "var(--radius-full)",
                            backgroundColor: "rgba(99, 102, 241, 0.15)",
                            color: "var(--brand-primary)",
                            fontWeight: 700
                          }}
                        >
                          HR
                        </span>
                      ) : null}
                    </div>
                    <div style={{ fontSize: "0.725rem", color: "var(--text-muted)", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                      {emp.designation} • {emp.department}
                    </div>
                  </div>
                  {isSelected && <Check size={16} style={{ color: "var(--brand-primary)", flexShrink: 0 }} />}
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
