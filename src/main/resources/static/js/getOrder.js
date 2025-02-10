document.addEventListener("DOMContentLoaded", () => {
    document.getElementById('getOrderForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        const orderId = document.getElementById('orderId').value;

        if (!orderId) {
            alert('Введите ID заказа');
            return;
        }

        try {
            const response = await fetch(`http://localhost:9090/orders/get/${orderId}`);
            if (!response.ok) {
                throw new Error('Не удалось получить заказ');
            }

            const data = await response.json();
            const order = data.order;

            if (!order) {
                throw new Error('Заказ не найден');
            }

            document.getElementById('recipient').textContent = order.recipient;
            document.getElementById('addressDelivery').textContent = order.addressDelivery;
            document.getElementById('paymentType').textContent = order.paymentType === 'card' ? 'Карта' : 'Наличные';
            document.getElementById('deliveryType').textContent = order.deliveryType === 'pickup' ? 'Самовывоз' : 'Доставка';

            const orderItemsList = document.getElementById('orderItems');
            orderItemsList.innerHTML = '';
            order.details.forEach((item, index) => {
                const listItem = document.createElement('li');
                listItem.textContent = `Товар ${index + 1}: ${item.productName} (ID: ${item.articleId}), Количество: ${item.quantity}, Цена за единицу: ${item.amountPerItem} ₽`;
                orderItemsList.appendChild(listItem);
            });

            document.getElementById('orderDetails').classList.remove('hidden');
        } catch (error) {
            alert(error.message);
        }
    });
})