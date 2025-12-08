import { CheckCircle, XCircle, History } from 'lucide-react';
import { EntranceLog } from '../App.tsx';

interface LogHistoryProps {
  logs: EntranceLog[];
}

export function LogHistory({ logs }: LogHistoryProps) {
  const authorizedCount = logs.filter(l => l.allowed).length;
  const deniedCount = logs.filter(l => l.allowed === false).length;
  const successRate = logs.length > 0
    ? ((authorizedCount / logs.length) * 100).toFixed(1)
    : 0;

  return (
    <div className="bg-white border border-gray-200 rounded-xl p-6 h-full">
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center space-x-2">
          <History className="w-5 h-5 text-gray-500" />
          <h2 className="text-xl font-bold text-gray-900">Historique</h2>
        </div>
        <div className="text-sm text-gray-500">
          {logs.length} entrées
        </div>
      </div>

      <div className="mb-6 p-4 bg-gray-50 rounded-lg border border-gray-200">
        <div className="flex items-center justify-between mb-3">
          <span className="text-sm text-gray-600">Taux de réussite</span>
          <span className="text-lg font-bold text-gray-900">{successRate}%</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
          <div
            className="h-full bg-green-600 transition-all duration-300"
            style={{ width: `${successRate}%` }}
          />
        </div>

        <div className="grid grid-cols-2 gap-3 mt-4">
          <div className="flex items-center space-x-2">
            <CheckCircle className="w-4 h-4 text-green-600" />
            <div>
              <div className="text-xs text-gray-600">Autorisées</div>
              <div className="text-lg font-bold text-gray-900">{authorizedCount}</div>
            </div>
          </div>
          <div className="flex items-center space-x-2">
            <XCircle className="w-4 h-4 text-red-600" />
            <div>
              <div className="text-xs text-gray-600">Refusées</div>
              <div className="text-lg font-bold text-gray-900">{deniedCount}</div>
            </div>
          </div>
        </div>
      </div>

      <div className="space-y-2 max-h-96 overflow-y-auto custom-scrollbar">
        {logs.map((log) => (
          <div
            key={log.num}
            className="p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-all border border-gray-200"
          >
            <div className="flex items-center justify-between mb-1">
              <div className="flex items-center space-x-2">
                {log.allowed ? (
                  <CheckCircle className="w-4 h-4 text-green-600" />
                ) : (
                  <XCircle className="w-4 h-4 text-red-600" />
                )}
                <span className="text-sm font-medium text-gray-900">
                  {log.firstname } {log.lastname}
                </span>
              </div>
              <span className="text-xs text-gray-400">
                {new Date(log.timestamp).toLocaleTimeString('fr-FR')}
              </span>
            </div>
            <div className="text-xs text-gray-600 ml-6">
              Porte : {log.doorId}
            </div>
          </div>
        ))}

        {logs.length === 0 && (
          <div className="text-center py-8 text-gray-400 text-sm">
            Aucune entrée enregistrée
          </div>
        )}
      </div>
    </div>
  );
}
