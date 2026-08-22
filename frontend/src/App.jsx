import React, { useState } from "react";
import { HRMSProvider, useHRMS } from "./context/HRMSContext";

// Components
import Sidebar from "./components/layout/Sidebar";
import Topbar from "./components/layout/Topbar";
import NotificationDrawer from "./components/layout/NotificationDrawer";
import CommandPalette from "./components/common/CommandPalette";
import ToastContainer from "./components/common/ToastContainer";
import DayBotChat from "./components/common/DayBotChat";

import AuthView from "./components/auth/AuthView";
import EmployeeDashboard from "./components/dashboard/EmployeeDashboard";
import AdminDashboard from "./components/dashboard/AdminDashboard";
import AttendanceView from "./components/attendance/AttendanceView";
import LeaveMasterView from "./components/leave/LeaveMasterView";
import AdminLeaveApprovalQueue from "./components/leave/AdminLeaveApprovalQueue";
import EmployeeDirectory from "./components/profile/EmployeeDirectory";
import ProfileView from "./components/profile/ProfileView";
import PayrollMasterView from "./components/payroll/PayrollMasterView";
import AnalyticsDashboard from "./components/analytics/AnalyticsDashboard";
import PayslipModal from "./components/payroll/PayslipModal";
import ApplyLeaveModal from "./components/leave/ApplyLeaveModal";

function HRMSApp() {
  const {
    isAuthenticated,
    activeTab,
    currentUser,
    isPayslipModalOpen,
    setIsPayslipModalOpen,
    isApplyLeaveOpen,
    setIsApplyLeaveOpen,
    selectedPayslipMonth
  } = useHRMS();

  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const dayBotApiUrl = (import.meta.env.VITE_DAYBOT_API_URL || "http://localhost:3000")
    .replace(/\/$/, "");

  // If user is logged out, show the auth interface
  if (!isAuthenticated) {
    return (
      <>
        <AuthView />
        <ToastContainer />
      </>
    );
  }

  const isAdmin = currentUser.role === "admin";

  const renderActiveTabContent = () => {
    switch (activeTab) {
      case "dashboard":
        return isAdmin ? <AdminDashboard /> : <EmployeeDashboard />;
      case "attendance":
        return <AttendanceView />;
      case "leaves":
        return <LeaveMasterView />;
      case "approvals":
        return isAdmin ? <AdminLeaveApprovalQueue /> : <LeaveMasterView />;
      case "directory":
        return <EmployeeDirectory />;
      case "profile":
        return <ProfileView />;
      case "payroll":
        return <PayrollMasterView />;
      case "analytics":
        return <AnalyticsDashboard />;
      default:
        return isAdmin ? <AdminDashboard /> : <EmployeeDashboard />;
    }
  };

  return (
    <div className="app-container">
      {/* Sidebar Navigation */}
      <Sidebar collapsed={sidebarCollapsed} setCollapsed={setSidebarCollapsed} />

      {/* Main App Canvas */}
      <div className="main-wrapper">
        <Topbar />

        <main className="main-content">
          {renderActiveTabContent()}
        </main>
      </div>

      {/* Global Drawers & Modals */}
      <NotificationDrawer />
      <CommandPalette />
      <ToastContainer />

      {/* Global Payslip Modal */}
      {isPayslipModalOpen && (
        <PayslipModal
          isOpen={isPayslipModalOpen}
          onClose={() => setIsPayslipModalOpen(false)}
          employee={currentUser}
          month={selectedPayslipMonth || "August 2026"}
        />
      )}

      {/* Global Apply Leave Modal */}
      {isApplyLeaveOpen && (
        <ApplyLeaveModal
          isOpen={isApplyLeaveOpen}
          onClose={() => setIsApplyLeaveOpen(false)}
        />
      )}

      {/* Pluggable AI Chatbot Widget */}
      <DayBotChat apiUrl={dayBotApiUrl} />
    </div>
  );
}

export default function App() {
  return (
    <HRMSProvider>
      <HRMSApp />
    </HRMSProvider>
  );
}
