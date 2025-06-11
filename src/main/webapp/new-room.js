document.addEventListener("DOMContentLoaded", () => {
  const hotelSelect = document.getElementById("hotel");

  // Get hotelId from URL if present
  const params = new URLSearchParams(window.location.search);
  const preselectHotelId = parseInt(params.get("hotelId"));

  // Fetch hotels from API
  fetch("http://localhost:8080/HotelManagementSystem/api/hotels")
    .then(response => {
      if (!response.ok) throw new Error("Failed to load hotels");
      return response.json();
    })
    .then(hotels => {
      hotels.forEach(hotel => {
        const option = document.createElement("option");
        option.value = hotel.id;
        option.textContent = hotel.name;

        if (preselectHotelId && hotel.id === preselectHotelId) {
          option.selected = true;
        }

        hotelSelect.appendChild(option);
      });
    })
    .catch(error => {
      console.error("Error loading hotels:", error);
      alert("Could not load hotels. Please try again later.");
    });

  // Handle form submission
  document.getElementById("roomForm").addEventListener("submit", e => {
    e.preventDefault();

    const room = {
      hotelId: parseInt(hotelSelect.value),
      roomNumber: document.getElementById("roomNumber").value.trim(),
      floor: parseInt(document.getElementById("floor").value),
      category: document.getElementById("category").value.trim(),
      size: document.getElementById("size").value.trim(),
      basePrice: parseFloat(document.getElementById("basePrice").value),
      maxOccupancy: parseInt(document.getElementById("maxOccupancy").value),
      isAvailable: document.getElementById("isAvailable").checked
    };

    fetch("http://localhost:8080/HotelManagementSystem/api/rooms", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(room)
    })
      .then(response => {
        if (!response.ok) throw new Error("Failed to add room");
        return response.json();
      })
      .then(() => {
        alert("Room added successfully!");
        window.location.href = "hotel.html";
      })
      .catch(error => {
        console.error("Error adding room:", error);
        alert("Failed to add room. Please try again.");
      });
  });
});
