// Polished misc screens: Pheno, Feed, Map, Reference, Settings, Onboarding

// ── PHENOLOGY ──
function PPheno() {
  const stages = [
    { n: 1, l: 'Начало сокодвижения', done: true },
    { n: 2, l: 'Набухание почек', done: true },
    { n: 3, l: 'Распускание почек', done: true, cur: true },
    { n: 4, l: 'Развёртывание листьев' },
    { n: 5, l: 'Начало цветения' },
    { n: 6, l: 'Завершение цветения' },
  ];
  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 18 }}>
          <div className="p-display" style={{ fontSize: 24 }}>Берёза</div>
          <div className="p-annot" style={{ fontSize: 10, marginTop: 4, marginBottom: 18 }}>Москва, Шереметьево · фенология</div>
          <div className="p-card" style={{ padding: 16 }}>
            <div className="p-eyebrow">Текущая стадия</div>
            <div className="p-display" style={{ fontSize: 20, marginTop: 6 }}>№3 · Распускание почек</div>
            <div className="p-annot" style={{ fontSize: 10, marginTop: 6 }}>Отметили 24 апр · 3 дня назад</div>
          </div>

          <div className="p-eyebrow" style={{ marginTop: 24, marginBottom: 10 }}>Все стадии</div>
          <div style={{ position: 'relative', paddingLeft: 30 }}>
            <div style={{ position: 'absolute', left: 12, top: 10, bottom: 10, width: 1.5, background: 'var(--line-2)', borderRadius: 1 }} />
            {stages.map(s => (
              <div key={s.n} style={{ position: 'relative', paddingBottom: 16 }}>
                <div style={{
                  position: 'absolute', left: -24, top: 2,
                  width: 24, height: 24, borderRadius: 12,
                  background: s.cur ? 'var(--accent)' : s.done ? 'var(--paper-2)' : 'transparent',
                  border: s.done || s.cur ? 'none' : '1.5px dashed var(--line)',
                  display: 'grid', placeItems: 'center',
                  fontFamily: 'var(--font-mono)', fontSize: 9, fontWeight: 600,
                  color: s.cur ? '#fff' : s.done ? 'var(--accent-2)' : 'var(--ink-3)',
                  boxShadow: s.cur ? '0 2px 8px rgba(61,122,90,0.25)' : 'none',
                }}>
                  {s.done && !s.cur ? <PIcon d={P_ICONS.check} size={12} stroke="var(--accent-2)" sw={2} /> : s.n}
                </div>
                <div style={{ fontSize: 13, fontWeight: s.cur ? 600 : 400, color: s.done || s.cur ? 'var(--ink)' : 'var(--ink-3)' }}>
                  Стадия №{s.n}
                </div>
                <div className="p-annot" style={{ fontSize: 10, marginTop: 1 }}>{s.l}</div>
              </div>
            ))}
          </div>
        </div>
        <div style={{ position: 'absolute', right: 16, bottom: 64 }}>
          <div className="p-fab"><PIcon d={P_ICONS.plus} size={20} stroke="#fff" sw={1.8} /></div>
        </div>
      </div>
      <PTabBar active="pheno" />
    </PPhone>
  );
}

