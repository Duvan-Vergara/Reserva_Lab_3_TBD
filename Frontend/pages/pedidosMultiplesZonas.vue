<template>
  <div class="container">
    <h1 class="title">Pedidos que Cruzan Más de 2 Zonas</h1>
    <table class="table">
      <thead>
        <tr>
          <th>ID Pedido</th>
          <th>ID Cliente</th>
          <th>ID Repartidor</th>
          <th>Hora del Pedido</th>
          <th>ID Medio de Pago</th>
          <th>ID Urgencia</th>
          <th>Zonas Cruzadas</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="pedido in pedidosConZonas" :key="pedido.id_pedido">
          <td>{{ pedido.id_pedido }}</td>
          <td>{{ pedido.id_cliente }}</td>
          <td>{{ pedido.id_repartidor }}</td>
          <td>{{ new Date(pedido.hora_pedido).toLocaleString() }}</td>
          <td>{{ pedido.id_medio_pago }}</td>
          <td>{{ pedido.id_urgencia }}</td>
          <td>{{ pedido.zonas_cruzadas }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useNuxtApp } from '#app'

const pedidosConZonas = ref([])
const { $apiClient } = useNuxtApp()

const fetchPedidos = async () => {
  try {
    // 1. Obtener los pedidos que cruzan más de 2 zonas
    const zonasResponse = await $apiClient.get('/pedido/pedidos-multiples-zonas')
    const zonasMap = {}
    for (const z of zonasResponse.data) {
      zonasMap[z.id_pedido] = z.zonas_cruzadas
    }

    // 2. Obtener todos los pedidos
    const pedidosResponse = await $apiClient.get('/pedido/')
    const todosLosPedidos = pedidosResponse.data

    // 3. Filtrar y agregar la cantidad de zonas cruzadas
    pedidosConZonas.value = todosLosPedidos
      .filter(p => zonasMap[p.id_pedido] !== undefined)
      .map(p => ({
        ...p,
        zonas_cruzadas: zonasMap[p.id_pedido]
      }))
  } catch (error) {
    console.error('Error al obtener pedidos con zonas cruzadas:', error)
    alert('No se pudo cargar la información de pedidos múltiples zonas.')
  }
}

onMounted(() => {
  fetchPedidos()
})
</script>
