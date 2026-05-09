// ─── v3 DETAIL — fix horizontal padding, daily labels ──────
// Feedback: don't pad chart inside card if it's horizontally scrollable.
// Per-day labels — current 17/26/30 spans too many days without granularity.

function DetailA3() {
  // 14 days: history + today + forecast
  const data = [0,0,1,1,2,2,3,4,3,3,2,2,1,1];
  const today = 9;
  const colW = 24;          // per-day cell
  const w = colW * data.length;
  const h = 130, padY = 6;
  const max = 5;
  const x = (i) => colW / 2 + i * colW;
  const y = (v) => h - padY - (v / max) * (h - padY * 2);
  const line = data.map((v, i) => `${i === 0 ? 'M' : 'L'} ${x(i)} ${y(v)}`).join(' ');
  const area = `${line} L ${x(data.length - 1)} ${h - padY} L ${x(0)} ${h - padY} Z`;
  const colors = ['#c9d3cd','#7fa86a','#d9b94a','#d97a3a','#c44545','#7d3a8a'];
  const startDay = 17;
  const months = ['апр','апр'];

  return (
    <Phone>
      <AppBar title="Берёза" sub="Москва" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingBottom: 0 }}>
          <div className="row" style={{ alignItems: 'baseline', gap: 8 }}>
            <div className="h-large num tx-2">5,2</div>
            <div style={{ fontSize: 11, color: 'var(--ink-3)' }}>/ 10 · сегодня</div>
            <div className="spacer" />
            <SevLabel level={2} />
          </div>
          <div className="row" style={{ justifyContent: 'space-between', marginTop: 14, marginBottom: 6 }}>
            <div className="h-eyebrow">Динамика по дням</div>
            <div className="annot" style={{ fontSize: 9 }}>↔ листайте</div>
          </div>
        </div>

        {/* edge-to-edge scroll, NO horizontal padding */}
        <div style={{ overflowX: 'auto', overflowY: 'hidden', WebkitOverflowScrolling: 'touch' }}>
          <svg width={w} height={h} style={{ display: 'block' }}>
            <defs>
              <linearGradient id="sevGradV3" x1="0" y1="1" x2="0" y2="0">
                <stop offset="0%" stopColor={colors[0]} />
                <stop offset="20%" stopColor={colors[1]} />
                <stop offset="40%" stopColor={colors[2]} />
                <stop offset="60%" stopColor={colors[3]} />
                <stop offset="80%" stopColor={colors[4]} />
                <stop offset="100%" stopColor={colors[5]} />
              </linearGradient>
              <clipPath id="areaClipV3"><path d={area} /></clipPath>
            </defs>
            {/* horizontal severity grid */}
            {[1,2,3,4,5].map(i => (
              <line key={i} x1={0} y1={y(i)} x2={w} y2={y(i)} stroke="var(--line-2)" strokeDasharray="2 3" />
            ))}
            {/* gradient area */}
            <rect x="0" y="0" width={w} height={h} fill="url(#sevGradV3)" opacity="0.4" clipPath="url(#areaClipV3)" />
            {/* vertical day separators */}
            {data.map((_, i) => i > 0 && (
              <line key={i} x1={i * colW} y1={padY} x2={i * colW} y2={h - padY} stroke="var(--line-2)" strokeWidth="0.5" opacity="0.5" />
            ))}
            {/* line */}
            <path d={line} fill="none" stroke="var(--ink)" strokeWidth="1.4" />
            {/* today vertical marker */}
            <line x1={x(today)} y1={padY} x2={x(today)} y2={h - padY} stroke="var(--ink)" strokeDasharray="3 2" strokeWidth="1" />
            {/* dot at each day */}
            {data.map((v, i) => (
              <circle key={i} cx={x(i)} cy={y(v)} r={i === today ? 4 : 2.2}
                fill={colors[v]} stroke={i === today ? '#fff' : 'none'} strokeWidth={i === today ? 1.4 : 0} />
            ))}
            {/* per-day labels */}
            {data.map((_, i) => {
              const d = startDay + i;
              const day = d > 30 ? d - 30 : d;
              const monthIdx = d > 30 ? 1 : 0;
              const isFirstOfMonth = d === 31;
              return (
                <g key={i}>
                  <text x={x(i)} y={h - padY + 14}
                    fontSize="9" textAnchor="middle"
                    fill={i === today ? 'var(--ink)' : 'var(--ink-3)'}
                    fontFamily="JetBrains Mono"
                    fontWeight={i === today ? 600 : 400}>
                    {day}
                  </text>
                  {(i === 0 || isFirstOfMonth) && (
                    <text x={x(i)} y={h - padY + 23}
                      fontSize="7" textAnchor="middle" fill="var(--ink-3)" fontFamily="JetBrains Mono">
                      {months[monthIdx]}
                    </text>
                  )}
                </g>
              );
            })}
            {/* severity labels along right edge — repositioned to be visible without overlap */}
          </svg>
        </div>

        <div className="pad" style={{ paddingTop: 14 }}>
          <div className="row" style={{ gap: 12, marginBottom: 12 }}>
            <div className="col">
              <div className="annot" style={{ fontSize: 9 }}>пик</div>
              <div className="num" style={{ fontSize: 13 }}>24 апр</div>
            </div>
            <div style={{ width: 1, alignSelf: 'stretch', background: 'var(--line-2)' }} />
            <div className="col">
              <div className="annot" style={{ fontSize: 9 }}>спад</div>
              <div className="num" style={{ fontSize: 13 }}>~30 апр</div>
            </div>
            <div style={{ width: 1, alignSelf: 'stretch', background: 'var(--line-2)' }} />
            <div className="col">
              <div className="annot" style={{ fontSize: 9 }}>ваше</div>
              <div className="num tx-3" style={{ fontSize: 13 }}>2 симп.</div>
            </div>
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Сопоставьте с самочувствием</div>
          {/* compact 7-day strip linking pollen vs symptom severity */}
          <div className="card" style={{ padding: 8 }}>
            {[
              { d: 20, wd: 'пн', p: 1, s: 0 },
              { d: 21, wd: 'вт', p: 2, s: 1 },
              { d: 22, wd: 'ср', p: 2, s: 1 },
              { d: 23, wd: 'чт', p: 3, s: 2 },
              { d: 24, wd: 'пт', p: 4, s: 3 },
              { d: 25, wd: 'сб', p: 3, s: 2 },
              { d: 26, wd: 'вс', p: 2, s: null, today: true },
            ].map(r => (
              <div key={r.d} className="row" style={{ padding: '4px 0', gap: 8 }}>
                <div className="num" style={{ fontSize: 10, width: 28, color: r.today ? 'var(--ink)' : 'var(--ink-3)' }}>{r.d} {r.wd}</div>
                <div style={{ flex: 1, display: 'flex', gap: 2 }}>
                  {[1,2,3,4,5].map(i => (
                    <div key={i} style={{
                      flex: 1, height: 4, borderRadius: 2,
                      background: i <= r.p ? colors[r.p] : 'var(--line)',
                    }} />
                  ))}
                </div>
                <div style={{ width: 36, textAlign: 'right', fontSize: 9, color: 'var(--ink-3)' }}>
                  {r.s === null ? '—' : r.s === 0 ? 'хорошо' : r.s === 1 ? 'хорошо' : r.s === 2 ? 'терп.' : 'плохо'}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

Object.assign(window, { DetailA3 });
