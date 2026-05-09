// ─── PHENOLOGY / FEED / MAP / REFERENCE / SETTINGS / ONBOARDING ─

// PHENOLOGY — observer logs flowering stage
function PhenoA() {
  const stages = [
    { n: 1, l: 'Начало сокодвижения', done: true },
    { n: 2, l: 'Набухание почек', done: true },
    { n: 3, l: 'Распускание почек', done: true, cur: true },
    { n: 4, l: 'Развёртывание листьев' },
    { n: 5, l: 'Начало цветения' },
    { n: 6, l: 'Завершение цветения' },
  ];
  return (
    <Phone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 16 }}>
          <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1 }}>Берёза</div>
          <div className="annot" style={{ fontSize: 10, marginTop: 4, marginBottom: 14 }}>Москва, Шереметьево · фенология</div>
          <div className="card" style={{ padding: 14 }}>
            <div className="h-eyebrow">Текущая стадия</div>
            <div style={{ fontFamily: 'var(--font-display)', fontSize: 20, marginTop: 4 }}>
              №3 · Распускание почек
            </div>
            <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 4 }}>
              Отметили 24 апр · 3 дня назад
            </div>
          </div>

          <div className="h-eyebrow" style={{ marginTop: 18, marginBottom: 8 }}>Все стадии</div>
          <div style={{ position: 'relative', paddingLeft: 26 }}>
            <div style={{ position: 'absolute', left: 11, top: 8, bottom: 8, width: 1, background: 'var(--line)' }} />
            {stages.map(s => (
              <div key={s.n} style={{ position: 'relative', paddingBottom: 14 }}>
                <div style={{
                  position: 'absolute', left: -22, top: 2,
                  width: 22, height: 22, borderRadius: 11,
                  background: s.cur ? 'var(--accent)' : s.done ? 'var(--paper-2)' : 'transparent',
                  border: s.done || s.cur ? 'none' : '1px dashed var(--line)',
                  display: 'grid', placeItems: 'center',
                  fontFamily: 'var(--font-mono)',
                  fontSize: 9,
                  color: s.cur ? '#fff' : s.done ? 'var(--accent-2)' : 'var(--ink-3)',
                }}>
                  {s.done && !s.cur ? <Icon d={ICONS.check} size={11} stroke="var(--accent-2)" sw={2} /> : s.n}
                </div>
                <div style={{ fontSize: 13, fontWeight: s.cur ? 500 : 400, color: s.done || s.cur ? 'var(--ink)' : 'var(--ink-3)' }}>
                  Стадия №{s.n}
                </div>
                <div className="annot" style={{ fontSize: 10 }}>{s.l}</div>
              </div>
            ))}
          </div>
        </div>
        <div style={{ position: 'absolute', right: 16, bottom: 60 }}>
          <div className="fab"><Icon d={ICONS.plus} size={18} stroke="#fff" sw={1.6} /></div>
        </div>
      </div>
      <TabBar active="pheno" />
    </Phone>
  );
}

// FEED — community posts
function FeedA() {
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
    <Phone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '14px 16px 6px' }}>
          <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1 }}>Сообщество</div>
        </div>
        <div style={{ display: 'flex', gap: 6, padding: '8px 16px 12px', overflow: 'auto' }}>
          {['Все','Друзья','Эксперты','Медиа'].map((t, i) => (
            <span key={t} className={'pill ' + (i === 0 ? 'active' : '')}>{t}</span>
          ))}
        </div>

        {posts.map((p, i) => (
          <div key={i} style={{ padding: '14px 16px', borderTop: '1px solid var(--line-2)' }}>
            <div className="row" style={{ marginBottom: 8 }}>
              <div style={{
                width: 28, height: 28, borderRadius: 14,
                background: p.expert ? 'var(--accent)' : 'var(--paper-2)',
                border: p.expert ? 'none' : '1px solid var(--line)',
                color: '#fff', display: 'grid', placeItems: 'center',
                fontSize: 10, fontFamily: 'var(--font-mono)',
              }}>
                {p.expert ? '★' : <span style={{ color: 'var(--ink-3)' }}>{p.name[0]}</span>}
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 12, fontWeight: 500 }}>{p.name}</div>
                <div className="annot" style={{ fontSize: 10 }}>{p.loc}{p.t ? ' · ' + p.t : ''}</div>
              </div>
            </div>
            {p.img && <div className="placeholder" style={{ height: 120, marginBottom: 10 }}>placeholder · фото от участника</div>}
            <div style={{ fontSize: 12, lineHeight: 1.5, color: 'var(--ink-2)' }}>{p.txt}</div>
          </div>
        ))}
      </div>
      <TabBar active="feed" />
    </Phone>
  );
}

