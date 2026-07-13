// ─── Mock Data (Staff dashboard + notifications remain mock for now) ──
export const MOCK_STAFF_ORDERS = [
  { id: "ORD-1042", customer: "Maria Santos", service: "Wash & Fold", weight: 5.2, price: 260, status: "Processing", date: "2026-07-04", paid: false },
  { id: "ORD-1041", customer: "Juan Dela Cruz", service: "Dry Clean", weight: 3.0, price: 450, status: "Pending", date: "2026-07-04", paid: false },
  { id: "ORD-1040", customer: "Ana Reyes", service: "Wash & Fold", weight: 6.5, price: 325, status: "Picked Up", date: "2026-07-04", paid: false },
  { id: "ORD-1038", customer: "Pedro Garcia", service: "Dry Clean", weight: 2.1, price: 315, status: "Ready", date: "2026-07-03", paid: false },
  { id: "ORD-1037", customer: "Lisa Tan", service: "Fold Only", weight: 4.2, price: 168, status: "Ready", date: "2026-07-03", paid: true },
  { id: "ORD-1035", customer: "Maria Santos", service: "Wash & Fold", weight: 3.8, price: 190, status: "Delivered", date: "2026-07-01", paid: true },
];

export const MOCK_NOTIFICATIONS = [
  { id: 1, text: "Your order ORD-1042 is now being processed.", time: "2 hours ago", read: false },
  { id: 2, text: "Order ORD-1038 is ready for pickup!", time: "5 hours ago", read: false },
  { id: 3, text: "Your order ORD-1035 has been delivered.", time: "2 days ago", read: true },
  { id: 4, text: "Payment received for ORD-1035. Thank you!", time: "2 days ago", read: true },
];

export const STATUS_STEPS = ["Pending", "Picked Up", "Processing", "Ready", "Delivered"];

export const SERVICE_ICONS = {
  "Wash & Fold": "🧺",
  "Dry Clean": "👔",
  "Fold Only": "👕",
};

export const STATUS_CLASS = {
  Pending: "pending",
  "Picked Up": "picked-up",
  Processing: "processing",
  Ready: "ready",
  Delivered: "delivered",
};

// ─── Backend <-> Display mapping ─────────────────────────────
export const SERVICE_TYPE_LABELS = {
  WASH_FOLD: "Wash & Fold",
  DRY_CLEAN: "Dry Clean",
  FOLD_ONLY: "Fold Only",
};

export const STATUS_LABELS = {
  PENDING: "Pending",
  PICKED_UP: "Picked Up",
  PROCESSING: "Processing",
  READY: "Ready",
  DELIVERED: "Delivered",
};

export const TIME_SLOTS = [
  "8:00 AM - 11:00 AM",
  "11:00 AM - 2:00 PM",
  "2:00 PM - 5:00 PM",
  "5:00 PM - 8:00 PM",
];
