// render.js
function selectRole(role) {
    setRole(role);
    const token = localStorage.getItem('token');
    if (role === "admin") {
        if (token) {
            // TODO Remove console log
            console.log("selectRole: admin with token: calling /adminDashboard/${token}");
            window.location.href = `/adminDashboard/${token}`;
        }
    } else if (role === "doctor") {
        if (token) {
            // TODO Remove console log
            console.log("selectRole: doctor with token: calling /doctorDashboard/${token}");
            window.location.href = `/doctorDashboard/${token}`;
        }
    } if (role === "patient") {
        // TODO Remove console log
        console.log("selectRole: patient calling /pages/patientDashboard.html");
        window.location.href = "/pages/patientDashboard.html";
    } else if (role === "loggedPatient") {
        if (token) {
            // TODO Remove console log
            console.log("selectRole: patient with token: calling /pages/loggedPatientDashboard");
            window.location.href = "/pages/loggedPatientDashboard.html";
        }
    }
}

function renderContent() {
  const role = getRole();
  if (!role) {
    window.location.href = "/"; // if no role, send to role selection page
    return;
  }
}
