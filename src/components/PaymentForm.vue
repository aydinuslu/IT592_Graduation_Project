<template>
    <div>
      <h2 class="text-2xl font-bold mb-4">Process Payment</h2>
      <form @submit.prevent="handlePayment" class="space-y-4">
        <div>
          <label for="orderId" class="block text-sm font-medium text-gray-700">Order ID:</label>
          <input
            v-model="orderId"
            id="orderId"
            type="text"
            required
            class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
          />
        </div>
        <div>
          <label for="amount" class="block text-sm font-medium text-gray-700">Amount:</label>
          <input
            v-model="amount"
            id="amount"
            type="number"
            required
            class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
          />
        </div>
        <button type="submit" class="w-full bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600">
          Process Payment
        </button>
      </form>
      <div v-if="paymentStatus" class="mt-4 p-4 bg-green-100 text-green-700 rounded">
        Payment Processed Successfully!
      </div>
      <div v-if="error" class="mt-4 p-4 bg-red-100 text-red-700 rounded">
        Error: {{ error }}
      </div>
    </div>
  </template>
  
  <script>
  import { usePaymentStore } from '@/stores/paymentStore';
  
  export default {
    name: 'PaymentForm',
    data() {
      return {
        orderId: '',
        amount: 0,
      };
    },
    computed: {
      paymentStatus() {
        const store = usePaymentStore();
        return store.paymentStatus;
      },
      error() {
        const store = usePaymentStore();
        return store.error;
      },
    },
    methods: {
      async handlePayment() {
        const store = usePaymentStore();
        await store.processPayment(this.orderId, this.amount);
      },
    },
  };
  </script>
  
  <style scoped>
  /* Add styles for the PaymentForm component if needed */
  </style>
  