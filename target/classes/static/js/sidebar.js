const NAV_ITEMS = [
  { href: "dashboard.html", label: "Dashboard", key: "dashboard", roles: ["Admin", "Doctor", "Receptionist", "Pharmacist"] },
  { href: "patients.html", label: "Patients", key: "patients", roles: ["Admin", "Doctor", "Receptionist"] },
  { href: "doctors.html", label: "Doctors", key: "doctors", roles: ["Admin", "Doctor", "Receptionist"] },
  { href: "prescriptions.html", label: "Prescriptions", key: "prescriptions", roles: ["Admin", "Doctor", "Pharmacist"] },
  { href: "prescription-items.html", label: "Prescription Items", key: "prescription-items", roles: ["Admin", "Doctor", "Pharmacist"] },
  { href: "medicines.html", label: "Medicines", key: "medicines", roles: ["Admin", "Pharmacist", "Doctor"] },
  { href: "inventory.html", label: "Inventory (Stock)", key: "inventory", roles: ["Admin", "Pharmacist"] },
  { href: "suppliers.html", label: "Suppliers", key: "suppliers", roles: ["Admin", "Pharmacist"] },
  { href: "purchase-orders.html", label: "Purchase Orders", key: "purchase-orders", roles: ["Admin", "Pharmacist"] },
  { href: "ai-dashboard.html", label: "AI Insights", key: "ai-dashboard", roles: ["Admin", "Pharmacist"] },
  { href: "bills.html", label: "Bills", key: "bills", roles: ["Admin", "Receptionist"] },
  { href: "bill-items.html", label: "Bill Items", key: "bill-items", roles: ["Admin", "Receptionist"] },
  { href: "reports.html", label: "Reports", key: "reports", roles: ["Admin"] },
  { href: "users.html", label: "Users / Staff", key: "users", roles: ["Admin"] },
];

function renderSidebar(activeKey) {
  const mount = document.getElementById("sidebar");
  if (!mount) return;

  const user = currentUser();
  const role = user ? user.role : null;

  const visibleItems = NAV_ITEMS.filter(item => !role || item.roles.includes(role));

  const links = visibleItems.map(item => {
    const cls = item.key === activeKey ? "active" : "";
    return `<a href="${item.href}" class="${cls}">${item.label}</a>`;
  }).join("");

  mount.innerHTML = `
    <div class="brand">Care<span>Hub</span></div>
    <nav>${links}</nav>
    <div class="user-box">
      Logged in as <strong>${user ? user.username : ""}</strong><br/>
      <span class="role">${user ? user.role : ""}</span><br/>
      <span class="logout-btn" onclick="logout()">Log out</span>
    </div>
  `;

  // Belt-and-suspenders: if a signed-in user somehow lands on a page their
  // role can't use, bounce them back to the dashboard. The real
  // enforcement is server-side (Spring Security), this is just UX.
  if (role && activeKey) {
    const current = NAV_ITEMS.find(i => i.key === activeKey);
    if (current && !current.roles.includes(role)) {
      window.location.href = "dashboard.html";
    }
  }
}
