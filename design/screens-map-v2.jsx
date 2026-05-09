// ─── v2 MAP — better header info, filters that mean something, legend with value
//
// Original problems:
//   - Header just says "Берёза. Карта рисков. 7 баллов" but the "7 баллов" is the
//     user's index — needs context. Filter pills sit on top of map blocking it.
//     Bottom legend "1-5" is divorced from pin appearance (pins encode SEVERITY
//     by COLOR + BEHAVIORAL TRAITS by filter — not just numbers).
//   - Filters (Друзья / АГ / АСИТ / Бризер / Дети / Маски / Побег / СижуДома)
//     are inclusion filters tied to participant traits — need to be reachable
//     but not dominate the map.
//
// All variants share the same map base + pin set.

const MAP_PINS = [
  { x: 22, y: 30, n: 3, sev: 3, traits: ['АГ'] },
  { x: 38, y: 42, n: 9, sev: 2, traits: ['Друзья'] },
  { x: 54, y: 22, n: 1, sev: 1, traits: ['АСИТ'] },
  { x: 64, y: 50, n: 11, sev: 3, traits: ['АГ', 'Бризер'] },
  { x: 30, y: 58, n: 2, sev: 3, traits: [] },
  { x: 70, y: 70, n: 6, sev: 3, traits: ['АГ'] },
  { x: 50, y: 75, n: 9, sev: 2, traits: ['Друзья', 'Маски'] },
  { x: 80, y: 38, n: 1, sev: 1, traits: ['СижуДома'] },
  { x: 18, y: 78, n: 4, sev: 4, traits: ['Побег'] },
  { x: 88, y: 60, n: 2, sev: 2, traits: ['Дети'] },
];

const SEV_COLORS = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
const SEV_LABELS = ['нулевой','низкий','средний','высокий','оч. выс.','экстра'];

// Reusable map base — soft contour placeholder.
function MapBase({ children }) {
  return (
    <div style={{ position: 'absolute', inset: 0, background: '#eef2ec' }}>
      <svg width="100%" height="100%" viewBox="0 0 260 400" style={{ position: 'absolute', inset: 0 }} preserveAspectRatio="none">
        <rect width="260" height="400" fill="#f0f4ed" />
        {[...Array(8)].map((_, i) => (
          <path key={i}
            d={`M 0 ${50 + i * 45} Q ${130 + (i % 2 ? 30 : -30)} ${30 + i * 45} 260 ${60 + i * 45}`}
            stroke="#dde5d6" fill="none" strokeWidth="1" />
        ))}
        <path d="M 0 200 L 260 200" stroke="#cdd5c5" strokeWidth="2" strokeDasharray="3 3" />
      </svg>
      {children}
    </div>
  );
}

function MapPins({ pins = MAP_PINS, ringedTraits = [] }) {
  return pins.map((p, i) => (
    <div key={i} style={{
      position: 'absolute',
      left: `${p.x}%`, top: `${p.y}%`,
      transform: 'translate(-50%, -100%)',
    }}>
      <div style={{
        width: 22, height: 22, borderRadius: '50% 50% 50% 0',
        transform: 'rotate(-45deg)',
        background: SEV_COLORS[p.sev],
        boxShadow: '0 2px 6px rgba(0,0,0,0.18)',
        display: 'grid', placeItems: 'center',
        outline: ringedTraits.length && p.traits.some(t => ringedTraits.includes(t))
          ? '2px solid var(--accent)' : 'none',
        outlineOffset: 1,
      }}>
        <div className="num" style={{ transform: 'rotate(45deg)', color: '#fff', fontSize: 10, fontWeight: 600 }}>{p.n}</div>
      </div>
    </div>
  ));
}

const TRAITS = [
  { l: 'Друзья',   d: 'метки друзей' },
  { l: 'АГ',       d: 'принимают антигистамины' },
  { l: 'АСИТ',     d: 'на иммунотерапии' },
  { l: 'Бризер',   d: 'есть очиститель' },
  { l: 'Дети',     d: 'аллергия у детей' },
  { l: 'Маски',    d: 'носят маску' },
  { l: 'Побег',    d: 'уехали из зоны' },
  { l: 'СижуДома', d: 'самоизоляция' },
];

