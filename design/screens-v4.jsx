// ─── v4 — addressing maxim's review ────────────────────────
// 1) "another useless header" → drop top app-bar "Дневник + дата" on Diary;
//    the date row + day context is enough.
// 2) "make as a separate line on the plot, hide/show, focus on allergen" →
//    add a "самочувствие" overlay LINE on the pollen chart (toggleable).
// 3) "takes a lot of space + duplicates info" → make day-strip more compact /
//    eliminate redundancy between app-bar date + day strip on diary screens.

// ── DETAIL v4 — chart with toggleable "самочувствие" overlay line
function DetailA4() {
  const [showFeel, setShowFeel] = React.useState(true);
  const pollen = [0,0,1,1,2,2,3,4,3,3,2,2,1,1];
  const feeling = [null,null,0,1,1,2,2,3,3,2,1,null,null,null]; // user-logged
  const today = 9;
  const colW = 26;
  const w = colW * pollen.length;
  const h = 150, padY = 8;
  const max = 5;
  const x = (i) => colW / 2 + i * colW;
  const y = (v) => h - padY - (v / max) * (h - padY * 2);
  const pollenLine = pollen.map((v, i) => `${i === 0 ? 'M' : 'L'} ${x(i)} ${y(v)}`).join(' ');
  const pollenArea = `${pollenLine} L ${x(pollen.length - 1)} ${h - padY} L ${x(0)} ${h - padY} Z`;
  // build feeling polyline only for entries with values
  const feelPts = feeling.map((v, i) => v == null ? null : { i, v }).filter(Boolean);
  const feelLine = feelPts.length === 0 ? '' :
    feelPts.map((p, i) => `${i === 0 ? 'M' : 'L'} ${x(p.i)} ${y(p.v)}`).join(' ');
  const colors = ['#c9d3cd','#7fa86a','#d9b94a','#d97a3a','#c44545','#7d3a8a'];
  const startDay = 17;

  return (
    <Phone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingBottom: 0, paddingTop: 14 }}>
          <div className="row" style={{ gap: 10, marginBottom: 4 }}>
            <Icon d={ICONS.chevR} size={16} stroke="var(--ink-2)" sw={1.6} style={{ transform: 'rotate(180deg)', flexShrink: 0 }} />
            <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1 }}>Берёза</div>
          </div>
          <div className="row" style={{ alignItems: 'baseline', gap: 8, marginTop: 10 }}>
            <div className="h-large num tx-2">5,2</div>
            <div style={{ fontSize: 11, color: 'var(--ink-3)' }}>/ 10 · сегодня</div>
            <div className="spacer" />
            <SevLabel level={2} />
          </div>

          <div className="row" style={{ justifyContent: 'space-between', marginTop: 16, marginBottom: 6 }}>
            <div className="h-eyebrow">Динамика по дням</div>
            <div className="annot" style={{ fontSize: 9 }}>↔ листайте</div>
          </div>

          {/* legend / overlay toggle */}
          <div className="row" style={{ gap: 10, marginBottom: 6, fontSize: 10 }}>
            <div className="row" style={{ gap: 5 }}>
              <span style={{
                width: 18, height: 3, borderRadius: 2,
                background: 'var(--ink)',
              }} />
              <span style={{ color: 'var(--ink-2)' }}>пыление</span>
            </div>
            <div
              className="row"
              onClick={() => setShowFeel(!showFeel)}
              style={{ gap: 5, cursor: 'pointer', opacity: showFeel ? 1 : 0.45 }}>
              <span style={{
                width: 18, height: 0,
                borderTop: '2px dashed #c44545',
              }} />
              <span style={{ color: 'var(--ink-2)' }}>ваше самочувствие</span>
              <span style={{
                width: 14, height: 14, borderRadius: 7, marginLeft: 2,
                border: '1px solid var(--line)',
                background: showFeel ? 'var(--accent)' : 'transparent',
                display: 'grid', placeItems: 'center',
              }}>
                {showFeel && <Icon d={ICONS.check} size={9} stroke="#fff" sw={2.4} />}
              </span>
            </div>
          </div>
        </div>

        {/* edge-to-edge horizontal scroll, NO horizontal padding */}
        <div style={{ display: 'flex' }}>
          {/* y-axis label gutter */}
          <svg width={26} height={h} style={{ flexShrink: 0, display: 'block' }}>
            {[1,2,3,4,5].map(i => (
              <text key={i} x={22} y={y(i) + 3}
                fontSize="8" textAnchor="end" fill="var(--ink-3)"
                fontFamily="JetBrains Mono">{i}</text>
            ))}
          </svg>
          <div style={{ overflowX: 'auto', overflowY: 'hidden', WebkitOverflowScrolling: 'touch', flex: 1 }}>
          <svg width={w} height={h} style={{ display: 'block' }}>
            <defs>
              <linearGradient id="sevGradV4" x1="0" y1="1" x2="0" y2="0">
                <stop offset="0%" stopColor={colors[0]} />
                <stop offset="20%" stopColor={colors[1]} />
                <stop offset="40%" stopColor={colors[2]} />
                <stop offset="60%" stopColor={colors[3]} />
                <stop offset="80%" stopColor={colors[4]} />
                <stop offset="100%" stopColor={colors[5]} />
              </linearGradient>
              <clipPath id="areaClipV4"><path d={pollenArea} /></clipPath>
            </defs>
            {[1,2,3,4,5].map(i => (
              <line key={i} x1={0} y1={y(i)} x2={w} y2={y(i)} stroke="var(--line-2)" strokeDasharray="2 3" />
            ))}
            <rect x="0" y="0" width={w} height={h} fill="url(#sevGradV4)" opacity="0.4" clipPath="url(#areaClipV4)" />
            {pollen.map((_, i) => i > 0 && (
              <line key={i} x1={i * colW} y1={padY} x2={i * colW} y2={h - padY} stroke="var(--line-2)" strokeWidth="0.5" opacity="0.5" />
            ))}
            {/* primary pollen line */}
            <path d={pollenLine} fill="none" stroke="var(--ink)" strokeWidth="1.6" />
            {/* secondary feeling line, dashed */}
            {showFeel && feelLine && (
              <>
                <path d={feelLine} fill="none" stroke="#c44545" strokeWidth="1.4" strokeDasharray="3 2" />
                {feelPts.map(p => (
                  <circle key={p.i} cx={x(p.i)} cy={y(p.v)} r="2.2" fill="#fff" stroke="#c44545" strokeWidth="1.2" />
                ))}
              </>
            )}
            <line x1={x(today)} y1={padY} x2={x(today)} y2={h - padY} stroke="var(--ink)" strokeDasharray="3 2" strokeWidth="1" />
            {pollen.map((v, i) => (
              <circle key={i} cx={x(i)} cy={y(v)} r={i === today ? 4 : 2.2}
                fill={colors[v]} stroke={i === today ? '#fff' : 'none'} strokeWidth={i === today ? 1.4 : 0} />
            ))}
            {pollen.map((_, i) => {
              const d = startDay + i;
              const day = d > 30 ? d - 30 : d;
              return (
                <text key={i} x={x(i)} y={h - padY + 14}
                  fontSize="9" textAnchor="middle"
                  fill={i === today ? 'var(--ink)' : 'var(--ink-3)'}
                  fontFamily="JetBrains Mono"
                  fontWeight={i === today ? 600 : 400}>
                  {day}
                </text>
              );
            })}
          </svg>
          </div>
        </div>

        <div className="pad" style={{ paddingTop: 14 }}>
          <div className="h-eyebrow" style={{ marginBottom: 6 }}>О периоде</div>
          <div style={{ fontSize: 12, lineHeight: 1.55, color: 'var(--ink-2)' }}>
            Активная фаза. Обычно пыление берёзы длится около 3 недель. Уточняется на основании данных пыльцеуловителей и фенологии.
          </div>
        </div>
      </div>
      <TabBar active="home" />
    </Phone>
  );
}

