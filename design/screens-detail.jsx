// ─── ALLERGEN DETAIL (chart) — 3 variants ──────────────

// A — Clean line chart with severity bands
function DetailA() {
  const points = [3,1,2,4,3,2,1];
  const max = 5;
  const w = 220, h = 130, pad = 4;
  const path = points.map((v, i) => {
    const x = pad + (i / (points.length - 1)) * (w - pad * 2);
    const y = h - pad - (v / max) * (h - pad * 2);
    return `${i === 0 ? 'M' : 'L'} ${x} ${y}`;
  }).join(' ');
  return (
    <Phone>
      <AppBar title="Берёза" sub="Москва · 26 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="row" style={{ alignItems: 'baseline', gap: 8 }}>
            <div className="h-large num tx-2">5,2</div>
            <div style={{ fontSize: 11, color: 'var(--ink-3)' }}>/ 10</div>
            <div className="spacer" />
            <SevLabel level={2} />
          </div>

          <div className="card" style={{ marginTop: 14, padding: 12 }}>
            <div className="h-eyebrow" style={{ marginBottom: 10 }}>Динамика · 7 дней</div>
            <svg width={w} height={h} style={{ display: 'block' }}>
              {[1,2,3,4,5].map(i => {
                const colors = ['','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
                const y = h - pad - (i / max) * (h - pad * 2);
                return (
                  <g key={i}>
                    <line x1={pad} y1={y} x2={w - pad} y2={y} stroke="var(--line-2)" strokeDasharray="2 3" />
                    <circle cx={w - pad - 1} cy={y} r="1.5" fill={colors[i]} />
                  </g>
                );
              })}
              <path d={path} fill="none" stroke="var(--accent-2)" strokeWidth="1.6" />
              {points.map((v, i) => {
                const x = pad + (i / (points.length - 1)) * (w - pad * 2);
                const y = h - pad - (v / max) * (h - pad * 2);
                return <circle key={i} cx={x} cy={y} r={i === 3 ? 4 : 2.5} fill={i === 3 ? 'var(--accent-2)' : '#fff'} stroke="var(--accent-2)" strokeWidth="1.4" />;
              })}
            </svg>
            <div className="row" style={{ justifyContent: 'space-between', fontSize: 9, color: 'var(--ink-3)', marginTop: 4 }}>
              {['23','24','25','26','27','28','29'].map(d => <div key={d}>{d}</div>)}
            </div>
          </div>

          <div className="row" style={{ marginTop: 14, gap: 8 }}>
            <div className="card" style={{ flex: 1, padding: 10 }}>
              <div className="h-eyebrow">Пик</div>
              <div className="num" style={{ fontSize: 18, marginTop: 2 }}>25 апр</div>
              <div className="tx-3" style={{ fontSize: 10 }}>Высокий</div>
            </div>
            <div className="card" style={{ flex: 1, padding: 10 }}>
              <div className="h-eyebrow">Спад</div>
              <div className="num" style={{ fontSize: 18, marginTop: 2 }}>3 мая</div>
              <div className="tx-1" style={{ fontSize: 10 }}>Прогноз</div>
            </div>
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

// B — Stacked daily bars + commentary
function DetailB() {
  const days = [
    { d: '23', wd: 'ср', l: 1 },
    { d: '24', wd: 'чт', l: 1 },
    { d: '25', wd: 'пт', l: 3 },
    { d: '26', wd: 'сб', l: 2, on: true },
    { d: '27', wd: 'вс', l: 3 },
    { d: '28', wd: 'пн', l: 2 },
    { d: '29', wd: 'вт', l: 1 },
  ];
  const colors = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
  return (
    <Phone>
      <AppBar title="Берёза" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad-lg">
          <div className="placeholder" style={{ height: 70, marginBottom: 12 }}>
            placeholder · betula sketch
          </div>
          <div className="h-display">Цветение в разгаре</div>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 4 }}>
            Активная фаза, пик 25 апр.
          </div>

          <div style={{ display: 'flex', alignItems: 'flex-end', gap: 5, height: 110, marginTop: 18 }}>
            {days.map(day => (
              <div key={day.d} className="col" style={{ flex: 1, alignItems: 'center', gap: 4 }}>
                <div className="num" style={{ fontSize: 9, color: 'var(--ink-3)' }}>{day.l}</div>
                <div style={{
                  width: '100%',
                  height: `${day.l * 16 + 4}px`,
                  background: colors[day.l],
                  borderRadius: '3px 3px 0 0',
                  outline: day.on ? '1px solid var(--ink)' : 'none',
                  outlineOffset: 2,
                }} />
                <div style={{ fontSize: 9, color: 'var(--ink-3)' }}>{day.d}</div>
              </div>
            ))}
          </div>

          <div className="div-h" />
          <div className="h-eyebrow">Сегодня</div>
          <div style={{ fontSize: 12, lineHeight: 1.55, marginTop: 8, color: 'var(--ink-2)' }}>
            Берёза пылит активно во всех уголках страны.
            Уровень самочувствия — 8 баллов: уверенная красная зона.
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

// C — Calendar heatmap
function DetailC() {
  const colors = ['var(--paper-2)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
  const month = [];
  const seed = [0,0,0,1,1,1,2,2,2,3,3,4,5,4,4,3,3,2,2,1,1,1,1,0,0,0,0,0,0,0];
  for (let i = 0; i < 30; i++) month.push(seed[i] || 0);
  return (
    <Phone>
      <AppBar title="Берёза" sub="Апрель 2026" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="row" style={{ alignItems: 'baseline' }}>
            <div className="h-display">26</div>
            <div style={{ marginLeft: 8, fontSize: 12, color: 'var(--ink-2)' }}>апреля</div>
            <div className="spacer" />
            <SevLabel level={2} />
          </div>
          <div className="div-h" />
          <div className="h-eyebrow" style={{ marginBottom: 10 }}>Месяц</div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 4 }}>
            {['П','В','С','Ч','П','С','В'].map((d, i) => (
              <div key={i} style={{ fontSize: 9, color: 'var(--ink-3)', textAlign: 'center' }}>{d}</div>
            ))}
            {month.map((v, i) => (
              <div key={i} style={{
                aspectRatio: '1 / 1',
                background: colors[v],
                borderRadius: 4,
                border: i === 25 ? '1.5px solid var(--ink)' : '1px solid var(--line-2)',
                fontSize: 9,
                color: v >= 2 ? '#fff' : 'var(--ink-3)',
                display: 'grid',
                placeItems: 'center',
                fontFamily: 'var(--font-mono)',
              }}>{i + 1}</div>
            ))}
          </div>
          <div className="div-h" />
          <div className="row" style={{ justifyContent: 'space-between', fontSize: 9, color: 'var(--ink-3)' }}>
            <div>нулевой</div>
            <div style={{ display: 'flex', gap: 3 }}>
              {colors.map((c, i) => <div key={i} style={{ width: 12, height: 12, background: c, borderRadius: 2 }} />)}
            </div>
            <div>экстра</div>
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

Object.assign(window, { DetailA, DetailB, DetailC });
