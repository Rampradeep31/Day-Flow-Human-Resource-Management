import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import ProfileEditModal from "./ProfileEditModal";
import StatusBadge from "../common/StatusBadge";
import {
  UserCheck,
  Briefcase,
  CreditCard,
  FileText,
  Clock,
  Edit2,
  Lock,
  Mail,
  Phone,
  MapPin,
  Calendar,
  ShieldCheck,
  Download,
  Eye,
  Camera,
  Heart,
  Award,
  Sparkles
} from "lucide-react";
import { formatDate, formatCurrency } from "../../utils/dateUtils";

export default function ProfileView() {
  const {
    employees,
    selectedEmployeeId,
    currentUser,
    updateEmployeeProfile,
    setIsPayslipModalOpen
  } = useHRMS();

  // Find target employee (default to selected or current)
  const targetEmployee =
    employees.find((e) => e.id === selectedEmployeeId) || currentUser;

  const isSelf = targetEmployee.id === currentUser.id;
  const isHRAdmin = currentUser.role === "admin";

  const [activeTab, setActiveTab] = useState("personal"); // "personal" | "job" | "salary" | "documents" | "activity"
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  // Avatar presets simulation
  const avatarOptions = [
    "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=300",
    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300",
    "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=300",
    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300",
    "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&q=80&w=300"
  ];

  const handleAvatarChange = (newUrl) => {
    updateEmployeeProfile(targetEmployee.id, { avatar: newUrl }, isHRAdmin);
  };

  const s = targetEmployee.salaryStructure || {};
  const grossPay = (s.basic || 0) + (s.hra || 0) + (s.specialAllowance || 0) + (s.bonus || 0);
  const totalDeductions = (s.providentFund || 0) + (s.taxDeduction || 0) + (s.insurance || 0);
  const netPay = grossPay - totalDeductions;

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
      {/* Profile Header Banner */}
      <div
        className="glass-card"
        style={{
          padding: "2rem",
          background: "linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(139, 92, 246, 0.06) 100%)",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "1.5rem"
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "1.5rem", flexWrap: "wrap" }}>
          {/* Avatar with live chooser */}
          <div style={{ position: "relative" }}>
            <img
              src={targetEmployee.avatar}
              alt={targetEmployee.name}
              style={{
                width: "90px",
                height: "90px",
                borderRadius: "50%",
                objectFit: "cover",
                border: "3px solid var(--brand-primary)",
                boxShadow: "var(--shadow-md)"
              }}
            />
            {/* Quick avatar cycle */}
            <button
              onClick={() => {
                const nextIdx = (avatarOptions.indexOf(targetEmployee.avatar) + 1) % avatarOptions.length;
                handleAvatarChange(avatarOptions[nextIdx]);
              }}
              style={{
                position: "absolute",
                bottom: 0,
                right: 0,
                width: "28px",
                height: "28px",
                borderRadius: "50%",
                backgroundColor: "var(--brand-primary)",
                color: "#ffffff",
                border: "2px solid var(--bg-elevated)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                cursor: "pointer"
              }}
              title="Click to cycle avatar image"
            >
              <Camera size={13} />
            </button>
          </div>

          <div>
            <div style={{ display: "flex", alignItems: "center", gap: "0.6rem" }}>
              <h1 style={{ fontSize: "1.6rem", fontWeight: 800, color: "var(--text-primary)" }}>
                {targetEmployee.name}
              </h1>
              <StatusBadge status={targetEmployee.status} />
              <StatusBadge status={targetEmployee.role === "admin" ? "Admin / HR Officer" : "Employee"} />
            </div>

            <div style={{ fontSize: "0.95rem", color: "var(--brand-primary)", fontWeight: 600, marginTop: "0.2rem" }}>
              {targetEmployee.designation} • {targetEmployee.department}
            </div>

            <div style={{ display: "flex", alignItems: "center", gap: "1.25rem", marginTop: "0.5rem", fontSize: "0.8rem", color: "var(--text-secondary)", flexWrap: "wrap" }}>
              <span style={{ display: "flex", alignItems: "center", gap: "0.35rem" }}>
                <Mail size={14} style={{ color: "var(--text-muted)" }} /> {targetEmployee.email}
              </span>
              <span style={{ display: "flex", alignItems: "center", gap: "0.35rem" }}>
                <Phone size={14} style={{ color: "var(--text-muted)" }} /> {targetEmployee.phone}
              </span>
              <span style={{ display: "flex", alignItems: "center", gap: "0.35rem" }}>
                <MapPin size={14} style={{ color: "var(--text-muted)" }} /> {targetEmployee.workLocation}
              </span>
            </div>
          </div>
        </div>

        <div>
          <button onClick={() => setIsEditModalOpen(true)} className="btn btn-primary">
            <Edit2 size={15} /> Edit Profile
          </button>
        </div>
      </div>

      {/* Segmented Profile Navigation Tabs (Section 3.3.1 Requirements) */}
      <div
        style={{
          display: "flex",
          borderBottom: "1px solid var(--border-color)",
          gap: "0.5rem",
          overflowX: "auto"
        }}
      >
        {[
          { id: "personal", label: "Personal Details", icon: UserCheck },
          { id: "job", label: "Job & Organization", icon: Briefcase },
          { id: "salary", label: "Salary Structure", icon: CreditCard },
          { id: "documents", label: "Documents & Files", icon: FileText }
        ].map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;

          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "0.5rem",
                padding: "0.75rem 1.25rem",
                border: "none",
                backgroundColor: "transparent",
                borderBottom: isActive ? "3px solid var(--brand-primary)" : "3px solid transparent",
                color: isActive ? "var(--brand-primary)" : "var(--text-secondary)",
                fontWeight: isActive ? 700 : 500,
                fontSize: "0.875rem",
                cursor: "pointer",
                transition: "all var(--transition-fast)",
                whiteSpace: "nowrap"
              }}
            >
              <Icon size={16} />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* TAB 1: Personal Details */}
      {activeTab === "personal" && (
        <div className="glass-card" style={{ padding: "1.75rem" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.25rem" }}>
            <h3 style={{ fontSize: "1.1rem", fontWeight: 700 }}>Personal & Emergency Contact Details</h3>
            <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>
              {isHRAdmin ? "Full Admin Access" : "Self-service Editable"}
            </span>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))", gap: "1.5rem" }}>
            <div style={{ display: "flex", flexDirection: "column", gap: "0.85rem" }}>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Full Legal Name</span>
                <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{targetEmployee.name}</div>
              </div>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Date of Birth</span>
                <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{formatDate(targetEmployee.dob)}</div>
              </div>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Gender</span>
                <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{targetEmployee.gender}</div>
              </div>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Permanent Residential Address</span>
                <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{targetEmployee.address}</div>
              </div>
            </div>

            <div style={{ display: "flex", flexDirection: "column", gap: "0.85rem" }}>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Emergency Contact Name</span>
                <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>
                  {targetEmployee.emergencyContact?.name || "N/A"} ({targetEmployee.emergencyContact?.relation || "Family"})
                </div>
              </div>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Emergency Contact Phone</span>
                <div style={{ fontWeight: 600, color: "var(--brand-primary)", fontFamily: "var(--font-mono)" }}>
                  {targetEmployee.emergencyContact?.phone || "N/A"}
                </div>
              </div>
              <div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Professional Bio</span>
                <div style={{ fontSize: "0.85rem", color: "var(--text-secondary)", lineHeight: "1.5" }}>
                  {targetEmployee.bio || "No bio added yet."}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: Job Details */}
      {activeTab === "job" && (
        <div className="glass-card" style={{ padding: "1.75rem" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.25rem" }}>
            <h3 style={{ fontSize: "1.1rem", fontWeight: 700 }}>Employment & Organizational Structure</h3>
            {!isHRAdmin && (
              <span style={{ display: "flex", alignItems: "center", gap: "0.3rem", fontSize: "0.75rem", color: "var(--brand-primary)", backgroundColor: "var(--brand-primary-light)", padding: "0.2rem 0.5rem", borderRadius: "var(--radius-sm)" }}>
                <Lock size={12} /> Managed by HR Operations
              </span>
            )}
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))", gap: "1.5rem" }}>
            <div>
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Employee ID</span>
              <div style={{ fontWeight: 700, fontFamily: "var(--font-mono)", color: "var(--brand-primary)" }}>
                {targetEmployee.id}
              </div>
            </div>

            <div>
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Designation</span>
              <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{targetEmployee.designation}</div>
            </div>

            <div>
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Department</span>
              <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{targetEmployee.department}</div>
            </div>

            <div>
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Employment Type</span>
              <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{targetEmployee.employmentType}</div>
            </div>

            <div>
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Joining Date</span>
              <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{formatDate(targetEmployee.joiningDate)}</div>
            </div>

            <div>
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Reporting Manager</span>
              <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{targetEmployee.reportingManager}</div>
            </div>

            <div>
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Work Location & Policy</span>
              <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{targetEmployee.workLocation}</div>
            </div>

            <div>
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", textTransform: "uppercase" }}>Status</span>
              <div><StatusBadge status={targetEmployee.status} /></div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: Salary Structure (Section 3.3.1 & 3.6.1) */}
      {activeTab === "salary" && (
        <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
          <div className="glass-card" style={{ padding: "1.75rem" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.25rem" }}>
              <div>
                <h3 style={{ fontSize: "1.1rem", fontWeight: 700 }}>Monthly Compensation & Salary Structure</h3>
                <p style={{ fontSize: "0.75rem", color: "var(--text-muted)", marginTop: "2px" }}>
                  {isHRAdmin ? "Admin editable structure" : "Read-only breakdown as of August 2026"}
                </p>
              </div>

              <button onClick={() => setIsPayslipModalOpen(true)} className="btn btn-secondary btn-sm">
                <FileText size={14} /> Generate Official Payslip
              </button>
            </div>

            {/* Top Net Pay Summary Banner */}
            <div
              style={{
                padding: "1.25rem 1.5rem",
                borderRadius: "var(--radius-lg)",
                background: "linear-gradient(135deg, rgba(16, 185, 129, 0.12) 0%, rgba(99, 102, 241, 0.08) 100%)",
                border: "1px solid rgba(16, 185, 129, 0.3)",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                marginBottom: "1.5rem",
                flexWrap: "wrap",
                gap: "1rem"
              }}
            >
              <div>
                <span style={{ fontSize: "0.75rem", fontWeight: 700, color: "var(--text-muted)", textTransform: "uppercase" }}>
                  Net Monthly In-Hand Salary
                </span>
                <div style={{ fontSize: "2rem", fontWeight: 800, color: "var(--color-success)", letterSpacing: "-0.02em" }}>
                  {formatCurrency(netPay)}
                </div>
              </div>

              <div style={{ display: "flex", gap: "1.5rem", textAlign: "right" }}>
                <div>
                  <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Gross Monthly</span>
                  <div style={{ fontWeight: 700, fontSize: "1.1rem", color: "var(--text-primary)" }}>
                    {formatCurrency(grossPay)}
                  </div>
                </div>
                <div>
                  <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Total Deductions</span>
                  <div style={{ fontWeight: 700, fontSize: "1.1rem", color: "var(--color-danger)" }}>
                    - {formatCurrency(totalDeductions)}
                  </div>
                </div>
              </div>
            </div>

            {/* Earnings vs Deductions 2-Column Table */}
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1.5rem" }}>
              {/* Earnings */}
              <div style={{ padding: "1rem", backgroundColor: "var(--bg-secondary)", borderRadius: "var(--radius-md)", border: "1px solid var(--border-subtle)" }}>
                <h4 style={{ fontSize: "0.9rem", fontWeight: 700, color: "var(--color-success)", marginBottom: "0.75rem" }}>
                  Monthly Earnings & Allowances
                </h4>
                <div style={{ display: "flex", flexDirection: "column", gap: "0.6rem", fontSize: "0.85rem" }}>
                  <div style={{ display: "flex", justifyContent: "space-between" }}>
                    <span style={{ color: "var(--text-secondary)" }}>Basic Salary:</span>
                    <strong style={{ fontFamily: "var(--font-mono)" }}>{formatCurrency(s.basic)}</strong>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between" }}>
                    <span style={{ color: "var(--text-secondary)" }}>House Rent Allowance (HRA):</span>
                    <strong style={{ fontFamily: "var(--font-mono)" }}>{formatCurrency(s.hra)}</strong>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between" }}>
                    <span style={{ color: "var(--text-secondary)" }}>Special & Flexible Allowance:</span>
                    <strong style={{ fontFamily: "var(--font-mono)" }}>{formatCurrency(s.specialAllowance)}</strong>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between" }}>
                    <span style={{ color: "var(--text-secondary)" }}>Performance Bonus:</span>
                    <strong style={{ fontFamily: "var(--font-mono)" }}>{formatCurrency(s.bonus)}</strong>
                  </div>
                  <div style={{ height: "1px", backgroundColor: "var(--border-color)", margin: "0.4rem 0" }} />
                  <div style={{ display: "flex", justifyContent: "space-between", fontWeight: 800 }}>
                    <span>Total Gross Earnings:</span>
                    <span style={{ color: "var(--color-success)" }}>{formatCurrency(grossPay)}</span>
                  </div>
                </div>
              </div>

              {/* Deductions */}
              <div style={{ padding: "1rem", backgroundColor: "var(--bg-secondary)", borderRadius: "var(--radius-md)", border: "1px solid var(--border-subtle)" }}>
                <h4 style={{ fontSize: "0.9rem", fontWeight: 700, color: "var(--color-danger)", marginBottom: "0.75rem" }}>
                  Statutory Deductions & Withholdings
                </h4>
                <div style={{ display: "flex", flexDirection: "column", gap: "0.6rem", fontSize: "0.85rem" }}>
                  <div style={{ display: "flex", justifyContent: "space-between" }}>
                    <span style={{ color: "var(--text-secondary)" }}>Provident Fund (PF @ 12%):</span>
                    <strong style={{ fontFamily: "var(--font-mono)", color: "var(--color-danger)" }}>{formatCurrency(s.providentFund)}</strong>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between" }}>
                    <span style={{ color: "var(--text-secondary)" }}>Income Tax / TDS:</span>
                    <strong style={{ fontFamily: "var(--font-mono)", color: "var(--color-danger)" }}>{formatCurrency(s.taxDeduction)}</strong>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between" }}>
                    <span style={{ color: "var(--text-secondary)" }}>Health Insurance Premium:</span>
                    <strong style={{ fontFamily: "var(--font-mono)", color: "var(--color-danger)" }}>{formatCurrency(s.insurance)}</strong>
                  </div>
                  <div style={{ height: "1px", backgroundColor: "var(--border-color)", margin: "0.4rem 0" }} />
                  <div style={{ display: "flex", justifyContent: "space-between", fontWeight: 800 }}>
                    <span>Total Deductions:</span>
                    <span style={{ color: "var(--color-danger)" }}>- {formatCurrency(totalDeductions)}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 4: Documents */}
      {activeTab === "documents" && (
        <div className="glass-card" style={{ padding: "1.75rem" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.25rem" }}>
            <h3 style={{ fontSize: "1.1rem", fontWeight: 700 }}>Employee Document Vault</h3>
            <button
              onClick={() => alert("Upload document simulation: Select file from your machine.")}
              className="btn btn-secondary btn-sm"
            >
              Upload Document
            </button>
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
            {(targetEmployee.documents || []).map((doc) => (
              <div
                key={doc.id}
                style={{
                  padding: "1rem",
                  borderRadius: "var(--radius-md)",
                  backgroundColor: "var(--bg-secondary)",
                  border: "1px solid var(--border-color)",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between"
                }}
              >
                <div style={{ display: "flex", alignItems: "center", gap: "0.85rem" }}>
                  <div
                    style={{
                      width: "38px",
                      height: "38px",
                      borderRadius: "var(--radius-sm)",
                      backgroundColor: "var(--brand-primary-light)",
                      color: "var(--brand-primary)",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center"
                    }}
                  >
                    <FileText size={18} />
                  </div>
                  <div>
                    <div style={{ fontWeight: 700, fontSize: "0.9rem", color: "var(--text-primary)" }}>
                      {doc.name}
                    </div>
                    <div style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>
                      Category: {doc.type} • Size: {doc.size} • Uploaded on {formatDate(doc.date)}
                    </div>
                  </div>
                </div>

                <div style={{ display: "flex", gap: "0.5rem" }}>
                  <button
                    onClick={() => alert(`Viewing document: ${doc.name}`)}
                    className="btn btn-secondary btn-sm"
                  >
                    <Eye size={13} /> View
                  </button>
                  <button
                    onClick={() => alert(`Downloading verified copy: ${doc.name}`)}
                    className="btn btn-secondary btn-sm"
                  >
                    <Download size={13} /> Download
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Edit Profile Modal */}
      {isEditModalOpen && (
        <ProfileEditModal
          isOpen={isEditModalOpen}
          onClose={() => setIsEditModalOpen(false)}
          targetEmployee={targetEmployee}
        />
      )}
    </div>
  );
}