// ── M1 ── floating context card (top-left), trait filters as horizontal scroll below,
//          severity legend as right-edge column WITH counts — every element earns its place
function MapM1() {
  // count by severity for legend
  const counts = [0,0,0,0,0,0];
  MAP_PINS.forEach(p => counts[p.sev]++);

  return (
    <Phone>
      <div style={{ flex: 1, position: 'relative' }}>
        <MapBase>
          <MapPins />
        </MapBase>

        {/* hamburger floating top-left */}
        <div style={{
          position: 'absolute', top: 10, left: 10,
          width: 32, height: 32, borderRadius: 16,
          background: '#fff', boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
          display: 'grid', placeItems: 'center',
        }}>
          <Icon d={ICONS.menu} size={16} stroke="var(--ink-2)" />
        </div>

        {/* context card top-center — what allergen + what's around me */}
        <div style={{
          position: 'absolute', top: 10, left: 52, right: 52,
          padding: '6px 10px',
          background: '#fff', borderRadius: 10,
          boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
          fontSize: 11,
        }}>
          <div className="row" style={{ gap: 6 }}>
            <span style={{ fontWeight: 600 }}>Берёза</span>
            <span style={{ color: 'var(--ink-3)' }}>·</span>
            <span style={{ color: 'var(--ink-2)' }}>средний у участников</span>
          </div>
          <div className="row" style={{ gap: 6, fontSize: 9, color: 'var(--ink-3)', marginTop: 1 }}>
            <span>{MAP_PINS.length} меток за сутки</span>
            <span>·</span>
            <span>радиус 30 км</span>
          </div>
        </div>

        {/* my-location floating top-right */}
        <div style={{
          position: 'absolute', top: 10, right: 10,
          width: 32, height: 32, borderRadius: 16,
          background: '#fff', boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
          display: 'grid', placeItems: 'center',
        }}>
          <Icon d={ICONS.loc} size={14} stroke="var(--accent)" />
        </div>

        {/* trait filters — horizontal scroll under context card */}
        <div style={{
          position: 'absolute', top: 56, left: 0, right: 0,
          padding: '0 10px',
          display: 'flex', gap: 6, overflowX: 'auto',
        }}>
          {TRAITS.map((t, i) => (
            <span key={t.l} className={'pill ' + (i === 1 ? 'active' : '')}
              style={{ fontSize: 10, padding: '4px 9px', flexShrink: 0,
                background: i === 1 ? 'var(--accent)' : '#fff',
                color: i === 1 ? '#fff' : 'var(--ink-2)',
                boxShadow: '0 1px 3px rgba(0,0,0,0.08)',
                border: 'none',
              }}>
              {t.l}
            </span>
          ))}
        </div>

        {/* severity legend as RIGHT-EDGE column with live counts */}
        <div style={{
          position: 'absolute', right: 8, top: '50%',
          transform: 'translateY(-50%)',
          background: '#fff', borderRadius: 8, padding: '8px 6px',
          boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
          display: 'flex', flexDirection: 'column', gap: 6,
        }}>
          <div className="annot" style={{ fontSize: 7, textAlign: 'center', writingMode: 'horizontal-tb' }}>УРОВЕНЬ</div>
          {[5,4,3,2,1].map(l => (
            <div key={l} className="row" style={{ gap: 4, fontSize: 9 }}>
              <div style={{ width: 12, height: 12, borderRadius: '50% 50% 50% 0', transform: 'rotate(-45deg)', background: SEV_COLORS[l] }} />
              <div className="num" style={{ color: counts[l] ? 'var(--ink)' : 'var(--ink-3)', fontWeight: 500 }}>{counts[l]}</div>
            </div>
          ))}
        </div>
      </div>
      <TabBar active="map" />
    </Phone>
  );
}

