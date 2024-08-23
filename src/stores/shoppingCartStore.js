import { defineStore } from 'pinia';
import { useAuthStore } from '@/stores/authStore'; // Add this import

const API_URL = import.meta.env.VITE_SHOPPING_CART_SERVICE_API;

export const useShoppingCartStore = defineStore('shoppingCart', {
  state: () => ({
    cart: {
      items: []
    },
    error: null,
  }),
  actions: {
    async fetchCart(userId) {
      console.log("Fetching cart for user:", userId); // Log the userId
    
      try {
        const response = await fetch(`${API_URL}/${userId}`, {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
          },
        });
    
        console.log("Fetch Cart Response:", response); // Log the entire response
    
        if (!response.ok) {
          throw new Error('Failed to fetch cart');
        }
    
        const data = await response.json();
        console.log("Fetched Cart Data:", data); // Log the parsed data
    
        this.cart.items = data.items || [];
        this.error = null;
      } catch (error) {
        this.error = error.message;
        console.error("Fetch Cart Error:", error.message);
      }
    },

    async addItemToCart(userId, book) {
      try {
        if (!userId) {
          throw new Error('User ID not found. User might not be logged in.');
        }
    
        // Create the item object with both bookId and quantity
        const item = {
          bookId: book.id,  // Ensure book.id is correct and available
          quantity: 1       // Set quantity to 1 for adding to cart
        };
    
        console.log("Item to add:", item); // Debugging log
    
        const requestBody = JSON.stringify(item);
    
        const response = await fetch(`${API_URL}/${userId}/add`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          body: requestBody,
        });
    
        if (!response.ok) {
          const errorData = await response.json();
          console.error('Response Error Data:', errorData);
          throw new Error('Failed to add item to cart');
        }
    
        console.log("Item added to remote cart:", item); // Debugging log
    
        // Optional: Refetch the cart to update the UI
        await this.fetchCart(userId);
      } catch (error) {
        console.error("Add Item Error:", error.message);
      }
    },

    async removeItemFromCart(userId, bookId) {
      try {
        const response = await fetch(`${API_URL}/${userId}/remove/${bookId}`, {
          method: 'DELETE',
          headers: {
            'Accept': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error('Failed to remove item from cart');
        }

        // Update the cart state
        this.cart.items = this.cart.items.filter(item => item.bookId !== bookId);

        // Force reactivity
        this.cart = { ...this.cart };
        this.error = null;
      } catch (error) {
        this.error = error.message;
        console.error("Remove Item Error:", error.message);
      }
    },

    async updateItemQuantity(userId, bookId, quantity) {
      try {
        const payload = { bookId, quantity };
        const response = await fetch(`${API_URL}/${userId}/update-quantity`, {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
          },
          body: JSON.stringify(payload),
        });

        if (!response.ok) {
          throw new Error('Failed to update item quantity');
        }

        const data = await response.json();

        // Update the cart with the new quantity
        const itemIndex = this.cart.items.findIndex(item => item.bookId === bookId);
        if (itemIndex !== -1) {
          this.cart.items[itemIndex].quantity = data.quantity;
        }

        // Force reactivity
        this.cart = { ...this.cart };
        this.error = null;
      } catch (error) {
        this.error = error.message;
        console.error("Update Item Quantity Error:", error.message);
      }
    },

    // New Method: Clear the cart after order placement
    // Updated clearCart method within shoppingCartStore.js

    async clearCart() {
      const authStore = useAuthStore();
      const userId = authStore.user?.id || localStorage.getItem('userId');
    
      if (!userId) {
        console.error('User ID not found, unable to clear cart.');
        return;
      }
    
      try {
        console.log(`Attempting to clear cart for userId: ${userId}`);
    
        const response = await fetch(`http://localhost:8083/api/cart/${userId}/clear`, {
          method: 'DELETE',
          headers: {
            'Accept': 'application/json',
          },
        });
    
        if (!response.ok) {
          console.error('Failed to clear cart:', response.statusText);
          throw new Error('Failed to clear cart');
        }
    
        // Clear the cart items in the state
        this.cart.items = [];
    
        // Ensure reactivity
        this.cart = { ...this.cart };
        this.error = null;
    
        console.log('Cart successfully cleared');
      } catch (error) {
        this.error = error.message;
        console.error("Clear Cart Error:", error.message);
      }
    }        
  },
});