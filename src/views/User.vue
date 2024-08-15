<template>
  <div class="profile-page">
    <UserForm />
  </div>
</template>

<script>
import UserForm from '@/components/UserForm.vue';

export default {
  components: {
    UserForm,
  },
};
</script>

<style scoped>
.profile-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
