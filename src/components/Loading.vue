<template>
  <div id="loading">
    <h2>Loading</h2>
  </div>
</template>

<script setup>
</script>

<style scoped>
  #loading{
    position: fixed;
    flex-direction: column;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background-color: #6b6b6b;
    z-index: 1000000;
    transition: 1s ease;
  }
</style>