// Polished Friends screens — only uses real backend data
// get_friends → friend_id only; name is local-only (user types it)
// get_pins_with_friends → lat/lng, value (0=good,1=mid,2=bad), pollen_type, tags, date
// add_friend → user_id + friend_id; name saved locally

const FRIENDS_LOCAL = [
  { serverId: 67890, name: 'Маша', lastPin: { value: 2, tags: 'birch', date: '25 апр' } },
  { serverId: 41205, name: 'Дима С.', lastPin: { value: 1, tags: 'birch', date: '24 апр' } },
  { serverId: 88310, name: 'Алсу', lastPin: null },
  { serverId: 12760, name: '', lastPin: { value: 0, tags: 'alder', date: '22 апр' } },
];

const FEELING_LABELS = ['Хорошо', 'Средне', 'Плохо'];
const FEELING_COLORS = ['var(--severity-1)', 'var(--severity-2)', 'var(--severity-4)'];
const TAG_NAMES = { birch: 'Берёза', alder: 'Ольха', oak: 'Дуб', grass: 'Злаки', ragweed: 'Амброзия', hazel: 'Орешник' };

// ── FRIENDS LIST (inside Feed tab, "Друзья" filter) ──
function PFriendsList() {
  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '16px 16px 8px' }}>
          <div className="p-display" style={{ fontSize: 24 }}>Сообщество</div>
        </div>
        <div style={{ display: 'flex', gap: 6, padding: '8px 16px 14px', overflow: 'auto' }}>
          {['Все', 'Друзья', 'Эксперты', 'Медиа'].map((t, i) => (
            <span key={t} className={'p-pill ' + (i === 1 ? 'active' : '')}>{t}</span>
          ))}
        </div>

        {/* Friends list */}
        <div style={{ padding: '0 16px' }}>
          <div className="row" style={{ justifyContent: 'space-between', marginBottom: 10 }}>
            <div className="p-eyebrow">Ваши друзья · {FRIENDS_LOCAL.length}</div>
            <div style={{
              display: 'flex', alignItems: 'center', gap: 4,
              fontSize: 10, color: 'var(--accent-2)', fontWeight: 500,
            }}>
              <PIcon d={P_ICONS.plus} size={11} stroke="var(--accent-2)" sw={2} />
              добавить
            </div>
          </div>

          <div className="p-card" style={{ padding: 0 }}>
            {FRIENDS_LOCAL.map((f, i) => {
              const displayName = f.name || String(f.serverId);
              return (
                <div key={i} style={{
                  padding: '12px 16px',
                  borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
                }}>
                  <div className="row" style={{ gap: 10 }}>
                    {/* Monogram circle */}
                    <div className="p-leaf" style={{
                      width: 34, height: 34,
                      fontSize: f.name ? 12 : 9,
                      fontFamily: f.name ? 'var(--font-ui)' : 'var(--font-mono)',
                      fontWeight: 600,
                      color: 'var(--ink-2)',
                    }}>
                      {f.name ? f.name[0] : 'ID'}
                    </div>

                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 13, fontWeight: 500 }}>{displayName}</div>
                      <div className="p-annot" style={{ fontSize: 10, marginTop: 1 }}>
                        ID: {f.serverId}
                      </div>
                    </div>

                    {/* Last pin info if available */}
                    {f.lastPin ? (
                      <div style={{ textAlign: 'right' }}>
                        <div className="row" style={{ gap: 4, justifyContent: 'flex-end' }}>
                          <div style={{
                            width: 6, height: 6, borderRadius: 3,
                            background: FEELING_COLORS[f.lastPin.value],
                          }} />
                          <span style={{ fontSize: 11, fontWeight: 500, color: FEELING_COLORS[f.lastPin.value] }}>
                            {FEELING_LABELS[f.lastPin.value]}
                          </span>
                        </div>
                        <div className="p-annot" style={{ fontSize: 9, marginTop: 2 }}>
                          {TAG_NAMES[f.lastPin.tags] || f.lastPin.tags} · {f.lastPin.date}
                        </div>
                      </div>
                    ) : (
                      <div className="p-annot" style={{ fontSize: 10 }}>нет отметок</div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>

          {/* Hint */}
          <div style={{
            marginTop: 14, padding: '10px 14px',
            background: 'var(--paper-2)', borderRadius: 10,
            fontSize: 11, color: 'var(--ink-3)', lineHeight: 1.5,
          }}>
            Отметки друзей видны на карте. Самочувствие обновляется, когда друг ставит новую точку.
          </div>
        </div>

        <div style={{ height: 16 }} />
      </div>
      <PTabBar active="feed" />
    </PPhone>
  );
}


// ── ADD FRIEND ──
function PAddFriend() {
  return (
    <PPhone>
      {/* Header */}
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
        <div style={{ flex: 1, textAlign: 'center', fontSize: 15, fontWeight: 600, letterSpacing: -0.2 }}>
          Добавить друга
        </div>
        <div style={{ width: 32 }} />
      </div>

      <div className="scr-scroll" style={{ flex: 1 }}>
        <div className="pad" style={{ paddingTop: 28 }}>
          {/* ID input */}
          <div className="p-eyebrow" style={{ marginBottom: 8 }}>ID участника</div>
          <div style={{
            padding: '14px 16px',
            background: 'var(--card)',
            border: '1.5px solid var(--accent)',
            borderRadius: 12,
            marginBottom: 20,
          }}>
            <div style={{
              fontSize: 20, fontWeight: 600, letterSpacing: 1.5,
              fontFamily: 'var(--font-mono)',
              color: 'var(--ink)',
            }}>67890</div>
          </div>

          {/* Name input */}
          <div className="p-eyebrow" style={{ marginBottom: 8 }}>Имя (для вас)</div>
          <div style={{
            padding: '14px 16px',
            background: 'var(--card)',
            border: '1.5px solid var(--line)',
            borderRadius: 12,
            marginBottom: 6,
          }}>
            <div style={{
              fontSize: 14,
              color: 'var(--ink)',
            }}>Маша</div>
          </div>
          <div className="p-annot" style={{ fontSize: 10, marginBottom: 28, paddingLeft: 4 }}>
            Имя хранится только у вас на устройстве
          </div>

          {/* Add button */}
          <div style={{
            padding: 14, background: 'var(--accent)', color: '#fff',
            borderRadius: 14, textAlign: 'center', fontSize: 14, fontWeight: 600,
            boxShadow: '0 6px 20px rgba(61,122,90,0.3)',
          }}>Добавить</div>

          {/* Divider */}
          <div style={{
            marginTop: 32, paddingTop: 20,
            borderTop: '1px solid var(--line-2)',
          }}>
            <div className="p-eyebrow" style={{ marginBottom: 6 }}>Ваш ID</div>
            <div className="p-card" style={{ padding: '12px 16px' }}>
              <div className="row">
                <div className="p-num" style={{ fontSize: 20, fontWeight: 600, letterSpacing: 1 }}>1126105</div>
                <div className="spacer" />
                <span style={{ fontSize: 11, color: 'var(--accent-2)', fontWeight: 500 }}>копировать</span>
              </div>
            </div>
            <div className="p-annot" style={{ fontSize: 10, marginTop: 8, paddingLeft: 4, lineHeight: 1.5 }}>
              Сообщите этот код другу, чтобы он добавил вас
            </div>
          </div>
        </div>
      </div>
    </PPhone>
  );
}


// ── FRIENDS EMPTY STATE ──
function PFriendsEmpty() {
  return (
    <PPhone>
      <div className="scr-scroll" style={{ flex: 1 }}>
        <div style={{ padding: '16px 16px 8px' }}>
          <div className="p-display" style={{ fontSize: 24 }}>Сообщество</div>
        </div>
        <div style={{ display: 'flex', gap: 6, padding: '8px 16px 14px', overflow: 'auto' }}>
          {['Все', 'Друзья', 'Эксперты', 'Медиа'].map((t, i) => (
            <span key={t} className={'p-pill ' + (i === 1 ? 'active' : '')}>{t}</span>
          ))}
        </div>

        {/* Empty state */}
        <div style={{
          flex: 1, display: 'flex', flexDirection: 'column',
          alignItems: 'center', justifyContent: 'center',
          padding: '48px 28px',
          textAlign: 'center',
        }}>
          <div style={{
            width: 72, height: 72, borderRadius: 36,
            background: 'var(--paper-2)',
            border: '2px dashed var(--line)',
            display: 'grid', placeItems: 'center',
            marginBottom: 20,
          }}>
            <PIcon d={P_ICONS.pin} size={24} stroke="var(--ink-3)" sw={1.2} />
          </div>
          <div className="p-display" style={{ fontSize: 20, marginBottom: 8 }}>
            Пока нет друзей
          </div>
          <div className="p-body" style={{
            color: 'var(--ink-3)', fontSize: 12, lineHeight: 1.5,
            marginBottom: 24,
          }}>
            Добавьте друзей по ID, чтобы видеть их отметки на карте
          </div>

          <div style={{
            padding: '12px 28px',
            background: 'var(--accent)', color: '#fff',
            borderRadius: 14, fontSize: 13, fontWeight: 600,
            boxShadow: '0 6px 20px rgba(61,122,90,0.3)',
            display: 'inline-flex', alignItems: 'center', gap: 7,
          }}>
            <PIcon d={P_ICONS.plus} size={14} stroke="#fff" sw={2} />
            Добавить друга
          </div>

          <div style={{
            marginTop: 32, padding: '14px 0', borderTop: '1px solid var(--line-2)', width: '100%',
          }}>
            <div className="p-annot" style={{ fontSize: 9, marginBottom: 5 }}>ВАШ ID ДЛЯ ДРУЗЕЙ</div>
            <div className="p-num" style={{ fontSize: 20, fontWeight: 600, letterSpacing: 1 }}>1126105</div>
          </div>
        </div>
      </div>
      <PTabBar active="feed" />
    </PPhone>
  );
}


Object.assign(window, { PFriendsList, PAddFriend, PFriendsEmpty });
