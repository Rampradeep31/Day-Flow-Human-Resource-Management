import React from "react";
import { useHRMS } from "../../context/HRMSContext";
import AdminPayrollManager from "./AdminPayrollManager";
import EmployeePayrollView from "./EmployeePayrollView";

export default function PayrollMasterView() {
  const { currentUser } = useHRMS();
  const isAdmin = currentUser.role === "admin";

  return isAdmin ? <AdminPayrollManager /> : <EmployeePayrollView />;
}
