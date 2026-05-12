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

// ─── Drilldown into category — browse + add ─────────────────
const P_CATEGORY_DRUGS = [
  { name: 'Цетрин',     active: 'Цетиризин',    note: 'неседативный · 10 мг', rx: false, taking: true },
  { name: 'Зиртек',     active: 'Цетиризин',    note: 'неседативный · 10 мг', rx: false },
  { name: 'Кларитин',   active: 'Лоратадин',    note: 'неседативный · 10 мг', rx: false },
  { name: 'Эриус',      active: 'Дезлоратадин', note: 'неседативный · 5 мг',  rx: true },
  { name: 'Телфаст',    active: 'Фексофенадин', note: 'неседативный · 120 мг', rx: true },
  { name: 'Супрастин',  active: 'Хлоропирамин', note: 'седативный · 25 мг',   rx: false },
  { name: 'Аллегра',    active: 'Фексофенадин', note: 'неседативный · 180 мг', rx: false },
];

function PCategoryHeader({ title = 'Антигистаминные', sub }) {
  return (
    <div style={{ borderBottom: '1px solid var(--line-2)' }}>
      <div className="row" style={{ height: 48, padding: '0 14px', gap: 10 }}>
        <PIcon d={P_ICONS.back} size={18} stroke="var(--ink-2)" sw={1.6} />
        <div className="p-display" style={{ fontSize: 18, lineHeight: 1, flex: 1 }}>{title}</div>
        <PIcon d={P_ICONS.search} size={17} stroke="var(--ink-2)" sw={1.6} />
      </div>
      {sub && (
        <div style={{ padding: '0 14px 10px', fontSize: 11, color: 'var(--ink-3)' }}>{sub}</div>
      )}
    </div>
  );
}

function PCategoryFilterRow() {
  const filters = [
    { l: 'Все', on: true },
    { l: 'Без рецепта' },
    { l: 'Неседативные' },
    { l: 'Длительные' },
  ];
  return (
    <div style={{
      display: 'flex', gap: 6, padding: '12px 14px 4px',
      overflowX: 'auto', whiteSpace: 'nowrap',
    }}>
      {filters.map(f => (
        <span key={f.l} className={'p-pill ' + (f.on ? 'active' : '')}
          style={{ fontSize: 11, padding: '5px 11px' }}>{f.l}</span>
      ))}
    </div>
  );
}

function PDrugRow({ d, selected = false, taking = false }) {
  return (
    <div className="row" style={{
      padding: '12px 14px',
      borderRadius: 12,
      border: selected ? '1.5px solid var(--accent)' : '1px solid var(--line-2)',
      background: selected ? 'var(--accent-light)' : 'var(--card)',
      gap: 10,
      boxShadow: selected ? '0 2px 8px rgba(61,122,90,0.10)' : 'none',
    }}>
      <div className="p-leaf" style={{
        width: 30, height: 30, fontSize: 9,
        background: taking ? 'var(--accent)' : 'var(--paper-2)',
        color: taking ? '#fff' : 'var(--ink-3)',
        border: taking ? 'none' : '1px solid var(--line-2)',
      }}>
        {taking ? <PIcon d={P_ICONS.check} size={13} stroke="#fff" sw={2.4} /> : d.name[0]}
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div className="row" style={{ gap: 6 }}>
          <div style={{ fontSize: 13, fontWeight: 500 }}>{d.name}</div>
          {d.rx && (
            <span style={{
              fontSize: 9, fontWeight: 600,
              padding: '1px 6px', borderRadius: 4,
              background: 'var(--paper-2)', color: 'var(--ink-3)',
              fontFamily: 'var(--font-mono)', letterSpacing: 0.4,
            }}>Rx</span>
          )}
        </div>
        <div className="p-annot" style={{ fontSize: 10, marginTop: 1 }}>{d.active}</div>
        <div className="p-annot" style={{ fontSize: 9, marginTop: 2, color: 'var(--ink-3)' }}>{d.note}</div>
      </div>
      <div style={{
        width: 28, height: 28, borderRadius: 14,
        border: selected || taking ? 'none' : '1px solid var(--line)',
        background: selected ? 'var(--accent)' : taking ? 'var(--paper-2)' : 'transparent',
        display: 'grid', placeItems: 'center', flexShrink: 0,
      }}>
        {selected ? (
          <PIcon d={P_ICONS.check} size={13} stroke="#fff" sw={2.4} />
        ) : taking ? (
          <PIcon d={P_ICONS.check} size={12} stroke="var(--accent-2)" sw={2.2} />
        ) : (
          <PIcon d={P_ICONS.plus || 'M5 12h14M12 5v14'} size={13} stroke="var(--ink-2)" sw={1.6} />
        )}
      </div>
    </div>
  );
}

