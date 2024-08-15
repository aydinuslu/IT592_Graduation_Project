import { defineStore } from 'pinia';

const API_URL = import.meta.env.VITE_ORDER_SERVICE_API;

export const useOrderStore = defineStore('order', {
  state: () => ({
    orders: [],
    error: null,
  }),
  actions: {
    async placeOrder(orderData) {
      try {
        const response = await fetch(`${API_URL}/orders`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(orderData),
        });

        if (!response.ok) {
          throw new Error('Failed to place order');
        }

        const newOrder = await response.json();
        this.orders.push(newOrder);
        this.error = null;
      } catch (error) {
        this.error = error.message;
      }
    },
    async fetchOrders() {
      try {
        const response = await fetch(`${API_URL}/orders`);
        if (!response.ok) {
          throw new Error('Failed to fetch orders');
        }
        this.orders = await response.json();
        this.error = null;
      } catch (error) {
        this.error = error.message;
      }
    },
  },
});
