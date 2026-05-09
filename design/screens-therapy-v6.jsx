// ─── Therapy A3 (bottom sheet) — 4 layout explorations ─────────
// Same picker base, different sheet-collapsed + sheet-expanded treatments.

const _T_TODAY = [
  { n: 'Цетрин', d: '10 мг' },
  { n: 'Назонекс', d: '2 впр.' },
];
const _T_RECENT = [
  { name: 'Цетрин',   sub: 'Цетиризин · 10 мг · перорально', last: 'вчера',         count: 12 },
  { name: 'Назонекс', sub: 'Мометазон · спрей в нос',         last: 'сегодня',        count: 8 },
  { name: 'Опатанол', sub: 'Олопатадин · капли в глаза',      last: '3 дня назад',    count: 4 },
  { name: 'Сингуляр', sub: 'Монтелукаст · 10 мг',             last: 'неделю назад',   count: 2 },
];

function _PickerBody() {
  return (
    <>
      <SearchBar />
      <div className="h-eyebrow" style={{ marginTop: 18, marginBottom: 6 }}>Ваши препараты</div>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 14 }}>
        {_T_RECENT.map((m, i) => (
          <RecentMedRow key={m.name} m={m} takenToday={i < 2} />
        ))}
      </div>
      <div className="div-h" />
      <div className="h-eyebrow" style={{ marginBottom: 6 }}>Категории</div>
      <CategoryList />
    </>
  );
}

// Reusable wrapper — picker scrolls in upper area, sheet is fixed at bottom
function _A3Wrap({ sheet, children, expanded = false }) {
  return (
    <Phone>
      {/* combined header — back chevron + title on the same line, no wasted row */}
      <div className="row" style={{
        height: 44, padding: '0 12px', gap: 8,
        borderBottom: '1px solid var(--line-2)',
      }}>
        <Icon d={ICONS.chevR} size={16} stroke="var(--ink-2)" sw={1.6}
          style={{ transform: 'rotate(180deg)' }} />
        <div className="h-display" style={{ fontSize: 18, lineHeight: 1 }}>Препарат</div>
      </div>
      <div className="scr-scroll" style={{ flex: 1, paddingBottom: expanded ? 0 : 60 }}>
        <div className="pad" style={{ paddingTop: 12 }}>
          <_PickerBody />
        </div>
      </div>
      {sheet}
      <TabBar active="diary" />
    </Phone>
  );
}

// ── T1: collapsed = chip row with checks + count
//     expanded = list with dose + remove
function TherapyT1Collapsed() {
  return _A3Wrap({
    sheet: (
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line)',
        boxShadow: '0 -4px 12px rgba(0,0,0,0.06)',
        padding: '10px 16px',
      }}>
        <div className="row" style={{ gap: 6 }}>
          <span className="num" style={{ fontSize: 11, fontWeight: 600 }}>{_T_TODAY.length}</span>
          <span style={{ fontSize: 11, color: 'var(--ink-3)' }}>сегодня</span>
          <div style={{ width: 1, height: 14, background: 'var(--line)' }} />
          {_T_TODAY.map(d => (
            <span key={d.n} className="pill active" style={{
              fontSize: 10, padding: '3px 8px', gap: 4,
            }}>
              <Icon d={ICONS.check} size={9} stroke="#fff" sw={2.4} />
              {d.n}
            </span>
          ))}
          <div className="spacer" />
          <Icon d={ICONS.chevD} size={14} stroke="var(--ink-3)" style={{ transform: 'rotate(180deg)' }} />
        </div>
      </div>
    ),
  });
}

function TherapyT1Expanded() {
  return _A3Wrap({
    expanded: true,
    sheet: (
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line)',
        boxShadow: '0 -8px 20px rgba(0,0,0,0.10)',
        borderRadius: '14px 14px 0 0',
        maxHeight: '55%',
      }}>
        <div style={{ padding: '8px 16px 0' }}>
          <div style={{ width: 36, height: 3, borderRadius: 2, background: 'var(--line)', margin: '0 auto 8px' }} />
        </div>
        <div className="row" style={{ padding: '0 16px 8px' }}>
          <div className="h-eyebrow">Сегодня · {_T_TODAY.length}</div>
          <div className="spacer" />
          <span className="annot" style={{ fontSize: 9 }}>пт, 24 апр</span>
        </div>
        <div style={{ padding: '0 16px 12px' }}>
          {_T_TODAY.map((d, i) => (
            <div key={d.n} className="row" style={{
              padding: '8px 0',
              borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              gap: 10,
            }}>
              <div style={{
                width: 22, height: 22, borderRadius: 11,
                background: 'var(--accent)',
                display: 'grid', placeItems: 'center', flexShrink: 0,
              }}>
                <Icon d={ICONS.check} size={11} stroke="#fff" sw={2.4} />
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 12, fontWeight: 500 }}>{d.n}</div>
                <div className="annot" style={{ fontSize: 10 }}>{d.d}</div>
              </div>
              <Icon d={ICONS.x} size={12} stroke="var(--ink-3)" />
            </div>
          ))}
          <div className="annot" style={{ fontSize: 10, marginTop: 8, color: 'var(--accent-2)' }}>+ добавить вручную</div>
        </div>
      </div>
    ),
  });
}

