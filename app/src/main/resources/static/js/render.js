// render.js

function selectRole(role) {
  setRole(role);
  const token = localStorage.getItem('token');
  if (role === "admin") {
    if (token) {
    // TODO Remove console log
      console.log("Role admin with token: calling /adminDashboard/${token}");
      window.location.href = `/adminDashboard/${token}`;
    }
  } if (role === "patient") {
    window.location.href = "/pages/patientDashboard.html";
  } else if (role === "doctor") {
    if (token) {
    // TODO Remove console log
          console.log("Role doctor with token: calling /doctorDashboard/${token}");
      window.location.href = `/doctorDashboard/${token}`;
    } else if (role === "loggedPatient") {
      window.location.href = "loggedPatientDashboard.html";
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
