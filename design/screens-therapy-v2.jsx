// ─── v2 THERAPY — fast medication selection ──────────────
// Feedback: many medications, focus on making selection easy.

// v2 A — Search-first picker; recent + popular surfaced; categories collapsed below
function TherapyV2A() {
  const recent = [
    { name: 'Цетрин', sub: 'Цетиризин · 10 мг' },
    { name: 'Назонекс', sub: 'Мометазон · спрей' },
  ];
  const popular = [
    { name: 'Зиртек', sub: 'Цетиризин · 10 мг' },
    { name: 'Эриус', sub: 'Дезлоратадин · 5 мг' },
    { name: 'Кларитин', sub: 'Лоратадин · 10 мг' },
    { name: 'Авамис', sub: 'Флутиказон · спрей' },
    { name: 'Сингуляр', sub: 'Монтелукаст · 10 мг' },
  ];
  return (
    <Phone>
      <AppBar title="Препарат" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="row" style={{ gap: 8, padding: '10px 12px', background: 'var(--paper-2)', borderRadius: 12, marginBottom: 16 }}>
            <Icon d={ICONS.search} size={14} stroke="var(--ink-3)" />
            <div style={{ fontSize: 12, color: 'var(--ink-3)', flex: 1 }}>Название или вещество…</div>
            <div className="annot" style={{ fontSize: 9 }}>500+</div>
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 8 }}>Недавно</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 16 }}>
            {recent.map(m => (
              <div key={m.name} className="row" style={{
                padding: '10px 12px',
                border: '1px solid var(--line-2)',
                borderRadius: 10,
                background: 'var(--card)',
              }}>
                <div className="leaf" style={{ width: 28, height: 28, fontSize: 8, background: 'var(--accent)', color: '#fff', border: 'none' }}>
                  {m.name[0]}
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 13, fontWeight: 500 }}>{m.name}</div>
                  <div className="annot" style={{ fontSize: 10 }}>{m.sub}</div>
                </div>
                <div className="annot" style={{ fontSize: 9 }}>повторить</div>
              </div>
            ))}
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 8 }}>Популярные · аллергия</div>
          <div className="card" style={{ padding: 0 }}>
            {popular.map((m, i) => (
              <div key={m.name} className="row" style={{
                padding: '10px 12px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 13 }}>{m.name}</div>
                  <div className="annot" style={{ fontSize: 10 }}>{m.sub}</div>
                </div>
                <div style={{
                  width: 24, height: 24, borderRadius: 12,
                  border: '1px solid var(--line)',
                  display: 'grid', placeItems: 'center',
                }}>
                  <Icon d={ICONS.plus} size={12} stroke="var(--ink-2)" />
                </div>
              </div>
            ))}
          </div>

          <div className="div-h" />
          <div className="annot">Не нашли? <span style={{ color: 'var(--accent-2)' }}>добавить вручную →</span></div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// v2 B — Filtered browse: zone chips → category chips → list (3 taps to add)
function TherapyV2B() {
  const drugs = [
    { name: 'Цетрин',     active: 'Цетиризин',    rx: false },
    { name: 'Зиртек',     active: 'Цетиризин',    rx: false },
    { name: 'Кларитин',   active: 'Лоратадин',    rx: false },
    { name: 'Эриус',      active: 'Дезлоратадин', rx: false },
    { name: 'Телфаст',    active: 'Фексофенадин', rx: false },
    { name: 'Супрастин',  active: 'Хлоропирамин', rx: false },
    { name: 'Аллегра',    active: 'Фексофенадин', rx: false },
  ];
  return (
    <Phone>
      <div className="appbar">
        <Icon d={ICONS.chevR} size={14} stroke="var(--ink-2)" sw={1.6} style={{ transform: 'rotate(180deg)' }} />
        <div className="title" style={{ flex: 1 }}>Антигистаминные</div>
        <Icon d={ICONS.search} size={16} stroke="var(--ink-2)" />
      </div>
      <div style={{ padding: '8px 12px 0', overflowX: 'auto', display: 'flex', gap: 6, whiteSpace: 'nowrap' }}>
        {[
          { l: 'Системные', on: true },
          { l: 'Глаза' },
          { l: 'Нос' },
          { l: 'Бронхи' },
          { l: 'Кожа' },
        ].map(c => (
          <span key={c.l} className={'pill ' + (c.on ? 'active' : '')}>{c.l}</span>
        ))}
      </div>
      <div style={{ padding: '8px 12px 0', overflowX: 'auto', display: 'flex', gap: 6, whiteSpace: 'nowrap' }}>
        {[
          { l: 'Антигистаминные', on: true },
          { l: 'АСИТ' },
          { l: 'Лейкотриены' },
          { l: 'ГКС' },
          { l: 'Моноклональные' },
        ].map(c => (
          <span key={c.l} className={'pill ' + (c.on ? 'active' : '')}
            style={c.on ? null : { fontSize: 10 }}>{c.l}</span>
        ))}
      </div>

      <div className="scr-scroll" style={{ flex: 1, marginTop: 8 }}>
        <div style={{ padding: '0 12px' }}>
          <div className="annot" style={{ marginBottom: 6 }}>{drugs.length} препаратов</div>
          <div className="card" style={{ padding: 0 }}>
            {drugs.map((d, i) => (
              <div key={d.name} className="row" style={{
                padding: '11px 12px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 13 }}>{d.name}</div>
                  <div className="annot" style={{ fontSize: 10 }}>{d.active}</div>
                </div>
                <div style={{
                  width: 24, height: 24, borderRadius: 12,
                  border: '1px solid var(--line)',
                  display: 'grid', placeItems: 'center',
                }}>
                  <Icon d={ICONS.plus} size={12} stroke="var(--ink-2)" />
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

// v2 C — "Schedule view": added meds shown as today/tomorrow timeline; quick + button
function TherapyV2C() {
  const slots = [
    { t: 'Утро', items: [{ n: 'Цетрин', d: '10 мг', taken: true }] },
    { t: 'День', items: [] },
    { t: 'Вечер', items: [
      { n: 'Назонекс', d: '2 впр.', taken: false },
      { n: 'Капли в глаза', d: 'опатанол', taken: false },
    ] },
  ];
  return (
    <Phone>
      <AppBar title="Терапия" sub="пт, 24 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="row" style={{ gap: 6, marginBottom: 14 }}>
            <span className="pill">Симптомы</span>
            <span className="pill active">Терапия</span>
          </div>

          {slots.map(s => (
            <div key={s.t} style={{ marginBottom: 14 }}>
              <div className="row" style={{ justifyContent: 'space-between', marginBottom: 6 }}>
                <div className="h-eyebrow">{s.t}</div>
                <div className="annot" style={{ fontSize: 9 }}>+ препарат</div>
              </div>
              {s.items.length === 0 ? (
                <div className="card card-flat" style={{
                  padding: '14px 12px', borderStyle: 'dashed',
                  fontSize: 11, color: 'var(--ink-3)', textAlign: 'center',
                }}>не запланировано</div>
              ) : (
                <div className="card" style={{ padding: 0 }}>
                  {s.items.map((m, i) => (
                    <div key={m.n} className="row" style={{
                      padding: '10px 12px',
                      borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
                    }}>
                      <div style={{
                        width: 22, height: 22, borderRadius: 11,
                        background: m.taken ? 'var(--accent)' : 'transparent',
                        border: m.taken ? 'none' : '1.5px solid var(--line)',
                        display: 'grid', placeItems: 'center',
                      }}>
                        {m.taken && <Icon d={ICONS.check} size={11} stroke="#fff" sw={2.2} />}
                      </div>
                      <div style={{ flex: 1 }}>
                        <div style={{ fontSize: 13, fontWeight: 500 }}>{m.n}</div>
                        <div className="annot" style={{ fontSize: 10 }}>{m.d}</div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

Object.assign(window, { TherapyV2A, TherapyV2B, TherapyV2C });
