import React, { useState } from "react";
import { HRMSProvider, useHRMS } from "./context/HRMSContext";

// Components
import Sidebar from "./components/layout/Sidebar";
import Topbar from "./components/layout/Topbar";
import NotificationDrawer from "./components/layout/NotificationDrawer";
import CommandPalette from "./components/common/CommandPalette";
import ToastContainer from "./components/common/ToastContainer";

import AuthView from "./components/auth/AuthView";
import EmployeeDashboard from "./components/dashboard/EmployeeDashboard";
import AttendanceView from "./components/attendance/AttendanceView";
import LeaveMasterView from "./components/leave/LeaveMasterView";
import EmployeeDirectory from "./components/profile/EmployeeDirectory";
import ProfileView from "./components/profile/ProfileView";
import EmployeePayrollView from "./components/payroll/EmployeePayrollView";
import PayslipModal from "./components/payroll/PayslipModal";
import ApplyLeaveModal from "./components/leave/ApplyLeaveModal";

function EmployeeHRMSApp() {
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

  if (!isAuthenticated) {
    return (
      <>
        <AuthView />
        <ToastContainer />
      </>
    );
  }

  const renderActiveTabContent = () => {
    switch (activeTab) {
      case "dashboard":
        return <EmployeeDashboard />;
      case "attendance":
        return <AttendanceView />;
      case "leaves":
        return <LeaveMasterView />;
      case "directory":
        return <EmployeeDirectory />;
      case "profile":
        return <ProfileView />;
      case "payroll":
        return <EmployeePayrollView />;
      default:
        return <EmployeeDashboard />;
    }
  };

  return (
    <div className="app-container">
      {/* Employee Sidebar Navigation */}
      <Sidebar collapsed={sidebarCollapsed} setCollapsed={setSidebarCollapsed} />

      {/* Main Employee Portal */}
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
    </div>
  );
}

export default function App() {
  return (
    <HRMSProvider>
      <EmployeeHRMSApp />
    </HRMSProvider>
  );
}
