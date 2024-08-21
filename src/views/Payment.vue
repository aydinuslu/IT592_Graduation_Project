<template>
  <div class="max-w-lg mx-auto p-6 bg-white rounded-lg shadow-md">
    <h2 class="text-2xl font-bold mb-4">Payment Information</h2>
    <form @submit.prevent="submitPayment" class="space-y-4">
      <div>
        <label for="cardNumber" class="block text-gray-700">Card Number</label>
        <input
          v-model="cardNumber"
          id="cardNumber"
          type="text"
          placeholder="1234 5678 9012 3456"
          class="w-full p-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
          required
        />
      </div>

      <div class="flex space-x-4">
        <div class="flex-1">
          <label for="expiryDate" class="block text-gray-700">Expiry Date</label>
          <input
            v-model="expiryDate"
            id="expiryDate"
            type="text"
            placeholder="MM/YY"
            class="w-full p-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
            required
          />
        </div>

        <div class="flex-1">
          <label for="cvv" class="block text-gray-700">CVV</label>
          <input
            v-model="cvv"
            id="cvv"
            type="text"
            placeholder="123"
            class="w-full p-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
            required
          />
        </div>
      </div>

      <div>
        <label for="cardHolderName" class="block text-gray-700">Cardholder Name</label>
        <input
          v-model="cardHolderName"
          id="cardHolderName"
          type="text"
          placeholder="John Doe"
          class="w-full p-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
          required
        />
      </div>

      <button
        type="submit"
        class="w-full py-2 px-4 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition duration-300"
      >
        Submit Payment
      </button>
    </form>

    <div v-if="paymentError" class="text-red-500 mt-4 text-center">{{ paymentError }}</div>
    <div v-if="paymentSuccess" class="text-green-500 mt-4 text-center">{{ paymentSuccess }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { usePaymentStore } from '@/stores/paymentStore';
import { useRouter } from 'vue-router';

const paymentStore = usePaymentStore();
const router = useRouter();

const cardNumber = ref('');
const expiryDate = ref('');
const cvv = ref('');
const cardHolderName = ref('');
const paymentError = ref(null);
const paymentSuccess = ref(null);

const submitPayment = async () => {
  paymentError.value = null;
  paymentSuccess.value = null;

  try {
    await paymentStore.processPayment({
      cardNumber: cardNumber.value,
      expiryDate: expiryDate.value,
      cvv: cvv.value,
      cardHolderName: cardHolderName.value,
    });

    paymentSuccess.value = 'Payment processed successfully!';
    router.push('/order-confirmation');
  } catch (error) {
    paymentError.value = 'Payment failed. Please try again.';
    console.error('Payment error:', error);
  }
};
</script>
