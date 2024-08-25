<template>
    <div class="container mx-auto py-8">
      <h1 class="text-3xl font-bold mb-6">Order Details</h1>
      
      <div v-if="order">
        <h2 class="text-xl font-semibold mb-4">Order ID: {{ order.id }}</h2>
        <p><strong>User ID:</strong> {{ order.userId }}</p>
        <p><strong>Order Date:</strong> {{ formatDate(order.orderDate) }}</p>
        <p><strong>Status:</strong> {{ order.status }}</p>
        <p v-if="payment"><strong>Payment Status:</strong> <span :class="{'text-green-500': payment.status === 'COMPLETED', 'text-red-500': payment.status !== 'COMPLETED'}">{{ payment.status }}</span></p>
        <p v-if="payment"><strong>Payment Amount:</strong> {{ formatCurrency(payment.amount) }}</p>
        <p v-if="payment"><strong>Payment Date:</strong> {{ formatDate(payment.paymentDate) }}</p>
        
        <h3 class="text-lg font-semibold mt-6 mb-2">Items</h3>
        <table class="min-w-full bg-white border border-gray-200">
          <thead>
            <tr class="bg-teal-500 text-white">
              <th class="py-2 px-4">Book ID</th>
              <th class="py-2 px-4">Title</th>
              <th class="py-2 px-4">Quantity</th>
              <th class="py-2 px-4">Price</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in order.items" :key="item.id" class="border-t">
              <td class="py-2 px-4 text-center">{{ item.bookId }}</td>
              <td class="py-2 px-4 text-center">{{ item.bookTitle || 'Loading...' }}</td>
              <td class="py-2 px-4 text-center">{{ item.quantity }}</td>
              <td class="py-2 px-4 text-center">{{ formatCurrency(item.price) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-else>
        <p>Loading order details...</p>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  import { useRoute } from 'vue-router';
  
  export default {
    setup() {
      const order = ref(null);
      const payment = ref(null);
      const route = useRoute();
  
      const fetchOrderDetails = async () => {
        const userId = route.params.userId;
        const orderId = route.params.orderId;
  
        try {
          // Fetch the order details
          const ordersResponse = await fetch(`http://localhost:8084/api/orders/${userId}`);
          if (!ordersResponse.ok) throw new Error(`Failed to fetch orders for user ID ${userId}`);
  
          const orders = await ordersResponse.json();
          if (!Array.isArray(orders)) throw new Error('Invalid orders data');
  
          const selectedOrder = orders.find(order => order.id === parseInt(orderId));
          if (selectedOrder) {
            // Fetch book details for each item
            for (const item of selectedOrder.items) {
              const bookResponse = await fetch(`http://localhost:8082/api/books/edit/${item.bookId}`);
              if (bookResponse.ok) {
                const bookData = await bookResponse.json();
                item.bookTitle = bookData.title; // Add book title to the item
              } else {
                console.error(`Failed to fetch details for book ID ${item.bookId}`);
                item.bookTitle = 'Unknown Title'; // Handle error case
              }
            }
            order.value = selectedOrder;
  
            // Fetch the payment details
            const paymentResponse = await fetch(`http://localhost:8085/api/payment/status/${orderId}`);
            if (paymentResponse.ok) {
              const paymentData = await paymentResponse.json();
              payment.value = paymentData;
            } else {
              console.warn(`No payment data found for order ID ${orderId}.`);
            }
          } else {
            console.warn(`Order with ID ${orderId} not found for user ID ${userId}`);
          }
        } catch (error) {
          console.error('Error fetching order details:', error.message);
        }
      };
  
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
  
      onMounted(fetchOrderDetails);
  
      return {
        order,
        payment,
        formatCurrency,
        formatDate,
      };
    },
  };
  </script>
  
  <style scoped>
  /* Tailwind CSS is used for inline styles */
  </style>
  