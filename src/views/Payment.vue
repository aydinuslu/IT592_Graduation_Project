<template>
  <div>
    <h1>Payment</h1>
    <PaymentForm :orderId="orderId" :amount="amount" />
  </div>
</template>

<script>
import PaymentForm from '@/components/PaymentForm.vue'; // Adjust the path accordingly
import { useRoute } from 'vue-router';

export default {
  components: {
    PaymentForm,
  },
  setup() {
    const route = useRoute(); // Get the current route
    const orderId = route.params.orderId;
    const amount = route.params.amount;

    return {
      orderId,
      amount,
    };
  }
}
</script>