// ── FEED ──
function PFeed() {
  const posts = [
    { name: 'Александра В.', loc: 'Люберцы', t: '6 апр',
      txt: 'Солнечная сторона. Часть пыльников закрыты в серёжках, часть пустые. Неравномерно даже на одном дереве.',
      img: true },
    { name: 'Алсу Г.', loc: 'Набережные Челны',
      txt: '11 градусов. Дочка начала почихивать.' },
    { name: 'Андрей · эксперт', loc: 'Мнение', t: '16 апр', expert: true,
      txt: 'Берёза пылит во всех уголках страны. Индекс самочувствия — 8 баллов, уверенная красная зона.' },
  ];
  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '16px 16px 8px' }}>
          <div className="p-display" style={{ fontSize: 24 }}>Сообщество</div>
        </div>
        <div style={{ display: 'flex', gap: 6, padding: '8px 16px 14px', overflow: 'auto' }}>
          {['Все','Друзья','Эксперты','Медиа'].map((t, i) => (
            <span key={t} className={'p-pill ' + (i === 0 ? 'active' : '')}>{t}</span>
          ))}
        </div>

        {posts.map((p, i) => (
          <div key={i} style={{ padding: '16px', borderTop: '1px solid var(--line-2)' }}>
            <div className="row" style={{ marginBottom: 10 }}>
              <div style={{
                width: 32, height: 32, borderRadius: 16,
                background: p.expert ? 'var(--accent)' : 'var(--paper-2)',
                border: p.expert ? 'none' : '1px solid var(--line-2)',
                color: '#fff', display: 'grid', placeItems: 'center',
                fontSize: 11, fontFamily: 'var(--font-mono)', fontWeight: 600,
              }}>
                {p.expert ? '★' : <span style={{ color: 'var(--ink-3)' }}>{p.name[0]}</span>}
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{p.name}</div>
                <div className="p-annot" style={{ fontSize: 10 }}>{p.loc}{p.t ? ' · ' + p.t : ''}</div>
              </div>
            </div>
            {p.img && <div className="p-placeholder" style={{ height: 120, marginBottom: 12, borderRadius: 12 }}>фото от участника</div>}
            <div className="p-body">{p.txt}</div>
          </div>
        ))}
      </div>
      <PTabBar active="feed" />
    </PPhone>
  );
}

// ── MAP ──
const P_MAP_PINS = [
  { x: 22, y: 30, n: 3, sev: 3 },
  { x: 38, y: 42, n: 9, sev: 2 },
  { x: 54, y: 22, n: 1, sev: 1 },
  { x: 64, y: 50, n: 11, sev: 3 },
  { x: 30, y: 58, n: 2, sev: 3 },
  { x: 70, y: 70, n: 6, sev: 3 },
  { x: 50, y: 75, n: 9, sev: 2 },
  { x: 80, y: 38, n: 1, sev: 1 },
];
const P_MAP_SEV_COLORS = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
const P_MAP_SEV_LABELS = ['нулевой','низкий','средний','высокий','оч. выс.','экстра'];

