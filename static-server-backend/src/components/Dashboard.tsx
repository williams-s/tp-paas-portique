import { CheckCircle, XCircle } from 'lucide-react';
import { EntranceLog } from '../App.tsx';

interface DashboardProps {
  logs: EntranceLog[];
}

export function Dashboard({ logs }: DashboardProps) {
  return (
    <div className="bg-white border border-gray-200 rounded-xl p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold text-gray-900">Entrées en temps réel</h2>
        <div className="flex items-center space-x-2">
          <div className="w-2 h-2 bg-green-600 rounded-full animate-pulse" />
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
              key={log.studentId}
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
                      <span>Badge: {log.studentId}</span>
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
