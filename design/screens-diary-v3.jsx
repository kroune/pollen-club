// ─── v3 DIARY — fix tab order, more variants ──────────────
// Feedback: "Симптомы / Терапия" tabs felt out of place under date row.
// Variant B was missing therapy entry. Try several layouts.

// Reusable abstract body silhouette (compact)
function BodyShapeV3({ activeZone, activeZones = {} }) {
  return (
    <svg viewBox="0 0 100 180" style={{ width: '100%', height: '100%', display: 'block' }}>
      <ellipse cx="50" cy="22" rx="13" ry="15" fill="var(--paper-2)" stroke="var(--line)" />
      <rect x="44" y="34" width="12" height="6" fill="var(--paper-2)" stroke="var(--line)" />
      <path d="M 30 40 Q 30 38 35 38 L 65 38 Q 70 38 70 40 L 72 95 Q 72 98 68 98 L 32 98 Q 28 98 28 95 Z"
        fill="var(--paper-2)" stroke="var(--line)" />
      <path d="M 30 42 L 18 80 L 22 95" fill="none" stroke="var(--line)" strokeWidth="1" />
      <path d="M 70 42 L 82 80 L 78 95" fill="none" stroke="var(--line)" strokeWidth="1" />
      <path d="M 38 98 L 36 165" fill="none" stroke="var(--line)" strokeWidth="1" />
      <path d="M 62 98 L 64 165" fill="none" stroke="var(--line)" strokeWidth="1" />

      {[
        { id: 'eyes',  cx: 50, cy: 18, r: 5 },
        { id: 'nose',  cx: 50, cy: 24, r: 4 },
        { id: 'throat',cx: 50, cy: 38, r: 5 },
        { id: 'chest', cx: 50, cy: 70, r: 9 },
        { id: 'skin',  cx: 26, cy: 90, r: 6 },
      ].map(z => {
        const count = activeZones[z.id];
        const has = !!count;
        const on = activeZone === z.id;
        return (
          <g key={z.id}>
            <circle cx={z.cx} cy={z.cy} r={z.r}
              fill={has ? 'rgba(217,122,58,0.18)' : 'transparent'}
              stroke={on ? 'var(--ink)' : has ? 'var(--severity-3)' : 'var(--ink-3)'}
              strokeWidth={on ? 1.4 : 0.8}
              strokeDasharray={has || on ? '' : '2 2'} />
            {has && (
              <text x={z.cx} y={z.cy + 1.5} textAnchor="middle" fontSize="6" fontWeight="600" fill="var(--severity-3)" fontFamily="JetBrains Mono">{count}</text>
            )}
          </g>
        );
      })}
    </svg>
  );
}

// Day strip used in all diary v3 variants (date row + mood at top)
function DiaryDateRow({ active = 24 }) {
  return (
    <div className="row" style={{ gap: 5 }}>
      {[20,21,22,23,24,25,26].map(d => (
        <div key={d} className="col" style={{
          flex: 1, alignItems: 'center', padding: '5px 0',
          background: d === active ? 'var(--accent)' : 'transparent',
          color: d === active ? '#fff' : 'var(--ink-2)',
          borderRadius: 8,
        }}>
          <div style={{ fontSize: 8, opacity: .7 }}>{['пн','вт','ср','чт','пт','сб','вс'][d - 20]}</div>
          <div className="num" style={{ fontSize: 13 }}>{d}</div>
        </div>
      ))}
    </div>
  );
}