// ── M2 ── full-bleed map; collapsible bottom sheet replaces useless legend strip
function MapM2() {
  const [open, setOpen] = React.useState(false);
  const counts = [0,0,0,0,0,0];
  MAP_PINS.forEach(p => counts[p.sev]++);

  return (
    <Phone>
      <div style={{ flex: 1, position: 'relative' }}>
        <MapBase>
          <MapPins />
        </MapBase>

        {/* minimal floating chrome */}
        <div style={{
          position: 'absolute', top: 10, left: 10,
          width: 32, height: 32, borderRadius: 16,
          background: '#fff', boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
          display: 'grid', placeItems: 'center',
        }}>
          <Icon d={ICONS.menu} size={16} stroke="var(--ink-2)" />
        </div>
        <div style={{
          position: 'absolute', top: 10, right: 10,
          width: 32, height: 32, borderRadius: 16,
          background: '#fff', boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
          display: 'grid', placeItems: 'center',
        }}>
          <Icon d={ICONS.loc} size={14} stroke="var(--accent)" />
        </div>

        {/* bottom sheet */}
        <div style={{
          position: 'absolute', left: 0, right: 0, bottom: 0,
          background: '#fff',
          borderRadius: '16px 16px 0 0',
          boxShadow: '0 -4px 12px rgba(0,0,0,0.08)',
          maxHeight: open ? '70%' : 'auto',
          overflow: 'hidden',
        }}>
          {/* drag handle + summary line */}
          <div onClick={() => setOpen(!open)} style={{ padding: '8px 16px 10px', cursor: 'pointer' }}>
            <div style={{ width: 36, height: 3, borderRadius: 2, background: 'var(--line)', margin: '0 auto 8px' }} />
            <div className="row">
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, fontWeight: 500 }}>Берёза · ваш район</div>
                <div className="annot" style={{ fontSize: 10, marginTop: 1 }}>
                  средний у участников · {MAP_PINS.length} меток за сутки
                </div>
              </div>
              <Icon d={ICONS.chevD} size={14} stroke="var(--ink-3)"
                style={{ transform: open ? 'rotate(180deg)' : 'rotate(0)' }} />
            </div>
          </div>

          {open && (
            <div style={{ padding: '0 16px 16px' }}>
              {/* severity row */}
              <div className="h-eyebrow" style={{ marginBottom: 6 }}>Уровень пыления у участников</div>
              <div className="row" style={{ gap: 4, marginBottom: 14 }}>
                {[1,2,3,4,5].map(l => (
                  <div key={l} className="col" style={{
                    flex: 1, alignItems: 'center', padding: '6px 0',
                    background: counts[l] ? 'var(--paper-2)' : 'transparent',
                    border: '1px solid ' + (counts[l] ? 'var(--line-2)' : 'transparent'),
                    borderRadius: 6,
                  }}>
                    <div style={{ width: 12, height: 12, borderRadius: '50% 50% 50% 0', transform: 'rotate(-45deg)', background: SEV_COLORS[l], marginBottom: 4 }} />
                    <div className="num" style={{ fontSize: 11, fontWeight: 500 }}>{counts[l]}</div>
                    <div className="annot" style={{ fontSize: 8, marginTop: 1 }}>{SEV_LABELS[l]}</div>
                  </div>
                ))}
              </div>

              {/* trait filter — meaningful list with descriptions */}
              <div className="h-eyebrow" style={{ marginBottom: 6 }}>Показать только участников</div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 5 }}>
                {TRAITS.map((t, i) => (
                  <span key={t.l} className={'pill ' + (i === 1 ? 'active' : '')}
                    style={{ fontSize: 10, padding: '4px 9px' }}>
                    {t.l}
                  </span>
                ))}
              </div>
              <div className="annot" style={{ fontSize: 9, marginTop: 8 }}>
                напр. «АГ» — те, кто принимает антигистамины
              </div>
            </div>
          )}
        </div>
      </div>
      <TabBar active="map" />
    </Phone>
  );
}

// ── M3 ── proper top strip: context + meaningful summary; filters second row;
//          legend tucked behind tap-to-show "(?)" — keeps map maximal
function MapM3() {
  const [legendOpen, setLegendOpen] = React.useState(false);
  return (
    <Phone>
      {/* honest header strip */}
      <div style={{
        padding: '10px 14px 8px',
        background: 'var(--paper)',
        borderBottom: '1px solid var(--line-2)',
      }}>
        <div className="row" style={{ marginBottom: 8 }}>
          <Icon d={ICONS.menu} size={18} stroke="var(--ink-2)" />
          <div style={{ flex: 1, marginLeft: 12 }}>
            <div style={{ fontSize: 13, fontWeight: 500 }}>Берёза · средний</div>
            <div className="annot" style={{ fontSize: 10 }}>
              {MAP_PINS.length} меток за сутки · радиус 30 км
            </div>
          </div>
          <Icon d={ICONS.loc} size={16} stroke="var(--accent)" />
        </div>
        {/* trait filters row */}
        <div style={{ display: 'flex', gap: 5, overflowX: 'auto', paddingBottom: 2 }}>
          {TRAITS.map((t, i) => (
            <span key={t.l} className={'pill ' + (i === 1 ? 'active' : '')}
              style={{ fontSize: 10, padding: '3px 8px', flexShrink: 0 }}>
              {t.l}
            </span>
          ))}
        </div>
      </div>

      <div style={{ flex: 1, position: 'relative' }}>
        <MapBase>
          <MapPins ringedTraits={['АГ']} />
        </MapBase>

        {/* legend toggle bottom-right */}
        <div onClick={() => setLegendOpen(!legendOpen)} style={{
          position: 'absolute', bottom: 10, right: 10,
          padding: '6px 10px',
          background: '#fff', borderRadius: 14,
          boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
          fontSize: 10, color: 'var(--ink-2)',
          display: 'flex', alignItems: 'center', gap: 5,
          cursor: 'pointer',
        }}>
          {legendOpen ? <Icon d={ICONS.x} size={11} stroke="var(--ink-2)" /> : '?'}
          <span>обозначения</span>
        </div>

        {legendOpen && (
          <div className="card" style={{
            position: 'absolute', bottom: 50, right: 10, left: 10,
            padding: 12, fontSize: 11,
          }}>
            <div className="h-eyebrow" style={{ marginBottom: 6 }}>Цвет метки</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 4, marginBottom: 10 }}>
              {[1,2,3,4,5].map(l => (
                <div key={l} className="row" style={{ gap: 8 }}>
                  <div style={{ width: 14, height: 14, borderRadius: '50% 50% 50% 0', transform: 'rotate(-45deg)', background: SEV_COLORS[l], flexShrink: 0 }} />
                  <span style={{ color: 'var(--ink-2)' }}>{SEV_LABELS[l]}</span>
                </div>
              ))}
            </div>
            <div className="h-eyebrow" style={{ marginBottom: 6 }}>Цифра внутри</div>
            <div style={{ fontSize: 11, color: 'var(--ink-2)', lineHeight: 1.4 }}>
              Уровень самочувствия по 10-балльной шкале, как его отметил участник.
            </div>
          </div>
        )}
      </div>
      <TabBar active="map" />
    </Phone>
  );
}

