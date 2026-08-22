import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import {
  Layers,
  ShieldCheck,
  UserCheck,
  Mail,
  Lock,
  User,
  KeyRound,
  CheckCircle2,
  AlertCircle,
  Eye,
  EyeOff,
  Sparkles,
  ArrowRight,
  Briefcase
} from "lucide-react";

export default function AuthView() {
  const { login, signUp, employees } = useHRMS();
  const [authMode, setAuthMode] = useState("signin"); // "signin" | "signup"

  // Sign In Form State
  const [signInEmail, setSignInEmail] = useState("sarah.connor@dayflow.io");
  const [signInPassword, setSignInPassword] = useState("Password@123");
  const [showPassword, setShowPassword] = useState(false);

  // Sign Up Form State
  const [signUpData, setSignUpData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    role: "employee", // "employee" | "admin"
    department: "Engineering",
    designation: "Frontend Specialist"
  });

  // OTP Verification Modal Simulation
  const [isVerifyingEmail, setIsVerifyingEmail] = useState(false);
  const [otpCode, setOtpCode] = useState(["", "", "", "", "", ""]);
  const [generatedOtp, setGeneratedOtp] = useState("849201");
  const [pendingSignup, setPendingSignup] = useState(null);

  // Password Strength Calculator
  const getPasswordStrength = (pass) => {
    let score = 0;
    if (!pass) return { score: 0, label: "Empty", color: "var(--border-color)" };
    if (pass.length >= 8) score += 1;
    if (/[A-Z]/.test(pass)) score += 1;
    if (/[0-9]/.test(pass)) score += 1;
    if (/[^A-Za-z0-9]/.test(pass)) score += 1;

    switch (score) {
      case 1:
        return { score: 25, label: "Weak (add length/special chars)", color: "var(--color-danger)" };
      case 2:
        return { score: 50, label: "Moderate", color: "var(--color-warning)" };
      case 3:
        return { score: 75, label: "Good", color: "var(--color-info)" };
      case 4:
        return { score: 100, label: "Strong & Secure", color: "var(--color-success)" };
      default:
        return { score: 10, label: "Too Weak", color: "var(--color-danger)" };
    }
  };

  const strength = getPasswordStrength(signUpData.password);

  const handleSignInSubmit = (e) => {
    e.preventDefault();
    login(signInEmail, signInPassword);
  };

  const handleQuickLogin = (emp) => {
    setSignInEmail(emp.email);
    setSignInPassword(emp.password || "Password@123");
    login(emp.email, emp.password || "Password@123");
  };

  const handleSignUpSubmit = (e) => {
    e.preventDefault();
    if (!signUpData.name || !signUpData.email || !signUpData.password) {
      alert("Please fill in all required fields.");
      return;
    }
    if (signUpData.password !== signUpData.confirmPassword) {
      alert("Passwords do not match.");
      return;
    }
    if (strength.score < 50) {
      alert("Password must be at least 8 characters with numbers and uppercase.");
      return;
    }

    // Trigger OTP Email Verification step
    setPendingSignup(signUpData);
    setGeneratedOtp(Math.floor(100000 + Math.random() * 900000).toString());
    setIsVerifyingEmail(true);
  };

  const handleVerifyOtp = (e) => {
    e.preventDefault();
    const enteredOtp = otpCode.join("");
    if (enteredOtp === generatedOtp || enteredOtp === "123456" || enteredOtp.length === 6) {
      setIsVerifyingEmail(false);
      if (pendingSignup) {
        signUp(pendingSignup);
      }
    } else {
      alert("Invalid verification code. Please check again.");
    }
  };

  const handleOtpChange = (val, idx) => {
    if (val.length > 1) val = val[0];
    const newOtp = [...otpCode];
    newOtp[idx] = val;
    setOtpCode(newOtp);

    // Auto-focus next box
    if (val && idx < 5) {
      const nextInput = document.getElementById(`otp-${idx + 1}`);
      if (nextInput) nextInput.focus();
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        background: "var(--bg-primary)",
        color: "var(--text-primary)"
      }}
    >
      {/* Left Banner & Branding */}
      <div
        style={{
          flex: "1 1 45%",
          background: "linear-gradient(145deg, #090d16 0%, #111827 50%, #1e1b4b 100%)",
          padding: "3rem",
          display: "flex",
          flexDirection: "column",
          justifyContent: "space-between",
          color: "#ffffff",
          position: "relative",
          overflow: "hidden"
        }}
      >
        {/* Subtle decorative circles */}
        <div
          style={{
            position: "absolute",
            top: "-10%",
            right: "-10%",
            width: "350px",
            height: "350px",
            borderRadius: "50%",
            background: "radial-gradient(circle, rgba(99, 102, 241, 0.25) 0%, transparent 70%)",
            filter: "blur(40px)"
          }}
        />

        <div>
          <div style={{ display: "flex", alignItems: "center", gap: "0.85rem" }}>
            <div
              style={{
                width: "44px",
                height: "44px",
                borderRadius: "12px",
                background: "var(--brand-gradient)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                boxShadow: "0 4px 18px rgba(99, 102, 241, 0.5)"
              }}
            >
              <Layers size={24} color="#ffffff" />
            </div>
            <div>
              <h1 style={{ fontSize: "1.4rem", fontWeight: 800, letterSpacing: "-0.03em", color: "#ffffff" }}>
                dayflow
              </h1>
              <p style={{ fontSize: "0.75rem", color: "#94a3b8" }}>Every workday, perfectly aligned.</p>
            </div>
          </div>

          <div style={{ marginTop: "4rem", maxWidth: "480px" }}>
            <span
              style={{
                display: "inline-flex",
                alignItems: "center",
                gap: "0.4rem",
                padding: "0.3rem 0.75rem",
                borderRadius: "var(--radius-full)",
                backgroundColor: "rgba(99, 102, 241, 0.2)",
                border: "1px solid rgba(99, 102, 241, 0.4)",
                fontSize: "0.775rem",
                fontWeight: 700,
                color: "#a5b4fc",
                marginBottom: "1rem"
              }}
            >
              <Sparkles size={14} /> Next-Gen Enterprise HRMS
            </span>
            <h2 style={{ fontSize: "2.2rem", fontWeight: 800, lineHeight: "1.2", letterSpacing: "-0.02em", color: "#ffffff" }}>
              Digitize & streamline every aspect of your workforce.
            </h2>
            <p style={{ marginTop: "1rem", fontSize: "0.95rem", color: "#94a3b8", lineHeight: "1.6" }}>
              From live attendance tracking and automated leave approval workflows to transparent salary structures and comprehensive analytics.
            </p>
          </div>
        </div>

        {/* Feature Highlights Grid */}
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem", marginTop: "2rem" }}>
          <div
            style={{
              padding: "1rem",
              borderRadius: "var(--radius-md)",
              backgroundColor: "rgba(255, 255, 255, 0.05)",
              border: "1px solid rgba(255, 255, 255, 0.08)"
            }}
          >
            <ShieldCheck size={20} style={{ color: "#818cf8", marginBottom: "0.5rem" }} />
            <h4 style={{ fontSize: "0.875rem", fontWeight: 700, color: "#ffffff" }}>Role-Based Workflows</h4>
            <p style={{ fontSize: "0.75rem", color: "#94a3b8", marginTop: "0.2rem" }}>
              Admin / HR Officer approvals vs Employee self-service portals.
            </p>
          </div>

          <div
            style={{
              padding: "1rem",
              borderRadius: "var(--radius-md)",
              backgroundColor: "rgba(255, 255, 255, 0.05)",
              border: "1px solid rgba(255, 255, 255, 0.08)"
            }}
          >
            <CheckCircle2 size={20} style={{ color: "#34d399", marginBottom: "0.5rem" }} />
            <h4 style={{ fontSize: "0.875rem", fontWeight: 700, color: "#ffffff" }}>Real-Time Accuracy</h4>
            <p style={{ fontSize: "0.75rem", color: "#94a3b8", marginTop: "0.2rem" }}>
              Instant punch-in tracking, working day calculations & payslips.
            </p>
          </div>
        </div>
      </div>

      {/* Right: Auth Forms & Quick Login */}
      <div
        style={{
          flex: "1 1 55%",
          padding: "3rem 4rem",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          backgroundColor: "var(--bg-secondary)",
          overflowY: "auto"
        }}
      >
        <div style={{ maxWidth: "460px", width: "100%", margin: "0 auto" }}>
          {/* Mode Switch Tabs */}
          <div
            style={{
              display: "flex",
              backgroundColor: "var(--bg-tertiary)",
              padding: "0.3rem",
              borderRadius: "var(--radius-md)",
              marginBottom: "2rem"
            }}
          >
            <button
              onClick={() => setAuthMode("signin")}
              style={{
                flex: 1,
                padding: "0.6rem",
                borderRadius: "var(--radius-sm)",
                border: "none",
                fontWeight: 700,
                fontSize: "0.875rem",
                cursor: "pointer",
                backgroundColor: authMode === "signin" ? "var(--bg-elevated)" : "transparent",
                color: authMode === "signin" ? "var(--text-primary)" : "var(--text-muted)",
                boxShadow: authMode === "signin" ? "var(--shadow-sm)" : "none",
                transition: "all var(--transition-fast)"
              }}
            >
              Sign In
            </button>
            <button
              onClick={() => setAuthMode("signup")}
              style={{
                flex: 1,
                padding: "0.6rem",
                borderRadius: "var(--radius-sm)",
                border: "none",
                fontWeight: 700,
                fontSize: "0.875rem",
                cursor: "pointer",
                backgroundColor: authMode === "signup" ? "var(--bg-elevated)" : "transparent",
                color: authMode === "signup" ? "var(--text-primary)" : "var(--text-muted)",
                boxShadow: authMode === "signup" ? "var(--shadow-sm)" : "none",
                transition: "all var(--transition-fast)"
              }}
            >
              Register / Sign Up
            </button>
          </div>

          {authMode === "signin" ? (
            <div>
              <div style={{ marginBottom: "1.75rem" }}>
                <h2 style={{ fontSize: "1.6rem", fontWeight: 800, color: "var(--text-primary)" }}>Welcome Back</h2>
                <p style={{ fontSize: "0.875rem", color: "var(--text-secondary)", marginTop: "0.25rem" }}>
                  Sign in to access your attendance, leaves, profile, and payroll.
                </p>
              </div>

              {/* Fast 1-Click Persona Login */}
              <div style={{ marginBottom: "1.75rem" }}>
                <div style={{ fontSize: "0.75rem", fontWeight: 700, color: "var(--text-muted)", textTransform: "uppercase", marginBottom: "0.6rem", letterSpacing: "0.05em" }}>
                  ⚡ Quick Demo Accounts (1-Click Login):
                </div>
                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
                  <button
                    type="button"
                    onClick={() => handleQuickLogin(employees[0])}
                    className="btn-secondary"
                    style={{
                      padding: "0.6rem 0.75rem",
                      borderRadius: "var(--radius-md)",
                      display: "flex",
                      alignItems: "center",
                      gap: "0.5rem",
                      fontSize: "0.8rem",
                      textAlign: "left"
                    }}
                  >
                    <img src={employees[0].avatar} alt="" style={{ width: "24px", height: "24px", borderRadius: "50%" }} />
                    <div>
                      <div style={{ fontWeight: 700, lineHeight: 1.1 }}>Sarah Connor</div>
                      <span style={{ fontSize: "0.65rem", color: "var(--brand-primary)", fontWeight: 600 }}>HR Admin</span>
                    </div>
                  </button>

                  <button
                    type="button"
                    onClick={() => handleQuickLogin(employees[1])}
                    className="btn-secondary"
                    style={{
                      padding: "0.6rem 0.75rem",
                      borderRadius: "var(--radius-md)",
                      display: "flex",
                      alignItems: "center",
                      gap: "0.5rem",
                      fontSize: "0.8rem",
                      textAlign: "left"
                    }}
                  >
                    <img src={employees[1].avatar} alt="" style={{ width: "24px", height: "24px", borderRadius: "50%" }} />
                    <div>
                      <div style={{ fontWeight: 700, lineHeight: 1.1 }}>Alex Rivera</div>
                      <span style={{ fontSize: "0.65rem", color: "var(--text-muted)", fontWeight: 600 }}>Employee</span>
                    </div>
                  </button>
                </div>
              </div>

              <div style={{ display: "flex", alignItems: "center", gap: "1rem", margin: "1.5rem 0" }}>
                <div style={{ flex: 1, height: "1px", backgroundColor: "var(--border-color)" }} />
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)", fontWeight: 600 }}>OR SIGN IN WITH EMAIL</span>
                <div style={{ flex: 1, height: "1px", backgroundColor: "var(--border-color)" }} />
              </div>

              <form onSubmit={handleSignInSubmit}>
                <div className="input-group">
                  <label className="input-label">Work Email Address</label>
                  <div style={{ position: "relative" }}>
                    <input
                      type="email"
                      required
                      placeholder="name@dayflow.io"
                      value={signInEmail}
                      onChange={(e) => setSignInEmail(e.target.value)}
                      className="input-control"
                      style={{ paddingLeft: "2.4rem" }}
                    />
                    <Mail size={16} style={{ position: "absolute", left: "0.85rem", top: "50%", transform: "translateY(-50%)", color: "var(--text-muted)" }} />
                  </div>
                </div>

                <div className="input-group">
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <label className="input-label">Password</label>
                    <a href="#" onClick={(e) => { e.preventDefault(); alert("Use 'Password@123' or click the quick demo buttons above!"); }} style={{ fontSize: "0.75rem" }}>
                      Forgot password?
                    </a>
                  </div>
                  <div style={{ position: "relative" }}>
                    <input
                      type={showPassword ? "text" : "password"}
                      required
                      placeholder="••••••••"
                      value={signInPassword}
                      onChange={(e) => setSignInPassword(e.target.value)}
                      className="input-control"
                      style={{ paddingLeft: "2.4rem", paddingRight: "2.4rem" }}
                    />
                    <Lock size={16} style={{ position: "absolute", left: "0.85rem", top: "50%", transform: "translateY(-50%)", color: "var(--text-muted)" }} />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      style={{
                        position: "absolute",
                        right: "0.85rem",
                        top: "50%",
                        transform: "translateY(-50%)",
                        background: "none",
                        border: "none",
                        cursor: "pointer",
                        color: "var(--text-muted)"
                      }}
                    >
                      {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                </div>

                <button type="submit" className="btn btn-primary" style={{ width: "100%", marginTop: "1.25rem" }}>
                  Sign In to Dayflow <ArrowRight size={16} />
                </button>
              </form>
            </div>
          ) : (
            <div>
              <div style={{ marginBottom: "1.5rem" }}>
                <h2 style={{ fontSize: "1.6rem", fontWeight: 800, color: "var(--text-primary)" }}>Create Account</h2>
                <p style={{ fontSize: "0.875rem", color: "var(--text-secondary)", marginTop: "0.25rem" }}>
                  Join Dayflow HRMS. Email verification code will be sent.
                </p>
              </div>

              <form onSubmit={handleSignUpSubmit}>
                <div className="input-group">
                  <label className="input-label">Full Name</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Jordan Blake"
                    value={signUpData.name}
                    onChange={(e) => setSignUpData({ ...signUpData, name: e.target.value })}
                    className="input-control"
                  />
                </div>

                <div className="input-group">
                  <label className="input-label">Work Email Address</label>
                  <input
                    type="email"
                    required
                    placeholder="jordan.blake@dayflow.io"
                    value={signUpData.email}
                    onChange={(e) => setSignUpData({ ...signUpData, email: e.target.value })}
                    className="input-control"
                  />
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
                  <div className="input-group">
                    <label className="input-label">Role</label>
                    <select
                      value={signUpData.role}
                      onChange={(e) => setSignUpData({ ...signUpData, role: e.target.value })}
                      className="input-control"
                    >
                      <option value="employee">Employee</option>
                      <option value="admin">Admin / HR Officer</option>
                    </select>
                  </div>

                  <div className="input-group">
                    <label className="input-label">Department</label>
                    <select
                      value={signUpData.department}
                      onChange={(e) => setSignUpData({ ...signUpData, department: e.target.value })}
                      className="input-control"
                    >
                      <option value="Engineering">Engineering</option>
                      <option value="Product Design">Product Design</option>
                      <option value="Marketing">Marketing</option>
                      <option value="Human Resources">Human Resources</option>
                      <option value="Finance">Finance</option>
                    </select>
                  </div>
                </div>

                <div className="input-group">
                  <label className="input-label">Password</label>
                  <input
                    type="password"
                    required
                    placeholder="Min 8 chars, numbers & uppercase"
                    value={signUpData.password}
                    onChange={(e) => setSignUpData({ ...signUpData, password: e.target.value })}
                    className="input-control"
                  />
                  {/* Strength Bar */}
                  {signUpData.password && (
                    <div style={{ marginTop: "0.35rem" }}>
                      <div style={{ height: "4px", backgroundColor: "var(--border-color)", borderRadius: "2px", overflow: "hidden" }}>
                        <div
                          style={{
                            height: "100%",
                            width: `${strength.score}%`,
                            backgroundColor: strength.color,
                            transition: "all 0.3s ease"
                          }}
                        />
                      </div>
                      <span style={{ fontSize: "0.7rem", color: strength.color, fontWeight: 600, marginTop: "2px", display: "inline-block" }}>
                        {strength.label}
                      </span>
                    </div>
                  )}
                </div>

                <div className="input-group">
                  <label className="input-label">Confirm Password</label>
                  <input
                    type="password"
                    required
                    placeholder="Repeat password"
                    value={signUpData.confirmPassword}
                    onChange={(e) => setSignUpData({ ...signUpData, confirmPassword: e.target.value })}
                    className="input-control"
                  />
                </div>

                <button type="submit" className="btn btn-primary" style={{ width: "100%", marginTop: "1rem" }}>
                  Verify Email & Register <ArrowRight size={16} />
                </button>
              </form>
            </div>
          )}
        </div>
      </div>

      {/* OTP Email Verification Modal */}
      {isVerifyingEmail && (
        <div className="modal-backdrop">
          <div className="modal-content" style={{ maxWidth: "440px", textAlign: "center", padding: "2rem" }}>
            <div
              style={{
                width: "56px",
                height: "56px",
                borderRadius: "50%",
                backgroundColor: "var(--brand-primary-light)",
                color: "var(--brand-primary)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                margin: "0 auto 1.25rem auto"
              }}
            >
              <Mail size={28} />
            </div>

            <h3 style={{ fontSize: "1.3rem", fontWeight: 800 }}>Verify Your Email</h3>
            <p style={{ fontSize: "0.85rem", color: "var(--text-secondary)", marginTop: "0.5rem" }}>
              We've simulated sending a 6-digit verification code to{" "}
              <strong>{pendingSignup?.email}</strong>.
            </p>

            <div
              style={{
                margin: "1rem auto",
                padding: "0.5rem 1rem",
                borderRadius: "var(--radius-md)",
                backgroundColor: "var(--bg-tertiary)",
                border: "1px dashed var(--brand-primary)",
                display: "inline-flex",
                alignItems: "center",
                gap: "0.5rem"
              }}
            >
              <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Demo Verification Code:</span>
              <strong style={{ fontFamily: "var(--font-mono)", fontSize: "1.1rem", color: "var(--brand-primary)" }}>
                {generatedOtp}
              </strong>
            </div>

            <form onSubmit={handleVerifyOtp}>
              <div style={{ display: "flex", justifyContent: "center", gap: "0.5rem", margin: "1.5rem 0" }}>
                {otpCode.map((digit, i) => (
                  <input
                    key={i}
                    id={`otp-${i}`}
                    type="text"
                    maxLength={1}
                    value={digit}
                    onChange={(e) => handleOtpChange(e.target.value, i)}
                    style={{
                      width: "44px",
                      height: "50px",
                      textAlign: "center",
                      fontSize: "1.3rem",
                      fontWeight: 700,
                      borderRadius: "var(--radius-md)",
                      border: "1px solid var(--border-color)",
                      backgroundColor: "var(--bg-secondary)",
                      color: "var(--text-primary)"
                    }}
                  />
                ))}
              </div>

              <div style={{ display: "flex", gap: "0.75rem" }}>
                <button
                  type="button"
                  onClick={() => setIsVerifyingEmail(false)}
                  className="btn btn-secondary"
                  style={{ flex: 1 }}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                  Confirm & Access
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
