// ─── v3 THERAPY — recent-first, multi check-in support ─────
// Feedback: drop "popular" (no data). Recent + repeat is great.
// Allow multiple feeling check-ins per day. Therapy log per dose.

// v3 A — recent-only picker (no popular, since data unavailable). Big "repeat last" CTA.
function TherapyV3A() {
  const recent = [
    { name: 'Цетрин', sub: 'Цетиризин · 10 мг · перорально', last: 'вчера', count: 12 },
    { name: 'Назонекс', sub: 'Мометазон · спрей в нос', last: 'сегодня', count: 8 },
    { name: 'Опатанол', sub: 'Олопатадин · капли в глаза', last: '3 дня назад', count: 4 },
    { name: 'Сингуляр', sub: 'Монтелукаст · 10 мг', last: 'неделю назад', count: 2 },
  ];
  return (
    <Phone>
      <MiniBar back />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 4 }}>
          <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1, marginBottom: 12 }}>Препарат</div>
          <div className="row" style={{ gap: 8, padding: '10px 12px', background: 'var(--paper-2)', borderRadius: 12, marginBottom: 14 }}>
            <Icon d={ICONS.search} size={14} stroke="var(--ink-3)" />
            <div style={{ fontSize: 12, color: 'var(--ink-3)', flex: 1 }}>Название или вещество…</div>
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Ваши препараты</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 14 }}>
            {recent.map(m => (
              <div key={m.name} className="row" style={{
                padding: '10px 12px',
                border: '1px solid var(--line-2)',
                borderRadius: 10,
                background: 'var(--card)',
                gap: 10,
              }}>
                <div className="leaf" style={{
                  width: 28, height: 28, fontSize: 8,
                  background: 'var(--accent)', color: '#fff', border: 'none', flexShrink: 0,
                }}>
                  {m.name[0]}
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 13, fontWeight: 500 }}>{m.name}</div>
                  <div className="annot" style={{ fontSize: 10, marginTop: 1 }}>{m.sub}</div>
                  <div className="annot" style={{ fontSize: 9, marginTop: 2 }}>
                    {m.count} приёмов · {m.last}
                  </div>
                </div>
                <div className="row" style={{ gap: 6 }}>
                  <div className="pill active" style={{ fontSize: 10, padding: '4px 10px' }}>+ принять</div>
                </div>
              </div>
            ))}
          </div>

          <div className="div-h" />
          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Категории</div>
          <div className="card" style={{ padding: 0 }}>
            {['Системного действия', 'Глаза', 'Нос', 'Бронхи', 'Кожа', 'Другие средства'].map((c, i) => (
              <div key={c} className="row" style={{
                padding: '10px 12px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1, fontSize: 12 }}>{c}</div>
                <Icon d={ICONS.chevR} size={12} stroke="var(--ink-3)" />
              </div>
            ))}
          </div>

          <div className="annot" style={{ marginTop: 12 }}>
            Не нашли? <span style={{ color: 'var(--accent-2)' }}>добавить вручную →</span>
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// v3 B — Per-dose log. Each dose stamped to time. Mood check-in same day, multiple times.
// Combines therapy log + symptom log on a single timeline so users can SEE what worked.
function TherapyV3B() {
  const events = [
    { type: 'mood',  time: '07:45', l: 1, label: 'Хорошо', note: 'Проснулся ок' },
    { type: 'med',   time: '08:00', n: 'Цетрин', d: '10 мг' },
    { type: 'mood',  time: '12:30', l: 2, label: 'Терпимо', note: 'Чихнул несколько раз' },
    { type: 'med',   time: '13:15', n: 'Авамис', d: '2 впр.' },
    { type: 'mood',  time: '15:00', l: 1, label: 'Хорошо', note: 'Помогло' },
    { type: 'mood',  time: '19:30', l: 3, label: 'Плохо', note: 'Глаза слезятся' },
    { type: 'med',   time: '20:00', n: 'Опатанол', d: '1 капля' },
  ];
  const moodC = [null, 'var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];

  return (
    <Phone>
      <MiniBar />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '8px 16px 0' }}>
          <DiaryDateRow active={24} />
        </div>

        <div style={{ padding: '12px 16px 0' }}>
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 8 }}>
            <div className="h-eyebrow">Хронология дня</div>
            <div className="row" style={{ gap: 6 }}>
              <span className="pill" style={{ fontSize: 9, padding: '3px 7px' }}>+ самочувствие</span>
              <span className="pill" style={{ fontSize: 9, padding: '3px 7px' }}>+ препарат</span>
            </div>
          </div>

          <div style={{ position: 'relative', paddingLeft: 38 }}>
            <div style={{
              position: 'absolute', left: 30, top: 4, bottom: 4,
              width: 1, background: 'var(--line)',
            }} />
            {events.map((e, i) => (
              <div key={i} style={{ position: 'relative', marginBottom: 8 }}>
                <div className="num" style={{
                  position: 'absolute', left: -36, top: 2,
                  fontSize: 9, color: 'var(--ink-3)', width: 28, textAlign: 'right',
                }}>{e.time}</div>
                {e.type === 'mood' ? (
                  <>
                    <div style={{
                      position: 'absolute', left: -10, top: 3,
                      width: 11, height: 11, borderRadius: 6,
                      background: moodC[e.l], border: '2px solid var(--paper)',
                      boxShadow: '0 0 0 1px var(--line)',
                    }} />
                    <div className="card" style={{ padding: '6px 9px' }}>
                      <div className="row" style={{ gap: 6 }}>
                        <div style={{ fontSize: 11, fontWeight: 500, color: moodC[e.l] }}>{e.label}</div>
                        <div style={{ fontSize: 11, color: 'var(--ink-2)', flex: 1 }}>· {e.note}</div>
                      </div>
                    </div>
                  </>
                ) : (
                  <>
                    <div style={{
                      position: 'absolute', left: -11, top: 1,
                      width: 13, height: 13, borderRadius: 3,
                      background: 'var(--accent)', border: '2px solid var(--paper)',
                      display: 'grid', placeItems: 'center',
                    }}>
                      <Icon d={ICONS.check} size={7} stroke="#fff" sw={2.4} />
                    </div>
                    <div className="row" style={{
                      padding: '6px 9px',
                      border: '1px dashed var(--line)',
                      borderRadius: 6,
                    }}>
                      <div style={{ fontSize: 11, fontWeight: 500 }}>{e.n}</div>
                      <div className="annot" style={{ fontSize: 10 }}>· {e.d}</div>
                    </div>
                  </>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// v3 C — Quick-add bar at bottom + simple list. "What did you take, when?" log feel.
function TherapyV3C() {
  const log = [
    { n: 'Цетрин', d: '10 мг', t: '08:00', taken: true, src: 'утренняя доза' },
    { n: 'Авамис', d: '2 впр.', t: '13:15', taken: true, src: 'после прогулки' },
    { n: 'Опатанол', d: '1 капля', t: '20:00', taken: false, src: 'по необходимости' },
  ];
  return (
    <Phone>
      <MiniBar />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 4 }}>
          <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1, marginBottom: 2 }}>Терапия</div>
          <div className="annot" style={{ fontSize: 10, marginBottom: 14 }}>пт, 24 апреля · 3 дозы</div>
          <div className="row" style={{ gap: 6, marginBottom: 12 }}>
            <span className="pill">Симптомы</span>
            <span className="pill active">Терапия</span>
          </div>

          {/* big "+" tile to add a new dose */}
          <div style={{
            padding: '14px 12px', borderRadius: 12,
            background: 'var(--paper-2)', border: '1px dashed var(--line)',
            display: 'flex', alignItems: 'center', gap: 10, marginBottom: 14,
          }}>
            <div style={{
              width: 28, height: 28, borderRadius: 14,
              background: 'var(--accent)',
              display: 'grid', placeItems: 'center',
            }}>
              <Icon d={ICONS.plus} size={14} stroke="#fff" sw={2.2} />
            </div>
            <div className="col" style={{ flex: 1 }}>
              <div style={{ fontSize: 12, fontWeight: 500 }}>Записать дозу</div>
              <div className="annot" style={{ fontSize: 10 }}>что приняли и когда</div>
            </div>
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Сегодня</div>
          <div className="card" style={{ padding: 0, marginBottom: 14 }}>
            {log.map((m, i) => (
              <div key={i} className="row" style={{
                padding: '10px 12px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div className="num" style={{
                  fontSize: 11, color: 'var(--ink-3)', width: 36,
                }}>{m.t}</div>
                <div style={{
                  width: 8, height: 8, borderRadius: 4,
                  background: m.taken ? 'var(--accent)' : 'var(--line)',
                  flexShrink: 0,
                }} />
                <div style={{ flex: 1 }}>
                  <div className="row" style={{ gap: 4 }}>
                    <div style={{ fontSize: 12, fontWeight: 500, textDecoration: m.taken ? 'none' : 'line-through', color: m.taken ? 'var(--ink)' : 'var(--ink-3)' }}>{m.n}</div>
                    <div className="annot" style={{ fontSize: 10 }}>· {m.d}</div>
                  </div>
                  <div className="annot" style={{ fontSize: 9 }}>{m.src}</div>
                </div>
                {!m.taken && (
                  <div className="pill active" style={{ fontSize: 10, padding: '3px 8px' }}>+ принять</div>
                )}
              </div>
            ))}
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Связь с самочувствием</div>
          <div className="card" style={{ padding: 10 }}>
            <div className="annot" style={{ fontSize: 10, marginBottom: 4 }}>после Цетрина (08:00)</div>
            <div className="row" style={{ gap: 4 }}>
              <span className="pill" style={{ fontSize: 9, padding: '2px 7px', borderColor: 'var(--severity-1)', color: 'var(--severity-1)' }}>хорошо</span>
              <span className="pill" style={{ fontSize: 9, padding: '2px 7px', borderColor: 'var(--severity-2)', color: 'var(--severity-2)' }}>терпимо</span>
              <span className="annot" style={{ fontSize: 10 }}>2 ч · 4 ч</span>
            </div>
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// v3 D — Repeat-last + smart suggestions. One screen, no drilldown for common case.
function TherapyV3D() {
  const lastDoses = [
    { n: 'Цетрин', d: '10 мг', when: 'каждое утро 08:00' },
    { n: 'Назонекс', d: '2 впр.', when: 'каждый вечер 20:00' },
  ];
  return (
    <Phone>
      <MiniBar back />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 4 }}>
          <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1, marginBottom: 12 }}>Повторить дозу</div>
          <div className="annot" style={{ marginBottom: 10, fontSize: 10 }}>
            ваши обычные приёмы — нажмите, чтобы записать сейчас
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 14 }}>
            {lastDoses.map(m => (
              <div key={m.n} className="row" style={{
                padding: '12px 14px',
                background: 'var(--accent)',
                borderRadius: 10,
                color: '#fff',
                gap: 10,
              }}>
                <Icon d={ICONS.check} size={14} stroke="#fff" sw={2.2} />
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 13, fontWeight: 500 }}>{m.n} · {m.d}</div>
                  <div style={{ fontSize: 10, opacity: 0.85, marginTop: 1 }}>{m.when}</div>
                </div>
                <div style={{ fontSize: 11, opacity: 0.85 }}>сейчас →</div>
              </div>
            ))}
          </div>

          <div className="div-h" />

          <div className="row" style={{ gap: 8, padding: '10px 12px', background: 'var(--paper-2)', borderRadius: 12, marginBottom: 14 }}>
            <Icon d={ICONS.search} size={14} stroke="var(--ink-3)" />
            <div style={{ fontSize: 12, color: 'var(--ink-3)', flex: 1 }}>Найти другой препарат…</div>
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Все ваши препараты</div>
          <div className="card" style={{ padding: 0 }}>
            {[
              { n: 'Цетрин', d: 'Цетиризин · 10 мг' },
              { n: 'Назонекс', d: 'Мометазон · спрей' },
              { n: 'Опатанол', d: 'Олопатадин · капли' },
              { n: 'Сингуляр', d: 'Монтелукаст · 10 мг' },
            ].map((m, i) => (
              <div key={m.n} className="row" style={{
                padding: '10px 12px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 12, fontWeight: 500 }}>{m.n}</div>
                  <div className="annot" style={{ fontSize: 10 }}>{m.d}</div>
                </div>
                <Icon d={ICONS.chevR} size={11} stroke="var(--ink-3)" />
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

Object.assign(window, { TherapyV3A, TherapyV3B, TherapyV3C, TherapyV3D });
