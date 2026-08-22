import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import StatusBadge from "../common/StatusBadge";
import ManualPunchModal from "./ManualPunchModal";
import {
  Search,
  Filter,
  Download,
  Plus,
  Edit2,
  Calendar,
  Building,
  CheckCircle2,
  Clock
} from "lucide-react";
import { formatDate } from "../../utils/dateUtils";
import { exportAttendanceToCSV } from "../../utils/exportUtils";

export default function AdminCompanyAttendance() {
  const { attendanceRecords, employees } = useHRMS();

  const [searchQuery, setSearchQuery] = useState("");
  const [selectedDept, setSelectedDept] = useState("all");
  const [selectedStatus, setSelectedStatus] = useState("all");
  const [selectedDate, setSelectedDate] = useState("2026-08-22");

  const [isManualModalOpen, setIsManualModalOpen] = useState(false);
  const [recordToEdit, setRecordToEdit] = useState(null);

  // Departments list
  const departments = ["Engineering", "Product Design", "Marketing", "Human Resources", "Finance"];

  // Filter records
  const filtered = attendanceRecords.filter((rec) => {
    const emp = employees.find((e) => e.id === rec.employeeId);
    const matchesSearch =
      rec.employeeName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      rec.employeeId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (emp && emp.department.toLowerCase().includes(searchQuery.toLowerCase()));

    const matchesDept = selectedDept === "all" || (emp && emp.department === selectedDept);
    const matchesStatus = selectedStatus === "all" || rec.status.toLowerCase() === selectedStatus.toLowerCase();
    const matchesDate = !selectedDate || rec.date === selectedDate;

    return matchesSearch && matchesDept && matchesStatus && matchesDate;
  });

  const handleEditRecord = (record) => {
    setRecordToEdit(record);
    setIsManualModalOpen(true);
  };

  const handleCreateRecord = () => {
    setRecordToEdit(null);
    setIsManualModalOpen(true);
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
      {/* Action Controls & Filters */}
      <div
        className="glass-card"
        style={{
          padding: "1.25rem 1.5rem",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "1rem"
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "0.75rem", flexWrap: "wrap" }}>
          {/* Search Box */}
          <div style={{ position: "relative", minWidth: "220px" }}>
            <input
              type="text"
              placeholder="Search employee..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="input-control"
              style={{ paddingLeft: "2.2rem" }}
            />
            <Search size={15} style={{ position: "absolute", left: "0.75rem", top: "50%", transform: "translateY(-50%)", color: "var(--text-muted)" }} />
          </div>

          {/* Department Filter */}
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

          {/* Status Filter */}
          <select
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value)}
            className="input-control"
            style={{ width: "auto" }}
          >
            <option value="all">All Statuses</option>
            <option value="present">Present</option>
            <option value="half-day">Half-day</option>
            <option value="absent">Absent</option>
            <option value="leave">On Leave</option>
          </select>

          {/* Date Picker */}
          <input
            type="date"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="input-control"
            style={{ width: "auto" }}
          />
        </div>

        <div style={{ display: "flex", gap: "0.5rem" }}>
          <button onClick={() => exportAttendanceToCSV(filtered)} className="btn btn-secondary btn-sm">
            <Download size={14} /> Export CSV
          </button>
          <button onClick={handleCreateRecord} className="btn btn-primary btn-sm">
            <Plus size={14} /> Manual Punch Entry
          </button>
        </div>
      </div>

      {/* Table Sheet */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Employee</th>
              <th>Department</th>
              <th>Date</th>
              <th>Check In</th>
              <th>Check Out</th>
              <th>Work Mode</th>
              <th>Work Hours</th>
              <th>Status</th>
              <th>Notes / Remarks</th>
              <th style={{ textAlign: "right" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan={10} style={{ textAlign: "center", padding: "2.5rem", color: "var(--text-muted)" }}>
                  No attendance records found matching filters for this date.
                </td>
              </tr>
            ) : (
              filtered.map((rec) => {
                const emp = employees.find((e) => e.id === rec.employeeId);

                return (
                  <tr key={rec.id}>
                    <td>
                      <div style={{ display: "flex", alignItems: "center", gap: "0.65rem" }}>
                        <img
                          src={emp?.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${rec.employeeName}`}
                          alt=""
                          style={{ width: "30px", height: "30px", borderRadius: "50%", objectFit: "cover" }}
                        />
                        <div>
                          <div style={{ fontWeight: 700, color: "var(--text-primary)" }}>{rec.employeeName}</div>
                          <div style={{ fontSize: "0.725rem", color: "var(--text-muted)", fontFamily: "var(--font-mono)" }}>
                            {rec.employeeId}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <span style={{ fontSize: "0.8rem", color: "var(--text-secondary)" }}>
                        {emp?.department || "General"}
                      </span>
                    </td>
                    <td>
                      <span style={{ fontSize: "0.8rem", fontFamily: "var(--font-mono)" }}>{formatDate(rec.date)}</span>
                    </td>
                    <td>
                      <span style={{ fontWeight: 600, color: "var(--text-primary)" }}>{rec.checkIn || "—"}</span>
                    </td>
                    <td>
                      <span style={{ fontWeight: 600, color: "var(--text-primary)" }}>
                        {rec.checkOut || (rec.checkIn ? "In Progress" : "—")}
                      </span>
                    </td>
                    <td>
                      <span style={{ fontSize: "0.8rem", color: "var(--text-secondary)" }}>{rec.workMode || "—"}</span>
                    </td>
                    <td>
                      <span style={{ fontWeight: 700, color: "var(--brand-primary)", fontFamily: "var(--font-mono)" }}>
                        {rec.workHours}
                      </span>
                    </td>
                    <td>
                      <StatusBadge status={rec.status} />
                    </td>
                    <td>
                      <span style={{ fontSize: "0.775rem", color: "var(--text-muted)" }}>
                        {rec.notes || "—"}
                      </span>
                    </td>
                    <td style={{ textAlign: "right" }}>
                      <button
                        onClick={() => handleEditRecord(rec)}
                        className="btn-ghost btn-sm"
                        style={{ padding: "0.3rem 0.5rem" }}
                        title="Adjust / Edit record"
                      >
                        <Edit2 size={14} />
                      </button>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {isManualModalOpen && (
        <ManualPunchModal
          isOpen={isManualModalOpen}
          onClose={() => setIsManualModalOpen(false)}
          recordToEdit={recordToEdit}
        />
      )}
    </div>
  );
}
