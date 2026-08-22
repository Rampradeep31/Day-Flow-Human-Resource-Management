import React from "react";

export default function StatusBadge({ status, type = "default" }) {
  if (!status) return null;

  const normalized = status.toLowerCase();

  let badgeClass = "badge-holiday";

  if (["present", "approved", "active", "paid", "success"].includes(normalized)) {
    badgeClass = "badge-present";
  } else if (["absent", "rejected", "inactive", "danger", "error"].includes(normalized)) {
    badgeClass = "badge-absent";
  } else if (["half-day", "pending", "draft", "warning"].includes(normalized)) {
    badgeClass = "badge-halfday";
  } else if (["leave", "sick", "casual", "unpaid", "info"].includes(normalized)) {
    badgeClass = "badge-leave";
  } else if (["admin", "hr officer"].includes(normalized)) {
    badgeClass = "badge-role-admin";
  } else if (["employee"].includes(normalized)) {
    badgeClass = "badge-role-employee";
  }

  return (
    <span className={`badge ${badgeClass}`}>
      <span className="badge-dot" />
      {status}
    </span>
  );
}
