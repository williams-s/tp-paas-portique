import { useState, useEffect } from 'react';
import { Dashboard } from './components/Dashboard.tsx';
import { LogHistory } from './components/LogHistory.tsx';
import { Stats } from './components/Stats.tsx';
import { Header } from './components/Header.tsx';

export interface EntranceLog {
  studentId: number;
  firstname: string;
  lastname: string;
  allowed: boolean;
  doorId: string;
  timestamp: string;
  source: 'cache' | 'database';
  latency_ms: number;
}

function App() {
  const [logs, setLogs] = useState<EntranceLog[]>([]);
  const [stats, setStats] = useState({
    totalAttempts: 0,
    authorized: 0,
    denied: 0,
    cacheHitRate: 0,
    avgLatency: 0
  });
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    const ws = new WebSocket('ws://192.168.248.140:8087/ws/entrance');

    ws.onopen = () => {
      setIsConnected(true);
      console.log('WebSocket connecté');
    };

    ws.onmessage = (event) => {
      try {
        const newLog: EntranceLog = JSON.parse(event.data);
        setLogs(prev => [newLog, ...prev].slice(0, 10));

        setStats(prev => {
          const newTotal = prev.totalAttempts + 1;
          const newAuthorized = prev.authorized + (newLog.allowed ? 1 : 0);
          const newDenied = prev.denied + (!newLog.allowed ? 1 : 0);
          const cacheHits = logs.filter(l => l.source === 'cache').length + (newLog.source === 'cache' ? 1 : 0);
          const avgLat = 0 /*logs.length > 0
              ? (logs.reduce((sum, l) => sum + l.latency_ms, 0) + newLog.latency_ms) / (logs.length + 1)
              : newLog.latency_ms;*/ //abuses de titi ismail
          return {
            totalAttempts: newTotal,
            authorized: newAuthorized,
            denied: newDenied,
            cacheHitRate: (cacheHits / newTotal) * 100,
            avgLatency: avgLat
          };
        });

      } catch (error) {
        console.error('Error parsing JSON:', error);
      }
    };
    ws.onerror = () => {
      setIsConnected(false);
      console.error('Erreur WebSocket');
    };

    ws.onclose = () => {
      setIsConnected(false);
      console.log('WebSocket déconnecté');
    };

    return () => {
      ws.close();
    };
  }, []);

  return (
    <div className="min-h-screen bg-white">
      <Header isConnected={isConnected} />

      <main className="container mx-auto px-4 py-8 space-y-6">
        <Stats stats={stats} />

        <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
          <div className="xl:col-span-2">
            <Dashboard logs={logs.slice(0, 10)} />
          </div>

          <div>
            <LogHistory logs={logs} />
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;
