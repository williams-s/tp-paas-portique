import { Activity, CheckCircle, XCircle, Zap, Clock } from 'lucide-react';

interface StatsProps {
  stats: {
    totalAttempts: number;
    authorized: number;
    denied: number;
    cacheHitRate: number;
    avgLatency: number;
  };
}

export function Stats({ stats }: StatsProps) {
  const statCards = [
    {
      icon: Activity,
      label: 'Tentatives totales',
      value: stats.totalAttempts,
      color: 'bg-gray-900',
      textColor: 'text-gray-900'
    },
    {
      icon: CheckCircle,
      label: 'Autorisées',
      value: stats.authorized,
      color: 'bg-green-600',
      textColor: 'text-green-600'
    },
    {
      icon: XCircle,
      label: 'Refusées',
      value: stats.denied,
      color: 'bg-red-600',
      textColor: 'text-red-600'
    },
    {
      icon: Zap,
      label: 'Cache Hit Rate',
      value: `${stats.cacheHitRate.toFixed(1)}%`,
      color: 'bg-blue-600',
      textColor: 'text-blue-600'
    },
    {
      icon: Clock,
      label: 'Latence moyenne',
      value: `${stats.avgLatency.toFixed(0)}ms`,
      color: 'bg-orange-600',
      textColor: 'text-orange-600'
    }
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
      {statCards.map((stat, index) => {
        const Icon = stat.icon;
        return (
          <div
            key={index}
            className="bg-gray-50 border border-gray-200 rounded-xl p-5 hover:bg-gray-100 transition-all"
          >
            <div className="flex items-center justify-between mb-3">
              <div className={`${stat.color} p-2 rounded-lg bg-opacity-20`}>
                <Icon className={`w-5 h-5 ${stat.textColor}`} />
              </div>
            </div>
            <div className="text-2xl font-bold text-gray-900 mb-1">
              {stat.value}
            </div>
            <div className="text-sm text-gray-600">
              {stat.label}
            </div>
          </div>
        );
      })}
    </div>
  );
}
