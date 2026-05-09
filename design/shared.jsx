// Shared mini-components used inside Android phone artboards
const SEVERITY = ['Нулевой', 'Низкий', 'Средний', 'Высокий', 'Очень высокий', 'Экстра'];
const ALLERGENS = [
  { name: 'Берёза',     code: 'BIR', sev: 2 },
  { name: 'Дуб',        code: 'OAK', sev: 0 },
  { name: 'Ольха',      code: 'ALN', sev: 0 },
  { name: 'Полынь',     code: 'ART', sev: 0 },
  { name: 'Орешник',    code: 'COR', sev: 0 },
  { name: 'Злаки',      code: 'GRA', sev: 0 },
  { name: 'Маревые',    code: 'CHE', sev: 0 },
  { name: 'Амброзия',   code: 'AMB', sev: 0 },
  { name: 'Кладоспориум', code: 'CLA', sev: 0 },
  { name: 'Альтернария', code: 'ALT', sev: 0 },
];

// little inline SVG icons — geometric only, no illustration
const Icon = ({ d, size = 16, stroke = 'currentColor', sw = 1.4, style }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none"
    stroke={stroke} strokeWidth={sw} strokeLinecap="round" strokeLinejoin="round"
    style={style}>
    <path d={d} />
  </svg>
);
const ICONS = {
  menu:    'M4 7h16M4 12h16M4 17h16',
  home:    'M4 11l8-7 8 7v9a1 1 0 0 1-1 1h-4v-7H9v7H5a1 1 0 0 1-1-1z',
  leaf:    'M5 19c8 0 14-6 14-14-8 0-14 6-14 14zM5 19l7-7',
  smile:   'M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18zM8 14s1.5 2 4 2 4-2 4-2M9 10h.01M15 10h.01',
  chat:    'M4 5h16v11H8l-4 4z',
  pin:     'M12 21s7-7 7-12a7 7 0 0 0-14 0c0 5 7 12 7 12zM12 11a2 2 0 1 0 0-4 2 2 0 0 0 0 4z',
  chevR:   'M9 6l6 6-6 6',
  chevD:   'M6 9l6 6 6-6',
  cal:     'M5 7h14v13H5zM5 7V4M19 7V4M3 11h18',
  loc:     'M12 21s7-7 7-12a7 7 0 0 0-14 0c0 5 7 12 7 12z M12 11a2 2 0 1 0 0-4 2 2 0 0 0 0 4z',
  plus:    'M12 5v14M5 12h14',
  search:  'M11 19a8 8 0 1 0 0-16 8 8 0 0 0 0 16zM21 21l-4.3-4.3',
  bell:    'M6 8a6 6 0 0 1 12 0c0 7 3 8 3 8H3s3-1 3-8M10 21a2 2 0 0 0 4 0',
  check:   'M5 12l5 5L20 7',
  x:       'M6 6l12 12M18 6L6 18',
  filter:  'M4 5h16M7 12h10M10 19h4',
};

// Bottom tab bar — used by ALL screens
function TabBar({ active = 'home' }) {
  const tabs = [
    { id: 'home',   label: 'Прогноз',   d: ICONS.home },
    { id: 'pheno',  label: 'Фенология', d: ICONS.leaf },
    { id: 'diary',  label: 'Дневник',   d: ICONS.smile },
    { id: 'feed',   label: 'Сообщество', d: ICONS.chat },
    { id: 'map',    label: 'Карта',     d: ICONS.pin },
  ];
  return (
    <div className="tabbar">
      {tabs.map(t => (
        <div key={t.id} className={'tab ' + (t.id === active ? 'active' : '')}>
          <div className="ico"><Icon d={t.d} size={18} sw={t.id === active ? 1.7 : 1.3} /></div>
          <div>{t.label}</div>
        </div>
      ))}
    </div>
  );
}

// App bar (legacy — kept for reference but headers should use MiniBar / nothing)
function AppBar({ title, sub, right, large = false }) {
  return (
    <div className="appbar" style={large ? { paddingBottom: 16 } : null}>
      <div className="menu-icon"><Icon d={ICONS.menu} size={18} /></div>
      <div style={{ flex: 1 }}>
        <div className="title">{title}</div>
        {sub && <div className="sub">{sub}</div>}
      </div>
      {right}
    </div>
  );
}

// Minimal top strip — left affordance + optional right slot, NO title/subtitle.
// `back` flips the left affordance from hamburger (drawer) to a back arrow.
// Use back={true} ONLY on screens drilled-into from another in-app screen
// (Sensitivity ← Home, AllergenDetail ← Home, TherapyForm ← Therapy list).
// Top-level screens reached from the drawer (Settings, Reference) keep the hamburger.
function MiniBar({ right, back = false }) {
  return (
    <div className="row" style={{
      padding: '10px 14px 4px', alignItems: 'center', minHeight: 36,
    }}>
      {back ? (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
          stroke="var(--ink-2)" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
          <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
      ) : (
        <Icon d={ICONS.menu} size={18} stroke="var(--ink-2)" />
      )}
      <div style={{ flex: 1 }} />
      {right}
    </div>
  );
}

// Severity bar — 6-segment visualisation (нулевой → экстра)
function SevBar({ level = 0, compact = false }) {
  const cls = ['', 'on-1', 'on-2', 'on-3', 'on-4', 'on-5'];
  return (
    <div className="bar-scale" style={compact ? { width: 44 } : null}>
      {[1,2,3,4,5].map(i => (
        <div key={i} className={'seg ' + (i <= level ? cls[i] : '')} />
      ))}
    </div>
  );
}

// Severity dot + label
function SevLabel({ level }) {
  const txClass = level === 0 ? 'tx-0' : 'tx-' + level;
  return (
    <span className={'sev sev-' + level}>
      <span className="dot" />
      <span className={txClass}>{SEVERITY[level]}</span>
    </span>
  );
}

// Vertical mini-meter (scale 0–5) — used in detail / map cards
function VerticalMeter({ level = 0 }) {
  const colors = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];
  return (
    <div className="meter-v">
      {[1,2,3,4,5].map(i => (
        <div key={i} className="step" style={{
          background: i <= level ? colors[i] : 'var(--line)',
        }} />
      ))}
    </div>
  );
}

// Wrap a bare screen in our copied AndroidDevice frame
function Phone({ children, dark = false }) {
  return (
    <AndroidDevice width={300} height={620} dark={dark}>
      <div className="scr">{children}</div>
    </AndroidDevice>
  );
}

Object.assign(window, {
  SEVERITY, ALLERGENS, Icon, ICONS,
  TabBar, AppBar, MiniBar, SevBar, SevLabel, VerticalMeter, Phone,
});
