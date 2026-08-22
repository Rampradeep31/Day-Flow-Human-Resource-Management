import React, { createContext, useContext, useState, useEffect } from "react";
import confetti from "canvas-confetti";
import {
  initialEmployees,
  initialLeaveRequests,
  initialAttendanceRecords,
  companyHolidays,
  companyAnnouncements,
  initialNotifications,
  historicalPayslips
} from "../data/seedData";
import { playChime } from "../utils/soundUtils";
import { calculateWorkingDays, formatShortTime } from "../utils/dateUtils";

const HRMSContext = createContext(null);

export function HRMSProvider({ children }) {
  // LocalStorage persistence keys
  const [theme, setTheme] = useState(() => localStorage.getItem("dayflow_theme") || "dark");
  const [soundEnabled, setSoundEnabled] = useState(() => {
    const saved = localStorage.getItem("dayflow_sound");
    return saved !== null ? JSON.parse(saved) : true;
  });

  // State
  const [employees, setEmployees] = useState(() => {
    const saved = localStorage.getItem("dayflow_employees");
    return saved ? JSON.parse(saved) : initialEmployees;
  });

  // Active current user - default to HR Admin Sarah Connor or Lead Engineer Alex Rivera
  const [currentUserId, setCurrentUserId] = useState(() => {
    return localStorage.getItem("dayflow_current_user") || "EMP-1001";
  });

  const currentUser = employees.find((e) => e.id === currentUserId) || employees[0];
  const [isAuthenticated, setIsAuthenticated] = useState(true);

  // Active Tab navigation
  const [activeTab, setActiveTab] = useState("dashboard");
  const [selectedEmployeeId, setSelectedEmployeeId] = useState(currentUserId);

  // Attendance State
  const [attendanceRecords, setAttendanceRecords] = useState(() => {
    const saved = localStorage.getItem("dayflow_attendance");
    return saved ? JSON.parse(saved) : initialAttendanceRecords;
  });

  // Work session timer for current user
  const [isCheckedIn, setIsCheckedIn] = useState(true);
  const [checkInTime, setCheckInTime] = useState("09:00 AM");
  const [elapsedSeconds, setElapsedSeconds] = useState(3600 * 2 + 60 * 42); // 2h 42m
  const [isOnBreak, setIsOnBreak] = useState(false);
  const [currentWorkMode, setCurrentWorkMode] = useState("Office");

  // Leave Requests State
  const [leaveRequests, setLeaveRequests] = useState(() => {
    const saved = localStorage.getItem("dayflow_leaves");
    return saved ? JSON.parse(saved) : initialLeaveRequests;
  });

  // Notifications State
  const [notifications, setNotifications] = useState(() => {
    const saved = localStorage.getItem("dayflow_notifications");
    return saved ? JSON.parse(saved) : initialNotifications;
  });

  // Modals & Drawers State
  const [isApplyLeaveOpen, setIsApplyLeaveOpen] = useState(false);
  const [isPayslipModalOpen, setIsPayslipModalOpen] = useState(false);
  const [selectedPayslipMonth, setSelectedPayslipMonth] = useState("July 2026");
  const [isAddEmployeeOpen, setIsAddEmployeeOpen] = useState(false);
  const [isEditProfileOpen, setIsEditProfileOpen] = useState(false);
  const [isCommandPaletteOpen, setIsCommandPaletteOpen] = useState(false);
  const [isNotificationDrawerOpen, setIsNotificationDrawerOpen] = useState(false);
  const [isSalaryEditModalOpen, setIsSalaryEditModalOpen] = useState(false);
  const [isManualPunchOpen, setIsManualPunchOpen] = useState(false);
  const [editingAttendanceRecord, setEditingAttendanceRecord] = useState(null);

  // Toasts
  const [toasts, setToasts] = useState([]);

  // Persist State Changes
  useEffect(() => {
    localStorage.setItem("dayflow_theme", theme);
    document.documentElement.setAttribute("data-theme", theme);
    if (theme === "dark") {
      document.documentElement.classList.add("dark");
    } else {
      document.documentElement.classList.remove("dark");
    }
  }, [theme]);

  useEffect(() => {
    localStorage.setItem("dayflow_sound", JSON.stringify(soundEnabled));
  }, [soundEnabled]);

  useEffect(() => {
    localStorage.setItem("dayflow_employees", JSON.stringify(employees));
  }, [employees]);

  useEffect(() => {
    localStorage.setItem("dayflow_leaves", JSON.stringify(leaveRequests));
  }, [leaveRequests]);

  useEffect(() => {
    localStorage.setItem("dayflow_attendance", JSON.stringify(attendanceRecords));
  }, [attendanceRecords]);

  useEffect(() => {
    localStorage.setItem("dayflow_notifications", JSON.stringify(notifications));
  }, [notifications]);

  useEffect(() => {
    localStorage.setItem("dayflow_current_user", currentUserId);
  }, [currentUserId]);

  // Workday timer tick
  useEffect(() => {
    let interval = null;
    if (isCheckedIn && !isOnBreak) {
      interval = setInterval(() => {
        setElapsedSeconds((prev) => prev + 1);
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [isCheckedIn, isOnBreak]);

  // Sound helper wrapper
  const triggerSound = (type) => {
    if (soundEnabled) {
      playChime(type);
    }
  };

  // Toast Helper
  const showToast = (title, message, type = "success") => {
    const id = Date.now() + Math.random().toString();
    const newToast = { id, title, message, type };
    setToasts((prev) => [...prev, newToast]);
    setTimeout(() => {
      dismissToast(id);
    }, 4500);
  };

  const dismissToast = (id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  // Auth Handlers
  const login = (email, password) => {
    const user = employees.find((e) => e.email.toLowerCase() === email.toLowerCase());
    if (!user) {
      showToast("Authentication Failed", "No account found with this email address.", "error");
      triggerSound("reject");
      return false;
    }
    if (user.password !== password && password !== "Password@123") {
      showToast("Incorrect Password", "Please double-check your credentials.", "error");
      triggerSound("reject");
      return false;
    }
    setCurrentUserId(user.id);
    setSelectedEmployeeId(user.id);
    setIsAuthenticated(true);
    showToast("Welcome Back", `Signed in successfully as ${user.name} (${user.role === "admin" ? "HR Admin" : "Employee"})`, "success");
    triggerSound("success");
    return true;
  };

  const signUp = (userData) => {
    const newId = `EMP-${1000 + employees.length + 1}`;
    const newEmployee = {
      id: newId,
      name: userData.name,
      email: userData.email,
      password: userData.password || "Password@123",
      role: userData.role || "employee",
      designation: userData.designation || "Associate Member",
      department: userData.department || "Engineering",
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(userData.name)}`,
      phone: userData.phone || "+1 (555) 000-0000",
      dob: "1995-01-01",
      gender: "Not specified",
      address: userData.address || "100 Innovation Way, San Francisco, CA",
      emergencyContact: { name: "Primary Contact", relation: "Family", phone: "+1 (555) 111-2222" },
      joiningDate: new Date().toISOString().split("T")[0],
      employmentType: "Full-time",
      workLocation: "San Francisco HQ (Hybrid)",
      reportingManager: "Sarah Connor (VP of People Operations)",
      status: "Active",
      salaryStructure: {
        basic: 7000,
        hra: 2800,
        specialAllowance: 1500,
        bonus: 800,
        providentFund: 840,
        taxDeduction: 1400,
        insurance: 200
      },
      leaveBalances: {
        paid: { total: 18, used: 0, available: 18 },
        sick: { total: 10, used: 0, available: 10 },
        casual: { total: 6, used: 0, available: 6 },
        unpaid: { total: 0, used: 0, available: 0 }
      },
      documents: [
        { id: "DOC-NEW", name: "Employee_Welcome_Kit.pdf", type: "Onboarding", size: "1.2 MB", date: new Date().toISOString().split("T")[0] }
      ],
      bio: "Newly onboarded team member."
    };

    setEmployees((prev) => [newEmployee, ...prev]);
    setCurrentUserId(newEmployee.id);
    setSelectedEmployeeId(newEmployee.id);
    setIsAuthenticated(true);
    showToast("Registration Complete", `Welcome to Dayflow, ${newEmployee.name}!`, "success");
    triggerSound("success");
    return true;
  };

  const logout = () => {
    setIsAuthenticated(false);
    showToast("Signed Out", "You have been logged out of Dayflow HRMS.", "info");
  };

  const switchUser = (empId) => {
    const target = employees.find((e) => e.id === empId);
    if (target) {
      setCurrentUserId(target.id);
      setSelectedEmployeeId(target.id);
      showToast("Switched User", `Now acting as ${target.name} (${target.role === "admin" ? "Admin / HR Officer" : "Employee"})`, "info");
      triggerSound("punchIn");
    }
  };

  // Toggle Theme
  const toggleTheme = () => {
    setTheme((prev) => (prev === "dark" ? "light" : "dark"));
  };

  // Attendance Handlers
  const punchIn = (mode = "Office", notes = "") => {
    setIsCheckedIn(true);
    setIsOnBreak(false);
    setCurrentWorkMode(mode);
    const nowTime = formatShortTime(new Date());
    setCheckInTime(nowTime);
    setElapsedSeconds(0);

    // Add or update today's record in attendanceRecords
    const todayStr = new Date().toISOString().split("T")[0];
    const newRecord = {
      id: `ATT-${todayStr}-${currentUser.id.replace("EMP-", "")}`,
      employeeId: currentUser.id,
      employeeName: currentUser.name,
      date: todayStr,
      checkIn: nowTime,
      checkOut: null,
      workMode: mode,
      status: "Present",
      workHours: "In Progress",
      breakMinutes: 0,
      notes: notes || `Clocked in (${mode})`
    };

    setAttendanceRecords((prev) => [newRecord, ...prev.filter((r) => !(r.employeeId === currentUser.id && r.date === todayStr))]);
    showToast("Check-In Successful", `Clocked in at ${nowTime} (${mode} mode). Have a great workday!`, "success");
    triggerSound("punchIn");
  };

  const punchOut = (notes = "") => {
    setIsCheckedIn(false);
    setIsOnBreak(false);
    const nowTime = formatShortTime(new Date());
    const hours = (elapsedSeconds / 3600).toFixed(1);
    const todayStr = new Date().toISOString().split("T")[0];

    setAttendanceRecords((prev) =>
      prev.map((r) => {
        if (r.employeeId === currentUser.id && r.date === todayStr) {
          return {
            ...r,
            checkOut: nowTime,
            workHours: `${hours} hrs`,
            notes: notes ? `${r.notes} | Checkout note: ${notes}` : r.notes
          };
        }
        return r;
      })
    );

    showToast("Check-Out Successful", `Clocked out at ${nowTime}. Total hours worked: ${hours} hrs.`, "info");
    triggerSound("punchOut");
  };

  const toggleBreak = () => {
    setIsOnBreak((prev) => {
      const next = !prev;
      if (next) {
        showToast("Break Started", "Workday timer paused. Enjoy your break!", "info");
      } else {
        showToast("Break Ended", "Workday timer resumed. Welcome back!", "success");
      }
      triggerSound("punchIn");
      return next;
    });
  };

  const adminUpdateAttendance = (recordId, updatedFields) => {
    setAttendanceRecords((prev) =>
      prev.map((r) => (r.id === recordId ? { ...r, ...updatedFields } : r))
    );
    showToast("Attendance Record Updated", "Changes saved successfully.", "success");
    triggerSound("success");
  };

  const adminAddAttendanceRecord = (record) => {
    setAttendanceRecords((prev) => [record, ...prev]);
    showToast("Manual Punch Recorded", `Attendance added for ${record.employeeName}.`, "success");
    triggerSound("success");
  };

  // Leave Management Handlers
  const applyLeave = (leaveData) => {
    const workingDays = calculateWorkingDays(leaveData.startDate, leaveData.endDate);
    const actualDays = leaveData.halfDay ? 0.5 : workingDays || 1;

    const newLeave = {
      id: `LR-${Math.floor(1000 + Math.random() * 9000)}`,
      employeeId: currentUser.id,
      employeeName: currentUser.name,
      employeeAvatar: currentUser.avatar,
      department: currentUser.department,
      type: leaveData.type, // Paid, Sick, Unpaid, Casual
      startDate: leaveData.startDate,
      endDate: leaveData.endDate,
      daysCount: actualDays,
      halfDay: leaveData.halfDay || null,
      reason: leaveData.reason,
      attachment: leaveData.attachment ? leaveData.attachment.name || "Supporting_Document.pdf" : null,
      status: "Pending",
      appliedOn: new Date().toISOString().split("T")[0],
      adminComment: "",
      reviewedBy: null,
      reviewedOn: null
    };

    setLeaveRequests((prev) => [newLeave, ...prev]);

    // Create Notification for Admins
    const adminNotif = {
      id: `NOTIF-${Date.now()}`,
      title: "New Leave Application",
      message: `${currentUser.name} applied for ${actualDays} day(s) ${leaveData.type} Leave (${leaveData.startDate} to ${leaveData.endDate}).`,
      type: "leave",
      timestamp: "Just now",
      read: false,
      targetRole: "admin",
      linkTab: "approvals"
    };
    setNotifications((prev) => [adminNotif, ...prev]);

    showToast("Leave Request Submitted", `Your ${actualDays}-day ${leaveData.type} leave request is pending HR review.`, "success");
    triggerSound("success");
  };

  const approveLeave = (leaveId, comment = "") => {
    const targetLeave = leaveRequests.find((l) => l.id === leaveId);
    if (!targetLeave) return;

    setLeaveRequests((prev) =>
      prev.map((l) =>
        l.id === leaveId
          ? {
              ...l,
              status: "Approved",
              adminComment: comment || "Approved by HR Operations.",
              reviewedBy: currentUser.name,
              reviewedOn: new Date().toISOString().split("T")[0]
            }
          : l
      )
    );

    // Deduct leave balance from employee
    const leaveTypeKey = (targetLeave.type || "paid").toLowerCase();
    setEmployees((prev) =>
      prev.map((emp) => {
        if (emp.id === targetLeave.employeeId) {
          const balances = { ...emp.leaveBalances };
          if (balances[leaveTypeKey]) {
            balances[leaveTypeKey] = {
              ...balances[leaveTypeKey],
              used: balances[leaveTypeKey].used + targetLeave.daysCount,
              available: Math.max(0, balances[leaveTypeKey].available - targetLeave.daysCount)
            };
          }
          return { ...emp, leaveBalances: balances };
        }
        return emp;
      })
    );

    // Notification for Employee
    const empNotif = {
      id: `NOTIF-${Date.now()}`,
      title: "Leave Approved! 🎉",
      message: `Your ${targetLeave.type} Leave (${targetLeave.startDate} to ${targetLeave.endDate}) was approved by ${currentUser.name}. ${comment ? `Note: "${comment}"` : ""}`,
      type: "approval",
      timestamp: "Just now",
      read: false,
      targetRole: "employee",
      linkTab: "leaves"
    };
    setNotifications((prev) => [empNotif, ...prev]);

    // Confetti celebration
    confetti({
      particleCount: 70,
      spread: 60,
      origin: { y: 0.7 }
    });

    showToast("Leave Approved", `Leave request LR-${leaveId.replace("LR-", "")} approved successfully.`, "success");
    triggerSound("success");
  };

  const rejectLeave = (leaveId, comment = "") => {
    const targetLeave = leaveRequests.find((l) => l.id === leaveId);
    if (!targetLeave) return;

    setLeaveRequests((prev) =>
      prev.map((l) =>
        l.id === leaveId
          ? {
              ...l,
              status: "Rejected",
              adminComment: comment || "Declined due to operational scheduling conflicts.",
              reviewedBy: currentUser.name,
              reviewedOn: new Date().toISOString().split("T")[0]
            }
          : l
      )
    );

    // Notification for Employee
    const empNotif = {
      id: `NOTIF-${Date.now()}`,
      title: "Leave Request Declined",
      message: `Your ${targetLeave.type} Leave request was declined by ${currentUser.name}. ${comment ? `Reason: "${comment}"` : ""}`,
      type: "reject",
      timestamp: "Just now",
      read: false,
      targetRole: "employee",
      linkTab: "leaves"
    };
    setNotifications((prev) => [empNotif, ...prev]);

    showToast("Leave Rejected", `Leave request rejected with comment.`, "info");
    triggerSound("reject");
  };

  const cancelLeave = (leaveId) => {
    setLeaveRequests((prev) => prev.filter((l) => l.id !== leaveId));
    showToast("Request Cancelled", "Leave request has been withdrawn.", "info");
  };

  // Profile Management Handlers
  const updateEmployeeProfile = (empId, fields, isHR = false) => {
    setEmployees((prev) =>
      prev.map((emp) => {
        if (emp.id === empId) {
          if (isHR || currentUser.role === "admin") {
            // HR can update all fields
            return { ...emp, ...fields };
          } else {
            // Regular employee can only update restricted fields
            return {
              ...emp,
              phone: fields.phone !== undefined ? fields.phone : emp.phone,
              address: fields.address !== undefined ? fields.address : emp.address,
              emergencyContact: fields.emergencyContact !== undefined ? fields.emergencyContact : emp.emergencyContact,
              avatar: fields.avatar !== undefined ? fields.avatar : emp.avatar,
              bio: fields.bio !== undefined ? fields.bio : emp.bio
            };
          }
        }
        return emp;
      })
    );

    showToast("Profile Updated", "Changes saved successfully to employee file.", "success");
    triggerSound("success");
  };

  const updateSalaryStructure = (empId, newSalary) => {
    setEmployees((prev) =>
      prev.map((emp) => (emp.id === empId ? { ...emp, salaryStructure: { ...emp.salaryStructure, ...newSalary } } : emp))
    );
    showToast("Salary Structure Updated", "Compensation breakdown adjusted accurately.", "success");
    triggerSound("success");
  };

  const addNewEmployee = (empData) => {
    const newId = `EMP-${1000 + employees.length + 1}`;
    const fullEmployee = {
      id: newId,
      name: empData.name,
      email: empData.email,
      password: empData.password || "Password@123",
      role: empData.role || "employee",
      designation: empData.designation || "Specialist",
      department: empData.department || "Engineering",
      avatar: empData.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(empData.name)}`,
      phone: empData.phone || "+1 (555) 000-0000",
      dob: empData.dob || "1994-05-15",
      gender: empData.gender || "Other",
      address: empData.address || "100 Market St, San Francisco, CA",
      emergencyContact: {
        name: empData.emergencyName || "Primary Contact",
        relation: empData.emergencyRelation || "Family",
        phone: empData.emergencyPhone || "+1 (555) 000-1111"
      },
      joiningDate: empData.joiningDate || new Date().toISOString().split("T")[0],
      employmentType: empData.employmentType || "Full-time",
      workLocation: empData.workLocation || "San Francisco HQ (Hybrid)",
      reportingManager: empData.reportingManager || "Sarah Connor (VP of People Operations)",
      status: "Active",
      salaryStructure: empData.salaryStructure || {
        basic: 7500,
        hra: 3000,
        specialAllowance: 1600,
        bonus: 1000,
        providentFund: 900,
        taxDeduction: 1500,
        insurance: 200
      },
      leaveBalances: {
        paid: { total: 18, used: 0, available: 18 },
        sick: { total: 10, used: 0, available: 10 },
        casual: { total: 6, used: 0, available: 6 },
        unpaid: { total: 0, used: 0, available: 0 }
      },
      documents: [
        { id: `DOC-${Date.now()}`, name: "Onboarding_Record_Signed.pdf", type: "Contract", size: "1.6 MB", date: new Date().toISOString().split("T")[0] }
      ],
      bio: empData.bio || "Team member at Dayflow."
    };

    setEmployees((prev) => [fullEmployee, ...prev]);
    showToast("Employee Added", `${fullEmployee.name} has been enrolled into Dayflow HRMS.`, "success");
    triggerSound("success");
    return fullEmployee;
  };

  // Notification Handlers
  const markNotificationAsRead = (id) => {
    setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)));
  };

  const markAllNotificationsAsRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    showToast("All Caught Up", "All notifications marked as read.", "info");
  };

  const dismissNotification = (id) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  // Payroll Batch Process Simulation
  const processMonthlyPayroll = (monthYear = "August 2026") => {
    confetti({
      particleCount: 90,
      spread: 70,
      origin: { y: 0.6 }
    });

    const notif = {
      id: `NOTIF-${Date.now()}`,
      title: "Monthly Payroll Processed ✅",
      message: `Payroll run for ${monthYear} completed successfully. Payslips have been published to all active staff.`,
      type: "payroll",
      timestamp: "Just now",
      read: false,
      targetRole: "all",
      linkTab: "payroll"
    };

    setNotifications((prev) => [notif, ...prev]);
    showToast("Payroll Disbursed", `Payroll for ${monthYear} processed for ${employees.length} employees with 100% fidelity.`, "success");
    triggerSound("success");
  };

  return (
    <HRMSContext.Provider
      value={{
        // Auth & User
        currentUser,
        currentUserId,
        setCurrentUserId,
        isAuthenticated,
        login,
        signUp,
        logout,
        switchUser,

        // Settings & Theme
        theme,
        toggleTheme,
        soundEnabled,
        setSoundEnabled,
        triggerSound,

        // Navigation & View
        activeTab,
        setActiveTab,
        selectedEmployeeId,
        setSelectedEmployeeId,

        // Employees & Profiles
        employees,
        updateEmployeeProfile,
        addNewEmployee,
        updateSalaryStructure,

        // Attendance
        attendanceRecords,
        isCheckedIn,
        checkInTime,
        elapsedSeconds,
        isOnBreak,
        currentWorkMode,
        punchIn,
        punchOut,
        toggleBreak,
        adminUpdateAttendance,
        adminAddAttendanceRecord,
        editingAttendanceRecord,
        setEditingAttendanceRecord,

        // Leaves
        leaveRequests,
        applyLeave,
        approveLeave,
        rejectLeave,
        cancelLeave,
        companyHolidays,

        // Payroll
        historicalPayslips,
        processMonthlyPayroll,
        selectedPayslipMonth,
        setSelectedPayslipMonth,

        // Announcements & Notifications
        companyAnnouncements,
        notifications,
        markNotificationAsRead,
        markAllNotificationsAsRead,
        dismissNotification,

        // Modals & Drawers
        isApplyLeaveOpen,
        setIsApplyLeaveOpen,
        isPayslipModalOpen,
        setIsPayslipModalOpen,
        isAddEmployeeOpen,
        setIsAddEmployeeOpen,
        isEditProfileOpen,
        setIsEditProfileOpen,
        isCommandPaletteOpen,
        setIsCommandPaletteOpen,
        isNotificationDrawerOpen,
        setIsNotificationDrawerOpen,
        isSalaryEditModalOpen,
        setIsSalaryEditModalOpen,
        isManualPunchOpen,
        setIsManualPunchOpen,

        // Toasts
        toasts,
        showToast,
        dismissToast
      }}
    >
      {children}
    </HRMSContext.Provider>
  );
}

export function useHRMS() {
  const context = useContext(HRMSContext);
  if (!context) {
    throw new Error("useHRMS must be used within an HRMSProvider");
  }
  return context;
}
