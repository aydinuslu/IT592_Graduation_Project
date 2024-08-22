import { defineStore } from 'pinia';
import { useAuthStore } from '@/stores/authStore';
import { useShoppingCartStore } from '@/stores/shoppingCartStore'; // Ensure this import is correct

export const useOrderStore = defineStore('order', {
  state: () => ({
    orders: [],
    currentOrder: null,
  }),
  actions: {
    async fetchOrders() {
      const authStore = useAuthStore();
      const userId = authStore.user?.id || localStorage.getItem('userId');
      
      if (!userId) {
        console.error('User ID not found, unable to fetch orders.');
        return;
      }

      try {
        const response = await fetch(`http://localhost:8084/api/orders/${userId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        if (!response.ok) throw new Error('Failed to fetch orders');
        this.orders = await response.json();
      } catch (error) {
        console.error('Error fetching orders:', error);
      }
    },

    async createOrder(orderData) {
      const authStore = useAuthStore();
      const userId = authStore.user?.id || localStorage.getItem('userId');
    
      if (!userId) {
        console.error('User ID not found, unable to create order.');
        return null;
      }
    
      try {
        const response = await fetch(`http://localhost:8084/api/orders/${userId}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(orderData),
        });
        if (!response.ok) throw new Error('Failed to create order');
        
        const order = await response.json();
        this.currentOrder = order; // Store it in the state if needed
        return order; // Return the order object
      } catch (error) {
        console.error('Error creating order:', error);
        return null;
      }
    },

    async fetchOrderById(orderId) {
      try {
        const response = await fetch(`http://localhost:8084/api/orders/${orderId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        if (!response.ok) throw new Error('Failed to fetch order');
        this.currentOrder = await response.json();
      } catch (error) {
        console.error('Error fetching order:', error);
      }
    },
  },
});
