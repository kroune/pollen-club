// Index card — scale bar variant (final)

function IndexScaleBar() {
  const colors = P_SEV_COLORS;
  return (
    <div className="p-card" style={{ padding: '10px 14px' }}>
      <div className="row" style={{ gap: 10 }}>
        <span className="p-num tx-2" style={{ fontSize: 24, fontWeight: 600, lineHeight: 1 }}>5,2</span>
        <div style={{ flex: 1, display: 'flex', gap: 3 }}>
          {[1,2,3,4,5].map(i => (
            <div key={i} style={{
              flex: 1, height: 5, borderRadius: 3,
              background: i <= 2 ? colors[i] : 'var(--line-2)',
            }} />
          ))}
        </div>
        <span style={{ fontSize: 11, color: 'var(--ink-2)', fontWeight: 500 }}>Средний</span>
      </div>
    </div>
  );
}

Object.assign(window, { IndexScaleBar });
