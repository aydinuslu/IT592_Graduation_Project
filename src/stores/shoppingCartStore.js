import { defineStore } from 'pinia';
import { useAuthStore } from '@/stores/authStore';

const API_URL = import.meta.env.VITE_SHOPPING_CART_SERVICE_API;

export const useShoppingCartStore = defineStore('shoppingCart', {
  state: () => ({
    cart: {
      items: []
    },
    error: null,
  }),
  actions: {
    async fetchBookDetails(bookId) {
      try {        
        const response = await fetch(`http://localhost:8082/api/books/edit/${bookId}`, {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error('Failed to fetch book details');
        }

        const bookData = await response.json();
        return bookData;
      } catch (error) {
        console.error(`Error fetching details for bookId ${bookId}:`, error);
        return null; // Handle gracefully if the book details cannot be fetched
      }
    },

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
    
        // Ensure each item has complete book data
        for (let item of data.items) {
          if (!item.book) {
            const bookDetails = await this.fetchBookDetails(item.bookId);
            item.book = bookDetails;
          }
        }
    
        // Update the cart state and ensure reactivity
        this.cart.items = data.items || [];
        this.cart = { ...this.cart };  // Force reactivity
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
    
        // Find if the book is already in the cart
        const existingItem = this.cart.items.find(item => item.bookId === book.id);
    
        if (existingItem) {
          // If the book is already in the cart, increase the quantity
          await this.updateItemQuantity(userId, book.id, existingItem.quantity + 1);
        } else {
          // If the book is not in the cart, add it with quantity 1
          const item = {
            bookId: book.id,
            quantity: 1,
          };
    
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
            const errorText = await response.text(); // Capture the plain text error message
            console.error('Failed to add item to cart:', errorText);
            this.error = errorText; // Store the error message in the state
            this.clearError(); // Clear the error after a timeout
            return;
          }
    
          console.log("Item added to remote cart:", item);
        }
    
        // Optional: Refetch the cart to update the UI
        await this.fetchCart(userId);
      } catch (error) {
        this.error = error.message;
        console.error("Add Item Error:", error.message);
        this.clearError(); // Clear the error after a timeout
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
        this.clearError(); // Clear the error after a timeout
      }
    },

    async updateItemQuantity(userId, bookId, quantity) {
      try {
        const response = await fetch(`${API_URL}/${userId}/update/${bookId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
          },
          body: JSON.stringify({ quantity }), // Send the quantity in the request body
        });
    
        if (!response.ok) {
          const errorText = await response.text(); // Capture the plain text error message
          console.error('Failed to update item quantity:', errorText);
          this.error = errorText; // Store the error message in the state
          this.clearError(); // Clear the error after a timeout
          return;
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
        this.clearError(); // Clear the error after a timeout
      }
    },

    clearError() {
      // Clear the error after a short delay
      setTimeout(() => {
        this.error = null;
      }, 5000); // Clear error after 5 seconds
    },

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
        this.clearError(); // Clear the error after a timeout
      }
    }        
  },
});
