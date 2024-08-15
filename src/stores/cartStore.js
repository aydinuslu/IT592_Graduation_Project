import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useCartStore = defineStore('cart', () => {
  const items = ref([]);

  const addItemToCart = (book) => {
    const existingItem = items.value.find(item => item.id === book.id);
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      items.value.push({ ...book, quantity: 1 });
    }
  };

  const removeItemFromCart = (bookId) => {
    items.value = items.value.filter(item => item.id !== bookId);
  };

  const updateItemQuantity = (bookId, quantity) => {
    const item = items.value.find(item => item.id === bookId);
    if (item) {
      item.quantity = quantity;
    }
  };

  const clearCart = () => {
    items.value = [];
  };

  const totalPrice = computed(() => {
    return items.value.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2);
  });

  return {
    items,
    addItemToCart,
    removeItemFromCart,
    updateItemQuantity,
    clearCart,
    totalPrice,
  };
});
