import { useState, useEffect } from 'react';
import { Dashboard } from './components/Dashboard.tsx';
import { LogHistory } from './components/LogHistory.tsx';
import { Stats } from './components/Stats.tsx';
import { Header } from './components/Header.tsx';
import { PeopleList } from './components/PeopleList.tsx';

export interface EntranceLog {
  num: string;
  firstname: string;
  lastname: string;
  allowed: boolean;
  doorId: string;
  timestamp: string;
  source: 'cache' | 'database';
  latency_ms: number;
}

interface StatsState {
  totalAttempts: number;
  authorized: number;
  denied: number;
  cacheHitRate: number; // percent
  avgLatency: number; // ms
}

function App() {
  const [logs, setLogs] = useState<EntranceLog[]>([]);
  const [stats, setStats] = useState<StatsState>({
    totalAttempts: 0,
    authorized: 0,
    denied: 0,
    cacheHitRate: 0,
    avgLatency: 0
  });
  const [isConnected, setIsConnected] = useState(false);
  // simple client-side view switch: tableau de bord ou liste des personnes
  // J'utilise un état local pour éviter d'ajouter react-router pour l'instant.
  // Si vous préférez des routes réelles (URL), on peut migrer vers react-router-dom.
  const [view, setView] = useState<'dashboard' | 'people'>('dashboard');

  useEffect(() => {
    const wsUrl = import.meta.env.VITE_WS_URL;
    //const ws = new WebSocket("wss://172.31.249.144/entrance/ws");
    const ws = new WebSocket(wsUrl);
    ws.onopen = () => {
      setIsConnected(true);
      console.log('WebSocket connecté');
    };

    ws.onmessage = (event) => {
      try {
        const newLog: EntranceLog = JSON.parse(event.data);

        // Use functional update to avoid stale closures and compute derived stats
        setLogs((prevLogs: EntranceLog[]) => {
          const updatedLogs = [newLog, ...prevLogs].slice(0, 10);

          setStats((prev: StatsState) => {
            const newTotal = prev.totalAttempts + 1;
            const newAuthorized = prev.authorized + (newLog.allowed ? 1 : 0);
            const newDenied = prev.denied + (!newLog.allowed ? 1 : 0);
            const cacheHits = updatedLogs.filter(l => l.source === 'cache').length;

            // compute running average latency correctly
            const prevSumLatency = prev.avgLatency * prev.totalAttempts;
            const avgLat = (prevSumLatency + newLog.latency_ms) / newTotal;

            return {
              totalAttempts: newTotal,
              authorized: newAuthorized,
              denied: newDenied,
              cacheHitRate: newTotal > 0 ? (cacheHits / newTotal) * 100 : 0,
              avgLatency: avgLat
            };
          });

          return updatedLogs;
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
        {/* bouton en haut à droite pour accéder à la page utilisateurs */}
        <div className="flex justify-end">
          {view === 'dashboard' ? (
            <button
              onClick={() => setView('people')}
              className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
            >
              Voir les membres
            </button>
          ) : (
            <button
              onClick={() => setView('dashboard')}
              className="inline-flex items-center px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
            >
              Retour
            </button>
          )}
        </div>

        {view === 'people' ? (
          <div>
            {/* lazy load the PeopleList component to show paginated users */}
            <PeopleList />
          </div>
        ) : (
          <>
            <Stats stats={stats} />

            <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
              <div className="xl:col-span-2">
                <Dashboard logs={logs.slice(0, 10)} />
              </div>

              <div>
                <LogHistory logs={logs} />
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}

export default App;