function PMap() {
  return (
    <PPhone>
      <div style={{ flex: 1, position: 'relative' }}>
        {/* Map background */}
        <div style={{ position: 'absolute', inset: 0, background: '#eef2ec' }}>
          <svg width="100%" height="100%" viewBox="0 0 260 400" style={{ position: 'absolute', inset: 0 }} preserveAspectRatio="none">
            <rect width="260" height="400" fill="#f0f4ed" />
            {[...Array(8)].map((_, i) => (
              <path key={i}
                d={`M 0 ${50 + i * 45} Q ${130 + (i % 2 ? 30 : -30)} ${30 + i * 45} 260 ${60 + i * 45}`}
                stroke="#dde5d6" fill="none" strokeWidth="1" />
            ))}
            <path d="M 0 200 L 260 200" stroke="#cdd5c5" strokeWidth="2" strokeDasharray="3 3" />
          </svg>

          {/* Pins */}
          {P_MAP_PINS.map((p, i) => (
            <div key={i} style={{
              position: 'absolute', left: `${p.x}%`, top: `${p.y}%`,
              transform: 'translate(-50%, -100%)',
            }}>
              <div style={{
                width: 24, height: 24, borderRadius: '50% 50% 50% 0',
                transform: 'rotate(-45deg)',
                background: P_MAP_SEV_COLORS[p.sev],
                boxShadow: '0 2px 8px rgba(0,0,0,0.18)',
                display: 'grid', placeItems: 'center',
              }}>
                <div className="p-num" style={{ transform: 'rotate(45deg)', color: '#fff', fontSize: 10, fontWeight: 700 }}>{p.n}</div>
              </div>
            </div>
          ))}
        </div>

        {/* Trait pills */}
        <div style={{
          position: 'absolute', top: 12, left: 0, right: 0,
          padding: '0 12px', display: 'flex', gap: 5, overflowX: 'auto',
        }}>
          {['Друзья','АГ','АСИТ','Бризер','Дети','Маски'].map((t, i) => (
            <span key={t} style={{
              flexShrink: 0, padding: '5px 11px', fontSize: 11, fontWeight: 500,
              background: i === 1 ? 'var(--accent)' : '#fff',
              color: i === 1 ? '#fff' : 'var(--ink-2)',
              borderRadius: 12, border: 'none',
              boxShadow: '0 1px 5px rgba(0,0,0,0.12)',
            }}>{t}</span>
          ))}
        </div>

        {/* Allergen chip */}
        <div style={{
          position: 'absolute', top: 44, left: '50%', transform: 'translateX(-50%)',
          padding: '4px 12px', background: '#fff', borderRadius: 14,
          boxShadow: '0 1px 5px rgba(0,0,0,0.12)',
          fontSize: 11, fontWeight: 600, display: 'flex', alignItems: 'center', gap: 6,
        }}>
          <PIcon d={P_ICONS.leaf} size={12} stroke="var(--accent)" sw={1.6} />
          Берёза
          <PIcon d={P_ICONS.chevD} size={10} stroke="var(--ink-3)" />
        </div>

        {/* Severity legend */}
        <div style={{
          position: 'absolute', right: 10, top: '50%', transform: 'translateY(-50%)',
          display: 'flex', flexDirection: 'column',
          background: '#fff', padding: 5, borderRadius: 8,
          boxShadow: '0 2px 8px rgba(0,0,0,0.12)',
        }}>
          {[5,4,3,2,1].map((l, i) => (
            <div key={l} className="row" style={{ gap: 5, padding: '1px 0' }}>
              <div style={{
                width: 14, height: 16, background: P_MAP_SEV_COLORS[l],
                borderRadius: i === 0 ? '3px 3px 0 0' : i === 4 ? '0 0 3px 3px' : 0,
              }} />
              <div style={{ fontSize: 8, color: 'var(--ink-3)' }}>{P_MAP_SEV_LABELS[l]}</div>
            </div>
          ))}
        </div>

        {/* My location */}
        <div style={{
          position: 'absolute', bottom: 14, right: 12,
          width: 40, height: 40, borderRadius: 14,
          background: '#fff', boxShadow: 'var(--shadow-elevated)',
          display: 'grid', placeItems: 'center',
        }}>
          <PIcon d={P_ICONS.loc} size={18} stroke="var(--accent)" sw={1.6} />
        </div>
      </div>
      <PTabBar active="map" />
    </PPhone>
  );
}

