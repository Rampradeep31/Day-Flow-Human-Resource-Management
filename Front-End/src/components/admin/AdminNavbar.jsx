import React, { useState } from "react";
import { 
  Users, 
  CalendarCheck2, 
  Clock, 
  DollarSign, 
  Search, 
  CheckCircle, 
  XCircle, 
  Edit3, 
  UserCheck, 
  Filter 
} from "lucide-react";

// Mock Pre-seeded Data for immediate visual feedback
const INITIAL_EMPLOYEES = [
  { id: "EMP-101", name: "Sarah Jenkins", role: "Software Engineer", dept: "Engineering", email: "sarah.j@dayflow.io", phone: "+1 234 567 890", salary: 85000, status: "Active" },
  { id: "EMP-102", name: "Alex Rivera", role: "UI/UX Designer", dept: "Product", email: "alex.r@dayflow.io", phone: "+1 234 567 891", salary: 72000, status: "Active" },
  { id: "EMP-103", name: "David Chen", role: "DevOps Engineer", dept: "Infrastructure", email: "david.c@dayflow.io", phone: "+1 234 567 892", salary: 92000, status: "Active" },
  { id: "EMP-104", name: "Priya Sharma", role: "QA Automation Lead", dept: "Quality", email: "priya.s@dayflow.io", phone: "+1 234 567 893", salary: 78000, status: "On Leave" },
];

const INITIAL_ATTENDANCE = [
  { id: "EMP-101", name: "Sarah Jenkins", checkIn: "08:55 AM", checkOut: "05:05 PM", status: "Present" },
  { id: "EMP-102", name: "Alex Rivera", checkIn: "09:15 AM", checkOut: "--", status: "Half-day" },
  { id: "EMP-103", name: "David Chen", checkIn: "09:00 AM", checkOut: "--", status: "Present" },
  { id: "EMP-104", name: "Priya Sharma", checkIn: "--", checkOut: "--", status: "Leave" },
];