// Diary v3 A — single screen, NO tabs. Body picks zone, "терапия" added inline as another card.
function DiaryV3A() {
  return (
    <Phone>
      <AppBar title="Дневник" sub="пт, 24 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '8px 16px 0' }}>
          <DiaryDateRow />
        </div>

        <div style={{ padding: '8px 16px' }}>
          <div className="h-eyebrow" style={{ marginBottom: 4 }}>Самочувствие</div>
          <div className="row" style={{ gap: 5 }}>
            {[
              { l: 'Хорошо',  c: 'var(--severity-1)' },
              { l: 'Терпимо', c: 'var(--severity-2)', on: true },
              { l: 'Плохо',   c: 'var(--severity-3)' },
            ].map(o => (
              <div key={o.l} className="row" style={{
                flex: 1, padding: '6px 8px', gap: 5, justifyContent: 'center',
                border: `1px solid ${o.on ? o.c : 'var(--line)'}`,
                borderRadius: 8,
                background: o.on ? 'rgba(217,185,74,0.12)' : 'transparent',
              }}>
                <div style={{ width: 6, height: 6, borderRadius: 3, background: o.c }} />
                <div style={{ fontSize: 11 }}>{o.l}</div>
              </div>
            ))}
          </div>
        </div>

        <div className="row" style={{ alignItems: 'flex-start', padding: '0 16px' }}>
          <div style={{ width: 110, height: 200 }}>
            <BodyShapeV3 activeZone="nose" activeZones={{ nose: 2 }} />
          </div>
          <div className="col" style={{ flex: 1, paddingLeft: 8, gap: 6 }}>
            <div className="h-eyebrow">Симптомы · Нос</div>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4 }}>
              {[
                { l: 'Зуд', on: true },
                { l: 'Заложенность', on: true },
                { l: 'Ринорея' },
                { l: 'Чихание', },
              ].map(s => (
                <span key={s.l} className={'pill ' + (s.on ? 'active' : '')} style={{ fontSize: 10, padding: '3px 8px' }}>{s.l}</span>
              ))}
            </div>
          </div>
        </div>

        <div style={{ padding: '12px 16px 0' }}>
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 6 }}>
            <div className="h-eyebrow">Терапия сегодня</div>
            <div className="annot" style={{ fontSize: 10 }}>+ препарат</div>
          </div>
          <div className="card" style={{ padding: 0 }}>
            {[
              { n: 'Цетрин', d: '10 мг · утром', taken: true },
              { n: 'Назонекс', d: '2 впр. · вечером', taken: false },
            ].map((m, i) => (
              <div key={m.n} className="row" style={{
                padding: '8px 12px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{
                  width: 18, height: 18, borderRadius: 9,
                  background: m.taken ? 'var(--accent)' : 'transparent',
                  border: m.taken ? 'none' : '1.5px solid var(--line)',
                  display: 'grid', placeItems: 'center',
                }}>
                  {m.taken && <Icon d={ICONS.check} size={9} stroke="#fff" sw={2.4} />}
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 12 }}>{m.n}</div>
                  <div className="annot" style={{ fontSize: 9 }}>{m.d}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// Diary v3 B — multi-checkin timeline; user can log feelings at different times of day
function DiaryV3B() {
  const checkins = [
    { time: '08:30', mood: 1, label: 'Хорошо', note: '—', symp: [] },
    { time: '13:15', mood: 2, label: 'Терпимо', note: 'Гулял, нос потёк', symp: ['Нос'] },
    { time: '19:00', mood: 3, label: 'Плохо', note: 'Глаза + нос', symp: ['Нос', 'Глаза'] },
  ];
  const moodC = [null, 'var(--severity-1)','var(--severity-2)','var(--severity-3)'];
  return (
    <Phone>
      <AppBar title="Дневник" sub="пт, 24 апреля · 3 отметки" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '8px 16px 12px' }}>
          <DiaryDateRow />
        </div>

        <div style={{ padding: '0 16px' }}>
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 8 }}>
            <div className="h-eyebrow">Чек-ины за день</div>
            <div className="annot" style={{ fontSize: 10, color: 'var(--accent-2)' }}>+ сейчас</div>
          </div>

          {/* timeline */}
          <div style={{ position: 'relative', paddingLeft: 28 }}>
            <div style={{
              position: 'absolute', left: 8, top: 6, bottom: 12,
              width: 1, background: 'var(--line)',
            }} />
            {checkins.map((c, i) => (
              <div key={c.time} style={{ position: 'relative', marginBottom: 10 }}>
                <div style={{
                  position: 'absolute', left: -22, top: 4,
                  width: 13, height: 13, borderRadius: 7,
                  background: moodC[c.mood], border: '2px solid var(--paper)',
                  boxShadow: '0 0 0 1px var(--line)',
                }} />
                <div className="card" style={{ padding: '8px 10px' }}>
                  <div className="row" style={{ justifyContent: 'space-between', marginBottom: 4 }}>
                    <div className="num" style={{ fontSize: 12, fontWeight: 500 }}>{c.time}</div>
                    <div style={{ fontSize: 11, color: moodC[c.mood] }}>{c.label}</div>
                  </div>
                  <div style={{ fontSize: 11, color: 'var(--ink-2)', marginBottom: c.symp.length ? 5 : 0 }}>{c.note}</div>
                  {c.symp.length > 0 && (
                    <div style={{ display: 'flex', gap: 4 }}>
                      {c.symp.map(s => (
                        <span key={s} className="pill active" style={{ fontSize: 9, padding: '2px 6px' }}>{s}</span>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>

          <div className="div-h" style={{ margin: '6px 0 10px' }} />
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 6 }}>
            <div className="h-eyebrow">Терапия</div>
            <div className="annot" style={{ fontSize: 10 }}>2 препарата</div>
          </div>
          <div className="card" style={{ padding: 0 }}>
            {[
              { n: 'Цетрин', d: '10 мг · 08:30', taken: true },
              { n: 'Назонекс', d: '2 впр. · 19:00', taken: true },
            ].map((m, i) => (
              <div key={m.n} className="row" style={{
                padding: '7px 10px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <Icon d={ICONS.check} size={11} stroke="var(--accent)" sw={2.2} />
                <div style={{ flex: 1, fontSize: 12 }}>{m.n}</div>
                <div className="annot" style={{ fontSize: 9 }}>{m.d}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// Diary v3 C — split: top half body silhouette, bottom half tabbed (zones / меds) — tabs ABOVE body
function DiaryV3C() {
  return (
    <Phone>
      <AppBar title="Дневник" sub="пт, 24 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '8px 16px 0' }}>
          <DiaryDateRow />
        </div>

        {/* segmented control INSTEAD of being below */}
        <div style={{ padding: '10px 16px 4px' }}>
          <div style={{
            display: 'flex', background: 'var(--paper-2)', padding: 3,
            borderRadius: 10, border: '1px solid var(--line-2)',
          }}>
            {['Самочувствие', 'Препараты'].map((l, i) => (
              <div key={l} style={{
                flex: 1, padding: '6px 0', textAlign: 'center', fontSize: 11,
                background: i === 0 ? 'var(--card)' : 'transparent',
                borderRadius: 7,
                fontWeight: i === 0 ? 500 : 400,
                color: i === 0 ? 'var(--ink)' : 'var(--ink-2)',
                boxShadow: i === 0 ? '0 1px 2px rgba(0,0,0,0.04)' : 'none',
              }}>{l}</div>
            ))}
          </div>
        </div>

        <div style={{ padding: '6px 16px 0' }}>
          <div className="row" style={{ gap: 5, marginBottom: 8 }}>
            {[
              { l: 'Хорошо',  c: 'var(--severity-1)' },
              { l: 'Терпимо', c: 'var(--severity-2)', on: true },
              { l: 'Плохо',   c: 'var(--severity-3)' },
            ].map(o => (
              <div key={o.l} className="row" style={{
                flex: 1, padding: '6px 8px', gap: 5, justifyContent: 'center',
                border: `1px solid ${o.on ? o.c : 'var(--line)'}`,
                borderRadius: 8,
                background: o.on ? 'rgba(217,185,74,0.12)' : 'transparent',
              }}>
                <div style={{ width: 6, height: 6, borderRadius: 3, background: o.c }} />
                <div style={{ fontSize: 11 }}>{o.l}</div>
              </div>
            ))}
          </div>
        </div>

        <div style={{ height: 200, padding: '0 16px', position: 'relative' }}>
          <BodyShapeV3 activeZone="nose" activeZones={{ nose: 2 }} />
          <div className="annot" style={{ position: 'absolute', top: 6, right: 16, fontSize: 9, textAlign: 'right' }}>нажмите<br />на зону</div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// Diary v3 D — body wide left, vertical mood scale right (5-step), zones list under
function DiaryV3D() {
  const moods = [
    { l: 'отлично', c: 'var(--severity-1)' },
    { l: 'хорошо',  c: 'var(--severity-1)' },
    { l: 'терпимо', c: 'var(--severity-2)', on: true },
    { l: 'плохо',   c: 'var(--severity-3)' },
    { l: 'ужасно',  c: 'var(--severity-4)' },
  ];
  return (
    <Phone>
      <AppBar title="Сегодня" sub="пт, 24 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '8px 16px 0' }}>
          <DiaryDateRow />
        </div>

        <div className="row" style={{ alignItems: 'flex-start', padding: '12px 16px 6px' }}>
          <div style={{ flex: 1, height: 220 }}>
            <BodyShapeV3 activeZone="nose" activeZones={{ nose: 2 }} />
          </div>
          <div className="col" style={{ width: 90, marginLeft: 8 }}>
            <div className="h-eyebrow" style={{ fontSize: 9, marginBottom: 4 }}>Самочувствие</div>
            {moods.map(m => (
              <div key={m.l} className="row" style={{
                padding: '5px 8px',
                gap: 6,
                borderLeft: `2px solid ${m.on ? m.c : 'transparent'}`,
                background: m.on ? 'var(--paper-2)' : 'transparent',
                borderRadius: m.on ? 4 : 0,
              }}>
                <div style={{ width: 5, height: 5, borderRadius: 3, background: m.c, opacity: m.on ? 1 : 0.6 }} />
                <div style={{ fontSize: 10, fontWeight: m.on ? 500 : 400 }}>{m.l}</div>
              </div>
            ))}
            <div className="div-h" style={{ margin: '8px 0' }} />
            <div className="annot" style={{ fontSize: 9, marginBottom: 4 }}>Препараты</div>
            <div style={{ fontSize: 10, color: 'var(--ink-2)' }}>2 за день</div>
            <div className="annot" style={{ fontSize: 9, color: 'var(--accent-2)', marginTop: 4 }}>+ добавить</div>
          </div>
        </div>

        <div className="hr" />
        <div style={{ padding: '8px 16px' }}>
          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Активные зоны</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 5 }}>
            <span className="pill" style={{ borderColor: 'var(--severity-3)', color: 'var(--severity-3)', fontSize: 10, padding: '3px 8px' }}>
              <span style={{ width: 5, height: 5, borderRadius: 3, background: 'var(--severity-3)' }} />
              Нос · 2
            </span>
            <span className="pill" style={{ fontSize: 10, padding: '3px 8px', borderStyle: 'dashed' }}>+ зону</span>
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

Object.assign(window, { DiaryV3A, DiaryV3B, DiaryV3C, DiaryV3D });
