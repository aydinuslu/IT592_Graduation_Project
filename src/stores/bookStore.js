import { defineStore } from 'pinia';

const API_URL = import.meta.env.VITE_BOOK_CATALOG_SERVICE_API;

export const useBookStore = defineStore('bookStore', {
  state: () => ({
    books: [],
    book: null,
    isLoading: false,
    error: null,
  }),

  actions: {
    async fetchBooks() {
      this.isLoading = true;
      this.error = null;
      try {
        const response = await fetch(`${API_URL}`);
        if (!response.ok) {
          throw new Error('Failed to fetch books');
        }
        const books = await response.json();
        this.books = books.map(book => ({
          ...book,
          coverImage: book.coverImage || 'https://picsum.photos/350/300?image=1073',
        }));
      } catch (error) {
        this.error = error.message;
      } finally {
        this.isLoading = false;
      }
    },

    async fetchBook(id) {
      this.isLoading = true;
      this.error = null;
      try {
        const response = await fetch(`${API_URL}/edit/${id}`);
        if (!response.ok) {
          throw new Error('Failed to fetch book');
        }
        this.book = await response.json();
      } catch (error) {
        this.error = error.message;
      } finally {
        this.isLoading = false;
      }
    },

    async addBook(book) {
      this.isLoading = true;
      this.error = null;
      try {
        const response = await fetch(`${API_URL}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(book),
        });
        if (!response.ok) {
          throw new Error('Failed to add book');
        }
        const newBook = await response.json();
        this.books.push(newBook);
      } catch (error) {
        this.error = error.message;
      } finally {
        this.isLoading = false;
      }
    },

    async updateBook(id, book) {
      this.isLoading = true;
      this.error = null;
      try {
        const response = await fetch(`${API_URL}/${id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(book),
        });
        if (!response.ok) {
          throw new Error('Failed to update book');
        }
        const updatedBook = await response.json();
        const index = this.books.findIndex(b => b.id === id);
        if (index !== -1) {
          this.books[index] = updatedBook;
        }
      } catch (error) {
        this.error = error.message;
      } finally {
        this.isLoading = false;
      }
    },

    async deleteBook(id) {
      this.isLoading = true;
      this.error = null;
      try {
        const response = await fetch(`${API_URL}/${id}`, {
          method: 'DELETE',
        });
        if (!response.ok) {
          throw new Error('Failed to delete book');
        }
        this.books = this.books.filter(book => book.id !== id);
      } catch (error) {
        this.error = error.message;
      } finally {
        this.isLoading = false;
      }
    },
  },
});
