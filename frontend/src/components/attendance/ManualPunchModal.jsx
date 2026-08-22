import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import Modal from "../common/Modal";

export default function ManualPunchModal({ isOpen, onClose, recordToEdit }) {
  const { employees, adminUpdateAttendance, adminAddAttendanceRecord } = useHRMS();

  const [employeeId, setEmployeeId] = useState(recordToEdit?.employeeId || employees[0].id);
  const [date, setDate] = useState(recordToEdit?.date || new Date().toISOString().split("T")[0]);
  const [checkIn, setCheckIn] = useState(recordToEdit?.checkIn || "09:00 AM");
  const [checkOut, setCheckOut] = useState(recordToEdit?.checkOut || "05:30 PM");
  const [workMode, setWorkMode] = useState(recordToEdit?.workMode || "Office");
  const [status, setStatus] = useState(recordToEdit?.status || "Present");
  const [workHours, setWorkHours] = useState(recordToEdit?.workHours || "8.5 hrs");
  const [notes, setNotes] = useState(recordToEdit?.notes || "Adjusted by HR Administrator");

  const handleSubmit = (e) => {
    e.preventDefault();
    const emp = employees.find((x) => x.id === employeeId);

    if (recordToEdit) {
      adminUpdateAttendance(recordToEdit.id, {
        checkIn,
        checkOut,
        workMode,
        status,
        workHours,
        notes
      });
    } else {
      adminAddAttendanceRecord({
        id: `ATT-MANUAL-${Date.now()}`,
        employeeId,
        employeeName: emp ? emp.name : "Employee",
        date,
        checkIn,
        checkOut,
        workMode,
        status,
        workHours,
        breakMinutes: 30,
        notes
      });
    }
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={recordToEdit ? `Edit Attendance Record (${recordToEdit.employeeName})` : "Record Manual Attendance Punch"}
    >
      <form onSubmit={handleSubmit}>
        <div className="modal-body" style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          {!recordToEdit && (
            <div className="input-group">
              <label className="input-label">Select Employee</label>
              <select
                value={employeeId}
                onChange={(e) => setEmployeeId(e.target.value)}
                className="input-control"
              >
                {employees.map((emp) => (
                  <option key={emp.id} value={emp.id}>
                    {emp.name} ({emp.id} - {emp.department})
                  </option>
                ))}
              </select>
            </div>
          )}

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">Date</label>
              <input
                type="date"
                required
                value={date}
                onChange={(e) => setDate(e.target.value)}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Attendance Status</label>
              <select
                value={status}
                onChange={(e) => setStatus(e.target.value)}
                className="input-control"
              >
                <option value="Present">Present</option>
                <option value="Half-day">Half-day</option>
                <option value="Absent">Absent</option>
                <option value="Leave">Leave</option>
                <option value="Holiday">Holiday</option>
              </select>
            </div>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">Check-In Time</label>
              <input
                type="text"
                placeholder="e.g. 09:00 AM"
                value={checkIn}
                onChange={(e) => setCheckIn(e.target.value)}
                className="input-control"
              />
            </div>

            <div className="input-group">
              <label className="input-label">Check-Out Time</label>
              <input
                type="text"
                placeholder="e.g. 05:30 PM"
                value={checkOut}
                onChange={(e) => setCheckOut(e.target.value)}
                className="input-control"
              />
            </div>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
            <div className="input-group">
              <label className="input-label">Work Mode</label>
              <select
                value={workMode}
                onChange={(e) => setWorkMode(e.target.value)}
                className="input-control"
              >
                <option value="Office">In Office (HQ)</option>
                <option value="Remote">Remote</option>
              </select>
            </div>

            <div className="input-group">
              <label className="input-label">Total Duration</label>
              <input
                type="text"
                placeholder="e.g. 8.5 hrs"
                value={workHours}
                onChange={(e) => setWorkHours(e.target.value)}
                className="input-control"
              />
            </div>
          </div>

          <div className="input-group">
            <label className="input-label">HR Remarks / Reason for Manual Entry</label>
            <input
              type="text"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              className="input-control"
              placeholder="e.g. Biometric reader sync correction"
            />
          </div>
        </div>

        <div className="modal-footer">
          <button type="button" onClick={onClose} className="btn btn-secondary">
            Cancel
          </button>
          <button type="submit" className="btn btn-primary">
            {recordToEdit ? "Save Changes" : "Record Punch"}
          </button>
        </div>
      </form>
    </Modal>
  );
}
