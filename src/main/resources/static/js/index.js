let detailIndex = 1;

function addOrderDetail() {
    const orderDetails = document.getElementById('orderDetails');

    const newDetail = document.createElement('div');
    newDetail.classList.add('border-t', 'border-gray-200', 'pt-4');
    newDetail.innerHTML = `
                <div class="mt-4 space-y-4">
                    <div>
                        <label for="articleId${detailIndex}" class="block text-sm font-medium text-gray-700">ID товара</label>
                        <input type="number" id="articleId${detailIndex}" name="details[${detailIndex}].articleId" required
                               class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
                    </div>
                    <div>
                        <label for="productName${detailIndex}" class="block text-sm font-medium text-gray-700">Название товара</label>
                        <input type="text" id="productName${detailIndex}" name="details[${detailIndex}].productName" required
                               class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
                    </div>
                    <div>
                        <label for="quantity${detailIndex}" class="block text-sm font-medium text-gray-700">Количество</label>
                        <input type="number" id="quantity${detailIndex}" name="details[${detailIndex}].quantity" required
                               class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
                    </div>
                    <div>
                        <label for="amountPerItem${detailIndex}" class="block text-sm font-medium text-gray-700">Цена за единицу</label>
                        <input type="number" step="0.01" id="amountPerItem${detailIndex}" name="details[${detailIndex}].amountPerItem" required
                               class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
                    </div>
                </div>
            `;

    orderDetails.appendChild(newDetail);
    detailIndex++;
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById('orderForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        const formData = new FormData(event.target);
        const data = {
            recipient: formData.get('recipient'),
            addressDelivery: formData.get('addressDelivery'),
            paymentType: formData.get('paymentType'),
            deliveryType: formData.get('deliveryType'),
            details: []
        };

        const detailElements = document.querySelectorAll('#orderDetails > div');
        detailElements.forEach((element, index) => {
            data.details.push({
                articleId: parseInt(formData.get(`details[${index}].articleId`)),
                productName: formData.get(`details[${index}].productName`),
                quantity: parseInt(formData.get(`details[${index}].quantity`)),
                amountPerItem: parseFloat(formData.get(`details[${index}].amountPerItem`))
            });
        });

        try {
            const response = await fetch('/orders/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert('Заказ успешно создан!');
            } else {
                alert('Ошибка при создании заказа.');
            }
        } catch (error) {
            console.error('Ошибка:', error);
            alert('Произошла ошибка при отправке запроса.');
        }
    });
})