const INITIAL_LEAVES = [
  { id: "LV-201", empId: "EMP-104", name: "Priya Sharma", type: "Sick", from: "2026-08-22", to: "2026-08-24", reason: "Viral fever and doctor prescribed rest.", status: "Pending" },
  { id: "LV-202", empId: "EMP-102", name: "Alex Rivera", type: "Paid", from: "2026-08-28", to: "2026-08-29", reason: "Personal family event.", status: "Pending" },
];

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState("employees"); // 'employees' | 'attendance' | 'leaves' | 'payroll'
  const [employees, setEmployees] = useState(INITIAL_EMPLOYEES);
  const [leaves, setLeaves] = useState(INITIAL_LEAVES);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedEmployee, setSelectedEmployee] = useState(null); // For Switcher / Edit Modal
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [actionModal, setActionModal] = useState({ open: false, leaveId: null, actionType: null });
  const [adminRemark, setAdminRemark] = useState("");

  // Handle Leave Approvals / Rejections
  const handleLeaveActionSubmit = () => {
    setLeaves((prev) =>
      prev.map((lv) =>
        lv.id === actionModal.leaveId
          ? { ...lv, status: actionModal.actionType === "approve" ? "Approved" : "Rejected", remark: adminRemark }
          : lv
      )
    );
    setActionModal({ open: false, leaveId: null, actionType: null });
    setAdminRemark("");
  };

  // Handle Full Employee Detail Edits
  const handleSaveEmployee = (e) => {
    e.preventDefault();
    setEmployees((prev) =>
      prev.map((emp) => (emp.id === selectedEmployee.id ? selectedEmployee : emp))
    );
    setIsEditModalOpen(false);
  };

  const filteredEmployees = employees.filter(
    (e) => e.name.toLowerCase().includes(searchTerm.toLowerCase()) || e.id.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 font-sans">
      {/* Top Navbar */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-30 px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="bg-indigo-600 text-white p-2 rounded-xl font-bold tracking-tight text-lg shadow-sm">
            DF
          </div>
          <div>
            <h1 className="text-xl font-bold text-slate-800 leading-none">Dayflow HRMS</h1>
            <span className="text-xs font-semibold text-indigo-600 uppercase tracking-wider">Admin Portal</span>
          </div>
        </div>

        {/* Global Employee Switcher View */}
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2 bg-slate-100 px-3 py-1.5 rounded-lg border border-slate-200">
            <UserCheck className="w-4 h-4 text-slate-500" />
            <span className="text-xs font-medium text-slate-600">Switch View:</span>
            <select
              className="bg-transparent text-xs font-semibold text-slate-800 focus:outline-none cursor-pointer"
              onChange={(e) => {
                const target = employees.find((emp) => emp.id === e.target.value);
                if (target) alert(`Switched to preview profile for: ${target.name} (${target.id})`);
              }}
              defaultValue=""
            >
              <option value="" disabled>Select Employee</option>
              {employees.map((emp) => (
                <option key={emp.id} value={emp.id}>{emp.name} ({emp.id})</option>
              ))}
            </select>
          </div>

          <div className="flex items-center gap-2 pl-4 border-l border-slate-200">
            <div className="w-8 h-8 rounded-full bg-indigo-100 text-indigo-700 flex items-center justify-center font-bold text-sm">
              AD
            </div>
            <div className="text-left hidden sm:block">
              <p className="text-xs font-bold leading-tight">Admin User</p>
              <p className="text-[10px] text-slate-500">hr.admin@dayflow.io</p>
            </div>
          </div>
        </div>
      </header>

      <main className="p-6 max-w-7xl mx-auto space-y-6">
        {/* KPI Metric Cards */}
        <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
            <div>
              <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">Total Headcount</p>
              <h3 className="text-2xl font-black text-slate-800 mt-1">{employees.length}</h3>
              <span className="text-xs text-emerald-600 font-medium font-mono">100% Onboarded</span>
            </div>
            <div className="p-3 bg-indigo-50 text-indigo-600 rounded-xl"><Users className="w-6 h-6" /></div>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
            <div>
              <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">Present Today</p>
              <h3 className="text-2xl font-black text-slate-800 mt-1">
                {INITIAL_ATTENDANCE.filter((a) => a.status === "Present" || a.status === "Half-day").length}
              </h3>
              <span className="text-xs text-emerald-600 font-medium">Active at desks</span>
            </div>
            <div className="p-3 bg-emerald-50 text-emerald-600 rounded-xl"><CalendarCheck2 className="w-6 h-6" /></div>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
            <div>
              <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">Pending Leaves</p>
              <h3 className="text-2xl font-black text-amber-600 mt-1">
                {leaves.filter((l) => l.status === "Pending").length}
              </h3>
              <span className="text-xs text-amber-600 font-medium">Requires sign-off</span>
            </div>
            <div className="p-3 bg-amber-50 text-amber-600 rounded-xl"><Clock className="w-6 h-6" /></div>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
            <div>
              <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">Monthly Payroll Est.</p>
              <h3 className="text-2xl font-black text-slate-800 mt-1">
                ${(employees.reduce((acc, curr) => acc + curr.salary, 0) / 12).toLocaleString(undefined, { maximumFractionDigits: 0 })}
              </h3>
              <span className="text-xs text-slate-500 font-medium">Calculated active CTC</span>
            </div>
            <div className="p-3 bg-violet-50 text-violet-600 rounded-xl"><DollarSign className="w-6 h-6" /></div>
          </div>
        </section>

        {/* Tab Navigation Menu */}
        <div className="flex border-b border-slate-200 gap-6 text-sm font-semibold">
          {[
            { id: "employees", label: "Employee Management", count: employees.length },
            { id: "attendance", label: "Attendance Logs", count: INITIAL_ATTENDANCE.length },
            { id: "leaves", label: "Leave Requests", count: leaves.filter(l => l.status === "Pending").length, highlight: true },
            { id: "payroll", label: "Payroll Control" },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`pb-3 flex items-center gap-2 relative transition-colors ${
                activeTab === tab.id ? "text-indigo-600 border-b-2 border-indigo-600" : "text-slate-500 hover:text-slate-800"
              }`}
            >
              {tab.label}
              {tab.count !== undefined && (
                <span className={`px-2 py-0.5 rounded-full text-xs ${
                  tab.highlight ? "bg-amber-100 text-amber-700" : "bg-slate-100 text-slate-600"
                }`}>
                  {tab.count}
                </span>
              )}
            </button>
          ))}
        </div>

        {/* TAB 1: EMPLOYEE MANAGEMENT & SWITCHER */}
        {activeTab === "employees" && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
            <div className="p-4 border-b border-slate-200 flex flex-col sm:flex-row gap-3 items-center justify-between">
              <div className="relative w-full sm:w-72">
                <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                <input
                  type="text"
                  placeholder="Search by ID or Name..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-9 pr-4 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600"
                />
              </div>
            </div>

            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 text-xs font-semibold text-slate-500 uppercase border-b border-slate-200">
                <tr>
                  <th className="px-6 py-3">Employee</th>
                  <th className="px-6 py-3">Role & Dept</th>
                  <th className="px-6 py-3">Contact</th>
                  <th className="px-6 py-3">Status</th>
                  <th className="px-6 py-3 text-right">Admin Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredEmployees.map((emp) => (
                  <tr key={emp.id} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-semibold text-slate-900">{emp.name}</div>
                      <div className="text-xs text-slate-400 font-mono">{emp.id}</div>
                    </td>
                    <td className="px-6 py-4">
                      <div>{emp.role}</div>
                      <div className="text-xs text-slate-400">{emp.dept}</div>
                    </td>
                    <td className="px-6 py-4 text-xs">
                      <div>{emp.email}</div>
                      <div className="text-slate-400">{emp.phone}</div>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`px-2 py-1 rounded-md text-xs font-semibold ${
                        emp.status === "Active" ? "bg-emerald-50 text-emerald-700" : "bg-blue-50 text-blue-700"
                      }`}>
                        {emp.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <button
                        onClick={() => {
                          setSelectedEmployee(emp);
                          setIsEditModalOpen(true);
                        }}
                        className="p-1.5 text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors"
                        title="Edit all fields"
                      >
                        <Edit3 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* TAB 2: ATTENDANCE TRACKER */}
        {activeTab === "attendance" && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
            <div className="p-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
              <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Today's Live Records</span>
              <div className="flex gap-2">
                <span className="inline-flex items-center gap-1.5 text-xs text-slate-600"><span className="w-2 h-2 rounded-full bg-emerald-500"></span> Present</span>
                <span className="inline-flex items-center gap-1.5 text-xs text-slate-600"><span className="w-2 h-2 rounded-full bg-amber-500"></span> Half-day</span>
                <span className="inline-flex items-center gap-1.5 text-xs text-slate-600"><span className="w-2 h-2 rounded-full bg-blue-500"></span> Leave</span>
              </div>
            </div>
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 text-xs font-semibold text-slate-500 uppercase border-b border-slate-200">
                <tr>
                  <th className="px-6 py-3">Employee ID</th>
                  <th className="px-6 py-3">Name</th>
                  <th className="px-6 py-3">Check In</th>
                  <th className="px-6 py-3">Check Out</th>
                  <th className="px-6 py-3">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {INITIAL_ATTENDANCE.map((att) => (
                  <tr key={att.id} className="hover:bg-slate-50">
                    <td className="px-6 py-4 font-mono text-xs">{att.id}</td>
                    <td className="px-6 py-4 font-semibold text-slate-900">{att.name}</td>
                    <td className="px-6 py-4 font-mono text-xs">{att.checkIn}</td>
                    <td className="px-6 py-4 font-mono text-xs">{att.checkOut}</td>
                    <td className="px-6 py-4">
                      <span className={`px-2 py-1 rounded-md text-xs font-semibold ${
                        att.status === "Present" ? "bg-emerald-50 text-emerald-700" :
                        att.status === "Half-day" ? "bg-amber-50 text-amber-700" : "bg-blue-50 text-blue-700"
                      }`}>
                        {att.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* TAB 3: LEAVE APPROVAL CENTER */}
        {activeTab === "leaves" && (
          <div className="space-y-4">
            {leaves.map((lv) => (
              <div key={lv.id} className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div>
                  <div className="flex items-center gap-3">
                    <h4 className="font-bold text-slate-900">{lv.name}</h4>
                    <span className="text-xs px-2 py-0.5 rounded bg-indigo-50 text-indigo-700 font-semibold">{lv.type} Leave</span>
                    <span className={`text-xs px-2 py-0.5 rounded font-semibold ${
                      lv.status === "Pending" ? "bg-amber-50 text-amber-700" :
                      lv.status === "Approved" ? "bg-emerald-50 text-emerald-700" : "bg-red-50 text-red-700"
                    }`}>
                      {lv.status}
                    </span>
                  </div>
                  <p className="text-xs text-slate-400 font-mono mt-0.5">Duration: {lv.from} to {lv.to}</p>
                  <p className="text-sm text-slate-600 mt-2 bg-slate-50 p-2.5 rounded-lg border border-slate-100">
                    <span className="font-semibold text-slate-700">Reason:</span> "{lv.reason}"
                  </p>
                  {lv.remark && <p className="text-xs text-slate-500 italic mt-1">Admin Remark: {lv.remark}</p>}
                </div>

                {lv.status === "Pending" && (
                  <div className="flex gap-2 w-full md:w-auto">
                    <button
                      onClick={() => setActionModal({ open: true, leaveId: lv.id, actionType: "approve" })}
                      className="flex-1 md:flex-none flex items-center justify-center gap-1.5 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-lg transition-colors"
                    >
                      <CheckCircle className="w-4 h-4" /> Approve
                    </button>
                    <button
                      onClick={() => setActionModal({ open: true, leaveId: lv.id, actionType: "reject" })}
                      className="flex-1 md:flex-none flex items-center justify-center gap-1.5 px-4 py-2 bg-red-600 hover:bg-red-700 text-white text-xs font-bold rounded-lg transition-colors"
                    >
                      <XCircle className="w-4 h-4" /> Reject
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {/* TAB 4: PAYROLL CONTROL */}
        {activeTab === "payroll" && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 text-xs font-semibold text-slate-500 uppercase border-b border-slate-200">
                <tr>
                  <th className="px-6 py-3">Employee ID</th>
                  <th className="px-6 py-3">Name</th>
                  <th className="px-6 py-3">Annual CTC</th>
                  <th className="px-6 py-3">Monthly Basic</th>
                  <th className="px-6 py-3 text-right">Salary Structure</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {employees.map((emp) => (
                  <tr key={emp.id} className="hover:bg-slate-50">
                    <td className="px-6 py-4 font-mono text-xs">{emp.id}</td>
                    <td className="px-6 py-4 font-semibold text-slate-900">{emp.name}</td>
                    <td className="px-6 py-4 font-mono font-semibold text-slate-800">${emp.salary.toLocaleString()}</td>
                    <td className="px-6 py-4 font-mono">${(emp.salary / 12).toFixed(2)}</td>
                    <td className="px-6 py-4 text-right">
                      <button
                        onClick={() => {
                          setSelectedEmployee(emp);
                          setIsEditModalOpen(true);
                        }}
                        className="text-xs text-indigo-600 hover:text-indigo-800 font-semibold"
                      >
                        Adjust Structure
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>

      {/* MODAL 1: ADMIN FULL EDIT MODAL (Section 3.3.2 & 3.6.2) */}
      {isEditModalOpen && selectedEmployee && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl border border-slate-200 w-full max-w-lg overflow-hidden">
            <div className="p-4 bg-slate-50 border-b border-slate-200 flex justify-between items-center">
              <h3 className="font-bold text-slate-800 text-sm">Admin Edit: {selectedEmployee.name}</h3>
              <button onClick={() => setIsEditModalOpen(false)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>
            <form onSubmit={handleSaveEmployee} className="p-5 space-y-3">
              <div>
                <label className="text-xs font-semibold text-slate-500">Full Name</label>
                <input
                  type="text"
                  value={selectedEmployee.name}
                  onChange={(e) => setSelectedEmployee({ ...selectedEmployee, name: e.target.value })}
                  className="w-full mt-1 p-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-500">Role Designation</label>
                  <input
                    type="text"
                    value={selectedEmployee.role}
                    onChange={(e) => setSelectedEmployee({ ...selectedEmployee, role: e.target.value })}
                    className="w-full mt-1 p-2 text-sm border border-slate-200 rounded-lg"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-500">Department</label>
                  <input
                    type="text"
                    value={selectedEmployee.dept}
                    onChange={(e) => setSelectedEmployee({ ...selectedEmployee, dept: e.target.value })}
                    className="w-full mt-1 p-2 text-sm border border-slate-200 rounded-lg"
                  />
                </div>
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-500">Annual Base CTC ($)</label>
                <input
                  type="number"
                  value={selectedEmployee.salary}
                  onChange={(e) => setSelectedEmployee({ ...selectedEmployee, salary: Number(e.target.value) })}
                  className="w-full mt-1 p-2 text-sm border border-slate-200 rounded-lg"
                />
              </div>
              <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
                <button type="button" onClick={() => setIsEditModalOpen(false)} className="px-4 py-2 text-xs font-semibold text-slate-600">Cancel</button>
                <button type="submit" className="px-4 py-2 bg-indigo-600 text-white text-xs font-bold rounded-lg hover:bg-indigo-700">Save Changes</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 2: LEAVE ACTION WITH REMARKS (Section 3.5.2) */}
      {actionModal.open && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl border border-slate-200 w-full max-w-sm overflow-hidden">
            <div className="p-4 bg-slate-50 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-sm capitalize">{actionModal.actionType} Leave Request</h3>
            </div>
            <div className="p-4 space-y-3">
              <label className="text-xs font-semibold text-slate-500">Admin Remarks / Notes</label>
              <textarea
                rows={3}
                placeholder="Add comments or instructions..."
                value={adminRemark}
                onChange={(e) => setAdminRemark(e.target.value)}
                className="w-full p-2.5 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
              <div className="flex justify-end gap-2 pt-2">
                <button onClick={() => setActionModal({ open: false, leaveId: null, actionType: null })} className="px-3 py-1.5 text-xs font-semibold text-slate-600">Cancel</button>
                <button
                  onClick={handleLeaveActionSubmit}
                  className={`px-4 py-1.5 text-xs font-bold rounded-lg text-white ${
                    actionModal.actionType === "approve" ? "bg-emerald-600 hover:bg-emerald-700" : "bg-red-600 hover:bg-red-700"
                  }`}
                >
                  Confirm {actionModal.actionType === "approve" ? "Approval" : "Rejection"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