// ── DIARY v4 — drop the redundant app-bar header; the date row IS the header
// Combines date pill row, mood, body, and therapy on one screen, tighter

function BodyShapeV4({ activeZones = {} }) {
  return (
    <svg viewBox="0 0 100 180" style={{ width: '100%', height: '100%', display: 'block' }}>
      <ellipse cx="50" cy="22" rx="13" ry="15" fill="var(--paper-2)" stroke="var(--line)" />
      <rect x="44" y="34" width="12" height="6" fill="var(--paper-2)" stroke="var(--line)" />
      <path d="M 30 40 Q 30 38 35 38 L 65 38 Q 70 38 70 40 L 72 95 Q 72 98 68 98 L 32 98 Q 28 98 28 95 Z"
        fill="var(--paper-2)" stroke="var(--line)" />
      <path d="M 30 42 L 18 80 L 22 95" fill="none" stroke="var(--line)" strokeWidth="1" />
      <path d="M 70 42 L 82 80 L 78 95" fill="none" stroke="var(--line)" strokeWidth="1" />
      <path d="M 38 98 L 36 165" fill="none" stroke="var(--line)" strokeWidth="1" />
      <path d="M 62 98 L 64 165" fill="none" stroke="var(--line)" strokeWidth="1" />
      {[
        { id: 'eyes',  cx: 50, cy: 18, r: 5 },
        { id: 'nose',  cx: 50, cy: 24, r: 4 },
        { id: 'throat',cx: 50, cy: 38, r: 5 },
        { id: 'chest', cx: 50, cy: 70, r: 9 },
        { id: 'skin',  cx: 26, cy: 90, r: 6 },
      ].map(z => {
        const count = activeZones[z.id];
        const has = !!count;
        return (
          <g key={z.id}>
            <circle cx={z.cx} cy={z.cy} r={z.r}
              fill={has ? 'rgba(217,122,58,0.18)' : 'transparent'}
              stroke={has ? 'var(--severity-3)' : 'var(--ink-3)'}
              strokeWidth={0.8}
              strokeDasharray={has ? '' : '2 2'} />
            {has && (
              <text x={z.cx} y={z.cy + 1.5} textAnchor="middle" fontSize="6" fontWeight="600" fill="var(--severity-3)" fontFamily="JetBrains Mono">{count}</text>
            )}
          </g>
        );
      })}
    </svg>
  );
}

