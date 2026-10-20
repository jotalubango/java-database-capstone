// patientDashboard.js
import { getDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import { createDoctorCard } from './components/doctorCard.js';
import { filterDoctors } from './services/doctorServices.js';//call the same function to avoid duplication coz the functionality was same
import { patientSignup, patientLogin } from './services/patientServices.js';

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});

document.addEventListener("DOMContentLoaded", () => {
  const btn = document.getElementById("patientSignup");
  if (btn) {
    btn.addEventListener("click", () => openModal("patientSignup"));
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const loginBtn = document.getElementById("patientLogin")
  if (loginBtn) {
    loginBtn.addEventListener("click", () => {
      openModal("patientLogin")
    })
  }
})

function loadDoctorCards() {
    const contentDiv = document.getElementById("content");
    // Guard check in case the container element isn't present
    if (!contentDiv) {
        console.error("Target container element '#content' not found.");
        return;
    }
    // 1. Clear existing content
    contentDiv.innerHTML = "";

    getDoctors()
    .then(result => {
      result.doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    })
    .catch(error => {
      console.error("Failed to load doctors:", error);
    });
}
// Filter Input
document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);



function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar").value.trim();
  const filterTime = document.getElementById("filterTime").value;
  const filterSpecialty = document.getElementById("filterSpecialty").value;


  const name = searchBar.length > 0 ? searchBar : null;
  const time = filterTime.length > 0 ? filterTime : null;
  const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;

  filterDoctors(name, specialty, time)
    .then(response => {
      const doctors = response.doctors;
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      if (doctors.length > 0) {
        console.log(doctors);
        doctors.forEach(doctor => {
          const card = createDoctorCard(doctor);
          contentDiv.appendChild(card);
        });
      } else {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
        console.log("Nothing");
      }
    })
    .catch(error => {
      console.error("Failed to filter doctors:", error);
      alert("❌ An error occurred while filtering doctors.");
    });
}

window.signupPatient = async function () {
  try {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const data = { name, email, password, phone, address };
    const { success, message } = await patientSignup(data);
    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    }
    else alert(message);
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

window.patientLoginHandler = async function () {
    const username = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const data = { username, password };
    try {
        const response = await patientLogin(data);
        const responseData = await response.json();
        if (!response.ok) {
            alert('❌ Invalid credentials! ' + responseData.message );
            return;
        }
        // Store the token in localStorage
        localStorage.setItem('token', responseData.token); // Adjust property name if your API uses 'accessToken' or 'jwt'
        // Call selectRole('loggedPatient')
        selectRole('loggedPatient');
    } catch (error) {
        alert("❌ Failed to Login : ", error);
        return;
    }
}