// ── T2: collapsed = "сегодня • Цетрин, Назонекс" inline text
//     expanded = card grid (2-col) of doses
function TherapyT2Collapsed() {
  return _A3Wrap({
    sheet: (
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line)',
        padding: '12px 16px',
      }}>
        <div className="row">
          <Icon d={ICONS.check} size={13} stroke="var(--accent-2)" sw={2.2} />
          <div style={{ fontSize: 12, color: 'var(--ink-2)', flex: 1 }}>
            <span style={{ fontWeight: 500, color: 'var(--ink)' }}>Сегодня · {_T_TODAY.length}</span>
            <span style={{ marginLeft: 8 }}>{_T_TODAY.map(d => d.n).join(' · ')}</span>
          </div>
          <Icon d={ICONS.chevD} size={13} stroke="var(--ink-3)" style={{ transform: 'rotate(180deg)' }} />
        </div>
      </div>
    ),
  });
}

function TherapyT2Expanded() {
  return _A3Wrap({
    expanded: true,
    sheet: (
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line)',
        boxShadow: '0 -8px 20px rgba(0,0,0,0.10)',
        borderRadius: '14px 14px 0 0',
      }}>
        <div style={{ padding: '8px 16px 0' }}>
          <div style={{ width: 36, height: 3, borderRadius: 2, background: 'var(--line)', margin: '0 auto 10px' }} />
        </div>
        <div className="row" style={{ padding: '0 16px 10px' }}>
          <div style={{ fontSize: 13, fontWeight: 500 }}>Сегодня</div>
          <div className="spacer" />
          <span className="annot" style={{ fontSize: 9 }}>пт, 24 апр</span>
        </div>
        <div style={{ padding: '0 16px 14px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
          {_T_TODAY.map(d => (
            <div key={d.n} className="card" style={{
              padding: 10, gap: 4, position: 'relative',
              border: '1px solid var(--accent)',
              background: 'rgba(74,125,94,0.05)',
            }}>
              <div className="row">
                <Icon d={ICONS.check} size={11} stroke="var(--accent-2)" sw={2.2} />
                <div className="spacer" />
                <Icon d={ICONS.x} size={11} stroke="var(--ink-3)" />
              </div>
              <div style={{ fontSize: 12, fontWeight: 500, marginTop: 4 }}>{d.n}</div>
              <div className="annot" style={{ fontSize: 10 }}>{d.d}</div>
            </div>
          ))}
          <div className="card" style={{
            padding: 10, gap: 4,
            border: '1px dashed var(--line)',
            background: 'transparent',
            display: 'grid', placeItems: 'center',
            color: 'var(--ink-3)', fontSize: 11,
          }}>
            + ещё
          </div>
        </div>
      </div>
    ),
  });
}

// ── T3: collapsed = severity-style colored ribbon (visual count)
//     expanded = list with dose count badge per drug (multi-dose support, no time)
function TherapyT3Collapsed() {
  return _A3Wrap({
    sheet: (
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line)',
      }}>
        {/* colored ribbon — segments per dose taken */}
        <div className="row" style={{ gap: 2, padding: '0 16px', marginTop: 8 }}>
          {_T_TODAY.map(d => (
            <div key={d.n} style={{ flex: 1, height: 4, borderRadius: 2, background: 'var(--accent)' }} />
          ))}
          {[0,1].map(i => (
            <div key={'e' + i} style={{ flex: 1, height: 4, borderRadius: 2, background: 'var(--line-2)' }} />
          ))}
        </div>
        <div className="row" style={{ padding: '6px 16px 12px' }}>
          <div style={{ fontSize: 11, color: 'var(--ink-2)' }}>
            <span className="num" style={{ fontWeight: 600 }}>{_T_TODAY.length}</span>
            {' '}доз сегодня
          </div>
          <div className="spacer" />
          <span style={{ fontSize: 11, color: 'var(--accent-2)' }}>посмотреть</span>
          <Icon d={ICONS.chevD} size={11} stroke="var(--accent-2)" style={{ transform: 'rotate(180deg)', marginLeft: 4 }} />
        </div>
      </div>
    ),
  });
}

