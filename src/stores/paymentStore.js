import { defineStore } from 'pinia';

const API_URL = import.meta.env.VITE_PAYMENT_SERVICE_API;

export const usePaymentStore = defineStore('payment', {
  state: () => ({
    paymentStatus: null,
    error: null,
  }),
  actions: {
    async processPayment(orderId, amount) {
      try {
        const response = await fetch(`${API_URL}/process?orderId=${orderId}&amount=${amount}`, {
          method: 'POST',
          headers: {
            'Accept': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error('Failed to process payment');
        }

        const data = await response.json();
        this.paymentStatus = data;
        this.error = null;
      } catch (error) {
        this.error = error.message;
      }
    },
  },
});