// Add P_ICONS.plus for safety in case shared.jsx doesn't export it
if (typeof P_ICONS !== 'undefined' && !P_ICONS.plus) {
  P_ICONS.plus = 'M5 12h14M12 5v14';
}

// Therapy category — drilldown after tapping a category
function PTherapyCategory() {
  return (
    <PPhone>
      <PCategoryHeader sub="системное действие · 7 препаратов" />
      <PCategoryFilterRow />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 8 }}>
          <div className="p-eyebrow" style={{ marginTop: 8, marginBottom: 8 }}>Препараты</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {P_CATEGORY_DRUGS.map(d => (
              <PDrugRow key={d.name} d={d} taking={d.taking} />
            ))}
          </div>
          <div style={{
            marginTop: 14, padding: '12px 14px',
            border: '1.5px dashed var(--line)',
            borderRadius: 12, textAlign: 'center',
            fontSize: 12, color: 'var(--ink-3)',
          }}>
            Не нашли препарат? <span style={{ color: 'var(--accent-2)', fontWeight: 500 }}>Добавить вручную</span>
          </div>
        </div>
      </div>
      <PTabBar active="diary" />
    </PPhone>
  );
}

// Therapy — selection in the group (one drug picked, sticky CTA appears)
function PTherapySelectedInGroup() {
  return (
    <PPhone>
      <PCategoryHeader sub="системное действие · 7 препаратов" />
      <PCategoryFilterRow />
      <div className="scr-scroll" style={{ flex: 1, paddingBottom: 80 }}>
        <div className="pad" style={{ paddingTop: 8 }}>
          <div className="p-eyebrow" style={{ marginTop: 8, marginBottom: 8 }}>Препараты</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {P_CATEGORY_DRUGS.map((d, i) => (
              <PDrugRow key={d.name} d={d} taking={d.taking} selected={i === 2} />
            ))}
          </div>
        </div>
      </div>

      {/* Sticky "add to therapy" CTA appears after selection */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line-2)',
        boxShadow: 'var(--shadow-sheet)',
        padding: '12px 14px',
      }}>
        <div className="row" style={{ marginBottom: 10 }}>
          <div className="p-leaf" style={{
            width: 28, height: 28, fontSize: 10,
            background: 'var(--accent)', color: '#fff', border: 'none',
          }}>К</div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: 13, fontWeight: 600 }}>Кларитин</div>
            <div className="p-annot" style={{ fontSize: 10 }}>Лоратадин · 10 мг</div>
          </div>
          <PIcon d={P_ICONS.x} size={14} stroke="var(--ink-3)" sw={1.6} />
        </div>
        <div className="row" style={{ gap: 8 }}>
          <div style={{
            flex: 1, padding: '11px 0', borderRadius: 10,
            background: 'var(--paper-2)', textAlign: 'center',
            fontSize: 12, fontWeight: 500, color: 'var(--ink-2)',
          }}>Подробнее</div>
          <div style={{
            flex: 1.4, padding: '11px 0', borderRadius: 10,
            background: 'var(--accent)', textAlign: 'center',
            fontSize: 12, fontWeight: 600, color: '#fff',
          }}>Добавить в терапию</div>
        </div>
      </div>

      <PTabBar active="diary" />
    </PPhone>
  );
}

