import { Activity, Wifi, WifiOff, DoorOpen } from 'lucide-react';
import { DoorService } from './DoorService';
import { useState } from 'react';

interface HeaderProps {
  isConnected: boolean;
}

export function Header({ isConnected }: HeaderProps) {
  const [isOpening, setIsOpening] = useState(false);

  const handleOpenDoor = async () => {
    setIsOpening(true);
    try {
      const success = await DoorService.openDoor();
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
            {/* Bouton d'ouverture dans le header */}
            <button
              onClick={handleOpenDoor}
              disabled={isOpening}
              className={`flex items-center space-x-2 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                isOpening 
                  ? 'bg-gray-400 cursor-not-allowed' 
                  : 'bg-green-600 hover:bg-green-700 text-white'
              }`}
            >
              <DoorOpen className="w-4 h-4" />
              <span>{isOpening ? '...' : 'Ouvrir Porte'}</span>
            </button>

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