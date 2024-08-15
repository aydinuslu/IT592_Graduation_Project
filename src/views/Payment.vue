<template>
  <div class="max-w-md mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">Process Payment</h1>
    <PaymentForm />
  </div>
</template>

<script>
import PaymentForm from '@/components/PaymentForm.vue';

export default {
  name: 'PaymentView',
  components: {
    PaymentForm,
  },
};
</script>

<style scoped>
/* Add styles for the Payment view if needed */
</style>