// Therapy — empty state (user has no meds yet)
function PTherapyEmpty() {
  return (
    <PPhone>
      <div className="row" style={{
        height: 48, padding: '0 14px', gap: 10,
        borderBottom: '1px solid var(--line-2)',
      }}>
        <PIcon d={P_ICONS.back} size={18} stroke="var(--ink-2)" sw={1.6} />
        <div className="p-display" style={{ fontSize: 18, lineHeight: 1 }}>Терапия</div>
      </div>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          <PSearchBar />

          <div style={{
            marginTop: 22, padding: '24px 18px',
            border: '1.5px dashed var(--line)',
            borderRadius: 16, textAlign: 'center',
            background: 'var(--paper)',
          }}>
            <div style={{
              width: 44, height: 44, borderRadius: 22,
              margin: '0 auto 12px',
              background: 'var(--accent-light)',
              display: 'grid', placeItems: 'center',
            }}>
              <PIcon d="M12 5v14M5 12h14" size={20} stroke="var(--accent)" sw={1.8} />
            </div>
            <div className="p-display" style={{ fontSize: 17, marginBottom: 4 }}>Список препаратов пуст</div>
            <div style={{ fontSize: 12, color: 'var(--ink-2)', lineHeight: 1.5, marginBottom: 14 }}>
              Добавьте препарат, чтобы фиксировать приёмы и видеть, что работает.
            </div>
            <div style={{
              display: 'inline-block',
              padding: '10px 20px', borderRadius: 10,
              background: 'var(--accent)', color: '#fff',
              fontSize: 12, fontWeight: 600,
            }}>Добавить препарат</div>
          </div>

          <div className="p-eyebrow" style={{ marginTop: 22, marginBottom: 8 }}>Категории</div>
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
      <PTabBar active="diary" />
    </PPhone>
  );
}

// Therapy — manual add (custom drug not in catalog)
function PTherapyAddManual() {
  const fields = [
    { l: 'Название', v: '', placeholder: 'напр. Эриус' },
    { l: 'Форма выпуска', v: '', placeholder: 'таблетки / спрей / капли' },
    { l: 'Действующее вещество', v: '', placeholder: 'необязательно' },
    { l: 'Доза', v: '', placeholder: '10 мг' },
  ];
  return (
    <PPhone>
      <div className="row" style={{ padding: '14px 16px 10px', borderBottom: '1px solid var(--line-2)' }}>
        <PIcon d={P_ICONS.x} size={18} stroke="var(--ink-2)" sw={1.6} />
        <div className="spacer" style={{ flex: 1 }} />
        <span style={{ fontSize: 13, color: 'var(--ink-3)', fontWeight: 500 }}>Сохранить</span>
      </div>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          <div className="p-display" style={{ fontSize: 22, marginBottom: 4 }}>Новый препарат</div>
          <div className="p-annot" style={{ fontSize: 10, marginBottom: 20 }}>
            заполните вручную — препарат не из каталога
          </div>

          {fields.map((f) => (
            <div key={f.l} style={{ marginBottom: 18 }}>
              <div className="p-eyebrow" style={{ marginBottom: 6 }}>{f.l}</div>
              <div className="row" style={{
                padding: '10px 0',
                borderBottom: '1.5px solid var(--line)',
              }}>
                <div style={{ flex: 1, fontSize: 14, color: 'var(--ink-3)', fontStyle: 'italic' }}>{f.placeholder}</div>
              </div>
            </div>
          ))}

          <div className="p-eyebrow" style={{ marginTop: 4, marginBottom: 10 }}>Категория</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6, marginBottom: 22 }}>
            {[
              { l: 'Антигистаминные', on: true },
              { l: 'ГКС' },
              { l: 'Капли в глаза' },
              { l: 'Спрей в нос' },
              { l: 'Бронхи' },
              { l: 'Другое' },
            ].map(c => (
              <span key={c.l} className={'p-pill ' + (c.on ? 'active' : '')}
                style={{ fontSize: 11, padding: '6px 12px' }}>{c.l}</span>
            ))}
          </div>

          <div style={{
            padding: '12px 14px',
            background: 'var(--paper-2)', borderRadius: 12,
            fontSize: 11, color: 'var(--ink-2)', lineHeight: 1.5,
          }}>
            Препарат появится в вашем списке. Доза, частота и расписание — на следующем шаге.
          </div>
        </div>
      </div>
      <PTabBar active="diary" />
    </PPhone>
  );
}

Object.assign(window, {
  PRecentMedRow, PTherapyCollapsed, PTherapyExpanded, PTherapyForm,
  PTherapyCategory, PTherapySelectedInGroup, PTherapyEmpty, PTherapyAddManual,
  PDrugRow, PCategoryHeader, PCategoryFilterRow,
  P_RECENT_MEDS, P_TODAY_DOSES, P_CATEGORY_DRUGS,
});
