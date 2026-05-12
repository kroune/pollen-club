// Allergen detail info — the encyclopedia modal/page when tapping ⓘ on an allergen card
// DATA: name, illustration (placeholder), and a long-form text description only.

const BIRCH_DESC = 'Берёза (Betula) — род листопадных деревьев и кустарников семейства Берёзовые. Берёза широко распространена в Северном полушарии; на территории России принадлежит к числу наиболее распространённых древесных пород. Пыльца берёзы — основной весенний аллерген. В средней полосе России первые пыльцевые зёрна обычно появляются в атмосфере в третьей декаде апреля, массовое цветение наблюдается в конце апреля — начале мая. Интенсивное пыление берёзы длится около 3 недель, в пик пыления концентрация пыльцы берёзы в атмосфере может достигать 20 000 пыльцевых зёрен в одном кубическом метре воздуха за сутки.\n\nБерёза — растение однодомное, но цветки у неё раздельнополые и собраны в разные соцветия. Цветение мужских и женских соцветий происходит одновременно, иначе бы опыление было невозможно. Мужские серёжки после цветения отпадают, а женские — остаются на дереве до созревания семян.';


// ═══════════════════════════════════════════
// VARIANT A — Full-screen page, clean scroll
// ═══════════════════════════════════════════
function PAllergenInfoA() {
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
          <div style={{ fontSize: 15, fontWeight: 600, letterSpacing: -0.2 }}>Берёза</div>
        </div>
        <div style={{ width: 32 }} />
      </div>

      <div className="scr-scroll" style={{ flex: 1 }}>
        {/* Illustration area */}
        <div className="p-placeholder" style={{
          height: 180, margin: '16px 16px 0',
          borderRadius: 16, fontSize: 10,
        }}>ботаническая иллюстрация</div>

        <div className="pad" style={{ paddingTop: 16 }}>
          <div className="p-display" style={{ fontSize: 22, marginBottom: 4 }}>Берёза</div>
          <div className="p-annot" style={{ fontSize: 10, marginBottom: 16 }}>Betula · Берёзовые</div>
          <div style={{ height: 1, background: 'var(--line-2)', marginBottom: 16 }} />
          <div className="p-body" style={{ lineHeight: 1.65, whiteSpace: 'pre-line', paddingBottom: 24 }}>
            {BIRCH_DESC}
          </div>
        </div>
      </div>
    </PPhone>
  );
}


// ═══════════════════════════════════════════
// VARIANT B — Bottom sheet over reference grid
// ═══════════════════════════════════════════
function PAllergenInfoB() {
  return (
    <PPhone>
      {/* Dimmed reference grid behind */}
      <div style={{ position: 'absolute', inset: 0, background: 'var(--paper)', zIndex: 0 }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
          padding: '12px 14px 10px',
          background: 'var(--card)',
          borderBottom: '1px solid var(--line-2)',
        }}>
          <div style={{ width: 32, height: 32, borderRadius: 10, background: 'var(--paper-2)' }} />
          <div style={{ flex: 1, textAlign: 'center' }}>
            <div style={{ fontSize: 15, fontWeight: 600, opacity: 0.4 }}>Справочник</div>
          </div>
          <div style={{ width: 32, height: 32, borderRadius: 10, background: 'var(--paper-2)' }} />
        </div>
        <div style={{ padding: 16, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, opacity: 0.25 }}>
          {P_ALLERGENS.slice(0, 4).map(a => (
            <div key={a.code} className="p-card" style={{ padding: 14, height: 100 }} />
          ))}
        </div>
      </div>

      {/* Scrim */}
      <div style={{ position: 'absolute', inset: 0, background: 'rgba(27,31,29,0.35)', zIndex: 1 }} />

      {/* Bottom sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0, top: 50,
        background: 'var(--card)',
        borderRadius: '20px 20px 0 0',
        boxShadow: 'var(--shadow-sheet)',
        zIndex: 2,
        display: 'flex', flexDirection: 'column',
        overflow: 'hidden',
      }}>
        {/* Handle + header */}
        <div style={{ padding: '10px 16px 0', flexShrink: 0 }}>
          <div style={{
            width: 36, height: 4, borderRadius: 2,
            background: 'var(--line)', margin: '0 auto 14px',
          }} />
          <div className="row" style={{ gap: 14, marginBottom: 14 }}>
            <div className="p-placeholder" style={{
              width: 52, height: 52, borderRadius: 16, flexShrink: 0, fontSize: 9,
            }}>BIR</div>
            <div style={{ flex: 1 }}>
              <div className="p-display" style={{ fontSize: 22 }}>Берёза</div>
              <div className="p-annot" style={{ fontSize: 10, marginTop: 2 }}>Betula · Берёзовые</div>
            </div>
            <div style={{
              width: 30, height: 30, borderRadius: 15,
              background: 'var(--paper-2)',
              display: 'grid', placeItems: 'center',
            }}>
              <PIcon d={P_ICONS.x} size={14} stroke="var(--ink-2)" sw={1.8} />
            </div>
          </div>
          <div style={{ height: 1, background: 'var(--line-2)' }} />
        </div>

        <div className="scr-scroll" style={{ flex: 1 }}>
          <div className="pad">
            <div className="p-body" style={{ lineHeight: 1.65, whiteSpace: 'pre-line' }}>
              {BIRCH_DESC}
            </div>
          </div>
        </div>
      </div>
    </PPhone>
  );
}


