// Polished Region screens — uses only get_locations.php data
// Each location: id, desc (RU name), comment (RU desc), eng_name, eng_desc, lat, lng
// User selection: user.location = id

const LOCATIONS_DATA = [
  { id: 1, desc: 'Москва', comment: 'Центральный район', selected: true },
  { id: 2, desc: 'Ростов-на-Дону', comment: 'Южный район' },
  { id: 3, desc: 'Воронеж', comment: 'Центрально-Чернозёмный район' },
  { id: 4, desc: 'Краснодар', comment: 'Южный район' },
  { id: 5, desc: 'Волгоград', comment: 'Южный район' },
  { id: 6, desc: 'Екатеринбург', comment: 'Уральский район' },
  { id: 7, desc: 'Ставрополь', comment: 'Северо-Кавказский район' },
  { id: 8, desc: 'Санкт-Петербург', comment: 'Северо-Западный район' },
  { id: 9, desc: 'Набережные Челны', comment: 'Поволжский район' },
];

// ── REGION SELECTOR ──
function PRegionSelect() {
  return (
    <PPhone>
      {/* Header */}
      <div style={{
        display: 'flex', alignItems: 'center', gap: 8,
        padding: '12px 14px 10px',
        background: 'var(--card)',
        borderBottom: '1px solid var(--line-2)',
        flexShrink: 0,
      }}>
        <div style={{
          width: 32, height: 32, borderRadius: 10,
          background: 'var(--paper-2)',
          display: 'grid', placeItems: 'center',
        }}>
          <PIcon d={P_ICONS.back} size={16} stroke="var(--ink-2)" sw={1.6} />
        </div>
        <div style={{ flex: 1, textAlign: 'center', fontSize: 15, fontWeight: 600, letterSpacing: -0.2 }}>
          Регион мониторинга
        </div>
        <div style={{ width: 32 }} />
      </div>

      <div className="scr-scroll" style={{ flex: 1 }}>
        {/* Search */}
        <div style={{ padding: '12px 16px 8px' }}>
          <PSearchBar placeholder="Поиск региона…" />
        </div>

        <div style={{ padding: '8px 16px' }}>
          <div className="p-card" style={{ padding: 0 }}>
            {LOCATIONS_DATA.map((loc, i) => (
              <div key={loc.id} className="row" style={{
                padding: '14px 16px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
                gap: 12,
                background: loc.selected ? 'var(--accent-light)' : 'transparent',
              }}>
                <div style={{ flex: 1 }}>
                  <div style={{
                    fontSize: 14,
                    fontWeight: loc.selected ? 600 : 500,
                    color: loc.selected ? 'var(--accent-2)' : 'var(--ink)',
                  }}>{loc.desc}</div>
                  <div className="p-annot" style={{ fontSize: 10, marginTop: 2 }}>{loc.comment}</div>
                </div>
                {loc.selected && (
                  <div style={{
                    width: 22, height: 22, borderRadius: 11,
                    background: 'var(--accent)',
                    display: 'grid', placeItems: 'center',
                    boxShadow: '0 2px 6px rgba(61,122,90,0.25)',
                  }}>
                    <PIcon d={P_ICONS.check} size={12} stroke="#fff" sw={2.4} />
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        <div style={{ height: 16 }} />
      </div>
    </PPhone>
  );
}

Object.assign(window, { PRegionSelect });
