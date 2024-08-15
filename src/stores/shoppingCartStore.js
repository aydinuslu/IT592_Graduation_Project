import { defineStore } from 'pinia';

const API_URL = import.meta.env.VITE_SHOPPING_CART_SERVICE_API;

export const useShoppingCartStore = defineStore('shoppingCart', {
  state: () => ({
    cart: [],
    error: null,
  }),
  actions: {
    async fetchCart(userId) {
      try {
        const response = await fetch(`${API_URL}/${userId}`, {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error('Failed to fetch cart');
        }

        const data = await response.json();
        this.cart = data;
        this.error = null;
      } catch (error) {
        this.error = error.message;
      }
    },
    async addItemToCart(userId, item) {
      try {
        const response = await fetch(`${API_URL}/${userId}/add`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
          },
          body: JSON.stringify(item),
        });

        if (!response.ok) {
          throw new Error('Failed to add item to cart');
        }

        const data = await response.json();
        this.cart.push(data);
        this.error = null;
      } catch (error) {
        this.error = error.message;
      }
    },
    async removeItemFromCart(userId, itemId) {
      try {
        const response = await fetch(`${API_URL}/${userId}/remove/${itemId}`, {
          method: 'DELETE',
          headers: {
            'Accept': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error('Failed to remove item from cart');
        }

        this.cart = this.cart.filter(item => item.id !== itemId);
        this.error = null;
      } catch (error) {
        this.error = error.message;
      }
    },
  },
});