// ═══════════════════════════════════════════
// VARIANT C — Centered modal card (closest to original)
// ═══════════════════════════════════════════
function PAllergenInfoC() {
  return (
    <PPhone>
      {/* Dimmed background */}
      <div style={{ position: 'absolute', inset: 0, background: 'var(--paper)', zIndex: 0 }}>
        <div style={{ padding: 16, opacity: 0.2 }}>
          <div className="p-display" style={{ fontSize: 24 }}>Справочник</div>
          <div style={{ marginTop: 16, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {P_ALLERGENS.slice(0, 4).map(a => (
              <div key={a.code} className="p-card" style={{ padding: 14, height: 110 }} />
            ))}
          </div>
        </div>
      </div>
      <div style={{ position: 'absolute', inset: 0, background: 'rgba(27,31,29,0.4)', zIndex: 1 }} />

      {/* Modal */}
      <div style={{
        position: 'absolute', left: 12, right: 12, top: 40, bottom: 50,
        background: 'var(--card)',
        borderRadius: 20,
        boxShadow: 'var(--shadow-elevated)',
        zIndex: 2,
        display: 'flex', flexDirection: 'column',
        overflow: 'hidden',
      }}>
        {/* Header */}
        <div style={{ padding: '16px 16px 12px', flexShrink: 0 }}>
          <div className="row" style={{ gap: 14 }}>
            <div className="p-placeholder" style={{
              width: 56, height: 56, borderRadius: 16, flexShrink: 0, fontSize: 9,
            }}>BIR</div>
            <div style={{ flex: 1 }}>
              <div className="p-display" style={{ fontSize: 22 }}>Берёза</div>
              <div className="p-annot" style={{ fontSize: 10, marginTop: 2 }}>Betula · Берёзовые</div>
            </div>
            <div style={{
              width: 30, height: 30, borderRadius: 15,
              background: 'var(--paper-2)',
              display: 'grid', placeItems: 'center',
              alignSelf: 'flex-start',
            }}>
              <PIcon d={P_ICONS.x} size={14} stroke="var(--ink-2)" sw={1.8} />
            </div>
          </div>
        </div>
        <div style={{ height: 1, background: 'var(--line-2)', margin: '0 16px' }} />

        <div className="scr-scroll" style={{ flex: 1 }}>
          <div style={{ padding: '14px 16px 24px' }}>
            <div className="p-body" style={{ lineHeight: 1.65, whiteSpace: 'pre-line' }}>
              {BIRCH_DESC}
            </div>
          </div>
        </div>
      </div>
    </PPhone>
  );
}


// ═══════════════════════════════════════════
// VARIANT D — Full page with large illustration hero
// ═══════════════════════════════════════════
function PAllergenInfoD() {
  return (
    <PPhone>
      {/* Large illustration hero bleed */}
      <div style={{
        position: 'relative', flexShrink: 0,
        height: 200,
        background: 'var(--paper-2)',
      }}>
        <div className="p-placeholder" style={{
          position: 'absolute', inset: 0, borderRadius: 0,
          border: 'none', fontSize: 10,
        }}>ботаническая иллюстрация</div>
        {/* Back button overlay */}
        <div style={{
          position: 'absolute', top: 12, left: 14,
          width: 32, height: 32, borderRadius: 10,
          background: 'rgba(255,255,255,0.85)',
          backdropFilter: 'blur(8px)',
          display: 'grid', placeItems: 'center',
        }}>
          <PIcon d={P_ICONS.back} size={16} stroke="var(--ink-2)" sw={1.6} />
        </div>
        {/* Close button overlay */}
        <div style={{
          position: 'absolute', top: 12, right: 14,
          padding: '6px 14px', borderRadius: 10,
          background: 'rgba(255,255,255,0.85)',
          backdropFilter: 'blur(8px)',
          fontSize: 12, fontWeight: 600, color: 'var(--accent-2)',
        }}>Закрыть</div>
      </div>

      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="p-display" style={{ fontSize: 24, marginBottom: 4 }}>Берёза</div>
          <div className="p-annot" style={{ fontSize: 11, marginBottom: 16 }}>Betula · Берёзовые (Betulaceae)</div>
          <div style={{ height: 1, background: 'var(--line-2)', marginBottom: 16 }} />
          <div className="p-body" style={{ lineHeight: 1.65, whiteSpace: 'pre-line', paddingBottom: 24 }}>
            {BIRCH_DESC}
          </div>
        </div>
      </div>
    </PPhone>
  );
}


// ═══════════════════════════════════════════
// VARIANT E — Bottom sheet, compact with illustration strip
// ═══════════════════════════════════════════
function PAllergenInfoE() {
  return (
    <PPhone>
      {/* Scrim */}
      <div style={{ position: 'absolute', inset: 0, background: 'var(--paper)', zIndex: 0, opacity: 0.5 }} />
      <div style={{ position: 'absolute', inset: 0, background: 'rgba(27,31,29,0.3)', zIndex: 1 }} />

      {/* Bottom sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0, top: 24,
        background: 'var(--card)',
        borderRadius: '20px 20px 0 0',
        boxShadow: 'var(--shadow-sheet)',
        zIndex: 2,
        display: 'flex', flexDirection: 'column',
        overflow: 'hidden',
      }}>
        <div style={{ padding: '10px 16px 0', flexShrink: 0 }}>
          <div style={{
            width: 36, height: 4, borderRadius: 2,
            background: 'var(--line)', margin: '0 auto 12px',
          }} />
          {/* Close row */}
          <div className="row" style={{ gap: 8, marginBottom: 12 }}>
            <div className="p-display" style={{ fontSize: 22, flex: 1 }}>Берёза</div>
            <span style={{ fontSize: 12, fontWeight: 600, color: 'var(--accent-2)' }}>Закрыть</span>
          </div>
        </div>

        {/* Illustration strip */}
        <div className="p-placeholder" style={{
          height: 130, margin: '0 16px', borderRadius: 14, fontSize: 10,
          flexShrink: 0,
        }}>ботаническая иллюстрация</div>

        <div style={{ padding: '0 16px', flexShrink: 0 }}>
          <div className="p-annot" style={{ fontSize: 10, marginTop: 10, marginBottom: 12 }}>Betula · Берёзовые (Betulaceae)</div>
          <div style={{ height: 1, background: 'var(--line-2)' }} />
        </div>

        <div className="scr-scroll" style={{ flex: 1 }}>
          <div className="pad" style={{ paddingTop: 12 }}>
            <div className="p-body" style={{ lineHeight: 1.65, whiteSpace: 'pre-line' }}>
              {BIRCH_DESC}
            </div>
          </div>
        </div>
      </div>
    </PPhone>
  );
}

// ═══════════════════════════════════════════
// FINAL — Variant B bottom sheet, no close button (for Polished)
// ═══════════════════════════════════════════
function PAllergenInfo() {
  return (
    <PPhone>
      {/* Dimmed reference grid behind */}
      <div style={{ position: 'absolute', inset: 0, background: 'var(--paper)', zIndex: 0 }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
          padding: '12px 14px 10px',
          background: 'var(--card)',
          borderBottom: '1px solid var(--line-2)',
        }}>
          <div style={{ width: 32, height: 32, borderRadius: 10, background: 'var(--paper-2)' }} />
          <div style={{ flex: 1, textAlign: 'center' }}>
            <div style={{ fontSize: 15, fontWeight: 600, opacity: 0.4 }}>Справочник</div>
          </div>
          <div style={{ width: 32, height: 32, borderRadius: 10, background: 'var(--paper-2)' }} />
        </div>
        <div style={{ padding: 16, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, opacity: 0.25 }}>
          {P_ALLERGENS.slice(0, 4).map(a => (
            <div key={a.code} className="p-card" style={{ padding: 14, height: 100 }} />
          ))}
        </div>
      </div>

      {/* Scrim */}
      <div style={{ position: 'absolute', inset: 0, background: 'rgba(27,31,29,0.35)', zIndex: 1 }} />

      {/* Bottom sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0, top: 50,
        background: 'var(--card)',
        borderRadius: '20px 20px 0 0',
        boxShadow: 'var(--shadow-sheet)',
        zIndex: 2,
        display: 'flex', flexDirection: 'column',
        overflow: 'hidden',
      }}>
        {/* Handle + header */}
        <div style={{ padding: '10px 16px 0', flexShrink: 0 }}>
          <div style={{
            width: 36, height: 4, borderRadius: 2,
            background: 'var(--line)', margin: '0 auto 14px',
          }} />
          <div className="row" style={{ gap: 14, marginBottom: 14 }}>
            <div className="p-placeholder" style={{
              width: 52, height: 52, borderRadius: 16, flexShrink: 0, fontSize: 9,
            }}>BIR</div>
            <div style={{ flex: 1 }}>
              <div className="p-display" style={{ fontSize: 22 }}>Берёза</div>
              <div className="p-annot" style={{ fontSize: 10, marginTop: 2 }}>Betula · Берёзовые</div>
            </div>
          </div>
          <div style={{ height: 1, background: 'var(--line-2)' }} />
        </div>

        <div className="scr-scroll" style={{ flex: 1 }}>
          <div className="pad">
            <div className="p-body" style={{ lineHeight: 1.65, whiteSpace: 'pre-line' }}>
              {BIRCH_DESC}
            </div>
          </div>
        </div>
      </div>
    </PPhone>
  );
}

Object.assign(window, {
  PAllergenInfoA, PAllergenInfoB, PAllergenInfoC, PAllergenInfoD, PAllergenInfoE,
  PAllergenInfo, BIRCH_DESC,
});
