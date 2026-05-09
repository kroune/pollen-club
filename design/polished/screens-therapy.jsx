// Polished Therapy screens — picker with bottom sheet + form

function PRecentMedRow({ m, takenToday = false }) {
  return (
    <div className="row" style={{
      padding: '12px 14px',
      border: takenToday ? '1.5px solid var(--accent)' : '1px solid var(--line-2)',
      borderRadius: 12,
      background: takenToday ? 'var(--accent-light)' : 'var(--card)',
      gap: 10,
      boxShadow: takenToday ? '0 2px 8px rgba(61,122,90,0.08)' : 'none',
      transition: 'all 0.15s',
    }}>
      <div className="p-leaf" style={{
        width: 30, height: 30, fontSize: 9,
        background: takenToday ? 'var(--accent)' : 'var(--paper-2)',
        color: takenToday ? '#fff' : 'var(--ink-3)',
        border: takenToday ? 'none' : '1px solid var(--line-2)',
      }}>
        {takenToday ? <PIcon d={P_ICONS.check} size={13} stroke="#fff" sw={2.4} /> : m.name[0]}
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 13, fontWeight: 500 }}>{m.name}</div>
        <div className="p-annot" style={{ fontSize: 10, marginTop: 1 }}>{m.sub}</div>
        <div className="p-annot" style={{ fontSize: 9, marginTop: 2 }}>{m.count} приёмов · {m.last}</div>
      </div>
      <div className={'p-pill ' + (takenToday ? '' : 'active')} style={{ fontSize: 10, padding: '5px 12px' }}>
        {takenToday ? 'отменить' : '+ принять'}
      </div>
    </div>
  );
}

const P_RECENT_MEDS = [
  { name: 'Цетрин', sub: 'Цетиризин · 10 мг · перорально', last: 'вчера', count: 12 },
  { name: 'Назонекс', sub: 'Мометазон · спрей в нос', last: 'сегодня', count: 8 },
  { name: 'Опатанол', sub: 'Олопатадин · капли в глаза', last: '3 дня назад', count: 4 },
  { name: 'Сингуляр', sub: 'Монтелукаст · 10 мг', last: 'неделю назад', count: 2 },
];
const P_TODAY_DOSES = [
  { n: 'Цетрин', d: '10 мг' },
  { n: 'Назонекс', d: '2 впр.' },
];

