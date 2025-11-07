import type { WebSocketMessage } from '@/types/chat'

export class WebSocketClient {
  private ws: WebSocket | null = null
  private url: string
  private userId: number
  private heartbeatTimer: number | null = null
  private reconnectTimer: number | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private onMessageCallback: ((message: WebSocketMessage) => void) | null = null
  private onConnectedCallback: (() => void) | null = null
  private onDisconnectedCallback: (() => void) | null = null

  constructor(url: string, userId: number) {
    this.url = url
    this.userId = userId
  }

  /**
   * 连接WebSocket
   */
  connect() {
    try {
      const wsUrl = `${this.url}?userId=${this.userId}`
      this.ws = new WebSocket(wsUrl)

      this.ws.onopen = () => {
        console.log('WebSocket连接成功')
        this.reconnectAttempts = 0
        this.startHeartbeat()
        if (this.onConnectedCallback) {
          this.onConnectedCallback()
        }
      }

      this.ws.onmessage = (event) => {
        try {
          const message: WebSocketMessage = JSON.parse(event.data)
          console.log('收到WebSocket消息:', message)

          if (this.onMessageCallback) {
            this.onMessageCallback(message)
          }
        } catch (error) {
          console.error('解析WebSocket消息失败:', error)
        }
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket错误:', error)
      }

      this.ws.onclose = () => {
        console.log('WebSocket连接已关闭')
        this.stopHeartbeat()
        if (this.onDisconnectedCallback) {
          this.onDisconnectedCallback()
        }
        this.reconnect()
      }
    } catch (error) {
      console.error('WebSocket连接失败:', error)
    }
  }

  /**
   * 断开连接
   */
  disconnect() {
    this.stopHeartbeat()
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  /**
   * 发送消息
   */
  send(message: WebSocketMessage) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message))
    } else {
      console.error('WebSocket未连接，无法发送消息')
    }
  }

  /**
   * 设置消息回调
   */
  onMessage(callback: (message: WebSocketMessage) => void) {
    this.onMessageCallback = callback
  }

  /**
   * 设置连接成功回调
   */
  onConnected(callback: () => void) {
    this.onConnectedCallback = callback
  }

  /**
   * 设置断开连接回调
   */
  onDisconnected(callback: () => void) {
    this.onDisconnectedCallback = callback
  }

  /**
   * 启动心跳
   */
  private startHeartbeat() {
    this.heartbeatTimer = window.setInterval(() => {
      this.send({
        type: 11, // 心跳
        timestamp: Date.now()
      })
    }, 30000) // 30秒发送一次心跳
  }

  /**
   * 停止心跳
   */
  private stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  /**
   * 重连
   */
  private reconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.log('达到最大重连次数，停止重连')
      return
    }

    this.reconnectAttempts++
    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000)
    console.log(`${delay}ms后尝试第${this.reconnectAttempts}次重连...`)

    this.reconnectTimer = window.setTimeout(() => {
      console.log(`开始第${this.reconnectAttempts}次重连...`)
      this.connect()
    }, delay)
  }

  /**
   * 检查连接状态
   */
  isConnected(): boolean {
    return this.ws !== null && this.ws.readyState === WebSocket.OPEN
  }
}
