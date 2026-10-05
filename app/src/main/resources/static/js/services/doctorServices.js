// doctorServices.js
/*
  Import the base API URL from the config file
*/
import { API_BASE_URL } from "../config/config.js";
/*
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions
*/
const DOCTOR_API = API_BASE_URL + '/doctor'

/*
  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API
*/
export async function getDoctors(){
/*
   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array
*/
  try {
    const response = await fetch(DOCTOR_API, {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' }
        });
    if (!response.ok) {
      console.error('Error on getDoctors:', response.message);
      return { doctors: [] };
    }
    const data = await response.json();

    return data ;

  } catch (error) {
     console.error('Error on getDoctors:', error);
     return { doctors: [] };
  }
}

/*
  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token
*/
export async function deleteDoctor(id, token) {
/*
   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response
*/
  try {
    const response = await fetch(`\({DOCTOR_API}/\){id}/${token}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || (response.ok ? 'Doctor deleted successfully.' : 'Failed to delete doctor.')
    };
  } catch (error) {
    console.error('Error deleting doctor:', error.message || error);
    return {
      success: false,
      message: 'An unexpected error occurred while attempting to delete the doctor.'
    };
  }
}


/*
  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request
*/
export async function saveDoctor(doctor, token) {
/*
   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs
*/

  try {
    // 1. Send POST request with token in the URL path
    const response = await fetch(`\({DOCTOR_API}/\){token}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(doctor)
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || (response.ok ? 'Doctor saved successfully.' : 'Failed to save doctor.')
    };
  } catch (error) {
    console.error('Error saving doctor:', error.message || error);
    return {
      success: false,
      message: 'An unexpected error occurred while attempting to save the doctor.'
    };
  }
}

/*
  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)
*/
export async function filterDoctors(name, time, specialty) {
/*
   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/

  try {
    // 1. Construct URL with parameters encoded to handle spaces or special characters safely
    const encodedName = encodeURIComponent(name || '');
    const encodedTime = encodeURIComponent(time || '');
    const encodedSpecialty = encodeURIComponent(specialty || '');

    const url = `\({DOCTOR_API}/\){encodedName}/\({encodedTime}/\){encodedSpecialty}`;

    // 2. Send GET request
    const response = await fetch(url);

    // 3. Check if response is OK
    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      console.error(`Failed to fetch filtered doctors. Server status: ${response.status}`);
      return { doctors: [] };
    }
  } catch (error) {
    // 4. Catch any other errors, alert the user, and return default empty result
    console.error('Error filtering doctors:', error.message || error);
    alert('An error occurred while filtering doctors. Please try again.');
    return { doctors: [] };
  }
}


   /*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/