// Therapy collapsed — avatar stack bottom sheet
function PTherapyCollapsed() {
  return (
    <PPhone>
      <div className="row" style={{
        height: 48, padding: '0 14px', gap: 10,
        borderBottom: '1px solid var(--line-2)',
      }}>
        <PIcon d={P_ICONS.back} size={18} stroke="var(--ink-2)" sw={1.6} />
        <div className="p-display" style={{ fontSize: 18, lineHeight: 1 }}>Препарат</div>
      </div>
      <div className="scr-scroll" style={{ flex: 1, paddingBottom: 60 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          <PSearchBar />
          <div className="p-eyebrow" style={{ marginTop: 18, marginBottom: 8 }}>Ваши препараты</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginBottom: 16 }}>
            {P_RECENT_MEDS.map((m, i) => (
              <PRecentMedRow key={m.name} m={m} takenToday={i < 2} />
            ))}
          </div>
          <div className="p-divider" />
          <div className="p-eyebrow" style={{ marginBottom: 8 }}>Категории</div>
          <div className="p-card" style={{ padding: 0 }}>
            {['Системного действия','Глаза','Нос','Бронхи','Кожа','Другие средства'].map((c, i) => (
              <div key={c} className="row" style={{
                padding: '12px 16px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1, fontSize: 13 }}>{c}</div>
                <PIcon d={P_ICONS.chevR} size={12} stroke="var(--ink-3)" sw={1.4} />
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Collapsed bottom sheet — avatar stack */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line-2)',
        boxShadow: 'var(--shadow-sheet)',
        padding: '12px 16px',
      }}>
        <div className="row">
          <div className="row" style={{ gap: 0 }}>
            {P_TODAY_DOSES.map((d, i) => (
              <div key={d.n} style={{
                width: 24, height: 24, borderRadius: 12,
                background: 'var(--accent)', color: '#fff',
                fontSize: 9, fontWeight: 600,
                display: 'grid', placeItems: 'center',
                marginLeft: i === 0 ? 0 : -7,
                border: '2px solid var(--card)',
                fontFamily: 'var(--font-mono)',
              }}>{d.n[0]}</div>
            ))}
          </div>
          <div style={{ marginLeft: 8, fontSize: 12, flex: 1 }}>
            <span style={{ fontWeight: 600 }}>Сегодня</span>
            <span style={{ color: 'var(--ink-3)' }}> · {P_TODAY_DOSES.length} приёма</span>
          </div>
          <span style={{ fontSize: 11, color: 'var(--accent-2)', fontWeight: 500 }}>детали</span>
          <PIcon d={P_ICONS.chevR} size={11} stroke="var(--accent-2)" sw={1.6} style={{ marginLeft: 4 }} />
        </div>
      </div>

      <PTabBar active="diary" />
    </PPhone>
  );
}

// Therapy expanded — cards with notes
function PTherapyExpanded() {
  return (
    <PPhone>
      <div className="row" style={{
        height: 48, padding: '0 14px', gap: 10,
        borderBottom: '1px solid var(--line-2)',
      }}>
        <PIcon d={P_ICONS.back} size={18} stroke="var(--ink-2)" sw={1.6} />
        <div className="p-display" style={{ fontSize: 18, lineHeight: 1 }}>Препарат</div>
      </div>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          <PSearchBar />
          <div className="p-eyebrow" style={{ marginTop: 18, marginBottom: 8 }}>Ваши препараты</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {P_RECENT_MEDS.slice(0, 2).map((m, i) => (
              <PRecentMedRow key={m.name} m={m} takenToday={i < 2} />
            ))}
          </div>
        </div>
      </div>

      {/* Expanded bottom sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line-2)',
        boxShadow: 'var(--shadow-sheet)',
        borderRadius: '16px 16px 0 0',
      }}>
        <div style={{ padding: '10px 16px 0' }}>
          <div style={{ width: 36, height: 4, borderRadius: 2, background: 'var(--line)', margin: '0 auto 12px' }} />
        </div>
        <div className="row" style={{ padding: '0 16px 10px' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>Сегодня</div>
          <div className="spacer" />
          <span className="p-annot">пт, 24 апр</span>
        </div>
        <div style={{ padding: '0 16px 14px' }}>
          {P_TODAY_DOSES.map(d => (
            <div key={d.n} className="p-card" style={{
              padding: '12px 14px', marginBottom: 8,
              border: '1px solid var(--line-2)',
              boxShadow: 'none',
            }}>
              <div className="row">
                <div className="p-leaf" style={{
                  width: 24, height: 24, fontSize: 9,
                  background: 'var(--accent)', color: '#fff', border: 'none',
                }}>{d.n[0]}</div>
                <div style={{ flex: 1, fontSize: 13, fontWeight: 500 }}>{d.n}</div>
                <div className="p-annot" style={{ fontSize: 11 }}>{d.d}</div>
                <PIcon d={P_ICONS.x} size={12} stroke="var(--ink-3)" sw={1.4} style={{ marginLeft: 6 }} />
              </div>
              <div className="p-annot" style={{ fontSize: 10, color: 'var(--ink-3)', marginTop: 6 }}>+ заметка</div>
            </div>
          ))}
        </div>
      </div>

      <PTabBar active="diary" />
    </PPhone>
  );
}

// Therapy form
function PTherapyForm() {
  const fields = [
    { l: 'Название препарата', v: 'Цетрин' },
    { l: 'Форма выпуска', v: 'Таблетки' },
    { l: 'Доза', v: '10 мг' },
    { l: 'Частота приёма', v: '1 раз в день' },
    { l: 'Начало приёма', v: '20 апр 2026' },
    { l: 'Действующее вещество', v: 'Цетиризин' },
  ];
  return (
    <PPhone>
      <div className="row" style={{ padding: '14px 16px 10px', borderBottom: '1px solid var(--line-2)' }}>
        <PIcon d={P_ICONS.x} size={18} stroke="var(--ink-2)" sw={1.6} />
        <div className="spacer" />
        <span style={{ fontSize: 13, color: 'var(--accent-2)', fontWeight: 600 }}>Сохранить</span>
      </div>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          <div className="p-display" style={{ fontSize: 22, marginBottom: 4 }}>Антигистаминные</div>
          <div className="p-annot" style={{ fontSize: 10, marginBottom: 20 }}>добавить препарат</div>
          {fields.map((f, i) => (
            <div key={f.l} style={{ marginBottom: 20 }}>
              <div className="p-eyebrow" style={{ marginBottom: 6 }}>{f.l}</div>
              <div className="row" style={{
                padding: '10px 0',
                borderBottom: '1.5px solid var(--line)',
              }}>
                <div style={{ flex: 1, fontSize: 15, color: i < 4 ? 'var(--ink)' : 'var(--ink-2)', fontWeight: 500 }}>{f.v}</div>
                <PIcon d={P_ICONS.chevD} size={14} stroke="var(--ink-3)" sw={1.4} />
              </div>
            </div>
          ))}
        </div>
      </div>
      <PTabBar active="diary" />
    </PPhone>
  );
}

Object.assign(window, {
  PRecentMedRow, PTherapyCollapsed, PTherapyExpanded, PTherapyForm,
  P_RECENT_MEDS, P_TODAY_DOSES,
});
