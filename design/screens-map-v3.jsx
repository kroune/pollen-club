// ─── v3 MAP — edge-to-edge map, no top chrome, no useless hamburger.
// Trait filter labels float at top (M1-style); severity legend on right edge (M4-style).
// 4 variants explore different treatments of those two elements + secondary elements.

// (re-uses MAP_PINS, MapBase, MapPins, TRAITS, SEV_COLORS, SEV_LABELS from screens-map-v2.jsx)

// ── N1 ── single floating row of trait pills at top, severity column at right (compact)
function MapN1() {
  const counts = [0,0,0,0,0,0];
  MAP_PINS.forEach(p => counts[p.sev]++);

  return (
    <Phone>
      <div style={{ flex: 1, position: 'relative' }}>
        <MapBase>
          <MapPins />
        </MapBase>

        {/* floating trait pills — horizontally scrollable */}
        <div style={{
          position: 'absolute', top: 12, left: 0, right: 0,
          padding: '0 12px',
          display: 'flex', gap: 6, overflowX: 'auto',
        }}>
          {TRAITS.map((t, i) => (
            <span key={t.l} className={'pill ' + (i === 1 ? 'active' : '')}
              style={{
                fontSize: 10, padding: '4px 10px', flexShrink: 0,
                background: i === 1 ? 'var(--accent)' : '#fff',
                color: i === 1 ? '#fff' : 'var(--ink-2)',
                border: 'none',
                boxShadow: '0 1px 4px rgba(0,0,0,0.12)',
              }}>
              {t.l}
            </span>
          ))}
        </div>

        {/* severity column on right edge — color + count */}
        <div style={{
          position: 'absolute', right: 10, top: '50%',
          transform: 'translateY(-50%)',
          background: '#fff', borderRadius: 8, padding: '8px 6px',
          boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
          display: 'flex', flexDirection: 'column', gap: 6,
        }}>
          <div className="annot" style={{ fontSize: 7, textAlign: 'center' }}>УРОВ.</div>
          {[5,4,3,2,1].map(l => (
            <div key={l} className="row" style={{ gap: 4, fontSize: 9 }}>
              <div style={{
                width: 12, height: 12, borderRadius: '50% 50% 50% 0',
                transform: 'rotate(-45deg)', background: SEV_COLORS[l],
              }} />
              <div className="num" style={{ color: counts[l] ? 'var(--ink)' : 'var(--ink-3)', fontWeight: 500 }}>
                {counts[l]}
              </div>
            </div>
          ))}
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

// ── N2 ── trait pills wrap to two rows (no horizontal scroll, all visible at once);
//          severity on right shown as a stacked color bar (no numbers, just visual)
function MapN2() {
  return (
    <Phone>
      <div style={{ flex: 1, position: 'relative' }}>
        <MapBase>
          <MapPins />
        </MapBase>

        {/* floating trait pills — wrap, all visible */}
        <div style={{
          position: 'absolute', top: 12, left: 12, right: 12,
          display: 'flex', gap: 4, flexWrap: 'wrap',
        }}>
          {TRAITS.map((t, i) => (
            <span key={t.l} className={'pill ' + (i === 1 ? 'active' : '')}
              style={{
                fontSize: 10, padding: '3px 8px',
                background: i === 1 ? 'var(--accent)' : '#fff',
                color: i === 1 ? '#fff' : 'var(--ink-2)',
                border: 'none',
                boxShadow: '0 1px 4px rgba(0,0,0,0.12)',
              }}>
              {t.l}
            </span>
          ))}
        </div>

        {/* severity color bar — stacked, no numbers */}
        <div style={{
          position: 'absolute', right: 10, top: '50%',
          transform: 'translateY(-50%)',
          display: 'flex', flexDirection: 'column',
          background: '#fff', padding: 4,
          borderRadius: 6,
          boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
        }}>
          {[5,4,3,2,1].map((l, i) => (
            <div key={l} className="row" style={{ gap: 5 }}>
              <div style={{
                width: 14, height: 16, background: SEV_COLORS[l],
                borderRadius: i === 0 ? '3px 3px 0 0' : i === 4 ? '0 0 3px 3px' : 0,
              }} />
              <div style={{ fontSize: 8, color: 'var(--ink-3)', alignSelf: 'center' }}>
                {SEV_LABELS[l]}
              </div>
            </div>
          ))}
        </div>

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

// ── N3 ── trait pills as compact icon-style chips (just first letter + active highlight);
//          severity column = vertical color strip with active "уровень" label per active level
function MapN3() {
  const counts = [0,0,0,0,0,0];
  MAP_PINS.forEach(p => counts[p.sev]++);
  const activeIdx = [1, 3]; // АГ + Бризер pretend-active

  return (
    <Phone>
      <div style={{ flex: 1, position: 'relative' }}>
        <MapBase>
          <MapPins ringedTraits={['АГ', 'Бризер']} />
        </MapBase>

        {/* trait pills — show count on each */}
        <div style={{
          position: 'absolute', top: 12, left: 0, right: 0,
          padding: '0 12px',
          display: 'flex', gap: 5, overflowX: 'auto',
        }}>
          {TRAITS.map((t, i) => {
            const on = activeIdx.includes(i);
            return (
              <div key={t.l} style={{
                flexShrink: 0,
                padding: '4px 9px',
                background: on ? 'var(--accent)' : '#fff',
                color: on ? '#fff' : 'var(--ink-2)',
                borderRadius: 12, fontSize: 10,
                boxShadow: '0 1px 4px rgba(0,0,0,0.12)',
                display: 'flex', alignItems: 'center', gap: 5,
              }}>
                <span style={{ fontWeight: on ? 600 : 400 }}>{t.l}</span>
              </div>
            );
          })}
        </div>

        {/* full-height severity column on right edge */}
        <div style={{
          position: 'absolute', right: 0, top: 56, bottom: 56,
          width: 32,
          background: '#fff',
          borderRadius: '12px 0 0 12px',
          boxShadow: '-2px 0 6px rgba(0,0,0,0.10)',
          display: 'flex', flexDirection: 'column',
          padding: '6px 0',
        }}>
          <div className="annot" style={{ fontSize: 7, textAlign: 'center', marginBottom: 4 }}>УРОВ.</div>
          {[5,4,3,2,1].map(l => (
            <div key={l} className="col" style={{
              flex: 1, alignItems: 'center', justifyContent: 'center', gap: 2,
              borderTop: '1px solid var(--line-2)',
            }}>
              <div style={{
                width: 12, height: 12, borderRadius: '50% 50% 50% 0',
                transform: 'rotate(-45deg)', background: SEV_COLORS[l],
              }} />
              <div className="num" style={{
                fontSize: 8,
                color: counts[l] ? 'var(--ink-2)' : 'var(--ink-3)',
                fontWeight: 500,
              }}>{counts[l]}</div>
            </div>
          ))}
        </div>

        <div style={{
          position: 'absolute', bottom: 14, left: 12,
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

// ── N4 ── two-tier top: tiny allergen chip top-center (no header bar — just floating tag),
//          trait pills below it; severity = mini drop-shape legend right
function MapN4() {
  return (
    <Phone>
      <div style={{ flex: 1, position: 'relative' }}>
        <MapBase>
          <MapPins />
        </MapBase>

        {/* trait pills row at top */}
        <div style={{
          position: 'absolute', top: 12, left: 0, right: 0,
          padding: '0 12px',
          display: 'flex', gap: 5, overflowX: 'auto',
        }}>
          {TRAITS.map((t, i) => (
            <span key={t.l} className={'pill ' + (i === 1 ? 'active' : '')}
              style={{
                fontSize: 10, padding: '3px 8px', flexShrink: 0,
                background: i === 1 ? 'var(--accent)' : '#fff',
                color: i === 1 ? '#fff' : 'var(--ink-2)',
                border: 'none',
                boxShadow: '0 1px 4px rgba(0,0,0,0.12)',
              }}>
              {t.l}
            </span>
          ))}
        </div>

        {/* allergen chip — moved BELOW trait labels for visual balance */}
        <div style={{
          position: 'absolute', top: 42, left: '50%',
          transform: 'translateX(-50%)',
          padding: '3px 10px',
          background: '#fff', borderRadius: 12,
          boxShadow: '0 1px 4px rgba(0,0,0,0.12)',
          fontSize: 10, fontWeight: 500,
          display: 'flex', alignItems: 'center', gap: 5,
        }}>
          <Icon d={ICONS.leaf} size={11} stroke="var(--accent)" sw={1.6} />
          Берёза
          <Icon d={ICONS.chevD} size={9} stroke="var(--ink-3)" />
        </div>

        {/* severity legend — N2 stacked color bar */}
        <div style={{
          position: 'absolute', right: 10, top: '50%',
          transform: 'translateY(-50%)',
          display: 'flex', flexDirection: 'column',
          background: '#fff', padding: 4,
          borderRadius: 6,
          boxShadow: '0 2px 6px rgba(0,0,0,0.12)',
        }}>
          {[5,4,3,2,1].map((l, i) => (
            <div key={l} className="row" style={{ gap: 5 }}>
              <div style={{
                width: 14, height: 16, background: SEV_COLORS[l],
                borderRadius: i === 0 ? '3px 3px 0 0' : i === 4 ? '0 0 3px 3px' : 0,
              }} />
              <div style={{ fontSize: 8, color: 'var(--ink-3)', alignSelf: 'center' }}>
                {SEV_LABELS[l]}
              </div>
            </div>
          ))}
        </div>

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

Object.assign(window, { MapN1, MapN2, MapN3, MapN4 });
