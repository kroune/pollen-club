// ─── HOME / FORECAST screens ──────────────────────────────────
// 4 variants exploring different ways to answer
// "how bad is it today, and what's coming?"

// Variant A — Hero number + ranked list (closest to original)
function HomeA() {
  return (
    <Phone>
      <AppBar title="Прогноз" sub="26 апреля · Москва" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="card" style={{ padding: 18 }}>
            <div className="h-eyebrow">Сегодня</div>
            <div className="row" style={{ alignItems: 'baseline', marginTop: 6, gap: 8 }}>
              <div className="h-large num tx-2">5,2</div>
              <div className="num" style={{ fontSize: 13, color: 'var(--ink-3)' }}>/ 10</div>
            </div>
            <div style={{ marginTop: 4, marginBottom: 12 }}>
              <SevLabel level={2} /> <span style={{ color: 'var(--ink-3)' }}>· берёза</span>
            </div>
            <SevBar level={2} />
            <div className="row" style={{ marginTop: 14, justifyContent: 'space-between', fontSize: 11, color: 'var(--ink-3)' }}>
              {['Сб','Вс','Пн','Вт','Ср','Чт'].map((d, i) => {
                const lvls = [3,3,2,1,1,0];
                const colors = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
                return (
                  <div key={d} className="col" style={{ alignItems: 'center', gap: 4 }}>
                    <div>{d}</div>
                    <div style={{ width: 8, height: 8, borderRadius: 4, background: colors[lvls[i]] }} />
                  </div>
                );
              })}
            </div>
          </div>

          <div className="h-eyebrow" style={{ marginTop: 18, marginBottom: 8 }}>Все аллергены</div>
          <div className="card" style={{ padding: 0 }}>
            {ALLERGENS.slice(0, 6).map((a, i) => (
              <div key={a.code} className="row" style={{
                padding: '12px 14px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div className="leaf">{a.code}</div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontWeight: 500 }}>{a.name}</div>
                  <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 2 }}>{SEVERITY[a.sev]}</div>
                </div>
                <div style={{ width: 44 }}><SevBar level={a.sev} compact /></div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

// Variant B — Status verdict ("can I go outside?")
function HomeB() {
  return (
    <Phone>
      <AppBar title="Сегодня" sub="26 апреля · Москва" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad-lg">
          <div className="h-eyebrow">Состояние воздуха</div>
          <div className="h-display" style={{ marginTop: 8 }}>
            Будьте осторожны<br />на улице
          </div>
          <div style={{ fontSize: 12, color: 'var(--ink-2)', marginTop: 10, lineHeight: 1.5 }}>
            Берёза пылит на средне-высоком уровне.
            Возьмите антигистаминное и маску, если выходите.
          </div>

          <div className="card" style={{ marginTop: 18, padding: 14 }}>
            <div className="row" style={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <div className="h-eyebrow">Главный аллерген</div>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 20, marginTop: 4 }}>Берёза</div>
                <div className="tx-2" style={{ fontSize: 11, marginTop: 2 }}>Средний · 5,2 балла</div>
              </div>
              <VerticalMeter level={2} />
            </div>
          </div>

          <div className="row" style={{ marginTop: 14, gap: 8 }}>
            <div className="card" style={{ flex: 1, padding: 12 }}>
              <div className="h-eyebrow">Завтра</div>
              <div className="num tx-3" style={{ fontSize: 22, marginTop: 4 }}>6,8</div>
              <div style={{ fontSize: 10, color: 'var(--ink-3)' }}>Высокий</div>
            </div>
            <div className="card" style={{ flex: 1, padding: 12 }}>
              <div className="h-eyebrow">Через 7 дн</div>
              <div className="num tx-1" style={{ fontSize: 22, marginTop: 4 }}>1,4</div>
              <div style={{ fontSize: 10, color: 'var(--ink-3)' }}>Низкий</div>
            </div>
          </div>

          <div className="h-eyebrow" style={{ marginTop: 20, marginBottom: 8 }}>Прочие аллергены</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {ALLERGENS.slice(1, 7).map(a => (
              <span key={a.code} className="pill">
                <span style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--severity-0)' }} />
                {a.name}
              </span>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

// Variant C — 7-day strip first, then list
function HomeC() {
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
      <AppBar title="Аллерго прогноз" sub="Москва" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '12px 8px 8px', display: 'flex', gap: 4 }}>
          {days.map(day => (
            <div key={day.d} style={{
              flex: 1,
              padding: '10px 4px',
              borderRadius: 10,
              background: day.on ? 'var(--card)' : 'transparent',
              border: day.on ? '1px solid var(--accent)' : '1px solid transparent',
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

        <div className="pad" style={{ paddingTop: 4 }}>
          <div className="card" style={{ padding: 16 }}>
            <div className="row" style={{ justifyContent: 'space-between', alignItems: 'baseline' }}>
              <div className="h-eyebrow">26 апреля · сб</div>
              <div className="tx-2" style={{ fontSize: 11 }}>Средний</div>
            </div>
            <div className="row" style={{ alignItems: 'baseline', gap: 6, marginTop: 4 }}>
              <div className="h-display num tx-2">5,2</div>
              <div style={{ fontSize: 11, color: 'var(--ink-3)' }}>пыления, балл</div>
            </div>
            <div style={{ height: 1, background: 'var(--line-2)', margin: '14px 0' }} />
            {ALLERGENS.slice(0, 5).map(a => (
              <div key={a.code} className="row" style={{ padding: '6px 0' }}>
                <div style={{ flex: 1, fontSize: 13 }}>{a.name}</div>
                <div style={{ width: 70 }}><SevBar level={a.sev} /></div>
                <div style={{ fontSize: 10, color: 'var(--ink-3)', width: 56, textAlign: 'right' }}>
                  {SEVERITY[a.sev]}
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

// Variant D — editorial / spread
function HomeD() {
  return (
    <Phone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad-lg" style={{ paddingTop: 22 }}>
          <div className="row" style={{ justifyContent: 'space-between', alignItems: 'center' }}>
            <Icon d={ICONS.menu} size={18} />
            <div className="annot">МСК · сб 26.04</div>
            <Icon d={ICONS.bell} size={18} />
          </div>

          <div className="h-eyebrow" style={{ marginTop: 24 }}>Главный аллерген</div>
          <div style={{
            fontFamily: 'var(--font-display)',
            fontSize: 56, lineHeight: 0.9, letterSpacing: -2,
            marginTop: 8,
          }}>Берёза.</div>
          <div style={{ marginTop: 12 }}>
            <SevLabel level={2} />
          </div>

          <div className="placeholder" style={{ height: 90, marginTop: 18 }}>
            placeholder · botanical mark
          </div>

          <div className="div-h" />
          <div className="annot" style={{ marginBottom: 12 }}>Прогноз на 7 дней</div>
          <div style={{ display: 'flex', alignItems: 'flex-end', gap: 4, height: 70 }}>
            {[2,3,4,3,2,1,1].map((v, i) => {
              const colors = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
              return (
                <div key={i} style={{
                  flex: 1,
                  height: `${v * 16 + 6}px`,
                  background: colors[v],
                  borderRadius: 2,
                }} />
              );
            })}
          </div>
          <div className="row" style={{ justifyContent: 'space-between', marginTop: 6, fontSize: 9, color: 'var(--ink-3)' }}>
            {['сб','вс','пн','вт','ср','чт','пт'].map(d => <div key={d}>{d}</div>)}
          </div>

          <div className="div-h" />
          <div className="row" style={{ justifyContent: 'space-between' }}>
            <div className="annot">Прочие аллергены</div>
            <div className="annot">все →</div>
          </div>
          <div style={{ marginTop: 8, fontSize: 12, lineHeight: 1.7, color: 'var(--ink-2)' }}>
            Дуб · Ольха · Полынь · Орешник · Злаки —{' '}
            <span style={{ color: 'var(--ink-3)' }}>нулевой</span>
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

Object.assign(window, { HomeA, HomeB, HomeC, HomeD });
