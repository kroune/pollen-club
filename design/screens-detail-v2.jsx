// ─── v2 DETAIL — new directions ─────────────────────────
// Feedback: A had only "today" coloured; bands should colour the chart by
// severity continuously. B placeholder too tall, no horizontal grid. C calendar
// is too noisy — keep recent days in foreground, calendar deeper.

// Detail v2 A — full-coloured area chart (gradient by severity bands), past
// + future merged, today marked. Horizontal grid lines, scrollable timeline.
function DetailA2() {
  // 14 days: 10 past (history) + today + 3 forecast
  const data = [0,0,1,1,2,2,3,4,3,3,2,2,1,1];
  const today = 9;
  const w = 220, h = 130, pad = 6;
  const max = 5;
  const x = (i) => pad + (i / (data.length - 1)) * (w - pad * 2);
  const y = (v) => h - pad - (v / max) * (h - pad * 2);
  const line = data.map((v, i) => `${i === 0 ? 'M' : 'L'} ${x(i)} ${y(v)}`).join(' ');
  const area = `${line} L ${x(data.length - 1)} ${h - pad} L ${x(0)} ${h - pad} Z`;
  const colors = ['#c9d3cd','#7fa86a','#d9b94a','#d97a3a','#c44545','#7d3a8a'];

  return (
    <Phone>
      <AppBar title="Берёза" sub="Москва" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="row" style={{ alignItems: 'baseline', gap: 8 }}>
            <div className="h-large num tx-2">5,2</div>
            <div style={{ fontSize: 11, color: 'var(--ink-3)' }}>/ 10 · сегодня</div>
            <div className="spacer" />
            <SevLabel level={2} />
          </div>

          <div className="card" style={{ marginTop: 14, padding: 12 }}>
            <div className="row" style={{ justifyContent: 'space-between', marginBottom: 8 }}>
              <div className="h-eyebrow">14 дней</div>
              <div className="annot" style={{ fontSize: 9 }}>← листайте, чтобы увидеть весь сезон</div>
            </div>
            <svg width={w} height={h} style={{ display: 'block' }}>
              <defs>
                <linearGradient id="sevGrad" x1="0" y1="1" x2="0" y2="0">
                  <stop offset="0%" stopColor={colors[0]} />
                  <stop offset="20%" stopColor={colors[1]} />
                  <stop offset="40%" stopColor={colors[2]} />
                  <stop offset="60%" stopColor={colors[3]} />
                  <stop offset="80%" stopColor={colors[4]} />
                  <stop offset="100%" stopColor={colors[5]} />
                </linearGradient>
                <clipPath id="areaClip"><path d={area} /></clipPath>
              </defs>
              {/* horizontal grid lines + threshold labels */}
              {[1,2,3,4,5].map(i => (
                <g key={i}>
                  <line x1={pad} y1={y(i)} x2={w - pad} y2={y(i)} stroke="var(--line-2)" strokeDasharray="2 3" />
                  <text x={w - pad} y={y(i) - 2} fontSize="8" textAnchor="end" fill="var(--ink-3)" fontFamily="JetBrains Mono">{i}</text>
                </g>
              ))}
              {/* gradient-filled area (severity bands) */}
              <rect x="0" y="0" width={w} height={h} fill="url(#sevGrad)" opacity="0.35" clipPath="url(#areaClip)" />
              {/* line */}
              <path d={line} fill="none" stroke="var(--ink)" strokeWidth="1.4" />
              {/* today marker */}
              <line x1={x(today)} y1={pad} x2={x(today)} y2={h - pad} stroke="var(--ink)" strokeDasharray="3 2" strokeWidth="1" />
              <circle cx={x(today)} cy={y(data[today])} r="4" fill={colors[data[today]]} stroke="#fff" strokeWidth="1.5" />
              {/* peak marker (built into chart, no separate label cards) */}
              {(() => {
                const pk = data.indexOf(Math.max(...data));
                return (
                  <g>
                    <circle cx={x(pk)} cy={y(data[pk])} r="3" fill="none" stroke={colors[data[pk]]} strokeWidth="1.5" />
                    <text x={x(pk)} y={y(data[pk]) - 8} fontSize="8" textAnchor="middle" fill="var(--ink-2)" fontFamily="JetBrains Mono">пик</text>
                  </g>
                );
              })()}
            </svg>
            <div className="row" style={{ justifyContent: 'space-between', fontSize: 9, color: 'var(--ink-3)', marginTop: 4 }}>
              <span>17 апр</span>
              <span style={{ color: 'var(--ink)' }}>сегодня</span>
              <span>30 апр</span>
            </div>
          </div>

          <div className="div-h" />
          <div className="h-eyebrow" style={{ marginBottom: 8 }}>О периоде</div>
          <div style={{ fontSize: 12, lineHeight: 1.55, color: 'var(--ink-2)' }}>
            Активная фаза. Спад ожидается через 4–5 дней.
            Обычно в это время года пыление берёзы длится около 3 недель.
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

// Detail v2 B — header compact (no big placeholder), focus is the chart with
// horizontal grid + scroll affordance. Stat strip is optional / non-redundant.
function DetailB2() {
  const days = [
    { d: 17, l: 0 }, { d: 18, l: 0 }, { d: 19, l: 1 },
    { d: 20, l: 1 }, { d: 21, l: 2 }, { d: 22, l: 2 },
    { d: 23, l: 3 }, { d: 24, l: 4 }, { d: 25, l: 3 },
    { d: 26, l: 2, on: true },
    { d: 27, l: 2 }, { d: 28, l: 1 }, { d: 29, l: 1 },
  ];
  const colors = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
  return (
    <Phone>
      <AppBar
        title="Берёза"
        right={<span className="annot" style={{ fontSize: 10 }}>СРЕДНИЙ</span>}
      />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          {/* compact hero row — number sits next to title-like context, no full-width placeholder */}
          <div className="row" style={{ alignItems: 'baseline', gap: 10 }}>
            <div className="h-large num tx-2">5,2</div>
            <div className="col">
              <div style={{ fontSize: 11, color: 'var(--ink-2)' }}>сегодня</div>
              <div style={{ fontSize: 10, color: 'var(--ink-3)' }}>пик 24 апр · спад к 30 апр</div>
            </div>
          </div>

          <div className="div-h" style={{ margin: '14px 0 10px' }} />
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 8 }}>
            <div className="h-eyebrow">Динамика</div>
            <div className="annot" style={{ fontSize: 9 }}>↔ листайте</div>
          </div>

          {/* bars on a grid */}
          <div style={{ position: 'relative', height: 130, marginBottom: 4 }}>
            {/* horizontal grid */}
            {[1,2,3,4,5].map(i => (
              <div key={i} style={{
                position: 'absolute', left: 0, right: 0,
                bottom: `${(i / 5) * 100}%`,
                borderTop: '1px dashed var(--line-2)',
              }}>
                <span style={{ position: 'absolute', right: 0, top: -7, fontSize: 8, color: 'var(--ink-3)', fontFamily: 'JetBrains Mono', background: 'var(--paper)', padding: '0 2px' }}>{i}</span>
              </div>
            ))}
            <div style={{ display: 'flex', alignItems: 'flex-end', gap: 3, height: '100%', position: 'relative' }}>
              {days.map(day => (
                <div key={day.d} style={{
                  flex: 1,
                  height: `${(day.l / 5) * 100}%`,
                  background: colors[day.l],
                  borderRadius: '2px 2px 0 0',
                  minHeight: 2,
                  outline: day.on ? '1.5px solid var(--ink)' : 'none',
                  outlineOffset: 1,
                }} />
              ))}
            </div>
          </div>
          <div className="row" style={{ justifyContent: 'space-between', fontSize: 9, color: 'var(--ink-3)' }}>
            <span>17</span><span>20</span><span>23</span>
            <span style={{ color: 'var(--ink)', fontWeight: 500 }}>26</span>
            <span>29</span>
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

// Detail v2 C — near-term focus: yesterday / today / +next-3, calendar collapsed
function DetailC2() {
  const colors = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
  const focus = [
    { d: 25, wd: 'пт', l: 3, label: 'вчера' },
    { d: 26, wd: 'сб', l: 2, label: 'сегодня', on: true },
    { d: 27, wd: 'вс', l: 3, label: 'завтра' },
    { d: 28, wd: 'пн', l: 2, label: '+2 дня' },
    { d: 29, wd: 'вт', l: 1, label: '+3 дня' },
  ];
  return (
    <Phone>
      <AppBar title="Берёза" sub="Москва" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="h-eyebrow">Ближайшие дни</div>
          <div className="card" style={{ padding: 8, marginTop: 8 }}>
            {focus.map((f, i) => (
              <div key={f.d} className="row" style={{
                padding: '10px 8px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
                background: f.on ? 'var(--paper-2)' : 'transparent',
                borderRadius: f.on ? 8 : 0,
              }}>
                <div style={{ width: 8, height: 36, borderRadius: 4, background: colors[f.l], flexShrink: 0 }} />
                <div className="col" style={{ flex: 1, marginLeft: 10 }}>
                  <div style={{ fontSize: 12, fontWeight: f.on ? 600 : 400 }}>
                    {f.label}{' '}
                    <span style={{ fontWeight: 400, color: 'var(--ink-3)', fontSize: 11 }}>· {f.d} {f.wd}</span>
                  </div>
                  <div className={'tx-' + f.l} style={{ fontSize: 10, marginTop: 2 }}>
                    {SEVERITY[f.l]}
                  </div>
                </div>
                <div style={{ width: 50 }}><SevBar level={f.l} /></div>
              </div>
            ))}
          </div>

          <div className="div-h" />
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 8 }}>
            <div className="h-eyebrow">Весь сезон</div>
            <div className="annot">апрель · открыть →</div>
          </div>
          {/* tiny preview heatmap — calendar collapsed */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(15, 1fr)', gap: 2 }}>
            {[0,0,0,1,1,1,2,2,2,3,3,4,5,4,4,3,3,2,2,1,1,1,1,0,0,0,0,0,0,0].map((v, i) => (
              <div key={i} style={{
                aspectRatio: '1 / 1',
                background: colors[v],
                borderRadius: 2,
              }} />
            ))}
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

Object.assign(window, { DetailA2, DetailB2, DetailC2 });
