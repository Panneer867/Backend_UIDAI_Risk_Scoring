const form = document.getElementById("riskForm");
const result = document.getElementById("result");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const body = {
        partner_id: document.getElementById("partnerId").value,
        aadhaar_number: document.getElementById("aadhaar").value,
        device_data: {
            device_id: document.getElementById("deviceId").value,
            browser: navigator.userAgent
        },
        callback_url: document.getElementById("callbackUrl").value
    };

    try {
        const response = await fetch("/api/v1/risk-score/initiate", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer demo-write-token"
            },
            body: JSON.stringify(body)
        });

        const data = await response.json();

        result.textContent = JSON.stringify({
            http_status: response.status,
            response: data
        }, null, 2);
    } catch (error) {
        result.textContent = "Request failed: " + error.message;
    }
});
