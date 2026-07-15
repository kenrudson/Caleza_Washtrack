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
