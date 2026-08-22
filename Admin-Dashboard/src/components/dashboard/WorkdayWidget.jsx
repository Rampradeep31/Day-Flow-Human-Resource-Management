import React, { useState } from "react";
import { useHRMS } from "../../context/HRMSContext";
import { Clock, Play, Pause, Square, MapPin, Laptop, Coffee, Sparkles } from "lucide-react";

export default function WorkdayWidget() {
  const {
    isCheckedIn,
    checkInTime,
    elapsedSeconds,
    isOnBreak,
    currentWorkMode,
    punchIn,
    punchOut,
    toggleBreak
  } = useHRMS();

  const [selectedMode, setSelectedMode] = useState(currentWorkMode || "Office");
  const [punchNote, setPunchNote] = useState("");

  const formatElapsedTime = (totalSec) => {
    const hours = Math.floor(totalSec / 3600);
    const minutes = Math.floor((totalSec % 3600) / 60);
    const seconds = totalSec % 60;
    return `${String(hours).padStart(2, "0")}:${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
  };

  // Target standard 8 hours (28,800 seconds)
  const targetSeconds = 8 * 3600;
  const progressPercent = Math.min(100, Math.round((elapsedSeconds / targetSeconds) * 100));

  return (
    <div
      className="glass-card"
      style={{
        padding: "1.5rem",
        background: isCheckedIn
          ? "linear-gradient(135deg, rgba(99, 102, 241, 0.08) 0%, rgba(16, 185, 129, 0.05) 100%)"
          : "var(--bg-card)",
        position: "relative",
        overflow: "hidden"
      }}
    >
      {/* Header */}
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1.25rem" }}>
        <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
          <div
            style={{
              width: "10px",
              height: "10px",
              borderRadius: "50%",
              backgroundColor: isCheckedIn
                ? isOnBreak
                  ? "var(--color-warning)"
                  : "var(--color-success)"
                : "var(--text-muted)",
              boxShadow: isCheckedIn && !isOnBreak ? "0 0 0 4px rgba(16, 185, 129, 0.25)" : "none"
            }}
          />
          <h3 style={{ fontSize: "1.05rem", fontWeight: 700, color: "var(--text-primary)" }}>
            {isCheckedIn
              ? isOnBreak
                ? "Workday Paused (On Break)"
                : "Workday in Progress"
              : "Not Clocked In"}
          </h3>
        </div>

        {isCheckedIn && (
          <span
            style={{
              fontSize: "0.75rem",
              fontWeight: 600,
              padding: "0.25rem 0.6rem",
              borderRadius: "var(--radius-full)",
              backgroundColor: "var(--bg-tertiary)",
              color: "var(--text-secondary)",
              display: "flex",
              alignItems: "center",
              gap: "0.35rem"
            }}
          >
            {currentWorkMode === "Office" ? <MapPin size={12} /> : <Laptop size={12} />}
            {currentWorkMode} Mode
          </span>
        )}
      </div>

      {/* Main Clock / Duration Display */}
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          padding: "1rem 0",
          textAlign: "center"
        }}
      >
        <div
          style={{
            fontSize: "2.5rem",
            fontWeight: 800,
            fontFamily: "var(--font-mono)",
            letterSpacing: "-0.03em",
            color: isCheckedIn ? (isOnBreak ? "var(--color-warning)" : "var(--brand-primary)") : "var(--text-muted)",
            lineHeight: 1
          }}
        >
          {isCheckedIn ? formatElapsedTime(elapsedSeconds) : "00:00:00"}
        </div>
        <div style={{ fontSize: "0.775rem", color: "var(--text-muted)", marginTop: "0.4rem" }}>
          {isCheckedIn ? `Punched in today at ${checkInTime}` : "Standard Shift: 9:00 AM – 6:00 PM (8.0 hrs)"}
        </div>

        {/* Workday Progress Bar */}
        {isCheckedIn && (
          <div style={{ width: "100%", maxWidth: "340px", marginTop: "1rem" }}>
            <div style={{ display: "flex", justifyContent: "space-between", fontSize: "0.725rem", color: "var(--text-muted)", marginBottom: "0.25rem" }}>
              <span>Day Progress</span>
              <span>{progressPercent}% of 8 hrs</span>
            </div>
            <div style={{ width: "100%", height: "6px", backgroundColor: "var(--border-color)", borderRadius: "3px", overflow: "hidden" }}>
              <div
                style={{
                  height: "100%",
                  width: `${progressPercent}%`,
                  backgroundColor: progressPercent >= 100 ? "var(--color-success)" : "var(--brand-primary)",
                  borderRadius: "3px",
                  transition: "width 0.3s ease"
                }}
              />
            </div>
          </div>
        )}
      </div>

      {/* Controls & Work Mode Toggle */}
      <div style={{ marginTop: "1rem" }}>
        {!isCheckedIn ? (
          <div>
            <div style={{ display: "flex", gap: "0.5rem", marginBottom: "0.85rem" }}>
              <button
                type="button"
                onClick={() => setSelectedMode("Office")}
                className={`btn btn-sm ${selectedMode === "Office" ? "btn-primary" : "btn-secondary"}`}
                style={{ flex: 1 }}
              >
                <MapPin size={14} /> In Office (HQ)
              </button>
              <button
                type="button"
                onClick={() => setSelectedMode("Remote")}
                className={`btn btn-sm ${selectedMode === "Remote" ? "btn-primary" : "btn-secondary"}`}
                style={{ flex: 1 }}
              >
                <Laptop size={14} /> Remote (Home)
              </button>
            </div>

            <button
              onClick={() => punchIn(selectedMode, punchNote)}
              className="btn btn-success"
              style={{ width: "100%", padding: "0.75rem", fontSize: "0.95rem" }}
            >
              <Play size={18} /> Clock In Now
            </button>
          </div>
        ) : (
          <div style={{ display: "flex", gap: "0.75rem" }}>
            <button
              onClick={toggleBreak}
              className={`btn ${isOnBreak ? "btn-primary" : "btn-secondary"}`}
              style={{ flex: 1 }}
            >
              {isOnBreak ? <Play size={16} /> : <Coffee size={16} />}
              {isOnBreak ? "Resume Work" : "Take Break"}
            </button>
            <button
              onClick={() => punchOut(punchNote)}
              className="btn btn-danger"
              style={{ flex: 1 }}
            >
              <Square size={16} /> Clock Out
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
