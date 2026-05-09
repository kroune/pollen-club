// ─── DIARY (symptoms + therapy) — 3 variants ───────────

// A — Body map abstracted to zones list
function DiaryA() {
  const zones = [
    { name: 'Глаза',   marks: 0 },
    { name: 'Нос',     marks: 2 },
    { name: 'Горло',   marks: 0 },
    { name: 'Бронхи',  marks: 1 },
    { name: 'Кожа',    marks: 0 },
    { name: 'Общее',   marks: 0 },
  ];
  return (
    <Phone>
      <AppBar title="Дневник" sub="пт, 24 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="row" style={{ gap: 6, marginBottom: 14 }}>
            {[20,21,22,23,24,25,26].map(d => (
              <div key={d} className="col" style={{
                flex: 1, alignItems: 'center', padding: '6px 0',
                background: d === 24 ? 'var(--accent)' : 'transparent',
                color: d === 24 ? '#fff' : 'var(--ink-2)',
                borderRadius: 8,
              }}>
                <div style={{ fontSize: 9, opacity: .7 }}>{['пн','вт','ср','чт','пт','сб','вс'][d - 20]}</div>
                <div className="num" style={{ fontSize: 14, fontWeight: 500 }}>{d}</div>
              </div>
            ))}
          </div>

          <div className="row" style={{ gap: 6, marginBottom: 14 }}>
            <span className="pill active">Симптомы</span>
            <span className="pill">Терапия</span>
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 10 }}>Самочувствие</div>
          <div className="row" style={{ gap: 6 }}>
            {[
              { l: 'Хорошо',   c: 'var(--severity-1)' },
              { l: 'Терпимо',  c: 'var(--severity-2)', on: true },
              { l: 'Плохо',    c: 'var(--severity-3)' },
            ].map(o => (
              <div key={o.l} className="col" style={{
                flex: 1, alignItems: 'center', padding: '14px 4px',
                border: `1.5px solid ${o.on ? o.c : 'var(--line)'}`,
                background: o.on ? 'rgba(217,185,74,0.08)' : 'transparent',
                borderRadius: 12,
              }}>
                <div style={{ width: 10, height: 10, borderRadius: 5, background: o.c, marginBottom: 6 }} />
                <div style={{ fontSize: 11 }}>{o.l}</div>
              </div>
            ))}
          </div>

          <div className="h-eyebrow" style={{ marginTop: 18, marginBottom: 8 }}>Зоны</div>
          <div className="card" style={{ padding: 0 }}>
            {zones.map((z, i) => (
              <div key={z.name} className="row" style={{
                padding: '12px 14px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1, fontSize: 13 }}>{z.name}</div>
                {z.marks > 0
                  ? <span className="pill" style={{ background: 'rgba(217,122,58,0.12)', borderColor: 'var(--severity-3)', color: 'var(--severity-3)' }}>{z.marks} отметок</span>
                  : <span style={{ fontSize: 11, color: 'var(--ink-3)' }}>+ добавить</span>}
                <Icon d={ICONS.chevR} size={14} stroke="var(--ink-3)" />
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// B — Single big mood scale + quick add
function DiaryB() {
  return (
    <Phone>
      <AppBar title="Как день?" sub="пт, 24 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad-lg">
          <div className="h-display">Оцените<br />самочувствие</div>

          <div style={{ marginTop: 22 }}>
            <div className="row" style={{ justifyContent: 'space-between', fontSize: 10, color: 'var(--ink-3)', marginBottom: 6 }}>
              <span>хорошо</span>
              <span>плохо</span>
            </div>
            <div style={{ position: 'relative', height: 12 }}>
              <div style={{
                position: 'absolute', inset: 0, borderRadius: 6,
                background: 'linear-gradient(to right, var(--severity-1), var(--severity-2), var(--severity-3), var(--severity-4))',
              }} />
              <div style={{
                position: 'absolute', left: '40%', top: -6, width: 24, height: 24,
                borderRadius: 12, background: '#fff', border: '2px solid var(--ink)',
              }} />
            </div>
            <div style={{ marginTop: 12, textAlign: 'center', fontSize: 12, color: 'var(--ink-2)' }}>
              <span className="num" style={{ fontSize: 22, color: 'var(--severity-2)' }}>4</span>
              <span style={{ marginLeft: 6 }}>из 10 · терпимо</span>
            </div>
          </div>

          <div className="div-h" />
          <div className="h-eyebrow" style={{ marginBottom: 10 }}>Где беспокоит</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {[
              { l: 'Зуд в носу', on: true },
              { l: 'Заложенность', on: true },
              { l: 'Чихание' },
              { l: 'Слезотечение' },
              { l: 'Кашель' },
              { l: 'Кожный зуд' },
              { l: '+ другое' },
            ].map(s => (
              <span key={s.l} className={'pill ' + (s.on ? 'active' : '')}>{s.l}</span>
            ))}
          </div>

          <div className="div-h" />
          <div className="h-eyebrow" style={{ marginBottom: 8 }}>Принято</div>
          <div className="row" style={{ gap: 8, fontSize: 12 }}>
            <span className="pill">Цетрин · 10мг</span>
            <span className="pill" style={{ borderStyle: 'dashed' }}>+ препарат</span>
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// C — Timeline log
function DiaryC() {
  const events = [
    { t: '08:30', kind: 'med', txt: 'Цетрин · 10 мг' },
    { t: '11:00', kind: 'sym', txt: 'Зуд в носу · слабый' },
    { t: '13:20', kind: 'sym', txt: 'Чихание < 10 раз' },
    { t: '16:45', kind: 'note', txt: 'Был в парке 2 часа' },
    { t: '20:00', kind: 'med', txt: 'Спрей Назонекс' },
  ];
  return (
    <Phone>
      <AppBar title="Лента" sub="пт, 24 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="card" style={{ padding: 14 }}>
            <div className="row" style={{ justifyContent: 'space-between' }}>
              <div className="h-eyebrow">Самочувствие</div>
              <SevLabel level={2} />
            </div>
            <div className="row" style={{ alignItems: 'baseline', marginTop: 4 }}>
              <div className="h-display num">4</div>
              <div style={{ fontSize: 11, color: 'var(--ink-3)', marginLeft: 6 }}>/ 10</div>
            </div>
          </div>

          <div style={{ position: 'relative', marginTop: 18, paddingLeft: 50 }}>
            <div style={{ position: 'absolute', left: 42, top: 0, bottom: 0, width: 1, background: 'var(--line)' }} />
            {events.map((e, i) => (
              <div key={i} style={{ position: 'relative', paddingBottom: 14 }}>
                <div className="num" style={{
                  position: 'absolute', left: -50, top: 1,
                  fontSize: 10, color: 'var(--ink-3)', width: 38, textAlign: 'right',
                }}>{e.t}</div>
                <div style={{
                  position: 'absolute', left: -10, top: 5,
                  width: 8, height: 8, borderRadius: 4,
                  background: e.kind === 'med' ? 'var(--accent)' : e.kind === 'sym' ? 'var(--severity-3)' : 'var(--ink-3)',
                }} />
                <div style={{ fontSize: 12, marginTop: 0 }}>{e.txt}</div>
                <div className="annot" style={{ textTransform: 'uppercase', fontSize: 9, marginTop: 1 }}>
                  {e.kind === 'med' ? 'препарат' : e.kind === 'sym' ? 'симптом' : 'заметка'}
                </div>
              </div>
            ))}
          </div>
        </div>
        <div style={{ position: 'absolute', right: 16, bottom: 60 }}>
          <div className="fab"><Icon d={ICONS.plus} size={18} stroke="#fff" sw={1.6} /></div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

Object.assign(window, { DiaryA, DiaryB, DiaryC });
