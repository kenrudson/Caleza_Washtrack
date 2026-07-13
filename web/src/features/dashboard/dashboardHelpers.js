import { STATUS_STEPS, SERVICE_TYPE_LABELS, STATUS_LABELS } from "./dashboardConstants";

// ─── Utility: get status step index ─────────────────────────
export function getStatusIndex(status) {
  return STATUS_STEPS.indexOf(status);
}

// ─── Utility: get initials ──────────────────────────────────
export function getInitials(name) {
  return name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);
}

// ─── Utility: get next status ───────────────────────────────
export function getNextStatus(current) {
  const idx = STATUS_STEPS.indexOf(current);
  if (idx < STATUS_STEPS.length - 1) return STATUS_STEPS[idx + 1];
  return null;
}

// Converts a backend OrderResponse into the shape the existing UI expects
export function toDisplayOrder(o) {
  return {
    id: `ORD-${1000 + o.orderId}`,
    service: SERVICE_TYPE_LABELS[o.serviceType] || o.serviceType,
    weight: o.weightKg,
    price: o.totalPrice,
    status: STATUS_LABELS[o.status] || o.status,
    date: (o.createdAt || "").split("T")[0],
    paid: o.paid,
  };
}

// Converts a backend StaffOrderResponse into the shape StaffDashboard expects.
// Keeps the raw numeric orderId (needed for the advance-status API call) alongside
// the display code, since the UI shows "ORD-1042" but the endpoint needs just 1042's id.
export function toStaffDisplayOrder(o) {
  return {
    orderId: o.orderId,
    id: o.orderCode,
    customer: o.customerName,
    address: o.pickupAddress,
    service: SERVICE_TYPE_LABELS[o.serviceType] || o.serviceType,
    weight: o.weightKg,
    price: o.totalPrice,
    status: STATUS_LABELS[o.status] || o.status,
    date: (o.createdAt || "").split("T")[0],
    paid: o.paid,
  };
}

// Minimum selectable pickup date: tomorrow, per BR-002 (same-day not permitted)
export function getMinPickupDate() {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return d.toISOString().split("T")[0];
}

// ─── Helper: Time-based greeting ────────────────────────────
export function getTimeGreeting() {
  const hour = new Date().getHours();
  if (hour < 12) return "morning";
  if (hour < 17) return "afternoon";
  return "evening";
}
