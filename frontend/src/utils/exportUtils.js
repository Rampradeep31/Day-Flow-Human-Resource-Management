// Export utilities for Dayflow HRMS (CSV & Reports)

export function exportAttendanceToCSV(records, filename = "Dayflow_Attendance_Report.csv") {
  if (!records || records.length === 0) return;

  const headers = ["Record ID", "Employee ID", "Employee Name", "Date", "Check In", "Check Out", "Status", "Work Mode", "Work Hours", "Notes"];
  const rows = records.map(r => [
    `"${r.id || ""}"`,
    `"${r.employeeId || ""}"`,
    `"${r.employeeName || ""}"`,
    `"${r.date || ""}"`,
    `"${r.checkIn || "N/A"}"`,
    `"${r.checkOut || "N/A"}"`,
    `"${r.status || ""}"`,
    `"${r.workMode || ""}"`,
    `"${r.workHours || ""}"`,
    `"${(r.notes || "").replace(/"/g, '""')}"`
  ]);

  const csvContent = "data:text/csv;charset=utf-8," + [headers.join(","), ...rows.map(e => e.join(","))].join("\n");
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", filename);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

export function exportPayrollSummaryCSV(employees, filename = "Dayflow_Payroll_Summary.csv") {
  if (!employees || employees.length === 0) return;

  const headers = ["Employee ID", "Full Name", "Department", "Designation", "Basic Pay ($)", "HRA ($)", "Allowances ($)", "Bonus ($)", "Gross Pay ($)", "PF ($)", "Tax ($)", "Insurance ($)", "Total Deductions ($)", "Net In-Hand ($)"];
  
  const rows = employees.map(emp => {
    const s = emp.salaryStructure || {};
    const gross = (s.basic || 0) + (s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0);
    const deductions = (s.providentFund || 0) + (s.taxDeduction || 0) + (s.insurance || 0);
    const net = gross - deductions;

    return [
      `"${emp.id}"`,
      `"${emp.name}"`,
      `"${emp.department}"`,
      `"${emp.designation}"`,
      s.basic || 0,
      s.hra || 0,
      s.specialAllowance || 0,
      s.bonus || 0,
      gross,
      s.providentFund || 0,
      s.taxDeduction || 0,
      s.insurance || 0,
      deductions,
      net
    ];
  });

  const csvContent = "data:text/csv;charset=utf-8," + [headers.join(","), ...rows.map(e => e.join(","))].join("\n");
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", filename);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}
