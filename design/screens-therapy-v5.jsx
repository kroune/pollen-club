// ─── v5 THERAPY — base = TherapyV3A (recent + categories) ────────
// Feedback: pick TherapyV3A as base; experiment with showing "today's intake"
// at different placements; drop time-of-day (just per-day).
//
// Common base: search bar, "Ваши препараты" recent list, categories.
// Variants differ in HOW today's intake surfaces.

const TODAY_DOSES = [
  { n: 'Цетрин', d: '10 мг' },
  { n: 'Назонекс', d: '2 впр.' },
];

const RECENT_MEDS = [
  { name: 'Цетрин',   sub: 'Цетиризин · 10 мг · перорально', last: 'вчера',         count: 12 },
  { name: 'Назонекс', sub: 'Мометазон · спрей в нос',         last: 'сегодня',        count: 8 },
  { name: 'Опатанол', sub: 'Олопатадин · капли в глаза',      last: '3 дня назад',    count: 4 },
  { name: 'Сингуляр', sub: 'Монтелукаст · 10 мг',             last: 'неделю назад',   count: 2 },
];

// shared chunks
function SearchBar() {
  return (
    <div className="row" style={{ gap: 8, padding: '10px 12px', background: 'var(--paper-2)', borderRadius: 12 }}>
      <Icon d={ICONS.search} size={14} stroke="var(--ink-3)" />
      <div style={{ fontSize: 12, color: 'var(--ink-3)', flex: 1 }}>Название или вещество…</div>
    </div>
  );
}

function RecentMedRow({ m, takenToday = false }) {
  return (
    <div className="row" style={{
      padding: '10px 12px',
      border: '1px solid ' + (takenToday ? 'var(--accent)' : 'var(--line-2)'),
      borderRadius: 10,
      background: takenToday ? 'rgba(74,125,94,0.05)' : 'var(--card)',
      gap: 10,
    }}>
      <div className="leaf" style={{
        width: 28, height: 28, fontSize: 8,
        background: takenToday ? 'var(--accent)' : 'var(--paper-2)',
        color: takenToday ? '#fff' : 'var(--ink-2)',
        border: takenToday ? 'none' : '1px solid var(--line)',
        flexShrink: 0,
      }}>
        {takenToday ? <Icon d={ICONS.check} size={11} stroke="#fff" sw={2.4} /> : m.name[0]}
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 13, fontWeight: 500 }}>{m.name}</div>
        <div className="annot" style={{ fontSize: 10, marginTop: 1 }}>{m.sub}</div>
        <div className="annot" style={{ fontSize: 9, marginTop: 2 }}>
          {m.count} приёмов · {m.last}
        </div>
      </div>
      <div className={'pill ' + (takenToday ? '' : 'active')} style={{ fontSize: 10, padding: '4px 10px' }}>
        {takenToday ? 'отменить' : '+ принять'}
      </div>
    </div>
  );
}

function CategoryList() {
  return (
    <div className="card" style={{ padding: 0 }}>
      {['Системного действия', 'Глаза', 'Нос', 'Бронхи', 'Кожа', 'Другие средства'].map((c, i) => (
        <div key={c} className="row" style={{
          padding: '10px 12px',
          borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
        }}>
          <div style={{ flex: 1, fontSize: 12 }}>{c}</div>
          <Icon d={ICONS.chevR} size={12} stroke="var(--ink-3)" />
        </div>
      ))}
    </div>
  );
}

