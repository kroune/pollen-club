// Polished Diary screen — date header + mood + body + therapy

function PDateHeader({ active = 24 }) {
  return (
    <div style={{ padding: '12px 16px 8px', borderBottom: '1px solid var(--line-2)' }}>
      <div className="row" style={{ justifyContent: 'space-between', marginBottom: 8 }}>
        <div className="p-eyebrow">Апрель 2026</div>
        <div className="row" style={{ gap: 10 }}>
          <PIcon d={P_ICONS.back} size={13} stroke="var(--ink-3)" sw={1.6} />
          <PIcon d={P_ICONS.cal} size={13} stroke="var(--ink-3)" sw={1.4} />
          <PIcon d={P_ICONS.chevR} size={13} stroke="var(--ink-3)" sw={1.6} />
        </div>
      </div>
      <div style={{ display: 'flex', gap: 4 }}>
        {[20,21,22,23,24,25,26].map(d => {
          const isActive = d === active;
          return (
            <div key={d} style={{
              flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center',
              padding: '5px 0',
              background: isActive ? 'var(--accent)' : 'transparent',
              color: isActive ? '#fff' : 'var(--ink-2)',
              borderRadius: 10,
              transition: 'all 0.15s',
            }}>
              <div style={{ fontSize: 9, opacity: .7, fontWeight: 500 }}>{['пн','вт','ср','чт','пт','сб','вс'][d - 20]}</div>
              <div className="p-num" style={{ fontSize: 13, fontWeight: isActive ? 600 : 500, marginTop: 2 }}>{d}</div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function PBodyShape({ activeZones = {} }) {
  return (
    <svg viewBox="0 0 100 180" style={{ width: '100%', height: '100%', display: 'block' }}>
      {/* body outline */}
      <ellipse cx="50" cy="22" rx="13" ry="15" fill="var(--paper-2)" stroke="var(--line)" strokeWidth="0.8" />
      <rect x="44" y="34" width="12" height="6" rx="2" fill="var(--paper-2)" stroke="var(--line)" strokeWidth="0.8" />
      <path d="M 30 40 Q 30 38 35 38 L 65 38 Q 70 38 70 40 L 72 95 Q 72 98 68 98 L 32 98 Q 28 98 28 95 Z"
        fill="var(--paper-2)" stroke="var(--line)" strokeWidth="0.8" />
      <path d="M 30 42 L 18 80 L 22 95" fill="none" stroke="var(--line)" strokeWidth="0.8" />
      <path d="M 70 42 L 82 80 L 78 95" fill="none" stroke="var(--line)" strokeWidth="0.8" />
      <path d="M 38 98 L 36 165" fill="none" stroke="var(--line)" strokeWidth="0.8" />
      <path d="M 62 98 L 64 165" fill="none" stroke="var(--line)" strokeWidth="0.8" />

      {[
        { id: 'eyes', cx: 50, cy: 18, r: 5 },
        { id: 'nose', cx: 50, cy: 24, r: 4 },
        { id: 'throat', cx: 50, cy: 38, r: 5 },
        { id: 'chest', cx: 50, cy: 70, r: 9 },
        { id: 'skin', cx: 26, cy: 90, r: 6 },
      ].map(z => {
        const count = activeZones[z.id];
        const has = !!count;
        return (
          <g key={z.id}>
            <circle cx={z.cx} cy={z.cy} r={z.r}
              fill={has ? 'rgba(212,113,58,0.15)' : 'transparent'}
              stroke={has ? 'var(--severity-3)' : 'var(--ink-3)'}
              strokeWidth={has ? 1 : 0.6}
              strokeDasharray={has ? '' : '2 2'} />
            {has && (
              <text x={z.cx} y={z.cy + 2} textAnchor="middle" fontSize="7" fontWeight="600"
                fill="var(--severity-3)" fontFamily="'JetBrains Mono'">{count}</text>
            )}
          </g>
        );
      })}
    </svg>
  );
}

function PDiary() {
  return (
    <PPhone>
      <PDateHeader active={24} />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          {/* Mood selector */}
          <div className="p-eyebrow" style={{ marginBottom: 8 }}>Самочувствие</div>
          <div style={{ display: 'flex', gap: 6, marginBottom: 16 }}>
            {[
              { l: 'Хорошо', c: 'var(--severity-1)' },
              { l: 'Терпимо', c: 'var(--severity-2)', on: true },
              { l: 'Плохо', c: 'var(--severity-3)' },
            ].map(o => (
              <div key={o.l} style={{
                flex: 1, padding: '8px 8px',
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
                border: o.on ? `1.5px solid ${o.c}` : '1.5px solid var(--line-2)',
                borderRadius: 10,
                background: o.on ? 'rgba(212,168,58,0.08)' : 'transparent',
                transition: 'all 0.15s',
              }}>
                <div style={{ width: 7, height: 7, borderRadius: 4, background: o.c }} />
                <div style={{ fontSize: 12, fontWeight: o.on ? 600 : 400 }}>{o.l}</div>
              </div>
            ))}
          </div>

          {/* Body + symptoms */}
          <div className="row" style={{ alignItems: 'flex-start', marginBottom: 12 }}>
            <div style={{ width: 110, height: 200, flexShrink: 0 }}>
              <PBodyShape activeZones={{ nose: 2 }} />
            </div>
            <div style={{ flex: 1, paddingLeft: 10, display: 'flex', flexDirection: 'column', gap: 8 }}>
              <div className="p-eyebrow">Симптомы · Нос</div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 5 }}>
                {[
                  { l: 'Зуд', on: true },
                  { l: 'Заложенность', on: true },
                  { l: 'Ринорея' },
                  { l: 'Чихание' },
                  { l: 'Кровотечения' },
                ].map(s => (
                  <span key={s.l} className={'p-pill ' + (s.on ? 'active' : '')} style={{ fontSize: 11, padding: '4px 10px' }}>{s.l}</span>
                ))}
              </div>
              <span style={{ fontSize: 11, color: 'var(--accent-2)', fontWeight: 500, marginTop: 2 }}>+ другую зону</span>
            </div>
          </div>

          <div className="p-divider" />

          {/* Therapy section */}
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 10 }}>
            <div className="p-eyebrow">Терапия</div>
            <span style={{ fontSize: 11, color: 'var(--accent-2)', fontWeight: 500 }}>+ препарат</span>
          </div>
          <div className="p-card" style={{ padding: 0 }}>
            {[
              { n: 'Цетрин', d: '10 мг', t: '08:00', taken: true },
              { n: 'Назонекс', d: '2 впр.', t: '20:00', taken: false },
            ].map((m, i) => (
              <div key={m.n} className="row" style={{
                padding: '12px 16px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div className="p-annot" style={{ width: 34, fontSize: 10 }}>{m.t}</div>
                <div style={{
                  width: 18, height: 18, borderRadius: 9,
                  background: m.taken ? 'var(--accent)' : 'transparent',
                  border: m.taken ? 'none' : '1.5px solid var(--line)',
                  display: 'grid', placeItems: 'center', flexShrink: 0,
                  transition: 'all 0.15s',
                }}>
                  {m.taken && <PIcon d={P_ICONS.check} size={10} stroke="#fff" sw={2.4} />}
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 13, fontWeight: 500 }}>{m.n}</div>
                  <div className="p-annot" style={{ fontSize: 10 }}>{m.d}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <PTabBar active="diary" />
    </PPhone>
  );
}

Object.assign(window, { PDateHeader, PBodyShape, PDiary });
