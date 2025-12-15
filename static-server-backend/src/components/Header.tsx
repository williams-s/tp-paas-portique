import { Activity, Wifi, WifiOff } from 'lucide-react';

interface HeaderProps {
  isConnected: boolean;
}

export function Header({ isConnected }: HeaderProps) {

  return (
    <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="bg-gray-900 p-2 rounded-lg">
              <Activity className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-xl font-bold text-gray-900">
                Système de Contrôle d'Entrée IoT
              </h1>
              <p className="text-sm text-gray-500">
                Surveillance en temps réel
              </p>
            </div>
          </div>

          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2">
              {isConnected ? (
                <>
                  <Wifi className="w-5 h-5 text-green-400" />
                  <span className="text-sm text-green-400 font-medium">
                    Connecté
                  </span>
                  <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse" />
                </>
              ) : (
                <>
                  <WifiOff className="w-5 h-5 text-red-400" />
                  <span className="text-sm text-red-400 font-medium">
                    Déconnecté
                  </span>
                </>
              )}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}