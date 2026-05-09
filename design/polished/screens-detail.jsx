// Polished Detail screen — allergen chart with feeling overlay

function PDetail() {
  const [showFeel, setShowFeel] = React.useState(true);
  const pollen = [0,0,1,1,2,2,3,4,3,3,2,2,1,1];
  const feeling = [null,null,0,1,1,2,2,3,3,2,1,null,null,null];
  const today = 9;
  const colW = 26;
  const w = colW * pollen.length;
  const h = 150, padY = 8;
  const max = 5;
  const x = (i) => colW / 2 + i * colW;
  const y = (v) => h - padY - (v / max) * (h - padY * 2);
  const pollenLine = pollen.map((v, i) => `${i === 0 ? 'M' : 'L'} ${x(i)} ${y(v)}`).join(' ');
  const pollenArea = `${pollenLine} L ${x(pollen.length - 1)} ${h - padY} L ${x(0)} ${h - padY} Z`;
  const feelPts = feeling.map((v, i) => v == null ? null : { i, v }).filter(Boolean);
  const feelLine = feelPts.length === 0 ? '' :
    feelPts.map((p, i) => `${i === 0 ? 'M' : 'L'} ${x(p.i)} ${y(p.v)}`).join(' ');
  const colors = ['#bcc8c0','#6ea85c','#d4a83a','#d4713a','#c43d3d','#7a3590'];
  const startDay = 17;

  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingBottom: 0, paddingTop: 16 }}>
          {/* Back + title */}
          <div className="row" style={{ gap: 10, marginBottom: 6 }}>
            <PIcon d={P_ICONS.back} size={18} stroke="var(--ink-2)" sw={1.6} />
            <div className="p-display" style={{ fontSize: 22 }}>Берёза</div>
          </div>

          {/* Score */}
          <div className="row" style={{ alignItems: 'baseline', gap: 8, marginTop: 12 }}>
            <span className="p-large tx-2" style={{ fontSize: 34 }}>5,2</span>
            <span style={{ fontSize: 12, color: 'var(--ink-3)' }}>/ 10 · сегодня</span>
            <div className="spacer" />
            <PSevLabel level={2} />
          </div>

          {/* Chart header + legend */}
          <div className="row" style={{ justifyContent: 'space-between', marginTop: 20, marginBottom: 8 }}>
            <div className="p-eyebrow">Динамика по дням</div>
            <div className="p-annot" style={{ fontSize: 9 }}>↔ листайте</div>
          </div>
          <div className="row" style={{ gap: 14, marginBottom: 10, fontSize: 11 }}>
            <div className="row" style={{ gap: 6 }}>
              <span style={{ width: 18, height: 3, borderRadius: 2, background: 'var(--ink)' }} />
              <span style={{ color: 'var(--ink-2)' }}>пыление</span>
            </div>
            <div className="row" onClick={() => setShowFeel(!showFeel)}
              style={{ gap: 6, cursor: 'pointer', opacity: showFeel ? 1 : 0.4, transition: 'opacity 0.2s' }}>
              <span style={{ width: 18, height: 0, borderTop: '2px dashed var(--severity-4)' }} />
              <span style={{ color: 'var(--ink-2)' }}>самочувствие</span>
              <span style={{
                width: 16, height: 16, borderRadius: 8,
                border: showFeel ? 'none' : '1.5px solid var(--line)',
                background: showFeel ? 'var(--accent)' : 'transparent',
                display: 'grid', placeItems: 'center',
                transition: 'all 0.15s',
              }}>
                {showFeel && <PIcon d={P_ICONS.check} size={10} stroke="#fff" sw={2.4} />}
              </span>
            </div>
          </div>
        </div>

        {/* Chart — edge-to-edge */}
        <div style={{ display: 'flex' }}>
          <svg width={26} height={h} style={{ flexShrink: 0, display: 'block' }}>
            {[1,2,3,4,5].map(i => (
              <text key={i} x={22} y={y(i) + 3}
                fontSize="9" textAnchor="end" fill="var(--ink-3)"
                fontFamily="'JetBrains Mono'" fontWeight="500">{i}</text>
            ))}
          </svg>
          <div style={{ overflowX: 'auto', overflowY: 'hidden', WebkitOverflowScrolling: 'touch', flex: 1 }}>
            <svg width={w} height={h} style={{ display: 'block' }}>
              <defs>
                <linearGradient id="pSevGrad" x1="0" y1="1" x2="0" y2="0">
                  <stop offset="0%" stopColor={colors[0]} />
                  <stop offset="20%" stopColor={colors[1]} />
                  <stop offset="40%" stopColor={colors[2]} />
                  <stop offset="60%" stopColor={colors[3]} />
                  <stop offset="80%" stopColor={colors[4]} />
                  <stop offset="100%" stopColor={colors[5]} />
                </linearGradient>
                <clipPath id="pAreaClip"><path d={pollenArea} /></clipPath>
              </defs>
              {[1,2,3,4,5].map(i => (
                <line key={i} x1={0} y1={y(i)} x2={w} y2={y(i)} stroke="var(--line-2)" strokeDasharray="2 3" />
              ))}
              <rect x="0" y="0" width={w} height={h} fill="url(#pSevGrad)" opacity="0.35" clipPath="url(#pAreaClip)" />
              {pollen.map((_, i) => i > 0 && (
                <line key={i} x1={i * colW} y1={padY} x2={i * colW} y2={h - padY} stroke="var(--line-2)" strokeWidth="0.5" opacity="0.4" />
              ))}
              <path d={pollenLine} fill="none" stroke="var(--ink)" strokeWidth="1.8" strokeLinejoin="round" />
              {showFeel && feelLine && (
                <>
                  <path d={feelLine} fill="none" stroke="var(--severity-4)" strokeWidth="1.4" strokeDasharray="4 3" strokeLinejoin="round" />
                  {feelPts.map(p => (
                    <circle key={p.i} cx={x(p.i)} cy={y(p.v)} r="2.5" fill="#fff" stroke="var(--severity-4)" strokeWidth="1.4" />
                  ))}
                </>
              )}
              <line x1={x(today)} y1={padY} x2={x(today)} y2={h - padY} stroke="var(--ink)" strokeDasharray="3 2" strokeWidth="1" />
              {pollen.map((v, i) => (
                <circle key={i} cx={x(i)} cy={y(v)} r={i === today ? 4.5 : 2.5}
                  fill={colors[v]} stroke={i === today ? '#fff' : 'none'} strokeWidth={i === today ? 1.6 : 0} />
              ))}
              {pollen.map((_, i) => {
                const d = startDay + i;
                const day = d > 30 ? d - 30 : d;
                return (
                  <text key={i} x={x(i)} y={h - padY + 14}
                    fontSize="9" textAnchor="middle"
                    fill={i === today ? 'var(--ink)' : 'var(--ink-3)'}
                    fontFamily="'JetBrains Mono'"
                    fontWeight={i === today ? 600 : 400}>
                    {day}
                  </text>
                );
              })}
            </svg>
          </div>
        </div>

        <div className="pad" style={{ paddingTop: 16 }}>
          {/* Stats row */}
          <div className="p-card" style={{ padding: '12px 16px' }}>
            <div className="row" style={{ gap: 0 }}>
              <div style={{ flex: 1 }}>
                <div className="p-annot" style={{ fontSize: 9 }}>пик</div>
                <div className="p-num" style={{ fontSize: 14, fontWeight: 600, marginTop: 2 }}>24 апр</div>
              </div>
              <div style={{ width: 1, alignSelf: 'stretch', background: 'var(--line-2)', margin: '0 12px' }} />
              <div style={{ flex: 1 }}>
                <div className="p-annot" style={{ fontSize: 9 }}>спад</div>
                <div className="p-num" style={{ fontSize: 14, fontWeight: 600, marginTop: 2 }}>~30 апр</div>
              </div>
              <div style={{ width: 1, alignSelf: 'stretch', background: 'var(--line-2)', margin: '0 12px' }} />
              <div style={{ flex: 1 }}>
                <div className="p-annot" style={{ fontSize: 9 }}>ваше</div>
                <div className="p-num tx-3" style={{ fontSize: 14, fontWeight: 600, marginTop: 2 }}>2 симп.</div>
              </div>
            </div>
          </div>

          {/* About section */}
          <div className="p-eyebrow" style={{ marginTop: 20, marginBottom: 8 }}>О периоде</div>
          <div className="p-body" style={{ lineHeight: 1.55 }}>
            Активная фаза. Обычно пыление берёзы длится около 3 недель. Уточняется на основании данных пыльцеуловителей и фенологии.
          </div>
        </div>
      </div>
      <PTabBar active="home" />
    </PPhone>
  );
}

Object.assign(window, { PDetail });
