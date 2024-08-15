<template>
    <div class="max-w-sm rounded overflow-hidden shadow-lg">
      <img class="w-full" :src="book.cover || 'https://picsum.photos/350/300?image=1073'" alt="Book Cover">
      <div class="px-6 py-4">
        <div class="font-bold text-xl mb-2">{{ book.title }}</div>
        <p class="text-gray-700 text-base">
          {{ book.author }}
        </p>
        <p class="text-gray-800 font-bold mb-4">${{ book.price }}</p>
        <div class="flex">
          <button @click="addToCart(book)" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mr-2">
            Add to Cart
          </button>
          <router-link :to="`/books/${book.id}`" class="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
            View Details
          </router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { useCartStore } from '@/stores/cartStore'
  
  export default {
    name: 'ItemCard',
    props: {
      book: Object
    },
    setup() {
      const cartStore = useCartStore()
      const addToCart = (book) => {
        cartStore.addToCart(book)
      }
      return { addToCart }
    }
  }
  </script>
  