// ── M4 ── severity color column on right edge IS the filter (drag = level range);
//          trait icons as left-edge column toggles. Map is maximal.
function MapM4() {
  return (
    <Phone>
      <MiniBar />
      <div style={{ flex: 1, position: 'relative' }}>
        <MapBase>
          <MapPins />
        </MapBase>

        {/* honest summary chip top */}
        <div style={{
          position: 'absolute', top: 10, left: '50%',
          transform: 'translateX(-50%)',
          padding: '4px 12px',
          background: '#fff', borderRadius: 12,
          boxShadow: '0 2px 6px rgba(0,0,0,0.10)',
          fontSize: 11, whiteSpace: 'nowrap',
        }}>
          <span style={{ fontWeight: 500 }}>Берёза</span>
          <span style={{ color: 'var(--ink-3)', margin: '0 6px' }}>·</span>
          <span style={{ color: 'var(--ink-2)' }}>{MAP_PINS.length} меток</span>
        </div>

        {/* LEFT edge — trait filter column */}
        <div style={{
          position: 'absolute', top: 50, left: 8,
          background: '#fff', borderRadius: 10, padding: 4,
          boxShadow: '0 2px 6px rgba(0,0,0,0.10)',
          display: 'flex', flexDirection: 'column', gap: 2,
        }}>
          {TRAITS.slice(0, 6).map((t, i) => (
            <div key={t.l} style={{
              padding: '5px 7px',
              background: i === 1 ? 'var(--accent)' : 'transparent',
              color: i === 1 ? '#fff' : 'var(--ink-2)',
              borderRadius: 6, fontSize: 9, fontWeight: 500,
              minWidth: 38, textAlign: 'center',
            }}>{t.l}</div>
          ))}
        </div>

        {/* RIGHT edge — severity color bar AS filter range slider */}
        <div style={{
          position: 'absolute', top: 50, right: 8,
          background: '#fff', borderRadius: 10, padding: '8px 4px',
          boxShadow: '0 2px 6px rgba(0,0,0,0.10)',
          display: 'flex', flexDirection: 'column', gap: 0,
          alignItems: 'center',
        }}>
          <div className="annot" style={{ fontSize: 7, marginBottom: 4 }}>УРОВ.</div>
          {[5,4,3,2,1].map((l, i) => (
            <div key={l} className="row" style={{ gap: 4 }}>
              <div style={{
                width: 14, height: 22, background: SEV_COLORS[l],
                opacity: l < 2 ? 0.25 : 1,  // illustrate filter range = 2-5
                borderRadius: i === 0 ? '4px 4px 0 0' : i === 4 ? '0 0 4px 4px' : 0,
              }} />
              <div className="num" style={{ fontSize: 9, color: l < 2 ? 'var(--ink-3)' : 'var(--ink)', width: 8 }}>{l}</div>
            </div>
          ))}
          {/* drag handles */}
          <div style={{
            position: 'absolute', top: 18, left: -4,
            width: 22, height: 6, borderRadius: 3,
            background: 'var(--accent)', boxShadow: '0 1px 3px rgba(0,0,0,0.2)',
          }} />
          <div style={{
            position: 'absolute', bottom: 38, left: -4,
            width: 22, height: 6, borderRadius: 3,
            background: 'var(--accent)', boxShadow: '0 1px 3px rgba(0,0,0,0.2)',
          }} />
        </div>

        {/* my-location bottom-right */}
        <div style={{
          position: 'absolute', bottom: 14, right: 12,
          width: 36, height: 36, borderRadius: 18,
          background: '#fff', boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
          display: 'grid', placeItems: 'center',
        }}>
          <Icon d={ICONS.loc} size={16} stroke="var(--accent)" />
        </div>
      </div>
      <TabBar active="map" />
    </Phone>
  );
}

Object.assign(window, {
  MapM1, MapM2, MapM3, MapM4,
  MapBase, MapPins, MAP_PINS, TRAITS, SEV_COLORS, SEV_LABELS,
});