function TherapyT3Expanded() {
  // group by drug; show dose count
  const byDrug = [{ n: 'Цетрин', d: '10 мг', count: 1 }, { n: 'Назонекс', d: '2 впр.', count: 1 }];
  return _A3Wrap({
    expanded: true,
    sheet: (
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line)',
        boxShadow: '0 -8px 20px rgba(0,0,0,0.10)',
        borderRadius: '14px 14px 0 0',
      }}>
        <div style={{ padding: '8px 16px 0' }}>
          <div style={{ width: 36, height: 3, borderRadius: 2, background: 'var(--line)', margin: '0 auto 10px' }} />
        </div>
        <div style={{ padding: '0 16px 6px' }}>
          <div className="row" style={{ gap: 2, marginBottom: 10 }}>
            {[...Array(_T_TODAY.length)].map((_, i) => (
              <div key={i} style={{ flex: 1, height: 4, borderRadius: 2, background: 'var(--accent)' }} />
            ))}
          </div>
          <div className="h-eyebrow" style={{ marginBottom: 8 }}>Сегодня · {_T_TODAY.length} {_T_TODAY.length === 1 ? 'доза' : 'дозы'}</div>
          {byDrug.map((d, i) => (
            <div key={d.n} className="row" style={{
              padding: '10px 0',
              borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              gap: 10,
            }}>
              <div className="leaf" style={{
                width: 26, height: 26, fontSize: 8,
                background: 'var(--accent)', color: '#fff', border: 'none',
              }}>{d.n[0]}</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 12, fontWeight: 500 }}>{d.n}</div>
                <div className="annot" style={{ fontSize: 10 }}>{d.d} · {d.count} {d.count === 1 ? 'раз' : 'раза'}</div>
              </div>
              <span className="pill" style={{ fontSize: 10, padding: '3px 8px' }}>+ ещё</span>
              <Icon d={ICONS.x} size={12} stroke="var(--ink-3)" />
            </div>
          ))}
        </div>
      </div>
    ),
  });
}

// ── T4: collapsed = avatar stack + count
//     expanded = stacked cards with notes input
function TherapyT4Collapsed() {
  return _A3Wrap({
    sheet: (
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line)',
        padding: '11px 16px',
      }}>
        <div className="row">
          {/* avatar stack */}
          <div className="row" style={{ gap: 0 }}>
            {_T_TODAY.map((d, i) => (
              <div key={d.n} style={{
                width: 22, height: 22, borderRadius: 11,
                background: 'var(--accent)',
                color: '#fff', fontSize: 9, fontWeight: 600,
                display: 'grid', placeItems: 'center',
                marginLeft: i === 0 ? 0 : -6,
                border: '2px solid var(--card)',
                fontFamily: 'var(--font-mono)',
              }}>{d.n[0]}</div>
            ))}
          </div>
          <div style={{ marginLeft: 8, fontSize: 12, flex: 1 }}>
            <span style={{ fontWeight: 500 }}>Сегодня</span>
            <span style={{ color: 'var(--ink-3)' }}> · {_T_TODAY.length} приём{_T_TODAY.length === 1 ? '' : 'а'}</span>
          </div>
          <span className="annot" style={{ fontSize: 10, color: 'var(--accent-2)' }}>детали</span>
          <Icon d={ICONS.chevR} size={11} stroke="var(--accent-2)" style={{ marginLeft: 4 }} />
        </div>
      </div>
    ),
  });
}

function TherapyT4Expanded() {
  return _A3Wrap({
    expanded: true,
    sheet: (
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 50,
        background: 'var(--card)',
        borderTop: '1px solid var(--line)',
        boxShadow: '0 -8px 20px rgba(0,0,0,0.10)',
        borderRadius: '14px 14px 0 0',
      }}>
        <div style={{ padding: '8px 16px 0' }}>
          <div style={{ width: 36, height: 3, borderRadius: 2, background: 'var(--line)', margin: '0 auto 10px' }} />
        </div>
        <div style={{ padding: '0 16px 12px' }}>
          <div className="row" style={{ marginBottom: 8 }}>
            <div style={{ fontSize: 13, fontWeight: 500 }}>Сегодня</div>
            <div className="spacer" />
            <span className="annot" style={{ fontSize: 9 }}>пт, 24 апр</span>
          </div>
          {_T_TODAY.map(d => (
            <div key={d.n} className="card" style={{
              padding: 10, marginBottom: 6,
              border: '1px solid var(--line-2)',
              gap: 4,
            }}>
              <div className="row">
                <div className="leaf" style={{
                  width: 22, height: 22, fontSize: 8,
                  background: 'var(--accent)', color: '#fff', border: 'none',
                }}>{d.n[0]}</div>
                <div style={{ flex: 1, fontSize: 12, fontWeight: 500 }}>{d.n}</div>
                <div className="annot" style={{ fontSize: 10 }}>{d.d}</div>
                <Icon d={ICONS.x} size={11} stroke="var(--ink-3)" style={{ marginLeft: 6 }} />
              </div>
              <div className="annot" style={{ fontSize: 10, color: 'var(--ink-3)', marginTop: 2 }}>+ заметка</div>
            </div>
          ))}
        </div>
      </div>
    ),
  });
}

Object.assign(window, {
  TherapyT1Collapsed, TherapyT1Expanded,
  TherapyT2Collapsed, TherapyT2Expanded,
  TherapyT3Collapsed, TherapyT3Expanded,
  TherapyT4Collapsed, TherapyT4Expanded,
});
