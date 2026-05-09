// ─── v2 DIARY — body-zone picker variants ───────────────
// Feedback: keep body silhouette as the symptom picker (loved feature).

// Geometric body silhouette — abstract, no faces / illustration
function BodyShape({ activeZone, onPick }) {
  // simple anatomical layout in 100x200 viewbox
  const zones = [
    { id: 'head',  cx: 50, cy: 22, r: 14, label: 'Голова' },
    { id: 'eyes',  cx: 50, cy: 18, r: 5,  label: 'Глаза', dot: true },
    { id: 'nose',  cx: 50, cy: 24, r: 4,  label: 'Нос', dot: true },
    { id: 'throat',cx: 50, cy: 38, r: 5,  label: 'Горло', dot: true },
    { id: 'chest', cx: 50, cy: 70, r: 9,  label: 'Бронхи', dot: true },
    { id: 'skin',  cx: 26, cy: 90, r: 6,  label: 'Кожа', dot: true },
  ];
  return (
    <svg viewBox="0 0 100 180" style={{ width: '100%', height: '100%', display: 'block' }}>
      {/* head */}
      <ellipse cx="50" cy="22" rx="13" ry="15" fill="var(--paper-2)" stroke="var(--line)" />
      {/* neck */}
      <rect x="44" y="34" width="12" height="6" fill="var(--paper-2)" stroke="var(--line)" />
      {/* torso */}
      <path d="M 30 40 Q 30 38 35 38 L 65 38 Q 70 38 70 40 L 72 95 Q 72 98 68 98 L 32 98 Q 28 98 28 95 Z"
        fill="var(--paper-2)" stroke="var(--line)" />
      {/* arms */}
      <path d="M 30 42 L 18 80 L 22 95" fill="none" stroke="var(--line)" strokeWidth="1" />
      <path d="M 70 42 L 82 80 L 78 95" fill="none" stroke="var(--line)" strokeWidth="1" />
      {/* legs */}
      <path d="M 38 98 L 36 165" fill="none" stroke="var(--line)" strokeWidth="1" />
      <path d="M 62 98 L 64 165" fill="none" stroke="var(--line)" strokeWidth="1" />

      {/* hit zones */}
      {zones.filter(z => z.dot).map(z => {
        const on = activeZone === z.id;
        const has = ['nose'].includes(z.id);
        return (
          <g key={z.id} onClick={() => onPick && onPick(z.id)} style={{ cursor: 'pointer' }}>
            <circle cx={z.cx} cy={z.cy} r={z.r}
              fill={has ? 'rgba(217,122,58,0.18)' : 'transparent'}
              stroke={on ? 'var(--ink)' : has ? 'var(--severity-3)' : 'var(--ink-3)'}
              strokeWidth={on ? 1.4 : 0.8}
              strokeDasharray={has || on ? '' : '2 2'} />
            {has && (
              <text x={z.cx} y={z.cy + 1.5} textAnchor="middle" fontSize="6" fontWeight="600" fill="var(--severity-3)" fontFamily="JetBrains Mono">2</text>
            )}
          </g>
        );
      })}
    </svg>
  );
}

// Diary v2 A — body silhouette + picker drawer (current zone)
function DiaryV2A() {
  return (
    <Phone>
      <AppBar title="Дневник" sub="пт, 24 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '10px 16px 0' }}>
          <div className="row" style={{ gap: 6, marginBottom: 8 }}>
            {[20,21,22,23,24,25,26].map(d => (
              <div key={d} className="col" style={{
                flex: 1, alignItems: 'center', padding: '6px 0',
                background: d === 24 ? 'var(--accent)' : 'transparent',
                color: d === 24 ? '#fff' : 'var(--ink-2)',
                borderRadius: 8,
              }}>
                <div style={{ fontSize: 9, opacity: .7 }}>{['пн','вт','ср','чт','пт','сб','вс'][d - 20]}</div>
                <div className="num" style={{ fontSize: 14 }}>{d}</div>
              </div>
            ))}
          </div>
          <div className="row" style={{ gap: 6 }}>
            <span className="pill active">Симптомы</span>
            <span className="pill">Терапия</span>
          </div>
        </div>

        <div style={{ height: 220, padding: '6px 16px', position: 'relative' }}>
          <BodyShape activeZone="nose" />
          <div className="annot" style={{
            position: 'absolute', top: 14, right: 16,
            fontSize: 9, textAlign: 'right',
          }}>нажмите<br />на зону</div>
        </div>

        <div className="card" style={{ margin: '0 16px 12px', padding: 14, borderColor: 'var(--severity-3)' }}>
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 10 }}>
            <div style={{ fontSize: 13, fontWeight: 500 }}>Нос · 2 отметки</div>
            <Icon d={ICONS.x} size={12} stroke="var(--ink-3)" />
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {[
              { l: 'Зуд', on: true },
              { l: 'Заложенность', on: true },
              { l: 'Ринорея' },
              { l: 'Чихание < 10' },
              { l: 'Чихание > 10' },
              { l: 'Кровотечения' },
            ].map(s => (
              <span key={s.l} className={'pill ' + (s.on ? 'active' : '')}>{s.l}</span>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// Diary v2 B — body silhouette + side mood scale, dots on body show severity
function DiaryV2B() {
  return (
    <Phone>
      <div className="appbar">
        <Icon d={ICONS.menu} size={18} stroke="var(--ink-2)" />
        <div className="title" style={{ flex: 1 }}>Симптомы</div>
        <Icon d={ICONS.cal} size={16} stroke="var(--ink-2)" />
      </div>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '8px 16px' }}>
          <div className="annot" style={{ marginBottom: 4 }}>пт · 24 апреля</div>
          <div className="h-display" style={{ fontSize: 22 }}>Где сегодня?</div>
        </div>

        <div className="row" style={{ padding: '10px 16px', alignItems: 'flex-start' }}>
          <div style={{ flex: 1, height: 240 }}>
            <BodyShape activeZone="nose" />
          </div>
          <div className="col" style={{ width: 90, gap: 8, marginLeft: 4 }}>
            <div className="h-eyebrow" style={{ fontSize: 9 }}>Самочувствие</div>
            {[
              { l: 'Хорошо',  c: 'var(--severity-1)' },
              { l: 'Терпимо', c: 'var(--severity-2)', on: true },
              { l: 'Плохо',   c: 'var(--severity-3)' },
            ].map(o => (
              <div key={o.l} className="row" style={{
                padding: '8px 8px',
                border: `1px solid ${o.on ? o.c : 'var(--line)'}`,
                borderRadius: 8,
                background: o.on ? 'rgba(217,185,74,0.1)' : 'transparent',
                gap: 6,
              }}>
                <div style={{ width: 8, height: 8, borderRadius: 4, background: o.c }} />
                <div style={{ fontSize: 11 }}>{o.l}</div>
              </div>
            ))}
          </div>
        </div>

        <div className="hr" />
        <div style={{ padding: '10px 16px' }}>
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 8 }}>
            <div className="h-eyebrow">Активные зоны</div>
            <span className="annot">+ зону</span>
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            <span className="pill" style={{ borderColor: 'var(--severity-3)', color: 'var(--severity-3)' }}>
              <span style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--severity-3)' }} />
              Нос · 2
            </span>
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

Object.assign(window, { DiaryV2A, DiaryV2B });
