(function () {
    'use strict';

    var insurancePrices = {};

    var carSelect = document.getElementById('carId');
    var insuranceSelect = document.getElementById('insuranceType');
    var summary = document.getElementById('priceSummary');
    var startInput = document.querySelector('input[name="startTime"]');
    var endInput = document.querySelector('input[name="endTime"]');

    function getHours() {
        var start = new Date(startInput.value);
        var end = new Date(endInput.value);
        if (isNaN(start) || isNaN(end) || end <= start) return 0;
        return Math.floor((end - start) / 3600000);
    }

    function updatePriceSummary() {
        var carOption = carSelect.options[carSelect.selectedIndex];
        var insuranceType = insuranceSelect.value;

        var hourlyPrice = carOption && carOption.dataset.hourlyPrice
            ? parseFloat(carOption.dataset.hourlyPrice)
            : 0;
        var insPrice = insurancePrices[insuranceType] || 0;
        var hours = getHours();

        if (hourlyPrice > 0 && insPrice > 0 && hours > 0) {
            var rental = hourlyPrice * hours;
            var total = rental + insPrice;

            document.getElementById('rentalHours').textContent = hours;
            document.getElementById('hourlyRate').textContent = hourlyPrice.toFixed(2);
            document.getElementById('rentalCost').textContent = rental.toFixed(2);
            document.getElementById('insuranceName').textContent =
                insuranceSelect.options[insuranceSelect.selectedIndex].textContent;
            document.getElementById('insuranceCost').textContent = insPrice.toFixed(2);
            document.getElementById('totalPrice').textContent = total.toFixed(2);
            summary.style.display = '';
        } else {
            summary.style.display = 'none';
        }
    }

    function fetchInsurancePrices() {
        fetch('/api/pricing/insurance')
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('Failed to fetch insurance prices');
                }
                return response.json();
            })
            .then(function (prices) {
                insurancePrices = prices;
                updatePriceSummary();
            })
            .catch(function (error) {
                console.error('Error fetching insurance prices:', error);
            });
    }

    carSelect.addEventListener('change', updatePriceSummary);
    insuranceSelect.addEventListener('change', updatePriceSummary);

    fetchInsurancePrices();
})();
