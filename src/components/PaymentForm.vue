<template>
  <div>
    <h2 class="text-2xl font-bold mb-4">Process Payment</h2>
    <form @submit.prevent="handlePayment" class="space-y-4">
      <div>
        <label for="orderId" class="block text-sm font-medium text-gray-700">Order ID:</label>
        <input
          :value="orderId" 
          id="orderId"
          type="text"
          readonly
          class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
        />
      </div>
      <div>
        <label for="amount" class="block text-sm font-medium text-gray-700">Amount:</label>
        <input
          :value="amount" 
          id="amount"
          type="number"
          readonly
          class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
        />
      </div>
      <button type="submit" :disabled="loading || paymentStatus === 'Payment successful'" class="w-full bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600">
        <span v-if="loading">Processing...</span>
        <span v-else>Process Payment</span>
      </button>
    </form>
    <div v-if="paymentStatus" :class="statusClass" class="mt-4 p-4 rounded">
      {{ paymentStatus }}
    </div>
  </div>
</template>

<script>
import { usePaymentStore } from '@/stores/paymentStore';

export default {
  name: 'PaymentForm',
  props: {
    orderId: {
      type: [String, Number],
      required: true,
    },
    amount: {
      type: [String, Number],
      required: true,
    },
  },
  data() {
    return {
      loading: false,
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
    statusClass() {
      return this.error ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700';
    },
  },
  methods: {
    async handlePayment() {
      const store = usePaymentStore();
      this.loading = true;

      await store.processPayment(this.orderId, this.amount);
      
      this.loading = false;

      if (!this.error) {
        this.$emit('payment-success'); // Emit success event if needed
      }
    },
  },
};
</script>

<style scoped>
/* Add styles for the PaymentForm component if needed */
</style>
