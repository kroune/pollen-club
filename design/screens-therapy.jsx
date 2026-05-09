// ─── THERAPY / MEDS — 2 variants ─────────────────────

function TherapyA() {
  const groups = [
    { l: 'Системного действия', n: 1 },
    { l: 'Глаза',     n: 0 },
    { l: 'Нос',       n: 1 },
    { l: 'Бронхи',    n: 0 },
    { l: 'Кожа',      n: 0 },
    { l: 'Другое',    n: 0 },
  ];
  return (
    <Phone>
      <AppBar title="Терапия" sub="пт, 24 апреля" />
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad">
          <div className="row" style={{ gap: 6, marginBottom: 14 }}>
            <span className="pill">Симптомы</span>
            <span className="pill active">Терапия</span>
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 8 }}>Сегодня · 2 препарата</div>
          <div className="card" style={{ padding: 0, marginBottom: 16 }}>
            {[
              { name: 'Цетрин', dose: '10 мг · 1 раз/день', taken: true },
              { name: 'Назонекс', dose: 'спрей · 2 впрыска', taken: false },
            ].map((m, i) => (
              <div key={m.name} className="row" style={{
                padding: '12px 14px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{
                  width: 22, height: 22, borderRadius: 11,
                  background: m.taken ? 'var(--accent)' : 'transparent',
                  border: m.taken ? 'none' : '1.5px solid var(--line)',
                  display: 'grid', placeItems: 'center',
                }}>
                  {m.taken && <Icon d={ICONS.check} size={12} stroke="#fff" sw={2} />}
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontWeight: 500, fontSize: 13 }}>{m.name}</div>
                  <div style={{ fontSize: 11, color: 'var(--ink-3)' }}>{m.dose}</div>
                </div>
              </div>
            ))}
          </div>

          <div className="h-eyebrow" style={{ marginBottom: 8 }}>Добавить</div>
          <div className="card" style={{ padding: 0 }}>
            {groups.map((g, i) => (
              <div key={g.l} className="row" style={{
                padding: '12px 14px',
                borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
              }}>
                <div style={{ flex: 1, fontSize: 13 }}>{g.l}</div>
                {g.n > 0 && <span className="pill">{g.n}</span>}
                <Icon d={ICONS.chevR} size={14} stroke="var(--ink-3)" />
              </div>
            ))}
          </div>
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

function TherapyForm() {
  const fields = [
    { l: 'Название препарата', v: 'Цетрин' },
    { l: 'Форма выпуска', v: 'Таблетки' },
    { l: 'Доза', v: '10 мг' },
    { l: 'Частота приёма', v: '1 раз в день' },
    { l: 'Начало приёма', v: '20 апр 2026' },
    { l: 'Действующее вещество', v: 'Цетиризин' },
  ];
  return (
    <Phone>
      <div className="row" style={{ padding: '12px 16px 8px', alignItems: 'center', borderBottom: '1px solid var(--line-2)' }}>
        <Icon d={ICONS.x} size={16} stroke="var(--ink-2)" />
        <div className="spacer" />
        <span className="annot" style={{ color: 'var(--accent-2)' }}>сохранить</span>
      </div>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 12 }}>
          <div className="h-display" style={{ fontSize: 22, lineHeight: 1.1, marginBottom: 4 }}>Антигистаминные</div>
          <div className="annot" style={{ fontSize: 10, marginBottom: 18 }}>добавить препарат</div>
          {fields.map((f, i) => (
            <div key={f.l} style={{ marginBottom: 18 }}>
              <div className="h-eyebrow" style={{ marginBottom: 4 }}>{f.l}</div>
              <div className="row" style={{
                padding: '8px 0',
                borderBottom: '1px solid var(--line)',
              }}>
                <div style={{ flex: 1, fontSize: 14, color: i < 4 ? 'var(--ink)' : 'var(--ink-2)' }}>{f.v}</div>
                <Icon d={ICONS.chevD} size={14} stroke="var(--ink-3)" />
              </div>
            </div>
          ))}
        </div>
      </div>
      <TabBar active="diary" />
    </Phone>
  );
}

Object.assign(window, { TherapyA, TherapyForm });
