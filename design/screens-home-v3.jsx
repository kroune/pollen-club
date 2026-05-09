// ─── v3 HOME — compact (vertical squish) ──────────────────
// Feedback: HomeC3 wastes vertical space; SensitivitySetup similarly.

function PersonalScoreCompact({ score, level, sub }) {
  return (
    <div className="card" style={{ padding: '10px 12px' }}>
      <div className="row" style={{ justifyContent: 'space-between', alignItems: 'center' }}>
        <div className="col">
          <div className="h-eyebrow" style={{ marginBottom: 2 }}>Ваш индекс</div>
          <div className="row" style={{ alignItems: 'baseline', gap: 5 }}>
            <div className={'h-display num tx-' + level} style={{ fontSize: 26, lineHeight: 1 }}>{score}</div>
            <div style={{ fontSize: 10, color: 'var(--ink-3)' }}>/ 10</div>
          </div>
          <div style={{ fontSize: 10, color: 'var(--ink-2)', marginTop: 2 }}>{sub}</div>
        </div>
        <span className={'sev sev-' + level}>
          <span className="dot" />
          <span className={'tx-' + level}>{SEVERITY[level]}</span>
        </span>
      </div>
    </div>
  );
}

function DayStripCompact({ activeIdx = 3 }) {
  const days = [
    { d: 23, wd: 'ср', l: 1 },
    { d: 24, wd: 'чт', l: 1 },
    { d: 25, wd: 'пт', l: 3 },
    { d: 26, wd: 'сб', l: 2 },
    { d: 27, wd: 'вс', l: 3 },
    { d: 28, wd: 'пн', l: 2 },
    { d: 29, wd: 'вт', l: 1 },
  ];
  const colors = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
  return (
    <div style={{ display: 'flex', gap: 3 }}>
      {days.map((day, i) => (
        <div key={day.d} style={{
          flex: 1,
          padding: '6px 2px 5px',
          borderRadius: 8,
          background: i === activeIdx ? 'var(--card)' : 'transparent',
          border: i === activeIdx ? '1px solid var(--accent)' : '1px solid transparent',
          textAlign: 'center',
        }}>
          <div style={{ fontSize: 8, color: 'var(--ink-3)', textTransform: 'uppercase', lineHeight: 1 }}>{day.wd}</div>
          <div className="num" style={{ fontSize: 13, fontWeight: 500, marginTop: 2, lineHeight: 1 }}>{day.d}</div>
          <div style={{
            width: 6, height: 6, borderRadius: 3,
            background: colors[day.l],
            margin: '4px auto 0',
          }} />
        </div>
      ))}
    </div>
  );
}

// Home C3-v3 — squished
function HomeC3v3() {
  const colors = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
  const Dots = ({ level }) => (
    <div style={{ display: 'flex', gap: 3 }}>
      {[1,2,3,4,5].map(i => (
        <div key={i} style={{
          width: 7, height: 7, borderRadius: 4,
          background: i <= level ? colors[level] : 'var(--line)',
        }} />
      ))}
    </div>
  );
  return (
    <Phone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '12px 16px 0' }}>
          <div className="row" style={{ marginBottom: 8 }}>
            <div className="row" style={{ gap: 5, fontSize: 11, color: 'var(--ink-2)' }}>
              <Icon d={ICONS.loc} size={12} stroke="var(--ink-2)" />
              Москва
            </div>
          </div>
        </div>
        <div style={{ padding: '0 8px 6px' }}>
          <DayStripCompact activeIdx={3} />
        </div>
        <div style={{ padding: '0 16px 12px' }}>
          <PersonalScoreCompact score="5,2" level={2} sub="по 3 вашим аллергенам" />

          <div className="row" style={{ marginTop: 10, marginBottom: 6, justifyContent: 'space-between' }}>
            <div className="h-eyebrow">Ваши аллергены</div>
            <div className="annot" style={{ fontSize: 9 }}>3</div>
          </div>
          <div className="card" style={{ padding: 0 }}>
            {[
              { name: 'Берёза',  sev: 2 },
              { name: 'Орешник', sev: 0 },
              { name: 'Ольха',   sev: 0 },
            ].map((a, i) => (
              <div key={a.name} className="row" style={{
                padding: '8px 12px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1, fontSize: 12 }}>{a.name}</div>
                <Dots level={a.sev} />
              </div>
            ))}
          </div>

          <div className="h-eyebrow" style={{ marginTop: 12, marginBottom: 6 }}>Прочие</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4 }}>
            {['Дуб','Полынь','Злаки','Маревые','Амброзия','Кладоспориум','Альтернария'].map(n => (
              <span key={n} className="pill" style={{ fontSize: 10, padding: '3px 8px' }}>
                <span style={{ width: 5, height: 5, borderRadius: 3, background: 'var(--severity-0)' }} />
                {n}
              </span>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

// Sensitivity — squished, 2-column-ish density
function SensitivityV3() {
  const list = [
    { name: 'Берёза',  s: 3 },
    { name: 'Дуб',     s: 0 },
    { name: 'Ольха',   s: 1 },
    { name: 'Полынь',  s: 0 },
    { name: 'Орешник', s: 2 },
    { name: 'Злаки',   s: 0 },
    { name: 'Амброзия', s: 0 },
    { name: 'Маревые', s: 0 },
  ];
  const labels = ['нет','лёгкая','средняя','сильная'];
  return (
    <Phone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          <div className="row" style={{ gap: 10, marginBottom: 6 }}>
            <Icon d={ICONS.chevR} size={16} stroke="var(--ink-2)" sw={1.6} style={{ transform: 'rotate(180deg)', flexShrink: 0 }} />
            <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1 }}>Чувствительность</div>
          </div>
          <div style={{ fontSize: 11, color: 'var(--ink-2)', lineHeight: 1.4, marginBottom: 14 }}>
            Насколько каждый аллерген влияет на вас.
          </div>
          <div className="card" style={{ padding: 0 }}>
            {list.map((a, i) => (
              <div key={a.name} className="row" style={{
                padding: '8px 12px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
                gap: 10,
              }}>
                <div style={{ width: 70, fontSize: 12 }}>{a.name}</div>
                <div style={{ flex: 1, display: 'flex', gap: 3 }}>
                  {[0,1,2,3].map(j => (
                    <div key={j} style={{
                      flex: 1, height: 5, borderRadius: 3,
                      background: j <= a.s ? 'var(--accent)' : 'var(--line)',
                    }} />
                  ))}
                </div>
                <div style={{ fontSize: 9, color: 'var(--ink-3)', width: 50, textAlign: 'right' }}>{labels[a.s]}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

Object.assign(window, { HomeC3v3, SensitivityV3 });
