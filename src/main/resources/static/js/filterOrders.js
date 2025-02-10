document.addEventListener("DOMContentLoaded", () => {
    document.getElementById('filterOrdersForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        const actualDay = document.getElementById('actualDay').value;
        const minAmount = document.getElementById('minAmount').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        const disableArticlesInput = document.getElementById('disableArticles').value;

        let url = 'http://localhost:9090/orders/filter/order';
        let params = {};

        if (actualDay || minAmount) {
            if (actualDay) params.actualDay = actualDay;
            if (minAmount) params.minAmount = minAmount;
        } else if (startDate && endDate) {
            url = 'http://localhost:9090/orders/filter/orderDetails';
            params.startDate = startDate;
            params.endDate = endDate;

            if (disableArticlesInput) {
                params.disableArticles = disableArticlesInput.split(',').map(item => parseInt(item.trim()));
            }
        } else {
            alert('Неверные параметры фильтрации');
            return;
        }

        try {
            const response = await fetch(url + '?' + new URLSearchParams(params));
            if (!response.ok) {
                throw new Error('Не удалось получить заказы');
            }

            const data = await response.json();
            const orders = data.order;

            if (!orders || orders.length === 0) {
                alert('Заказы не найдены');
                return;
            }

            const ordersList = document.getElementById('ordersList');
            ordersList.innerHTML = '';

            orders.forEach((order, index) => {
                const listItem = document.createElement('li');
                listItem.textContent = `Заказ ${order.orderId}: Имя получателя - ${order.recipient}, Адрес доставки - ${order.addressDelivery}, Сумма - ${order.totalAmount} ₽`;
                ordersList.appendChild(listItem);
            });

            document.getElementById('filteredOrders').classList.remove('hidden');
        } catch (error) {
            alert(error.message);
        }
    });
})