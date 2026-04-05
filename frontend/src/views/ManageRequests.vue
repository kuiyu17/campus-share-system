<template>
  <div class="manage-requests">
    <h1>借用请求管理</h1>
    <el-table :data="requests" style="width: 100%">
      <el-table-column prop="id" label="请求ID" width="100" />
      <el-table-column prop="borrower.name" label="申请人" />
      <el-table-column prop="item.name" label="物品名称" />
      <el-table-column prop="borrowDate" label="借用日期">
        <template #default="scope">
          {{ formatDateTime(scope.row.borrowDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="expectedReturnDate" label="预计归还日期">
        <template #default="scope">
          {{ formatDateTime(scope.row.expectedReturnDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="primary" size="small" @click="approveRequest(scope.row.id)" v-if="scope.row.status === 'pending'">批准</el-button>
          <el-button type="danger" size="small" @click="rejectRequest(scope.row.id)" v-if="scope.row.status === 'pending'">拒绝</el-button>
          <el-button type="success" size="small" @click="confirmReturn(scope.row.id)" v-if="scope.row.status === 'borrowed'">确认归还</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBorrowStore } from '../store'
import { ElMessage, ElMessageBox } from 'element-plus'

const borrowStore = useBorrowStore()
const requests = ref([])

// 格式化日期时间函数
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(async () => {
  await borrowStore.fetchPendingRequests()
  requests.value = borrowStore.pendingRequests
})

const approveRequest = async (id) => {
  try {
    const success = await borrowStore.approveRequest(id)
    if (success) {
      ElMessage.success('批准成功')
      await borrowStore.fetchPendingRequests()
      requests.value = borrowStore.pendingRequests
    } else {
      ElMessage.error('批准失败，请稍后重试')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const rejectRequest = async (id) => {
  try {
    const success = await borrowStore.rejectRequest(id)
    if (success) {
      ElMessage.success('拒绝成功')
      await borrowStore.fetchPendingRequests()
      requests.value = borrowStore.pendingRequests
    } else {
      ElMessage.error('拒绝失败，请稍后重试')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const confirmReturn = async (id) => {
  try {
    const success = await borrowStore.returnItem(id)
    if (success) {
      ElMessage.success('确认归还成功')
      await borrowStore.fetchPendingRequests()
      requests.value = borrowStore.pendingRequests
    } else {
      ElMessage.error('确认归还失败，请稍后重试')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}
</script>

<style scoped>
.manage-requests {
  padding: 20px;
}
</style>