import { useEffect, useRef } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

/**
 * Connects to the backend's WebSocket endpoint and subscribes to
 * /topic/monitors. Calls onStatusUpdate(update) whenever the backend
 * broadcasts a status change — the frontend never polls for this data,
 * it only reacts to pushed updates.
 */
export function useMonitorUpdates(onStatusUpdate) {
  const clientRef = useRef(null)

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe('/topic/monitors', (message) => {
          const update = JSON.parse(message.body)
          onStatusUpdate(update)
        })
      },
    })

    client.activate()
    clientRef.current = client

    return () => {
      client.deactivate()
    }
  }, [onStatusUpdate])
}