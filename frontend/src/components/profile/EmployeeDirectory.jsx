import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import StatusBadge from "../common/StatusBadge";
import AddEmployeeModal from "./AddEmployeeModal";
import {
  Users,
  Search,
  Filter,
  Plus,
  Mail,
  Phone,
  MapPin,
  Building,
  UserCheck,
  LayoutGrid,
  List,
  ArrowRight
} from "lucide-react";

export default function EmployeeDirectory() {
  const {
    employees,
    currentUser,
    setSelectedEmployeeId,
    setActiveTab,
    switchUser,
    isAddEmployeeOpen,
    setIsAddEmployeeOpen
  } = useHRMS();

  const isAdmin = currentUser.role === "admin";
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedDept, setSelectedDept] = useState("all");
  const [viewMode, setViewMode] = useState("grid"); // "grid" | "list"

  const departments = ["Engineering", "Product Design", "Marketing", "Human Resources", "Finance"];

  const filtered = employees.filter((emp) => {
    const matchesSearch =
      emp.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      emp.designation.toLowerCase().includes(searchQuery.toLowerCase()) ||
      emp.department.toLowerCase().includes(searchQuery.toLowerCase()) ||
      emp.id.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesDept = selectedDept === "all" || emp.department === selectedDept;

    return matchesSearch && matchesDept;
  });

  const handleInspectProfile = (empId) => {
    setSelectedEmployeeId(empId);
    setActiveTab("profile");
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
      {/* Header Banner */}
      <div
        className="glass-card"
        style={{
          padding: "1.5rem 2rem",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "1rem"
        }}
      >
        <div>
          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
            <Users size={20} style={{ color: "var(--brand-primary)" }} />
            <h1 style={{ fontSize: "1.4rem", fontWeight: 800 }}>Employee Directory</h1>
          </div>
          <p style={{ fontSize: "0.85rem", color: "var(--text-secondary)", marginTop: "0.2rem" }}>
            Directory of {employees.length} team members across 5 global departments.
          </p>
        </div>

        {isAdmin && (
          <button onClick={() => setIsAddEmployeeOpen(true)} className="btn btn-primary">
            <Plus size={16} /> Onboard New Employee
          </button>
        )}
      </div>

      {/* Filter and Search Toolbar */}
      <div
        className="glass-card"
        style={{
          padding: "1rem 1.5rem",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "0.75rem"
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "0.75rem", flexWrap: "wrap" }}>
          <div style={{ position: "relative", minWidth: "240px" }}>
            <input
              type="text"
              placeholder="Search by name, role, ID..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="input-control"
              style={{ paddingLeft: "2.2rem" }}
            />
            <Search size={15} style={{ position: "absolute", left: "0.75rem", top: "50%", transform: "translateY(-50%)", color: "var(--text-muted)" }} />
          </div>

          <select
            value={selectedDept}
            onChange={(e) => setSelectedDept(e.target.value)}
            className="input-control"
            style={{ width: "auto" }}
          >
            <option value="all">All Departments</option>
            {departments.map((d) => (
              <option key={d} value={d}>
                {d}
              </option>
            ))}
          </select>
        </div>

        {/* View mode toggle (Grid vs List) */}
        <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
          <div style={{ display: "flex", backgroundColor: "var(--bg-tertiary)", padding: "0.2rem", borderRadius: "var(--radius-sm)" }}>
            <button
              onClick={() => setViewMode("grid")}
              className="btn-ghost"
              style={{
                padding: "0.35rem 0.5rem",
                borderRadius: "var(--radius-sm)",
                backgroundColor: viewMode === "grid" ? "var(--bg-elevated)" : "transparent",
                color: viewMode === "grid" ? "var(--brand-primary)" : "var(--text-muted)"
              }}
            >
              <LayoutGrid size={15} />
            </button>
            <button
              onClick={() => setViewMode("list")}
              className="btn-ghost"
              style={{
                padding: "0.35rem 0.5rem",
                borderRadius: "var(--radius-sm)",
                backgroundColor: viewMode === "list" ? "var(--bg-elevated)" : "transparent",
                color: viewMode === "list" ? "var(--brand-primary)" : "var(--text-muted)"
              }}
            >
              <List size={15} />
            </button>
          </div>
        </div>
      </div>

      {/* Content Rendering: Grid vs List */}
      {viewMode === "grid" ? (
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))", gap: "1.25rem" }}>
          {filtered.map((emp) => (
            <div
              key={emp.id}
              className="glass-card"
              style={{
                padding: "1.5rem",
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                textAlign: "center",
                gap: "0.75rem",
                position: "relative"
              }}
            >
              {/* Role pill badge */}
              <div style={{ position: "absolute", top: "1rem", right: "1rem" }}>
                <StatusBadge status={emp.role === "admin" ? "Admin" : "Employee"} />
              </div>

              <img
                src={emp.avatar}
                alt={emp.name}
                style={{
                  width: "72px",
                  height: "72px",
                  borderRadius: "50%",
                  objectFit: "cover",
                  border: "3px solid var(--border-color)",
                  marginTop: "0.5rem"
                }}
              />

              <div>
                <h3 style={{ fontSize: "1.05rem", fontWeight: 700, color: "var(--text-primary)" }}>{emp.name}</h3>
                <div style={{ fontSize: "0.775rem", color: "var(--brand-primary)", fontWeight: 600, marginTop: "2px" }}>
                  {emp.designation}
                </div>
                <div style={{ fontSize: "0.725rem", color: "var(--text-muted)", marginTop: "2px" }}>
                  {emp.department} • <span style={{ fontFamily: "var(--font-mono)" }}>{emp.id}</span>
                </div>
              </div>

              <div style={{ width: "100%", padding: "0.75rem", backgroundColor: "var(--bg-tertiary)", borderRadius: "var(--radius-md)", fontSize: "0.75rem", display: "flex", flexDirection: "column", gap: "0.3rem", textAlign: "left" }}>
                <div style={{ display: "flex", alignItems: "center", gap: "0.4rem", color: "var(--text-secondary)" }}>
                  <Mail size={13} style={{ color: "var(--text-muted)" }} />
                  <span style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{emp.email}</span>
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: "0.4rem", color: "var(--text-secondary)" }}>
                  <MapPin size={13} style={{ color: "var(--text-muted)" }} />
                  <span style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{emp.workLocation}</span>
                </div>
              </div>

              <div style={{ width: "100%", display: "flex", gap: "0.5rem", marginTop: "auto" }}>
                <button
                  onClick={() => handleInspectProfile(emp.id)}
                  className="btn btn-secondary btn-sm"
                  style={{ flex: 1 }}
                >
                  View Profile
                </button>
                <button
                  onClick={() => switchUser(emp.id)}
                  className="btn btn-primary btn-sm"
                  style={{ flex: 1 }}
                  title="Act as this user"
                >
                  Act As
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Employee</th>
                <th>Employee ID</th>
                <th>Designation</th>
                <th>Department</th>
                <th>Location</th>
                <th>Contact</th>
                <th>Role</th>
                <th style={{ textAlign: "right" }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((emp) => (
                <tr key={emp.id}>
                  <td>
                    <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
                      <img
                        src={emp.avatar}
                        alt=""
                        style={{ width: "32px", height: "32px", borderRadius: "50%", objectFit: "cover" }}
                      />
                      <span style={{ fontWeight: 700, color: "var(--text-primary)" }}>{emp.name}</span>
                    </div>
                  </td>
                  <td>
                    <span style={{ fontFamily: "var(--font-mono)", fontSize: "0.8rem", color: "var(--text-muted)" }}>
                      {emp.id}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontWeight: 600, fontSize: "0.825rem" }}>{emp.designation}</span>
                  </td>
                  <td>
                    <span style={{ fontSize: "0.8rem", color: "var(--text-secondary)" }}>{emp.department}</span>
                  </td>
                  <td>
                    <span style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}>{emp.workLocation}</span>
                  </td>
                  <td>
                    <span style={{ fontSize: "0.8rem", color: "var(--text-secondary)" }}>{emp.phone}</span>
                  </td>
                  <td>
                    <StatusBadge status={emp.role === "admin" ? "Admin" : "Employee"} />
                  </td>
                  <td style={{ textAlign: "right" }}>
                    <div style={{ display: "flex", justifyContent: "flex-end", gap: "0.35rem" }}>
                      <button
                        onClick={() => handleInspectProfile(emp.id)}
                        className="btn btn-secondary btn-sm"
                        style={{ padding: "0.25rem 0.5rem" }}
                      >
                        Profile
                      </button>
                      <button
                        onClick={() => switchUser(emp.id)}
                        className="btn btn-primary btn-sm"
                        style={{ padding: "0.25rem 0.5rem" }}
                      >
                        Act As
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {isAddEmployeeOpen && (
        <AddEmployeeModal
          isOpen={isAddEmployeeOpen}
          onClose={() => setIsAddEmployeeOpen(false)}
        />
      )}
    </div>
  );
}
