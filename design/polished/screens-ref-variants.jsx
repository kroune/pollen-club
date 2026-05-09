// Reference screen variations — exploring back + search + navigation

// ── Variant A: Large title, full-width search, no back button ──
// Navigation: user relies on system back or tab bar; clean top hierarchy
function PRefA() {
  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 18 }}>
          <div className="p-display" style={{ fontSize: 24 }}>Справочник</div>
          <div className="p-annot" style={{ fontSize: 10, marginTop: 4, marginBottom: 14 }}>
            Энциклопедия аллергенов
          </div>
          <PSearchBar placeholder="Найти аллерген…" />

          <div className="p-eyebrow" style={{ marginTop: 20, marginBottom: 10 }}>Все аллергены</div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {P_ALLERGENS.slice(0, 6).map(a => (
              <div key={a.code} className="p-card" style={{ padding: 14 }}>
                <div className="p-placeholder" style={{ height: 64, marginBottom: 10, borderRadius: 10 }}>{a.code}</div>
                <div style={{ fontSize: 13, fontWeight: 600 }}>{a.name}</div>
                <div className="p-annot" style={{ fontSize: 10, marginTop: 3 }}>
                  {a.sev > 0 ? <PSevLabel level={a.sev} /> : <span style={{ color: 'var(--ink-3)' }}>не активен</span>}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <PTabBar active="home" />
    </PPhone>
  );
}

// ── Variant B: Compact header bar, centred title, icon buttons ──
// Back is a small ← in top-left, search is an icon that could expand
function PRefB() {
  return (
    <PPhone>
      {/* Header bar */}
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
        <div style={{ flex: 1, textAlign: 'center' }}>
          <div style={{ fontSize: 15, fontWeight: 600, letterSpacing: -0.2 }}>Справочник</div>
        </div>
        <div style={{
          width: 32, height: 32, borderRadius: 10,
          background: 'var(--paper-2)',
          display: 'grid', placeItems: 'center',
        }}>
          <PIcon d={P_ICONS.search} size={15} stroke="var(--ink-2)" sw={1.6} />
        </div>
      </div>

      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {P_ALLERGENS.slice(0, 6).map(a => (
              <div key={a.code} className="p-card" style={{ padding: 14 }}>
                <div className="p-placeholder" style={{ height: 64, marginBottom: 10, borderRadius: 10 }}>{a.code}</div>
                <div style={{ fontSize: 13, fontWeight: 600 }}>{a.name}</div>
                <div className="p-annot" style={{ fontSize: 10, marginTop: 3 }}>
                  {a.sev > 0 ? <PSevLabel level={a.sev} /> : <span style={{ color: 'var(--ink-3)' }}>не активен</span>}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <PTabBar active="home" />
    </PPhone>
  );
}


// ── Variant C: Category pills + search icon in corner ──
// No back button; categories as the primary navigation within reference
function PRefC() {
  const cats = ['Все','Деревья','Злаки','Грибки','Сорные'];
  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '18px 16px 0' }}>
          <div className="row" style={{ gap: 10 }}>
            <div className="p-display" style={{ fontSize: 24, flex: 1 }}>Справочник</div>
            <div style={{
              width: 36, height: 36, borderRadius: 12,
              background: 'var(--paper-2)',
              display: 'grid', placeItems: 'center',
            }}>
              <PIcon d={P_ICONS.search} size={17} stroke="var(--ink-2)" sw={1.6} />
            </div>
          </div>
        </div>

        {/* Category pills */}
        <div style={{
          display: 'flex', gap: 6, padding: '14px 16px 4px',
          overflowX: 'auto',
        }}>
          {cats.map((c, i) => (
            <span key={c} className={'p-pill ' + (i === 0 ? 'active' : '')}>{c}</span>
          ))}
        </div>

        <div className="pad" style={{ paddingTop: 12 }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {P_ALLERGENS.slice(0, 6).map(a => (
              <div key={a.code} className="p-card" style={{ padding: 14 }}>
                <div className="p-placeholder" style={{ height: 64, marginBottom: 10, borderRadius: 10 }}>{a.code}</div>
                <div style={{ fontSize: 13, fontWeight: 600 }}>{a.name}</div>
                <div className="p-annot" style={{ fontSize: 10, marginTop: 3 }}>
                  {a.sev > 0 ? <PSevLabel level={a.sev} /> : <span style={{ color: 'var(--ink-3)' }}>не активен</span>}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <PTabBar active="home" />
    </PPhone>
  );
}


// ── Variant D: Dedicated tab in bottom nav ──
// Reference gets its own tab — no back button needed at all
function PRefTabBar({ active = 'ref' }) {
  const tabs = [
    { id: 'home', label: 'Прогноз', d: P_ICONS.home },
    { id: 'diary', label: 'Дневник', d: P_ICONS.smile },
    { id: 'ref', label: 'Справочник', d: 'M12 6.25a.75.75 0 0 1 .75.75v10a.75.75 0 0 1-.75.75H4a.75.75 0 0 1-.75-.75V7A.75.75 0 0 1 4 6.25zM4 3.25A.75.75 0 0 1 4.75 4v1.5h14.5V4a.75.75 0 0 1 1.5 0v13a.75.75 0 0 1-1.5 0v-1.5H4.75V17A.75.75 0 0 1 3.25 17V4A.75.75 0 0 1 4 3.25z' },
    { id: 'feed', label: 'Лента', d: P_ICONS.chat },
    { id: 'map', label: 'Карта', d: P_ICONS.pin },
  ];
  // Use a book icon for reference
  const bookIcon = 'M4 19V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v14l-8-4z';
  return (
    <div className="p-tabbar">
      {tabs.map(t => (
        <div key={t.id} className={'p-tab ' + (t.id === active ? 'active' : '')}>
          <div className="p-tab-icon">
            {t.id === 'ref' ? (
              <svg width={20} height={20} viewBox="0 0 24 24" fill="none"
                stroke={t.id === active ? 'var(--accent-2)' : 'var(--ink-3)'}
                strokeWidth={t.id === active ? 1.8 : 1.4}
                strokeLinecap="round" strokeLinejoin="round">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
                <path d="M8 7h8M8 11h5" />
              </svg>
            ) : (
              <PIcon d={t.d} size={20} sw={t.id === active ? 1.8 : 1.4}
                stroke={t.id === active ? 'var(--accent-2)' : 'var(--ink-3)'} />
            )}
          </div>
          <div>{t.label}</div>
        </div>
      ))}
    </div>
  );
}

function PRefD() {
  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 18 }}>
          <div className="p-display" style={{ fontSize: 24 }}>Справочник</div>
          <div className="p-annot" style={{ fontSize: 10, marginTop: 4, marginBottom: 14 }}>
            Энциклопедия аллергенов
          </div>
          <PSearchBar placeholder="Найти аллерген…" />

          <div className="p-eyebrow" style={{ marginTop: 20, marginBottom: 10 }}>Все аллергены</div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {P_ALLERGENS.slice(0, 6).map(a => (
              <div key={a.code} className="p-card" style={{ padding: 14 }}>
                <div className="p-placeholder" style={{ height: 64, marginBottom: 10, borderRadius: 10 }}>{a.code}</div>
                <div style={{ fontSize: 13, fontWeight: 600 }}>{a.name}</div>
                <div className="p-annot" style={{ fontSize: 10, marginTop: 3 }}>
                  {a.sev > 0 ? <PSevLabel level={a.sev} /> : <span style={{ color: 'var(--ink-3)' }}>не активен</span>}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <PRefTabBar active="ref" />
    </PPhone>
  );
}


// ── Variant E: List layout with section headers + sticky search ──
// Alphabetical list instead of grid; compact, scannable
function PRefE() {
  const grouped = [
    { letter: 'А', items: [
      { name: 'Альтернария', code: 'ALT', sev: 0 },
      { name: 'Амброзия', code: 'AMB', sev: 0 },
    ]},
    { letter: 'Б', items: [
      { name: 'Берёза', code: 'BIR', sev: 2 },
    ]},
    { letter: 'Д', items: [
      { name: 'Дуб', code: 'OAK', sev: 0 },
    ]},
    { letter: 'З', items: [
      { name: 'Злаки', code: 'GRA', sev: 0 },
    ]},
    { letter: 'К', items: [
      { name: 'Кладоспориум', code: 'CLA', sev: 0 },
    ]},
    { letter: 'М', items: [
      { name: 'Маревые', code: 'CHE', sev: 0 },
    ]},
    { letter: 'О', items: [
      { name: 'Ольха', code: 'ALN', sev: 0 },
      { name: 'Орешник', code: 'COR', sev: 0 },
    ]},
    { letter: 'П', items: [
      { name: 'Полынь', code: 'ART', sev: 0 },
    ]},
  ];
  return (
    <PPhone>
      {/* Sticky search header */}
      <div style={{
        padding: '14px 16px 10px',
        background: 'var(--paper)',
        flexShrink: 0,
      }}>
        <div className="row" style={{ gap: 10, marginBottom: 12 }}>
          <div className="p-display" style={{ fontSize: 22, flex: 1 }}>Справочник</div>
        </div>
        <PSearchBar placeholder="Найти аллерген…" />
      </div>

      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '4px 16px 16px' }}>
          {grouped.map(g => (
            <div key={g.letter}>
              <div style={{
                fontSize: 12, fontWeight: 600, color: 'var(--accent-2)',
                fontFamily: 'var(--font-mono)',
                padding: '12px 0 6px',
                borderBottom: '1px solid var(--line-2)',
              }}>{g.letter}</div>
              {g.items.map(a => (
                <div key={a.code} className="row" style={{
                  padding: '12px 0',
                  borderBottom: '1px solid var(--line-2)',
                  gap: 12,
                }}>
                  <div className="p-leaf" style={{ width: 36, height: 36, fontSize: 9 }}>{a.code}</div>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: 13, fontWeight: 500 }}>{a.name}</div>
                    <div className="p-annot" style={{ fontSize: 10, marginTop: 1 }}>
                      {a.sev > 0 ? P_SEVERITY[a.sev].toLowerCase() : 'не активен'}
                    </div>
                  </div>
                  {a.sev > 0 && <PSevDots level={a.sev} />}
                  <PIcon d={P_ICONS.chevR} size={13} stroke="var(--ink-3)" sw={1.4} />
                </div>
              ))}
            </div>
          ))}
        </div>
      </div>
      <PTabBar active="home" />
    </PPhone>
  );
}


// ── Variant F: Modal / bottom-sheet reference ──
// Opens as a full-height sheet with a drag handle, close ×, search embedded
function PRefF() {
  return (
    <PPhone>
      {/* Dimmed background suggesting previous screen underneath */}
      <div style={{
        position: 'absolute', inset: 0,
        background: 'var(--paper)',
        zIndex: 0,
      }}>
        <div style={{ padding: '18px 16px', opacity: 0.3 }}>
          <div className="p-display" style={{ fontSize: 24 }}>Прогноз</div>
          <div style={{ marginTop: 12, height: 60, borderRadius: 14, background: 'var(--paper-2)' }} />
          <div style={{ marginTop: 10, height: 40, borderRadius: 10, background: 'var(--paper-2)' }} />
          <div style={{ marginTop: 10, height: 40, borderRadius: 10, background: 'var(--paper-2)' }} />
        </div>
      </div>

      {/* The sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        top: 36,
        background: 'var(--card)',
        borderRadius: '20px 20px 0 0',
        boxShadow: 'var(--shadow-sheet)',
        zIndex: 1,
        display: 'flex', flexDirection: 'column',
        overflow: 'hidden',
      }}>
        {/* Handle + close row */}
        <div style={{ padding: '10px 16px 0', flexShrink: 0 }}>
          <div style={{
            width: 36, height: 4, borderRadius: 2,
            background: 'var(--line)', margin: '0 auto 12px',
          }} />
          <div className="row" style={{ gap: 10, marginBottom: 12 }}>
            <div className="p-display" style={{ fontSize: 22, flex: 1 }}>Справочник</div>
            <div style={{
              width: 30, height: 30, borderRadius: 15,
              background: 'var(--paper-2)',
              display: 'grid', placeItems: 'center',
            }}>
              <PIcon d={P_ICONS.x} size={14} stroke="var(--ink-2)" sw={1.8} />
            </div>
          </div>
          <PSearchBar placeholder="Найти аллерген…" />
        </div>

        <div className="scr-scroll" style={{ flex: 1, padding: '12px 16px 16px' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {P_ALLERGENS.slice(0, 6).map(a => (
              <div key={a.code} style={{
                padding: 14, borderRadius: 14,
                border: '1px solid var(--line-2)',
                background: 'var(--card)',
              }}>
                <div className="p-placeholder" style={{ height: 64, marginBottom: 10, borderRadius: 10 }}>{a.code}</div>
                <div style={{ fontSize: 13, fontWeight: 600 }}>{a.name}</div>
                <div className="p-annot" style={{ fontSize: 10, marginTop: 3 }}>
                  {a.sev > 0 ? <PSevLabel level={a.sev} /> : <span style={{ color: 'var(--ink-3)' }}>не активен</span>}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </PPhone>
  );
}

Object.assign(window, { PRefA, PRefB, PRefC, PRefD, PRefE, PRefF });
