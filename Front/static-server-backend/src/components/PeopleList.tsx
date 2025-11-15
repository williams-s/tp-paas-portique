import { useEffect, useState } from 'react';



export interface People {
  id: number;
  num: string;
  firstName: string;
  lastName: string;
  function: string; 
  allowedIntervalStart?: string;
  allowedIntervalEnd?: string;
  registrationDate?: string;
}

interface PageResponse {
  content?: People[];
  totalElements?: number;
  totalPages?: number;
  size?: number;
  number?: number; // page index
}

export function PeopleList() {
  const [people, setPeople] = useState<People[]>([]);
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState('');

  /**
   * PeopleList: component affichant une table paginée d'utilisateurs.
   *
   * Ce composant interroge le backend sur les endpoints suivants:
   * - GET /api/people/paginate?page=<index>&size=<size>
   * - GET /api/people/search?firstName=<q>&lastName=<q>&num=<q>&page=<index>&size=<size>
   *
   * Lorsqu'une recherche est saisie, le composant appelle `/search` en renseignant
   * `firstName`, `lastName` et `num` avec la même valeur afin d'effectuer une recherche
   * multi-champs côté backend.
   */

  useEffect(() => {
    // Lorsque `query` est vide on utilise le endpoint de pagination.
    // Sinon on effectue une recherche multi-champs.
    fetchPage(page, query);
  }, [page, query]);



  async function fetchPage(p: number, q?: string) {
    setLoading(true);
    setError(null);
    try {
      const trimmed = q && q.trim();
      let url: string;
      if (trimmed) {
        const enc = encodeURIComponent(trimmed);
        // On envoie la même valeur sur firstName, lastName et num pour une recherche multi-champs
        url = `/api/people/search?firstName=${enc}&lastName=${enc}&num=${enc}&page=${p}&size=${pageSize}`;
      } else {
        url = `/api/people/paginate?page=${p}&size=${pageSize}`;
      }
      const res = await fetch(url);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();

      if (data && Array.isArray(data)) {
        // Cas simple : le backend renvoie directement les personnes
        setPeople(data);
        setTotalPages(Math.ceil(data.length / pageSize));
      } else if (data && data.content) {
        // Cas Spring Data 
        const pageResp = data as PageResponse;
        setPeople(pageResp.content || []);
        setTotalPages(typeof pageResp.totalPages === 'number' ? pageResp.totalPages : null);
      } else {
        // Fallback 
        setPeople(data || []);
        setTotalPages(null);
      }
    } catch (err: any) {
      console.error('Erreur lors du fetch jsp pourqoui :( ):', err);
      setError(String(err.message || err));
    } finally {
      setLoading(false);
    }
  }

  // formatDate
  function formatDate(d?: string) {
    if (!d) return '-';
    try {
      return new Date(d).toLocaleString();
    } catch {
      return d;
    }
  }
  return (
    <div className="bg-white border rounded p-4">
      <div className="relative flex items-center justify-center mb-4">
        <h2 className="text-lg font-semibold absolute left-0">Liste des utilisateurs</h2>
        <div className="space-x-2">
          <div className="flex items-center">
            <input
              type="search"
              value={query}
              onChange={e => { setQuery(e.target.value); setPage(0); }}
              placeholder="Rechercher nom, prénom, num, fonction..."
              aria-label="Recherche des utilisateurs"
              className="px-3 py-2 border rounded w-64 text-sm"
            />
            {query && (
              <button
                onClick={() => { setQuery(''); setPage(0); }}
                className="ml-2 text-sm text-gray-600"
                aria-label="Effacer la recherche"
              >Effacer</button>
            )}
          </div>
        </div>
      </div>

      {error && (
        <div className="text-red-600 mb-4">Erreur: {error}</div>
      )}

      <div className="overflow-x-auto">
        <table className="min-w-full text-left">
          <thead className="bg-gray-50">
            <tr>
              <th scope="col" className="px-3 py-3 w-12 text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
              <th scope="col" className="px-3 py-3 w-28 text-xs font-medium text-gray-500 uppercase tracking-wider">Num</th>
              <th scope="col" className="px-3 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Nom complet</th>
              <th scope="col" className="px-3 py-3 w-36 text-xs font-medium text-gray-500 uppercase tracking-wider">Fonction</th>
              <th scope="col" className="px-3 py-3 w-40 text-xs font-medium text-gray-500 uppercase tracking-wider">Inscription</th>
            </tr>
          </thead>

          <tbody className="bg-white divide-y divide-gray-200">
            {loading ? (
              <tr>
                <td colSpan={5} className="px-4 py-6 text-center text-sm text-gray-600">Chargement...</td>
              </tr>
            ) : people.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-4 py-6 text-center text-sm text-gray-600">Aucun utilisateur trouvé</td>
              </tr>
            ) : (
              people.map((p, idx) => (
                <tr key={p.id} className={`${idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'} hover:bg-gray-100`}> 
                  <td className="px-3 py-4 align-middle text-sm text-gray-700">{p.id}</td>

                  <td className="px-3 py-4 align-middle">
                    <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">{p.num}</span>
                  </td>

                  <td className="px-3 py-4 align-middle">
                    <div className="text-sm font-medium text-gray-900">{p.firstName} {p.lastName}</div>
                    <div className="text-xs text-gray-500">{p.num}</div>
                  </td>

                  <td className="px-3 py-4 align-middle text-sm text-gray-700">{p.function}</td>

                  <td className="px-3 py-4 align-middle">
                    <div className="text-sm text-gray-900">{formatDate(p.registrationDate)}</div>
                    <div className="text-xs text-gray-500">{p.allowedIntervalStart ? `${new Date(p.allowedIntervalStart).toLocaleString()} → ${p.allowedIntervalEnd ? new Date(p.allowedIntervalEnd).toLocaleString() : '-'}` : '-'}</div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className="flex items-center justify-between mt-4">
        <div className="text-sm text-gray-600">Page {page + 1}{totalPages ? ` / ${totalPages}` : ''}</div>

        <div className="space-x-2">
          <button
            onClick={() => setPage(p => Math.max(0, p - 1))}
            disabled={page === 0 || loading}
            className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
          >Précédent</button>

          <button
            onClick={() => setPage(p => p + 1)}
            disabled={((totalPages !== null) ? page >= (totalPages - 1) : false) || loading}
            className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
          >Suivant</button>
        </div>
      </div>

      <div className="mt-2 text-xs text-gray-500">Note: endpoints utilisés: <code>/api/people/paginate?page=&lt;index&gt;&amp;size=10</code> et <code>/api/people/search?firstName=&lt;q&gt;&amp;lastName=&lt;q&gt;&amp;num=&lt;q&gt;&amp;page=&lt;index&gt;&amp;size=10</code>. Si votre backend expose un chemin différent, adaptez le fetch dans <code>PeopleList.tsx</code>.</div>
    </div>
  );
}
