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
        return;
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
        
        this.currentOrder = await response.json();
    
        // Clear the cart after successful order placement
        const shoppingCartStore = useShoppingCartStore(); // Correctly import and use the shopping cart store
        await shoppingCartStore.clearCart(); // Ensure this is awaited in case it's an async action
    
      } catch (error) {
        console.error('Error creating order:', error);
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