// ── REFERENCE ──
// Variant B: compact header bar, centred title, icon buttons
function PReference() {
  return (
    <PPhone>
      {/* Header bar */}
      <div style={{
        display: 'flex', alignItems: 'center', gap: 8,
        padding: '12px 14px 10px',
        background: 'var(--card)',
        borderBottom: '1px solid var(--line-2)',
        flexShrink: 0,
      }}>
        <div style={{
          width: 32, height: 32, borderRadius: 10,
          background: 'var(--paper-2)',
          display: 'grid', placeItems: 'center',
        }}>
          <PIcon d={P_ICONS.back} size={16} stroke="var(--ink-2)" sw={1.6} />
        </div>
        <div style={{ flex: 1, textAlign: 'center' }}>
          <div style={{ fontSize: 15, fontWeight: 600, letterSpacing: -0.2 }}>Справочник</div>
        </div>
        <div style={{
          width: 32, height: 32, borderRadius: 10,
          background: 'var(--paper-2)',
          display: 'grid', placeItems: 'center',
        }}>
          <PIcon d={P_ICONS.search} size={15} stroke="var(--ink-2)" sw={1.6} />
        </div>
      </div>

      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {P_ALLERGENS.slice(0, 6).map(a => (
              <div key={a.code} className="p-card" style={{ padding: 14 }}>
                <div className="p-placeholder" style={{ height: 64, marginBottom: 10, borderRadius: 10 }}>{a.code}</div>
                <div style={{ fontSize: 13, fontWeight: 600 }}>{a.name}</div>
                <div className="p-annot" style={{ fontSize: 10, marginTop: 3 }}>
                  {a.sev > 0 ? <PSevLabel level={a.sev} /> : <span style={{ color: 'var(--ink-3)' }}>не активен</span>}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <PTabBar active="home" />
    </PPhone>
  );
}

// ── SETTINGS ──
function PSettings() {
  const items = [
    { l: 'Язык', v: 'Русский' },
    { l: 'Регион мониторинга', v: 'Москва' },
    { l: 'Основной аллерген', v: 'Берёза' },
    { l: 'Друзья', v: '4 участника' },
  ];
  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 16 }}>
          <div className="row" style={{ gap: 10, marginBottom: 16 }}>
            <PIcon d={P_ICONS.back} size={18} stroke="var(--ink-2)" sw={1.6} />
            <div className="p-display" style={{ fontSize: 22 }}>Настройки</div>
          </div>
          <div className="p-card" style={{ padding: 16, marginBottom: 20 }}>
            <div className="p-annot" style={{ fontSize: 9 }}>КОД УЧАСТНИКА</div>
            <div className="row" style={{ alignItems: 'baseline', marginTop: 6 }}>
              <div className="p-num" style={{ fontSize: 24, fontWeight: 600, letterSpacing: 1 }}>1126105</div>
              <div className="spacer" />
              <span style={{ fontSize: 11, color: 'var(--accent-2)', fontWeight: 500 }}>копировать</span>
            </div>
          </div>

          <div className="p-eyebrow" style={{ marginBottom: 8 }}>Основные</div>
          <div className="p-card" style={{ padding: 0 }}>
            {items.map((it, i) => (
              <div key={it.l} className="row" style={{
                padding: '13px 16px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1, fontSize: 13, fontWeight: 500 }}>{it.l}</div>
                {it.v && <div style={{ fontSize: 12, color: 'var(--ink-3)' }}>{it.v}</div>}
                <PIcon d={P_ICONS.chevR} size={13} stroke="var(--ink-3)" sw={1.4} />
              </div>
            ))}
          </div>
        </div>
      </div>
      <PTabBar active="home" />
    </PPhone>
  );
}

// ── ONBOARDING ──
function POnboard() {
  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '36px 20px 20px' }}>
          <div className="p-annot" style={{ fontSize: 10 }}>Шаг 1 из 3</div>
          <div className="p-display" style={{ marginTop: 10, fontSize: 28 }}>
            На что у&nbsp;вас<br />реакция?
          </div>
          <div className="p-body" style={{ marginTop: 10 }}>
            Выберите основной аллерген. Можно изменить позже.
          </div>

          <div style={{ marginTop: 24, display: 'flex', flexDirection: 'column', gap: 8 }}>
            {P_ALLERGENS.slice(0, 6).map((a, i) => (
              <div key={a.code} className="row" style={{
                padding: '12px 14px',
                border: i === 0 ? '1.5px solid var(--accent)' : '1.5px solid var(--line-2)',
                borderRadius: 12,
                background: i === 0 ? 'var(--accent-light)' : 'transparent',
                transition: 'all 0.15s',
              }}>
                <div className="p-leaf" style={{ width: 32, height: 32, fontSize: 9 }}>{a.code}</div>
                <div style={{ flex: 1, fontSize: 14, fontWeight: 500 }}>{a.name}</div>
                {i === 0 && (
                  <div style={{
                    width: 20, height: 20, borderRadius: 10, background: 'var(--accent)',
                    display: 'grid', placeItems: 'center',
                    boxShadow: '0 2px 6px rgba(61,122,90,0.25)',
                  }}>
                    <PIcon d={P_ICONS.check} size={12} stroke="#fff" sw={2.4} />
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
        <div style={{
          position: 'absolute', bottom: 14, left: 20, right: 20,
          padding: 15, background: 'var(--accent)', color: '#fff',
          borderRadius: 14, textAlign: 'center', fontSize: 14, fontWeight: 600,
          boxShadow: '0 6px 20px rgba(61,122,90,0.3)',
        }}>Продолжить</div>
      </div>
    </PPhone>
  );
}

Object.assign(window, { PPheno, PFeed, PMap, PReference, PSettings, POnboard });
