<template>
  <div class="container">
    <h1 class="title">Distancia Recorrida por Repartidores (Último Mes)</h1>
    <table class="table">
      <thead>
        <tr>
          <th>ID Repartidor</th>
          <th>Nombre</th>
          <th>Distancia (km)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="repartidor in listaRepartidores" :key="repartidor.id">
          <td>{{ repartidor.id }}</td>
          <td>{{ repartidor.nombre }}</td>
          <td>{{ repartidor.distancia_km.toFixed(2) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useNuxtApp } from '#app'

const listaRepartidores = ref([])
const { $apiClient } = useNuxtApp()

const fetchDistancias = async () => {
  try {
    const repartidoresResponse = await $apiClient.get('/repartidor/')
    const todosRepartidores = repartidoresResponse.data

    const distanciasResponse = await $apiClient.get('/repartidor/distancia-recorrida')
    const distancias = distanciasResponse.data

    const mapaDistancias = {}
    for (const d of distancias) {
      mapaDistancias[d.repartidor] = d.km_recorridos
    }

    listaRepartidores.value = todosRepartidores.map(r => {
      const distancia = mapaDistancias[r.nombre]
      return {
        id: r.id_repartidor,
        nombre: r.nombre,
        distancia_km: distancia !== undefined ? distancia : 0
      }
    })
  } catch (error) {
    console.error('Error al obtener las distancias:', error)
    alert('No se pudo cargar la información de distancia recorrida.')
  }
}

onMounted(() => {
  fetchDistancias()
})
</script>
