import React, { useState } from "react";
import { HRMSProvider, useHRMS } from "./context/HRMSContext";

// Components
import Sidebar from "./components/layout/Sidebar";
import Topbar from "./components/layout/Topbar";
import NotificationDrawer from "./components/layout/NotificationDrawer";
import CommandPalette from "./components/common/CommandPalette";
import ToastContainer from "./components/common/ToastContainer";

import AuthView from "./components/auth/AuthView";
import AdminDashboard from "./components/dashboard/AdminDashboard";
import AdminCompanyAttendance from "./components/attendance/AdminCompanyAttendance";
import AdminLeaveApprovalQueue from "./components/leave/AdminLeaveApprovalQueue";
import EmployeeDirectory from "./components/profile/EmployeeDirectory";
import ProfileView from "./components/profile/ProfileView";
import AdminPayrollManager from "./components/payroll/AdminPayrollManager";
import AnalyticsDashboard from "./components/analytics/AnalyticsDashboard";
import PayslipModal from "./components/payroll/PayslipModal";
import AddEmployeeModal from "./components/profile/AddEmployeeModal";

function AdminHRMSApp() {
  const {
    isAuthenticated,
    activeTab,
    currentUser,
    isPayslipModalOpen,
    setIsPayslipModalOpen,
    isAddEmployeeOpen,
    setIsAddEmployeeOpen,
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
        return <AdminDashboard />;
      case "attendance":
        return <AdminCompanyAttendance />;
      case "leaves":
      case "approvals":
        return <AdminLeaveApprovalQueue />;
      case "directory":
        return <EmployeeDirectory />;
      case "profile":
        return <ProfileView />;
      case "payroll":
        return <AdminPayrollManager />;
      case "analytics":
        return <AnalyticsDashboard />;
      default:
        return <AdminDashboard />;
    }
  };

  return (
    <div className="app-container">
      {/* Admin Sidebar Navigation */}
      <Sidebar collapsed={sidebarCollapsed} setCollapsed={setSidebarCollapsed} />

      {/* Main Admin Console */}
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

      {/* Global Add Employee Modal */}
      {isAddEmployeeOpen && (
        <AddEmployeeModal
          isOpen={isAddEmployeeOpen}
          onClose={() => setIsAddEmployeeOpen(false)}
        />
      )}
    </div>
  );
}

export default function App() {
  return (
    <HRMSProvider>
      <AdminHRMSApp />
    </HRMSProvider>
  );
}
