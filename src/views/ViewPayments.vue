<template>
    <div class="container mx-auto py-8">
      <h1 class="text-3xl font-bold mb-6">View Payments</h1>
      <table class="min-w-full bg-white border border-gray-200">
        <thead>
          <tr class="bg-teal-500 text-white">
            <th @click="sortBy('orderId')" class="py-2 px-4 cursor-pointer">Order ID</th>
            <th @click="sortBy('userId')" class="py-2 px-4 cursor-pointer">User ID</th>
            <th @click="sortBy('username')" class="py-2 px-4 cursor-pointer">Username</th>
            <th @click="sortBy('amount')" class="py-2 px-4 cursor-pointer">Amount</th>
            <th @click="sortBy('status')" class="py-2 px-4 cursor-pointer">Status</th>
            <th @click="sortBy('date')" class="py-2 px-4 cursor-pointer">Date</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="payment in sortedPayments" :key="payment.orderId" class="border-t">
            <td class="py-2 px-4 text-center">
              <router-link :to="`/order-details/${payment.userId}/${payment.orderId}`" class="text-blue-600 underline">
                {{ payment.orderId }}
              </router-link>
            </td>
            <td class="py-2 px-4 text-center">{{ payment.userId }}</td>
            <td class="py-2 px-4 text-center">{{ payment.username }}</td>
            <td class="py-2 px-4 text-center">{{ formatCurrency(payment.amount) }}</td>
            <td class="py-2 px-4 text-center">
              <span :class="{'text-green-500': payment.status === 'COMPLETED', 'text-red-500': payment.status !== 'COMPLETED'}">
                {{ payment.status }}
              </span>
            </td>
            <td class="py-2 px-4 text-center">{{ formatDate(payment.date) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted } from 'vue';
  
  export default {
    setup() {
      const payments = ref([]);
      const sortField = ref('orderId');
      const sortOrder = ref(1); // 1 for ascending, -1 for descending
  
      const fetchPaymentsAndUsers = async () => {
        try {
          // Fetch all users
          const usersResponse = await fetch('http://74.248.84.174/api/users');
          if (!usersResponse.ok) throw new Error('Failed to fetch users');
          
          const users = await usersResponse.json();
          if (!Array.isArray(users)) throw new Error('Invalid users data');
  
          // Loop through each user and get their orders
          for (const user of users) {
            const ordersResponse = await fetch(`http://74.248.84.174/api/orders/${user.id}`);
            if (!ordersResponse.ok) throw new Error(`Failed to fetch orders for user ID ${user.id}`);
  
            const orders = await ordersResponse.json();
            if (!Array.isArray(orders)) throw new Error('Invalid orders data');
  
            // For each order, fetch the payment status
            for (const order of orders) {
              const paymentResponse = await fetch(`http://74.248.84.174/api/payment/status/${order.id}`);
  
              if (paymentResponse.status === 204) {
                console.log(`No payment data for order ID ${order.id}. Skipping.`);
                continue; // Skip orders with no payment data
              }
  
              if (!paymentResponse.ok) throw new Error(`Failed to fetch payment status for order ID ${order.id}`);
  
              const paymentData = await paymentResponse.json();
              if (!paymentData || paymentData.status !== 'COMPLETED') continue; // Skip if payment is not completed
  
              // Add the payment information to the payments array
              payments.value.push({
                orderId: order.id,
                userId: user.id,
                username: user.username,
                amount: paymentData.amount,
                status: paymentData.status,
                date: paymentData.paymentDate,
              });
            }
          }
        } catch (error) {
          console.error('Error fetching payments or users:', error.message);
        }
      };
  
      const sortBy = (field) => {
        if (sortField.value === field) {
          sortOrder.value *= -1; // Reverse order if the same field is clicked again
        } else {
          sortField.value = field;
          sortOrder.value = 1; // Default to ascending order
        }
      };
  
      const sortedPayments = computed(() => {
        return [...payments.value].sort((a, b) => {
          if (a[sortField.value] < b[sortField.value]) return -1 * sortOrder.value;
          if (a[sortField.value] > b[sortField.value]) return 1 * sortOrder.value;
          return 0;
        });
      });
  
      const formatCurrency = (amount) => {
        if (typeof amount !== 'number' || isNaN(amount)) {
          return 'N/A'; // Return 'N/A' if amount is not a valid number
        }
        return `$${amount.toFixed(2)}`;
      };
  
      const formatDate = (dateString) => {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
          return 'Invalid Date';
        }
        return date.toLocaleDateString();
      };
  
      onMounted(fetchPaymentsAndUsers);
  
      return {
        payments,
        sortBy,
        sortedPayments,
        formatCurrency,
        formatDate,
      };
    },
  };
  </script>
  
  <style scoped>
  /* Tailwind CSS is used for inline styles */
  </style>
  