// MAP — risk map
function MapA() {
  const pins = [
    { x: 22, y: 30, n: 3, c: 'var(--severity-3)' },
    { x: 38, y: 42, n: 9, c: 'var(--severity-2)' },
    { x: 54, y: 22, n: 1, c: 'var(--severity-1)' },
    { x: 64, y: 50, n: 11, c: 'var(--severity-3)' },
    { x: 30, y: 58, n: 2, c: 'var(--severity-3)' },
    { x: 70, y: 70, n: 6, c: 'var(--severity-3)' },
    { x: 50, y: 75, n: 9, c: 'var(--severity-2)' },
    { x: 80, y: 38, n: 1, c: 'var(--severity-1)' },
  ];
  return (
    <Phone>
      <MiniBar right={<Icon d={ICONS.filter} size={16} stroke="var(--ink-2)" />} />
      <div style={{ flex: 1, position: 'relative', background: '#eef2ec' }}>
        {/* abstracted map placeholder — soft contours */}
        <svg width="100%" height="100%" viewBox="0 0 260 400" style={{ position: 'absolute', inset: 0 }} preserveAspectRatio="none">
          <rect width="260" height="400" fill="#f0f4ed" />
          {[...Array(8)].map((_, i) => (
            <path key={i}
              d={`M 0 ${50 + i * 45} Q ${130 + (i % 2 ? 30 : -30)} ${30 + i * 45} 260 ${60 + i * 45}`}
              stroke="#dde5d6" fill="none" strokeWidth="1" />
          ))}
          <path d="M 0 200 L 260 200" stroke="#cdd5c5" strokeWidth="2" strokeDasharray="3 3" />
        </svg>

        {/* filter pills row */}
        <div style={{ position: 'absolute', top: 10, left: 10, right: 10, display: 'flex', gap: 6, overflow: 'hidden' }}>
          {['Друзья','АГ','АСИТ','Бризер'].map(t => (
            <span key={t} className="pill" style={{ background: '#fff', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' }}>{t}</span>
          ))}
        </div>

        {/* pins */}
        {pins.map((p, i) => (
          <div key={i} style={{
            position: 'absolute',
            left: `${p.x}%`, top: `${p.y}%`,
            transform: 'translate(-50%, -100%)',
          }}>
            <div style={{
              width: 22, height: 22, borderRadius: '50% 50% 50% 0',
              transform: 'rotate(-45deg)',
              background: p.c,
              boxShadow: '0 2px 6px rgba(0,0,0,0.18)',
              display: 'grid', placeItems: 'center',
            }}>
              <div className="num" style={{ transform: 'rotate(45deg)', color: '#fff', fontSize: 10, fontWeight: 600 }}>{p.n}</div>
            </div>
          </div>
        ))}

        {/* legend mini */}
        <div className="card" style={{
          position: 'absolute', bottom: 14, left: 12, right: 12,
          padding: 10, fontSize: 10,
        }}>
          <div className="row" style={{ justifyContent: 'space-between' }}>
            <div className="h-eyebrow" style={{ fontSize: 9 }}>Уровень</div>
            <div className="annot" style={{ fontSize: 9 }}>обозначения →</div>
          </div>
          <div className="row" style={{ marginTop: 6, gap: 8 }}>
            {[1,2,3,4,5].map(l => {
              const colors = ['','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
              return (
                <div key={l} className="row" style={{ gap: 4 }}>
                  <div style={{ width: 10, height: 10, borderRadius: 5, background: colors[l] }} />
                  <span style={{ color: 'var(--ink-3)' }}>{l}</span>
                </div>
              );
            })}
          </div>
        </div>
      </div>
      <TabBar active="map" />
    </Phone>
  );
}

// REFERENCE — allergen encyclopedia
function ReferenceA() {
  return (
    <Phone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          <div className="row" style={{ gap: 10, marginBottom: 14 }}>
            <Icon d={ICONS.chevR} size={16} stroke="var(--ink-2)" sw={1.6} style={{ transform: 'rotate(180deg)', flexShrink: 0 }} />
            <div className="row" style={{ flex: 1, gap: 8, padding: '8px 12px', background: 'var(--paper-2)', borderRadius: 10 }}>
              <Icon d={ICONS.search} size={14} stroke="var(--ink-3)" />
              <div style={{ fontSize: 12, color: 'var(--ink-3)' }}>Найти аллерген…</div>
            </div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {ALLERGENS.slice(0, 6).map(a => (
              <div key={a.code} className="card" style={{ padding: 12 }}>
                <div className="placeholder" style={{ height: 60, marginBottom: 8 }}>{a.code}</div>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{a.name}</div>
                <div className="annot" style={{ fontSize: 9, marginTop: 2 }}>
                  {a.sev > 0 ? SEVERITY[a.sev].toLowerCase() : 'не активен'}
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

// SETTINGS / DRAWER
function SettingsA() {
  const groups = [
    {
      h: 'Основные',
      items: [
        { l: 'Язык', v: 'Русский' },
        { l: 'Регион мониторинга', v: 'Москва' },
        { l: 'Основной аллерген', v: 'Берёза' },
        { l: 'Друзья', v: '4 участника' },
      ],
    },
    {
      h: 'Информация',
      items: [
        { l: 'Справочник аллергенов', v: '' },
        { l: 'Руководство', v: '' },
        { l: 'Стать соучастником', v: '' },
      ],
    },
  ];
  return (
    <Phone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 14 }}>
          <div className="row" style={{ gap: 10, marginBottom: 14 }}>
            <Icon d={ICONS.chevR} size={16} stroke="var(--ink-2)" sw={1.6} style={{ transform: 'rotate(180deg)', flexShrink: 0 }} />
            <div className="h-display" style={{ fontSize: 20, lineHeight: 1.1 }}>Настройки</div>
          </div>
          <div className="card" style={{ padding: 14, marginBottom: 18 }}>
            <div className="annot">КОД УЧАСТНИКА</div>
            <div className="row" style={{ alignItems: 'baseline', marginTop: 4 }}>
              <div className="num" style={{ fontSize: 22, letterSpacing: 1 }}>1126105</div>
              <div className="spacer" />
              <span className="annot">копировать</span>
            </div>
          </div>

          {groups.map(g => (
            <div key={g.h} style={{ marginBottom: 18 }}>
              <div className="h-eyebrow" style={{ marginBottom: 6 }}>{g.h}</div>
              <div className="card" style={{ padding: 0 }}>
                {g.items.map((it, i) => (
                  <div key={it.l} className="row" style={{
                    padding: '11px 14px',
                    borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
                  }}>
                    <div style={{ flex: 1, fontSize: 13 }}>{it.l}</div>
                    {it.v && <div style={{ fontSize: 11, color: 'var(--ink-3)' }}>{it.v}</div>}
                    <Icon d={ICONS.chevR} size={13} stroke="var(--ink-3)" />
                  </div>
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

// ONBOARDING — pick allergen + region
function OnboardA() {
  return (
    <Phone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad-lg" style={{ paddingTop: 32 }}>
          <div className="annot">Шаг 1 из 3</div>
          <div className="h-display" style={{ marginTop: 8 }}>
            На что у&nbsp;вас<br />реакция?
          </div>
          <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 8 }}>
            Выберите основной аллерген. Можно изменить позже.
          </div>

          <div style={{ marginTop: 22, display: 'flex', flexDirection: 'column', gap: 6 }}>
            {ALLERGENS.slice(0, 6).map((a, i) => (
              <div key={a.code} className="row" style={{
                padding: '10px 12px',
                border: '1px solid ' + (i === 0 ? 'var(--accent)' : 'var(--line-2)'),
                borderRadius: 10,
                background: i === 0 ? 'rgba(74,125,94,0.06)' : 'transparent',
              }}>
                <div className="leaf" style={{ width: 28, height: 28, fontSize: 8 }}>{a.code}</div>
                <div style={{ flex: 1, fontSize: 13 }}>{a.name}</div>
                {i === 0 && (
                  <div style={{
                    width: 18, height: 18, borderRadius: 9, background: 'var(--accent)',
                    display: 'grid', placeItems: 'center',
                  }}>
                    <Icon d={ICONS.check} size={11} stroke="#fff" sw={2.4} />
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
        <div style={{
          position: 'absolute', bottom: 12, left: 16, right: 16,
          padding: 14, background: 'var(--accent)', color: '#fff',
          borderRadius: 12, textAlign: 'center', fontSize: 13, fontWeight: 500,
        }}>Продолжить</div>
      </div>
    </Phone>
  );
}

Object.assign(window, { PhenoA, FeedA, MapA, ReferenceA, SettingsA, OnboardA });
