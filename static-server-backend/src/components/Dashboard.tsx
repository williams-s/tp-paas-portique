import {CheckCircle, XCircle, DoorOpen} from 'lucide-react';
import {Door, EntranceLog} from '../App.tsx';
import {DoorService} from './DoorService.tsx';
import {useState, useEffect} from 'react';

interface DashboardProps {
    logs: EntranceLog[];
}


export function Dashboard({logs}: DashboardProps) {
    const displayedLogs = logs.slice(0, 10);
    const [isOpening, setIsOpening] = useState(false);
    const [selectedDoor, setSelectedDoor] = useState<Door | null>(null);
    const [doors, setDoors] = useState<Door[]>([]);

    useEffect(() => {
        fetch('/doors.json')
            .then((res) => res.json())
            .then((data: Door[]) => setDoors(data))
            .catch((err) => console.error("Erreur chargement doors.json:", err));
    }, []);

    const handleOpenDoor = async () => {
        if (!selectedDoor) {
            alert("Veuillez sélectionner une porte");
            return;
        }

        setIsOpening(true);
        try {
            const success = await DoorService.openDoor(selectedDoor);
            if (success) {
                console.log('Door opened successfully:', selectedDoor);
            } else {
                console.error('Failed to open door:', selectedDoor);
            }
        } catch (error) {
            console.error('Error opening door:', error);
        } finally {
            setIsOpening(false);
        }
    };

    // @ts-ignore
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
                    <select
                        value={selectedDoor?.id || ""}
                        onChange={(e) => {
                            const door = doors.find(p => p.id === parseInt(e.target.value));
                            setSelectedDoor(door || null);
                        }}
                        className="px-2 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring focus:ring-green-500"
                        style={{width: '150px'}}
                    >
                        <option value="">Sélectionnez porte</option>
                        {doors.map((porte) => (
                            <option key={porte.id} value={porte.id}>
                                {porte.name}
                            </option>
                        ))}
                    </select>
                    <div className="flex items-center space-x-2"></div>
                    <div className="w-2 h-2 bg-green-600 rounded-full animate-pulse"/>
                    <span className="text-sm text-gray-500">Live</span>
                </div>
            </div>

            <div className="space-y-3">
                {displayedLogs.length === 0 ? (
                    <div className="text-center py-12 text-gray-400">
                        En attente de données...
                    </div>
                ) : (
                    displayedLogs.map((log) => (
                        <div
                            key={`${log.num ?? 'manual'}-${log.timestamp}`}
                            className={`p-4 rounded-lg border-l-4 transition-all duration-300 ${
                                log.allowed
                                    ? 'bg-green-50 border-green-600 hover:bg-green-100'
                                    : 'bg-red-50 border-red-600 hover:bg-red-100'
                            }`}
                        >
                            <div className="flex items-center justify-between">
                                <div className="flex-1 space-y-2">
                                    <div className="flex items-center space-x-2">
                                       <span className="font-semibold text-gray-900">
                                           {log.firstname} {log.lastname}
                                       </span>
                                        <span className={`px-2 py-0.5 rounded text-xs font-medium ${
                                            log.allowed ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                                        }`}>
                                            {log.allowed ? 'AUTORISÉ' : 'REFUSÉ'}
                                        </span>
                                    </div>

                                    <div className="flex items-center space-x-4 text-sm text-gray-600">
                                        <span>
                                          {log.num ? `Badge: ${log.num}` : "Ouverture manuelle"}
                                        </span>
                                        <span>{log.doorName}</span>
                                        <span>{new Date(log.timestamp).toLocaleTimeString('fr-FR')}</span>
                                    </div>
                                </div>
                                <img
                                    //src={`http://127.0.0.1:8000/faces/${log.num}`}
                                    src={`/images/${log.num}`}
                                    alt={`Photo Badge ${log.num}`}
                                    width={100}
                                    height={100}
                                    className="ml-4 flex-shrink-0"
                                />
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
