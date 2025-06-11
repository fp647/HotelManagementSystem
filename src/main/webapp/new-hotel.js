document.getElementById("hotelForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const name = document.getElementById("name").value.trim();
  const address = document.getElementById("address").value.trim();
  const country = document.getElementById("country").value.trim();
  const city = document.getElementById("city").value.trim();
  const phone = document.getElementById("phone").value.trim();
  const email = document.getElementById("email").value.trim();
  let picture = document.getElementById("picture").value.trim();
  
  if (!picture.endsWith(".jpg") && !picture.endsWith(".png")) {
    picture += ".jpg";
  }

  const newHotel = {
    name,
    address,
    country,
    city,
    phone,
	email,
	picture
  };

  const apiUrl = "http://localhost:8080/HotelManagementSystem/api/hotels";

  fetch(apiUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(newHotel)
  })
  .then(response => {
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    return response.json(); // only works if servlet returns JSON
  })
  .then(data => {
    console.log("Hotel created:", data);
    alert("Hotel added successfully!");
    window.location.href = "hotel.html";
  })
  .catch(error => {
    console.error("There was a problem with the fetch operation:", error);
    alert("Failed to add hotel. Please try again.");
  });
});