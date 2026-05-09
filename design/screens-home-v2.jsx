// ─── v2 HOME — iterations on Variant C ──────────────────
// Feedback: dedup "26 апреля / сб", make "общий уровень" personalised
// (based on user's allergen sensitivities), explore allergen level viz.

// Helper — personalised score header used by C2/C3
function PersonalScore({ score = 5.2, level = 2, sub }) {
  return (
    <div className="card" style={{ padding: 14 }}>
      <div className="row" style={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div className="h-eyebrow">Ваш индекс</div>
          <div className="row" style={{ alignItems: 'baseline', marginTop: 4, gap: 6 }}>
            <div className={'h-display num tx-' + level} style={{ fontSize: 30 }}>{score}</div>
            <div style={{ fontSize: 11, color: 'var(--ink-3)' }}>/ 10</div>
          </div>
          <div style={{ fontSize: 11, color: 'var(--ink-2)', marginTop: 2 }}>{sub}</div>
        </div>
        <span className={'sev sev-' + level} style={{ flexShrink: 0 }}>
          <span className="dot" />
          <span className={'tx-' + level}>{SEVERITY[level]}</span>
        </span>
      </div>
    </div>
  );
}

// 7-day strip (extracted, deduped header)
function DayStrip({ activeIdx = 3 }) {
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
    <div style={{ display: 'flex', gap: 4 }}>
      {days.map((day, i) => (
        <div key={day.d} style={{
          flex: 1,
          padding: '10px 4px',
          borderRadius: 10,
          background: i === activeIdx ? 'var(--card)' : 'transparent',
          border: i === activeIdx ? '1px solid var(--accent)' : '1px solid transparent',
          textAlign: 'center',
        }}>
          <div style={{ fontSize: 9, color: 'var(--ink-3)', textTransform: 'uppercase' }}>{day.wd}</div>
          <div className="num" style={{ fontSize: 14, fontWeight: 500, marginTop: 2 }}>{day.d}</div>
          <div style={{
            width: 8, height: 8, borderRadius: 4,
            background: colors[day.l],
            margin: '6px auto 0',
          }} />
        </div>
      ))}
    </div>
  );
}

// Home C2 — strip first, personalised score, allergen viz: dot + bar
function HomeC2() {
  return (
    <Phone>
      <div className="appbar">
        <Icon d={ICONS.menu} size={18} stroke="var(--ink-2)" />
        <div style={{ flex: 1 }}>
          <div className="title">Прогноз</div>
          <div className="sub">Москва</div>
        </div>
        <Icon d={ICONS.bell} size={18} stroke="var(--ink-2)" />
      </div>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '12px 8px 8px' }}>
          <DayStrip activeIdx={3} />
        </div>
        <div className="pad" style={{ paddingTop: 4 }}>
          <PersonalScore score="5,2" level={2} sub="по 3 вашим аллергенам" />

          <div className="h-eyebrow" style={{ marginTop: 18, marginBottom: 8 }}>Ваши аллергены</div>
          <div className="card" style={{ padding: 0 }}>
            {[
              { name: 'Берёза',  sev: 2, you: true },
              { name: 'Орешник', sev: 0, you: true },
              { name: 'Ольха',   sev: 0, you: true },
            ].map((a, i) => (
              <div key={a.name} className="row" style={{
                padding: '12px 14px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div className="leaf" style={{ width: 28, height: 28, fontSize: 8 }}>{a.name.slice(0,3).toUpperCase()}</div>
                <div style={{ flex: 1, fontSize: 13 }}>{a.name}</div>
                <div style={{ width: 60 }}><SevBar level={a.sev} /></div>
                <div className={'tx-' + a.sev} style={{ fontSize: 10, width: 50, textAlign: 'right' }}>
                  {SEVERITY[a.sev]}
                </div>
              </div>
            ))}
          </div>

          <div className="row" style={{ marginTop: 14, justifyContent: 'space-between' }}>
            <div className="h-eyebrow">Прочие</div>
            <div className="annot">показать →</div>
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

// Home C3 — same structure, allergen level shown as filled circle scale (dots out of 5)
function HomeC3() {
  const colors = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
  const Dots = ({ level }) => (
    <div style={{ display: 'flex', gap: 3 }}>
      {[1,2,3,4,5].map(i => (
        <div key={i} style={{
          width: 8, height: 8, borderRadius: 4,
          background: i <= level ? colors[level] : 'var(--line)',
        }} />
      ))}
    </div>
  );
  return (
    <Phone>
      <div className="appbar">
        <Icon d={ICONS.menu} size={18} stroke="var(--ink-2)" />
        <div style={{ flex: 1 }}>
          <div className="title">Прогноз</div>
          <div className="sub">Москва</div>
        </div>
        <Icon d={ICONS.bell} size={18} stroke="var(--ink-2)" />
      </div>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '12px 8px 8px' }}>
          <DayStrip activeIdx={3} />
        </div>
        <div className="pad" style={{ paddingTop: 4 }}>
          <PersonalScore score="5,2" level={2} sub="по 3 вашим аллергенам" />

          <div className="h-eyebrow" style={{ marginTop: 18, marginBottom: 8 }}>Ваши аллергены</div>
          <div className="card" style={{ padding: 0 }}>
            {[
              { name: 'Берёза',  sev: 2 },
              { name: 'Орешник', sev: 0 },
              { name: 'Ольха',   sev: 0 },
            ].map((a, i) => (
              <div key={a.name} className="row" style={{
                padding: '12px 14px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1, fontSize: 13 }}>{a.name}</div>
                <Dots level={a.sev} />
              </div>
            ))}
          </div>

          <div className="h-eyebrow" style={{ marginTop: 18, marginBottom: 8 }}>Прочие</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {['Дуб','Полынь','Злаки','Маревые','Амброзия','Кладоспориум','Альтернария'].map(n => (
              <span key={n} className="pill">
                <span style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--severity-0)' }} />
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

// Sensitivity-setup screen — referenced by HomeC2/C3 ("по 3 вашим аллергенам")
function SensitivitySetup() {
  const list = [
    { name: 'Берёза',  s: 3 },
    { name: 'Дуб',     s: 0 },
    { name: 'Ольха',   s: 1 },
    { name: 'Полынь',  s: 0 },
    { name: 'Орешник', s: 2 },
    { name: 'Злаки',   s: 0 },
  ];
  const labels = ['нет','лёгкая','средняя','сильная'];
  return (
    <Phone>
      <AppBar title="Чувствительность" sub="настройте для точного индекса" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div style={{ fontSize: 12, color: 'var(--ink-2)', lineHeight: 1.5, marginBottom: 14 }}>
            Отметьте, насколько каждый аллерген влияет на вас лично.
            Индекс на главном будет рассчитан по этим коэффициентам.
          </div>
          {list.map(a => (
            <div key={a.name} className="card" style={{ padding: 12, marginBottom: 8 }}>
              <div className="row" style={{ justifyContent: 'space-between' }}>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{a.name}</div>
                <div style={{ fontSize: 10, color: 'var(--ink-3)' }}>{labels[a.s]}</div>
              </div>
              <div style={{ display: 'flex', gap: 4, marginTop: 8 }}>
                {[0,1,2,3].map(i => (
                  <div key={i} style={{
                    flex: 1, height: 6, borderRadius: 3,
                    background: i <= a.s ? 'var(--accent)' : 'var(--line)',
                  }} />
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

Object.assign(window, { HomeC2, HomeC3, SensitivitySetup });