// ── A1 ── pinned compact chip strip at top: "сегодня приняли" — tiny visual receipt
function TherapyV5A1() {
  return (
    <Phone>
      <MiniBar back />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 4 }}>
          <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1, marginBottom: 4 }}>Препарат</div>
          <div className="annot" style={{ fontSize: 10, marginBottom: 12 }}>пт, 24 апреля</div>

          {/* sticky chip strip */}
          <div className="row" style={{ gap: 6, flexWrap: 'wrap', marginBottom: 14 }}>
            <div style={{ fontSize: 10, color: 'var(--ink-3)', fontFamily: 'var(--font-mono)', textTransform: 'uppercase', letterSpacing: 1 }}>Сегодня</div>
            {TODAY_DOSES.map(d => (
              <span key={d.n} className="pill active" style={{ fontSize: 10, padding: '3px 8px', gap: 4 }}>
                <Icon d={ICONS.check} size={9} stroke="#fff" sw={2.4} />
                {d.n}
              </span>
            ))}
            <span className="pill" style={{ fontSize: 10, padding: '3px 8px', color: 'var(--ink-3)' }}>+ ещё</span>
          </div>

          <div style={{ marginBottom: 14 }}>
            <SearchBar />
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Ваши препараты</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 14 }}>
            {RECENT_MEDS.map((m, i) => (
              <RecentMedRow key={m.name} m={m} takenToday={i < 2} />
            ))}
          </div>

          <div className="div-h" />
          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Категории</div>
          <CategoryList />
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// ── A2 ── full "сегодня" card section between title and recent list (more prominent)
function TherapyV5A2() {
  return (
    <Phone>
      <MiniBar back />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 4 }}>
          <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1, marginBottom: 12 }}>Препарат</div>
          <SearchBar />

          {/* today section as full card */}
          <div style={{
            marginTop: 14, marginBottom: 14,
            padding: 12,
            background: 'rgba(74,125,94,0.06)',
            border: '1px solid rgba(74,125,94,0.2)',
            borderRadius: 12,
          }}>
            <div className="row" style={{ marginBottom: 8 }}>
              <div className="h-eyebrow" style={{ color: 'var(--accent-2)' }}>Сегодня · приняли {TODAY_DOSES.length}</div>
              <div className="spacer" />
              <div className="annot" style={{ fontSize: 9 }}>пт, 24 апр</div>
            </div>
            {TODAY_DOSES.map((d, i) => (
              <div key={d.n} className="row" style={{
                padding: '6px 0',
                borderTop: i === 0 ? 'none' : '1px solid rgba(74,125,94,0.15)',
              }}>
                <Icon d={ICONS.check} size={12} stroke="var(--accent-2)" sw={2.2} />
                <div style={{ flex: 1, fontSize: 12 }}>{d.n}</div>
                <div className="annot" style={{ fontSize: 10 }}>{d.d}</div>
                <Icon d={ICONS.x} size={11} stroke="var(--ink-3)" />
              </div>
            ))}
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Ваши препараты</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 14 }}>
            {RECENT_MEDS.map((m, i) => (
              <RecentMedRow key={m.name} m={m} takenToday={i < 2} />
            ))}
          </div>

          <div className="div-h" />
          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Категории</div>
          <CategoryList />
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// ── A3 ── bottom sheet collapsed: "сегодня: 2" — tap to expand. Picker gets full screen.
function TherapyV5A3() {
  const [open, setOpen] = React.useState(false);
  return (
    <Phone>
      <MiniBar back />
      <div className="scr-scroll" style={{ flex: 1, paddingBottom: 56 }}>
        <div className="pad" style={{ paddingTop: 4 }}>
          <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1, marginBottom: 12 }}>Препарат</div>
          <SearchBar />

          <div className="h-eyebrow" style={{ marginTop: 18, marginBottom: 6 }}>Ваши препараты</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 14 }}>
            {RECENT_MEDS.map((m, i) => (
              <RecentMedRow key={m.name} m={m} takenToday={i < 2} />
            ))}
          </div>

          <div className="div-h" />
          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Категории</div>
          <CategoryList />
        </div>
      </div>

      {/* sticky bottom sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line)',
        boxShadow: '0 -4px 12px rgba(0,0,0,0.04)',
      }}>
        <div className="row" style={{ padding: '10px 16px', cursor: 'pointer' }}
          onClick={() => setOpen(!open)}>
          <Icon d={ICONS.chevD} size={14} stroke="var(--ink-3)"
            style={{ transform: open ? 'rotate(0deg)' : 'rotate(180deg)' }} />
          <div style={{ flex: 1, fontSize: 12 }}>
            <span className="num" style={{ fontWeight: 600 }}>Сегодня: {TODAY_DOSES.length}</span>
            <span style={{ color: 'var(--ink-3)', marginLeft: 6 }}>· {TODAY_DOSES.map(d => d.n).join(', ')}</span>
          </div>
          <div className="annot" style={{ fontSize: 9 }}>пт, 24 апр</div>
        </div>
        {open && (
          <div style={{ padding: '0 16px 12px' }}>
            {TODAY_DOSES.map(d => (
              <div key={d.n} className="row" style={{ padding: '6px 0' }}>
                <Icon d={ICONS.check} size={12} stroke="var(--accent-2)" sw={2.2} />
                <div style={{ flex: 1, fontSize: 12 }}>{d.n}</div>
                <div className="annot" style={{ fontSize: 10 }}>{d.d}</div>
                <Icon d={ICONS.x} size={11} stroke="var(--ink-3)" />
              </div>
            ))}
          </div>
        )}
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// ── A4 ── inline counter beside title: "Сегодня · 2"  — minimalist, expands on tap to mini list
function TherapyV5A4() {
  return (
    <Phone>
      <MiniBar back />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 4 }}>
          <div className="row" style={{ alignItems: 'baseline', marginBottom: 10 }}>
            <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1 }}>Препарат</div>
            <div className="spacer" />
            <div className="row" style={{ gap: 4, fontSize: 11, color: 'var(--ink-2)' }}>
              <Icon d={ICONS.check} size={11} stroke="var(--accent-2)" sw={2.2} />
              <span>сегодня <span className="num" style={{ fontWeight: 600, color: 'var(--ink)' }}>{TODAY_DOSES.length}</span></span>
              <Icon d={ICONS.chevR} size={10} stroke="var(--ink-3)" />
            </div>
          </div>

          {/* tiny inline list under counter, no boxing */}
          <div style={{ marginBottom: 14, paddingLeft: 0 }}>
            {TODAY_DOSES.map((d, i) => (
              <div key={d.n} style={{
                fontSize: 11, color: 'var(--ink-2)',
                padding: '4px 0',
                borderTop: i === 0 ? 'none' : '1px dashed var(--line-2)',
              }}>
                <span className="num" style={{ color: 'var(--ink-3)', marginRight: 8, fontSize: 9 }}>{String(i + 1).padStart(2, '0')}</span>
                {d.n}
                <span className="annot" style={{ marginLeft: 6, fontSize: 10 }}>{d.d}</span>
              </div>
            ))}
          </div>

          <SearchBar />

          <div className="h-eyebrow" style={{ marginTop: 18, marginBottom: 6 }}>Ваши препараты</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 14 }}>
            {RECENT_MEDS.map((m, i) => (
              <RecentMedRow key={m.name} m={m} takenToday={i < 2} />
            ))}
          </div>

          <div className="div-h" />
          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Категории</div>
          <CategoryList />
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

Object.assign(window, {
  TherapyV5A1, TherapyV5A2, TherapyV5A3, TherapyV5A4,
  SearchBar, RecentMedRow, CategoryList, TODAY_DOSES, RECENT_MEDS,
});