// Date row that doubles as the header — month label + day pills, no separate app bar
function DateHeader({ active = 24 }) {
  return (
    <div style={{ padding: '10px 16px 4px', borderBottom: '1px solid var(--line-2)' }}>
      <div className="row" style={{ justifyContent: 'space-between', marginBottom: 6 }}>
        <div className="h-eyebrow">Апрель 2026</div>
        <div className="row" style={{ gap: 8 }}>
          <Icon d={ICONS.chevR} size={12} stroke="var(--ink-3)" sw={1.6} style={{ transform: 'rotate(180deg)' }} />
          <Icon d={ICONS.cal} size={12} stroke="var(--ink-3)" />
          <Icon d={ICONS.chevR} size={12} stroke="var(--ink-3)" sw={1.6} />
        </div>
      </div>
      <div className="row" style={{ gap: 4 }}>
        {[20,21,22,23,24,25,26].map(d => (
          <div key={d} className="col" style={{
            flex: 1, alignItems: 'center', padding: '4px 0',
            background: d === active ? 'var(--accent)' : 'transparent',
            color: d === active ? '#fff' : 'var(--ink-2)',
            borderRadius: 8,
          }}>
            <div style={{ fontSize: 8, opacity: .7 }}>{['пн','вт','ср','чт','пт','сб','вс'][d - 20]}</div>
            <div className="num" style={{ fontSize: 12 }}>{d}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

// Diary v4 A — no app bar, date IS the header. Symptoms + therapy stacked.
function DiaryV4A() {
  return (
    <Phone>
      <DateHeader active={24} />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 12 }}>
          <div className="h-eyebrow" style={{ marginBottom: 6 }}>Самочувствие</div>
          <div className="row" style={{ gap: 5, marginBottom: 12 }}>
            {[
              { l: 'Хорошо',  c: 'var(--severity-1)' },
              { l: 'Терпимо', c: 'var(--severity-2)', on: true },
              { l: 'Плохо',   c: 'var(--severity-3)' },
            ].map(o => (
              <div key={o.l} className="row" style={{
                flex: 1, padding: '7px 8px', gap: 5, justifyContent: 'center',
                border: `1px solid ${o.on ? o.c : 'var(--line)'}`,
                borderRadius: 8,
                background: o.on ? 'rgba(217,185,74,0.12)' : 'transparent',
              }}>
                <div style={{ width: 6, height: 6, borderRadius: 3, background: o.c }} />
                <div style={{ fontSize: 11 }}>{o.l}</div>
              </div>
            ))}
          </div>

          <div className="row" style={{ alignItems: 'flex-start', marginBottom: 8 }}>
            <div style={{ width: 110, height: 200, flexShrink: 0 }}>
              <BodyShapeV4 activeZones={{ nose: 2 }} />
            </div>
            <div className="col" style={{ flex: 1, paddingLeft: 8, gap: 6 }}>
              <div className="h-eyebrow">Симптомы · Нос</div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4 }}>
                {[
                  { l: 'Зуд', on: true },
                  { l: 'Заложенность', on: true },
                  { l: 'Ринорея' },
                  { l: 'Чихание' },
                  { l: 'Кровотечения' },
                ].map(s => (
                  <span key={s.l} className={'pill ' + (s.on ? 'active' : '')} style={{ fontSize: 10, padding: '3px 8px' }}>{s.l}</span>
                ))}
              </div>
              <div className="annot" style={{ fontSize: 10, marginTop: 4 }}>+ другую зону</div>
            </div>
          </div>

          <div className="div-h" />
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 6 }}>
            <div className="h-eyebrow">Терапия</div>
            <div className="annot" style={{ fontSize: 10, color: 'var(--accent-2)' }}>+ препарат</div>
          </div>
          <div className="card" style={{ padding: 0 }}>
            {[
              { n: 'Цетрин',   d: '10 мг',  t: '08:00', taken: true },
              { n: 'Назонекс', d: '2 впр.', t: '20:00', taken: false },
            ].map((m, i) => (
              <div key={m.n} className="row" style={{
                padding: '8px 12px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div className="num" style={{ fontSize: 10, color: 'var(--ink-3)', width: 32 }}>{m.t}</div>
                <div style={{
                  width: 16, height: 16, borderRadius: 8,
                  background: m.taken ? 'var(--accent)' : 'transparent',
                  border: m.taken ? 'none' : '1.5px solid var(--line)',
                  display: 'grid', placeItems: 'center', flexShrink: 0,
                }}>
                  {m.taken && <Icon d={ICONS.check} size={9} stroke="#fff" sw={2.4} />}
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 12 }}>{m.n}</div>
                  <div className="annot" style={{ fontSize: 9 }}>{m.d}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

// Diary v4 B — even tighter: collapsed date as a single chip, more space for content
function DiaryV4B() {
  return (
    <Phone>
      <div style={{ padding: '10px 16px 8px', borderBottom: '1px solid var(--line-2)' }}>
        <div className="row" style={{ justifyContent: 'space-between' }}>
          <div className="row" style={{ gap: 8 }}>
            <div style={{
              padding: '4px 10px', borderRadius: 12,
              background: 'var(--accent)', color: '#fff',
              fontSize: 12, fontWeight: 500,
            }}>пт, 24 апр</div>
            <Icon d={ICONS.chevR} size={12} stroke="var(--ink-3)" />
          </div>
          <div className="row" style={{ gap: 12 }}>
            <Icon d={ICONS.cal} size={14} stroke="var(--ink-2)" />
            <Icon d={ICONS.menu} size={14} stroke="var(--ink-2)" />
          </div>
        </div>
      </div>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          <div className="row" style={{ gap: 5, marginBottom: 14 }}>
            {[
              { l: 'Хорошо',  c: 'var(--severity-1)' },
              { l: 'Терпимо', c: 'var(--severity-2)', on: true },
              { l: 'Плохо',   c: 'var(--severity-3)' },
            ].map(o => (
              <div key={o.l} className="row" style={{
                flex: 1, padding: '8px 8px', gap: 5, justifyContent: 'center',
                border: `1px solid ${o.on ? o.c : 'var(--line)'}`,
                borderRadius: 8,
                background: o.on ? 'rgba(217,185,74,0.12)' : 'transparent',
              }}>
                <div style={{ width: 6, height: 6, borderRadius: 3, background: o.c }} />
                <div style={{ fontSize: 12 }}>{o.l}</div>
              </div>
            ))}
          </div>

          <div className="row" style={{ alignItems: 'flex-start' }}>
            <div style={{ width: 130, height: 230, flexShrink: 0 }}>
              <BodyShapeV4 activeZones={{ nose: 2 }} />
            </div>
            <div className="col" style={{ flex: 1, paddingLeft: 10, gap: 6 }}>
              <div className="h-eyebrow">Активная зона</div>
              <div style={{ fontSize: 13, fontWeight: 500 }}>Нос</div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4 }}>
                {[
                  { l: 'Зуд', on: true },
                  { l: 'Заложенность', on: true },
                  { l: 'Ринорея' },
                  { l: 'Чихание' },
                ].map(s => (
                  <span key={s.l} className={'pill ' + (s.on ? 'active' : '')} style={{ fontSize: 10, padding: '3px 8px' }}>{s.l}</span>
                ))}
              </div>

              <div className="div-h" style={{ margin: '8px 0' }} />

              <div className="h-eyebrow">Терапия</div>
              <div className="row" style={{ gap: 4 }}>
                <span className="pill active" style={{ fontSize: 10, padding: '3px 8px' }}>
                  <Icon d={ICONS.check} size={9} stroke="#fff" sw={2.4} />
                  Цетрин
                </span>
                <span className="pill" style={{ fontSize: 10, padding: '3px 8px' }}>+ препарат</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

Object.assign(window, { DetailA4, DiaryV4A, DiaryV4B });
