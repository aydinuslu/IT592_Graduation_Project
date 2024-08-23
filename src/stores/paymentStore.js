import { defineStore } from 'pinia';

const API_URL = import.meta.env.VITE_PAYMENT_SERVICE_API;

export const usePaymentStore = defineStore('payment', {
  state: () => ({
    paymentStatus: null,
    error: null,
  }),
  actions: {
    async processPayment(orderId, amount) {
      this.error = null; // Reset any previous errors
      console.log(`Processing payment for orderId: ${orderId}, amount: ${amount}`);

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
        console.log(`Payment response:`, data);

        if (data && data.status === 'FAILED') {
          this.paymentStatus = `Payment failed for Order ID: ${orderId}. Please try again.`;
          const errorMessage = data.message || 'Unknown error occurred'; // Fallback message
          console.error(`Payment failed: ${errorMessage}`);
        } else {
          this.paymentStatus = `Payment successful! Payment ID: ${data.id}, Amount: $${data.amount}`;
          console.log(`Payment successful: Payment ID: ${data.id}, Amount: $${data.amount}`);
        }

        this.error = null;

      } catch (error) {
        this.paymentStatus = null;
        this.error = `Error processing payment: ${error.message}`;
        console.error(`Error during payment processing: ${error.message}`);
      }
    },
  },
});
