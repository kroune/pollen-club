// Polished shared components
const P_SEVERITY = ['Нулевой','Низкий','Средний','Высокий','Очень высокий','Экстра'];
const P_SEV_COLORS = ['var(--severity-0)','var(--severity-1)','var(--severity-2)','var(--severity-3)','var(--severity-4)','var(--severity-5)'];

const P_ALLERGENS = [
  { name: 'Берёза', code: 'BIR', sev: 2 },
  { name: 'Дуб', code: 'OAK', sev: 0 },
  { name: 'Ольха', code: 'ALN', sev: 0 },
  { name: 'Полынь', code: 'ART', sev: 0 },
  { name: 'Орешник', code: 'COR', sev: 0 },
  { name: 'Злаки', code: 'GRA', sev: 0 },
  { name: 'Маревые', code: 'CHE', sev: 0 },
  { name: 'Амброзия', code: 'AMB', sev: 0 },
  { name: 'Кладоспориум', code: 'CLA', sev: 0 },
  { name: 'Альтернария', code: 'ALT', sev: 0 },
];

// Icon component
function PIcon({ d, size = 16, stroke = 'currentColor', sw = 1.5, fill = 'none', style }) {
  return React.createElement('svg', {
    width: size, height: size, viewBox: '0 0 24 24', fill: fill,
    stroke: stroke, strokeWidth: sw, strokeLinecap: 'round', strokeLinejoin: 'round', style
  }, React.createElement('path', { d }));
}

const P_ICONS = {
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
  back:    'M19 12H5M12 19l-7-7 7-7',
};

// Tab bar
function PTabBar({ active = 'home' }) {
  const tabs = [
    { id: 'home', label: 'Прогноз', d: P_ICONS.home },
    { id: 'pheno', label: 'Фенология', d: P_ICONS.leaf },
    { id: 'diary', label: 'Дневник', d: P_ICONS.smile },
    { id: 'feed', label: 'Лента', d: P_ICONS.chat },
    { id: 'map', label: 'Карта', d: P_ICONS.pin },
  ];
  return (
    <div className="p-tabbar">
      {tabs.map(t => (
        <div key={t.id} className={'p-tab ' + (t.id === active ? 'active' : '')}>
          <div className="p-tab-icon">
            <PIcon d={t.d} size={20} sw={t.id === active ? 1.8 : 1.4}
              stroke={t.id === active ? 'var(--accent-2)' : 'var(--ink-3)'} />
          </div>
          <div>{t.label}</div>
        </div>
      ))}
    </div>
  );
}

// Severity label with dot
function PSevLabel({ level }) {
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6, fontSize: 11, fontWeight: 500, letterSpacing: 0.2 }}>
      <span className={'sev-dot sev-dot-' + level} />
      <span className={'tx-' + level}>{P_SEVERITY[level]}</span>
    </span>
  );
}

// Severity dots row (5 dots)
function PSevDots({ level }) {
  return (
    <div style={{ display: 'flex', gap: 3 }}>
      {[1,2,3,4,5].map(i => (
        <div key={i} style={{
          width: 7, height: 7, borderRadius: 4,
          background: i <= level ? P_SEV_COLORS[level] : 'var(--line-2)',
          transition: 'background 0.2s',
        }} />
      ))}
    </div>
  );
}

// Phone wrapper using AndroidDevice
function PPhone({ children }) {
  return (
    <AndroidDevice width={300} height={620}>
      <div className="scr">{children}</div>
    </AndroidDevice>
  );
}

// Search bar
function PSearchBar({ placeholder = 'Название или вещество…' }) {
  return (
    <div className="row" style={{
      gap: 10, padding: '11px 14px',
      background: 'var(--paper-2)', borderRadius: 12,
    }}>
      <PIcon d={P_ICONS.search} size={15} stroke="var(--ink-3)" sw={1.6} />
      <div style={{ fontSize: 13, color: 'var(--ink-3)', flex: 1 }}>{placeholder}</div>
    </div>
  );
}

Object.assign(window, {
  P_SEVERITY, P_SEV_COLORS, P_ALLERGENS,
  PIcon, P_ICONS, PTabBar, PSevLabel, PSevDots, PPhone, PSearchBar,
});
