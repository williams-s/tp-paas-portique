import { CheckCircle, XCircle, DoorOpen } from 'lucide-react';
import { EntranceLog } from '../App.tsx';
import { DoorService } from './DoorService.tsx';
import{ useState } from 'react';

interface DashboardProps {
  logs: EntranceLog[];
}

export function Dashboard({ logs }: DashboardProps) {
  const [isOpening, setIsOpening] = useState(false);
  const [doorId , setDoorId] = useState(0);
  const handleOpenDoor = async () => {
    if (doorId === 0) {
      alert("Veuillez spécifier quelle porte vous voulez ouvrir");
      return;
    }
    if (doorId > 4 || doorId < 1) {
      alert("Veuillez specifier une porte entre 1 et 4");
      return;
    }
    setIsOpening(true);
    try {
      const success = await DoorService.openDoor(doorId.toString());
      if (success) {
        console.log('Door opened successfully');
      } else {
        console.error('Failed to open door');
      }
    } catch (error) {
      console.error('Error opening door:', error);
    } finally {
      setIsOpening(false);
    }
  };

  return (
    <div className="bg-white border border-gray-200 rounded-xl p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold text-gray-900">Entrées en temps réel</h2>
        <div className="flex items-center space-x-4">
          {/* Bouton d'ouverture manuelle */}
          <button
            onClick={handleOpenDoor}
            disabled={isOpening}
            className={`flex items-center space-x-2 px-4 py-2 rounded-lg font-medium transition-all ${
                isOpening
                    ? 'bg-gray-400 cursor-not-allowed'
                    : 'bg-green-600 hover:bg-green-700 text-white'
            }`}
          >
            <DoorOpen className="w-4 h-4"/>
            <span>{isOpening ? 'Ouverture...' : 'Ouvrir Porte'}</span>
          </button>
          <input
            type="number"
            min="1"
            max="4"
            step="1"
            onChange={(e) => setDoorId(parseInt(e.target.value))}
            placeholder="Id porte"
            className="px-2 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring focus:ring-green-500"
            style={{ width: '100px'}}
          />
          <div className="flex items-center space-x-2"></div>
          <div className="w-2 h-2 bg-green-600 rounded-full animate-pulse"/>
          <span className="text-sm text-gray-500">Live</span>
        </div>
      </div>

      <div className="space-y-3">
        {logs.length === 0 ? (
            <div className="text-center py-12 text-gray-400">
              En attente de données...
            </div>
        ) : (
          logs.map((log) => (
            <div
              key={log.num}
              className={`p-4 rounded-lg border-l-4 transition-all duration-300 ${
                log.allowed
                  ? 'bg-green-50 border-green-600 hover:bg-green-100'
                  : 'bg-red-50 border-red-600 hover:bg-red-100'
              }`}
            >
              <div className="flex items-start justify-between">
                <div className="flex items-start space-x-3 flex-1">
                  <div className={`mt-1 ${log.allowed ? 'text-green-600' : 'text-red-600'}`}>
                    {log.allowed ? (
                      <CheckCircle className="w-6 h-6" />
                    ) : (
                      <XCircle className="w-6 h-6" />
                    )}
                  </div>

                  <div className="flex-1">
                    <div className="flex items-center space-x-2 mb-1">
                      <span className="font-semibold text-gray-900">
                        {log.firstname} {log.lastname}
                      </span>
                      <span className={`px-2 py-0.5 rounded text-xs font-medium ${
                        log.allowed
                          ? 'bg-green-100 text-green-700'
                          : 'bg-red-100 text-red-700'
                      }`}>
                        {log.allowed ? 'AUTORISÉ' : 'REFUSÉ'}
                      </span>
                    </div>

                    <div className="flex items-center space-x-4 text-sm text-gray-600">
                      <span>Badge: {log.num}</span>
                      <span>Porte: {log.doorId}</span>
                      <span>{new Date(log.timestamp).toLocaleTimeString('fr-FR')}</span>
                    </div>
                  </div>
                </div>

                  {/*<div className="flex items-center space-x-3 text-xs">
                  <div className={`flex items-center space-x-1 px-2 py-1 rounded ${
                    log.source === 'cache'
                      ? 'bg-blue-100 text-blue-700'
                      : 'bg-gray-100 text-gray-700'
                  }`}>
                    {log.source === 'cache' ? (
                      <Zap className="w-3 h-3" />
                    ) : (
                      <Database className="w-3 h-3" />
                    )}
                    <span>{log.source === 'cache' ? 'Cache' : 'DB'}</span>
                  </div>

                    {/*} <div className="px-2 py-1 bg-gray-100 rounded text-gray-600">
                    {log.latency_ms}ms
                  </div>
                </div>*/}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
