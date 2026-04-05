import { defineStore } from 'pinia'
import axios from 'axios'

// 设置axios默认配置
axios.defaults.headers.common['Content-Type'] = 'application/json; charset=utf-8'
axios.defaults.headers.common['Accept'] = 'application/json; charset=utf-8'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || '',
    isLoggedIn: !!localStorage.getItem('token')
  }),
  actions: {
    async login(account, password, loginType = 'username') {
      try {
        console.log('========== STORE LOGIN ==========')
        console.log('store loginType:', loginType)
        console.log('store loginType === "phone":', loginType === 'phone')
        const loginData = loginType === 'phone' 
          ? { phone: account, password }
          : { username: account, password }
        console.log('store loginData:', JSON.stringify(loginData))
        console.log('=================================')
        const response = await axios.post('/api/users/login', loginData)
        this.user = response.data
        this.token = 'dummy-token' // 实际项目中应该从响应中获取token
        localStorage.setItem('token', this.token)
        localStorage.setItem('user', JSON.stringify(this.user))
        this.isLoggedIn = true
        return true
      } catch (error) {
        console.error('Login failed:', error)
        return false
      }
    },
    async register(userData) {
      try {
        const response = await axios.post('/api/users/register', userData)
        return true
      } catch (error) {
        console.error('Registration failed:', error)
        return false
      }
    },
    logout() {
      this.user = null
      this.token = ''
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.isLoggedIn = false
    },
    async updateUserInfo(userData) {
      try {
        console.log('Sending update request with data:', JSON.stringify(userData))
        const response = await axios.put('/api/users/update', userData)
        console.log('Update response:', response.data)
        // 更新本地存储的用户信息
        this.user = response.data
        localStorage.setItem('user', JSON.stringify(this.user))
        return true
      } catch (error) {
        console.error('Update user info failed:', error)
        if (error.response) {
          console.error('Error response status:', error.response.status)
          console.error('Error response data:', error.response.data)
          console.error('Error response headers:', error.response.headers)
        }
        return false
      }
    },
    async deleteAccount() {
      try {
        if (this.user && this.user.id) {
          await axios.delete(`/api/users/delete/${this.user.id}`)
          // 清除本地存储
          this.user = null
          this.token = ''
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          this.isLoggedIn = false
          return { success: true }
        }
        return { success: false, message: '用户未登录' }
      } catch (error) {
        console.error('Delete account failed:', error)
        if (error.response && error.response.data) {
          return { success: false, message: error.response.data }
        }
        return { success: false, message: '注销失败，请稍后重试' }
      }
    }
  }
})

export const useItemStore = defineStore('item', {
  state: () => ({
    items: [],
    currentItem: null,
    categories: []
  }),
  actions: {
    async fetchItems() {
      try {
        const response = await axios.get('/api/items/findAvailable')
        this.items = response.data
      } catch (error) {
        console.error('Failed to fetch items:', error)
      }
    },
    async fetchItemById(id) {
      try {
        const response = await axios.get(`/api/items/find/${id}`)
        this.currentItem = response.data
      } catch (error) {
        console.error('Failed to fetch item:', error)
      }
    },
    async searchItems(keyword) {
      try {
        const response = await axios.get(`/api/items/search?keyword=${keyword}`)
        this.items = response.data
      } catch (error) {
        console.error('Failed to search items:', error)
      }
    },
    async fetchTopBorrowedItems() {
      try {
        const response = await axios.get('/api/items/topBorrowed')
        return response.data
      } catch (error) {
        console.error('Failed to fetch top borrowed items:', error)
        return []
      }
    }
  }
})

export const useBorrowStore = defineStore('borrow', {
  state: () => ({
    borrowRecords: [],
    pendingRequests: []
  }),
  actions: {
    async createBorrowRequest(itemId, expectedReturnDate, notes) {
      try {
        const userStore = useUserStore()
        // 转换日期时间格式为ISO格式
        const isoDate = new Date(expectedReturnDate).toISOString()
        const response = await axios.post('/api/borrow/create', {
          item: {
            id: itemId
          },
          borrower: {
            id: userStore.user.id
          },
          expectedReturnDate: isoDate,
          notes
        })
        return true
      } catch (error) {
        console.error('Failed to create borrow request:', error)
        return false
      }
    },
    async fetchBorrowHistory() {
      try {
        const userStore = useUserStore()
        const response = await axios.get(`/api/borrow/findByBorrower/${userStore.user.id}`)
        this.borrowRecords = response.data
      } catch (error) {
        console.error('Failed to fetch borrow history:', error)
      }
    },
    async fetchPendingRequests() {
      try {
        const userStore = useUserStore()
        const response = await axios.get(`/api/borrow/pending/${userStore.user.id}`)
        this.pendingRequests = response.data
      } catch (error) {
        console.error('Failed to fetch pending requests:', error)
      }
    },
    async approveRequest(id) {
      try {
        const response = await axios.put(`/api/borrow/approve/${id}`)
        return true
      } catch (error) {
        console.error('Failed to approve request:', error)
        return false
      }
    },
    async rejectRequest(id) {
      try {
        await axios.put(`/api/borrow/reject/${id}`)
        return true
      } catch (error) {
        console.error('Failed to reject request:', error)
        return false
      }
    },
    async returnItem(id) {
      try {
        await axios.put(`/api/borrow/return/${id}`)
        return true
      } catch (error) {
        console.error('Failed to return item:', error)
        return false
      }
    }
  }
})

export const useAIStore = defineStore('ai', {
  state: () => ({
    recommendations: [],
    personalizedRecommendations: [],
    popularRecommendations: [],
    newRecommendations: []
  }),
  actions: {
    async fetchRecommendations() {
      try {
        const userStore = useUserStore()
        const response = await axios.get(`/api/ai/recommendations/${userStore.user.id}`)
        this.recommendations = response.data
        return response.data
      } catch (error) {
        console.error('Failed to fetch recommendations:', error)
        return []
      }
    },
    async fetchPersonalizedRecommendations() {
      try {
        const userStore = useUserStore()
        const response = await axios.get(`/api/ai/personalized/${userStore.user.id}`)
        this.personalizedRecommendations = response.data
        return response.data
      } catch (error) {
        console.error('Failed to fetch personalized recommendations:', error)
        return []
      }
    },
    async fetchPopularRecommendations() {
      try {
        const response = await axios.get('/api/ai/popular')
        this.popularRecommendations = response.data
        return response.data
      } catch (error) {
        console.error('Failed to fetch popular recommendations:', error)
        return []
      }
    },
    async fetchNewRecommendations() {
      try {
        const response = await axios.get('/api/ai/new')
        this.newRecommendations = response.data
        return response.data
      } catch (error) {
        console.error('Failed to fetch new recommendations:', error)
        return []
      }
    },
    async updateRecommendations() {
      try {
        const userStore = useUserStore()
        await axios.post(`/api/ai/update/${userStore.user.id}`)
        await this.fetchRecommendations()
        await this.fetchPersonalizedRecommendations()
        await this.fetchPopularRecommendations()
        await this.fetchNewRecommendations()
        return true
      } catch (error) {
        console.error('Failed to update recommendations:', error)
        return false
      }
    }
  }
})