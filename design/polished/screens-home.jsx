// Polished Home + Sensitivity screens

function PHome({ indexStyle = 'v2' }) {
  const colors = P_SEV_COLORS;
  const days = [
    { d: 23, wd: 'ср', l: 1 },
    { d: 24, wd: 'чт', l: 1 },
    { d: 25, wd: 'пт', l: 3 },
    { d: 26, wd: 'сб', l: 2 },
    { d: 27, wd: 'вс', l: 3 },
    { d: 28, wd: 'пн', l: 2 },
    { d: 29, wd: 'вт', l: 1 },
  ];
  const activeIdx = 3;
  const IndexCard = IndexScaleBar;

  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        {/* Location row */}
        <div style={{ padding: '14px 16px 0' }}>
          <div className="row" style={{ gap: 6 }}>
            <PIcon d={P_ICONS.loc} size={13} stroke="var(--ink-3)" sw={1.6} />
            <span style={{ fontSize: 12, color: 'var(--ink-2)', fontWeight: 500 }}>Москва</span>
            <PIcon d={P_ICONS.chevD} size={11} stroke="var(--ink-3)" />
            <div className="spacer" />
            <PIcon d={P_ICONS.bell} size={16} stroke="var(--ink-3)" sw={1.4} />
          </div>
        </div>

        {/* Day strip */}
        <div style={{ padding: '12px 10px 8px' }}>
          <div style={{ display: 'flex', gap: 4 }}>
            {days.map((day, i) => {
              const isActive = i === activeIdx;
              return (
                <div key={day.d} style={{
                  flex: 1, padding: '7px 2px 6px',
                  borderRadius: 10,
                  background: isActive ? 'var(--card)' : 'transparent',
                  border: isActive ? '1.5px solid var(--accent)' : '1.5px solid transparent',
                  boxShadow: isActive ? 'var(--shadow-card)' : 'none',
                  textAlign: 'center',
                  transition: 'all 0.15s',
                }}>
                  <div style={{ fontSize: 9, color: isActive ? 'var(--accent-2)' : 'var(--ink-3)', textTransform: 'uppercase', fontWeight: 500, lineHeight: 1 }}>{day.wd}</div>
                  <div className="p-num" style={{ fontSize: 14, fontWeight: isActive ? 600 : 500, marginTop: 3, lineHeight: 1, color: isActive ? 'var(--ink)' : 'var(--ink-2)' }}>{day.d}</div>
                  <div style={{
                    width: 6, height: 6, borderRadius: 3,
                    background: colors[day.l],
                    margin: '5px auto 0',
                  }} />
                </div>
              );
            })}
          </div>
        </div>

        {/* Personal score card */}
        <div style={{ padding: '0 16px 12px' }}>
          <IndexCard />

          {/* Your allergens */}
          <div className="row" style={{ marginTop: 20, marginBottom: 8, justifyContent: 'space-between' }}>
            <div className="p-eyebrow">Ваши аллергены</div>
            <span style={{ fontSize: 10, color: 'var(--accent-2)', fontWeight: 500 }}>настроить</span>
          </div>
          <div className="p-card" style={{ padding: 0 }}>
            {[
              { name: 'Берёза', sev: 2 },
              { name: 'Орешник', sev: 0 },
              { name: 'Ольха', sev: 0 },
            ].map((a, i) => (
              <div key={a.name} className="row" style={{
                padding: '11px 16px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1, fontSize: 13, fontWeight: 500 }}>{a.name}</div>
                <PSevDots level={a.sev} />
                <PIcon d={P_ICONS.chevR} size={12} stroke="var(--ink-3)" sw={1.4} />
              </div>
            ))}
          </div>

          {/* Other allergens */}
          <div className="p-eyebrow" style={{ marginTop: 20, marginBottom: 8 }}>Прочие</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {['Дуб','Полынь','Злаки','Маревые','Амброзия','Кладоспориум','Альтернария'].map(n => (
              <span key={n} className="p-pill" style={{ fontSize: 11, padding: '4px 10px' }}>
                <span className="sev-dot sev-dot-0" style={{ width: 5, height: 5 }} />
                {n}
              </span>
            ))}
          </div>
        </div>
      </div>
      <PTabBar active="home" />
    </PPhone>
  );
}

function PSensitivity() {
  const list = [
    { name: 'Берёза', s: 3 },
    { name: 'Дуб', s: 0 },
    { name: 'Ольха', s: 1 },
    { name: 'Полынь', s: 0 },
    { name: 'Орешник', s: 2 },
    { name: 'Злаки', s: 0 },
    { name: 'Амброзия', s: 0 },
    { name: 'Маревые', s: 0 },
  ];
  const labels = ['нет','лёгкая','средняя','сильная'];

  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 16 }}>
          <div className="row" style={{ gap: 10, marginBottom: 8 }}>
            <PIcon d={P_ICONS.back} size={18} stroke="var(--ink-2)" sw={1.6} />
            <div className="p-display" style={{ fontSize: 22 }}>Чувствительность</div>
          </div>
          <div className="p-body" style={{ marginBottom: 16 }}>
            Насколько каждый аллерген влияет на вас. Это определяет ваш персональный индекс.
          </div>
          <div className="p-card" style={{ padding: 0 }}>
            {list.map((a, i) => (
              <div key={a.name} className="row" style={{
                padding: '12px 16px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
                gap: 10,
              }}>
                <div style={{ width: 72, fontSize: 13, fontWeight: 500 }}>{a.name}</div>
                <div style={{ flex: 1, display: 'flex', gap: 3 }}>
                  {[0,1,2,3].map(j => (
                    <div key={j} style={{
                      flex: 1, height: 6, borderRadius: 3,
                      background: j <= a.s ? 'var(--accent)' : 'var(--line-2)',
                      transition: 'background 0.2s',
                    }} />
                  ))}
                </div>
                <div style={{ fontSize: 10, color: a.s > 0 ? 'var(--ink-2)' : 'var(--ink-3)', width: 52, textAlign: 'right', fontWeight: 500 }}>{labels[a.s]}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <PTabBar active="home" />
    </PPhone>
  );
}

Object.assign(window, { PHome, PSensitivity });
