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

// Reusable copyable ID row
function PCopyableId({ id, size = 20 }) {
  return (
    <div className="row" style={{ gap: 8 }}>
      <div className="p-num" style={{ fontSize: size, fontWeight: 600, letterSpacing: 1, fontFamily: 'var(--font-mono)' }}>{id}</div>
      <div style={{
        padding: '3px 8px', borderRadius: 6,
        background: 'var(--accent-light)',
        fontSize: 10, fontWeight: 500, color: 'var(--accent-2)',
        flexShrink: 0,
      }}>копировать</div>
    </div>
  );
}

// Simple QR code placeholder (grid pattern)
function PQrCode({ size = 100 }) {
  const grid = 9;
  const cell = size / grid;
  // Deterministic pattern that looks like a QR code
  const pattern = [
    [1,1,1,0,1,0,1,1,1],
    [1,0,1,0,0,0,1,0,1],
    [1,1,1,0,1,0,1,1,1],
    [0,0,0,0,1,0,0,0,0],
    [1,0,1,1,0,1,1,0,1],
    [0,0,0,0,1,0,0,0,0],
    [1,1,1,0,0,0,1,1,1],
    [1,0,1,0,1,0,1,0,1],
    [1,1,1,0,1,0,1,1,1],
  ];
  return (
    <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} style={{ display: 'block' }}>
      <rect width={size} height={size} fill="#fff" rx={4} />
      {pattern.map((row, y) =>
        row.map((v, x) =>
          v ? <rect key={`${x}-${y}`} x={x * cell + 1} y={y * cell + 1} width={cell - 2} height={cell - 2} rx={1.5} fill="var(--ink)" /> : null
        )
      )}
    </svg>
  );
}

// ── FRIENDS LIST ──
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
              const displayName = f.name || ('ID ' + f.serverId);
              return (
                <div key={i} style={{
                  padding: '12px 16px',
                  borderTop: i === 0 ? 'none' : '1px solid var(--line-2)',
                }}>
                  <div className="row" style={{ gap: 10 }}>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 13, fontWeight: 500 }}>{displayName}</div>
                      <div className="row" style={{ gap: 6, marginTop: 3 }}>
                        <span style={{
                          fontSize: 10, fontFamily: 'var(--font-mono)',
                          color: 'var(--ink-3)', letterSpacing: 0.3,
                        }}>{f.serverId}</span>
                        <span style={{
                          fontSize: 9, fontWeight: 500, color: 'var(--accent-2)',
                          padding: '1px 5px', borderRadius: 4,
                          background: 'var(--accent-light)',
                        }}>копировать</span>
                      </div>
                    </div>

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


// ── ADD FRIEND — two tabs: QR scan / manual ID ──
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
        <div className="pad" style={{ paddingTop: 16 }}>

          {/* Tabs: QR / Manual */}
          <div style={{
            display: 'flex', gap: 0,
            background: 'var(--paper-2)', borderRadius: 10,
            padding: 3, marginBottom: 20,
          }}>
            <div style={{
              flex: 1, padding: '8px 0', textAlign: 'center',
              fontSize: 12, fontWeight: 600,
              background: 'var(--card)', borderRadius: 8,
              color: 'var(--ink)',
              boxShadow: 'var(--shadow-card)',
            }}>QR-код</div>
            <div style={{
              flex: 1, padding: '8px 0', textAlign: 'center',
              fontSize: 12, fontWeight: 500,
              color: 'var(--ink-3)',
            }}>Ввести ID</div>
          </div>

          {/* QR scan area */}
          <div style={{
            borderRadius: 16, overflow: 'hidden',
            background: 'var(--ink)',
            padding: 24,
            display: 'flex', flexDirection: 'column', alignItems: 'center',
            marginBottom: 20,
          }}>
            {/* Viewfinder frame */}
            <div style={{
              width: 160, height: 160,
              borderRadius: 12,
              position: 'relative',
              marginBottom: 14,
            }}>
              {/* Corner brackets */}
              {[
                { top: 0, left: 0, borderTop: '3px solid #fff', borderLeft: '3px solid #fff', borderRadius: '8px 0 0 0' },
                { top: 0, right: 0, borderTop: '3px solid #fff', borderRight: '3px solid #fff', borderRadius: '0 8px 0 0' },
                { bottom: 0, left: 0, borderBottom: '3px solid #fff', borderLeft: '3px solid #fff', borderRadius: '0 0 0 8px' },
                { bottom: 0, right: 0, borderBottom: '3px solid #fff', borderRight: '3px solid #fff', borderRadius: '0 0 8px 0' },
              ].map((s, i) => (
                <div key={i} style={{
                  position: 'absolute', width: 28, height: 28,
                  ...s,
                }} />
              ))}
              {/* Scan line */}
              <div style={{
                position: 'absolute', left: 10, right: 10, top: '45%',
                height: 2, background: 'var(--accent)',
                borderRadius: 1,
                opacity: 0.8,
                boxShadow: '0 0 12px var(--accent)',
              }} />
            </div>
            <div style={{
              fontSize: 12, color: 'rgba(255,255,255,0.6)',
              textAlign: 'center',
            }}>Наведите камеру на QR-код друга</div>
          </div>

          {/* Divider with "or" */}
          <div className="row" style={{ gap: 12, marginBottom: 20 }}>
            <div style={{ flex: 1, height: 1, background: 'var(--line-2)' }} />
            <span style={{ fontSize: 11, color: 'var(--ink-3)' }}>или</span>
            <div style={{ flex: 1, height: 1, background: 'var(--line-2)' }} />
          </div>

          {/* Your QR + ID */}
          <div className="p-eyebrow" style={{ marginBottom: 8 }}>Ваш код для друзей</div>
          <div className="p-card" style={{ padding: 16, display: 'flex', alignItems: 'center', gap: 14 }}>
            <PQrCode size={72} />
            <div style={{ flex: 1 }}>
              <PCopyableId id="1126105" size={18} />
              <div className="p-annot" style={{ fontSize: 10, marginTop: 6, lineHeight: 1.4 }}>
                Покажите QR-код другу или отправьте ID
              </div>
            </div>
          </div>
        </div>
      </div>
    </PPhone>
  );
}


// ── ADD FRIEND — manual tab selected ──
function PAddFriendManual() {
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
        <div className="pad" style={{ paddingTop: 16 }}>

          {/* Tabs: QR / Manual */}
          <div style={{
            display: 'flex', gap: 0,
            background: 'var(--paper-2)', borderRadius: 10,
            padding: 3, marginBottom: 20,
          }}>
            <div style={{
              flex: 1, padding: '8px 0', textAlign: 'center',
              fontSize: 12, fontWeight: 500,
              color: 'var(--ink-3)',
            }}>QR-код</div>
            <div style={{
              flex: 1, padding: '8px 0', textAlign: 'center',
              fontSize: 12, fontWeight: 600,
              background: 'var(--card)', borderRadius: 8,
              color: 'var(--ink)',
              boxShadow: 'var(--shadow-card)',
            }}>Ввести ID</div>
          </div>

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
            <div style={{ fontSize: 14, color: 'var(--ink)' }}>Маша</div>
          </div>
          <div className="p-annot" style={{ fontSize: 10, marginBottom: 24, paddingLeft: 4 }}>
            Имя хранится только у вас на устройстве
          </div>

          {/* Add button */}
          <div style={{
            padding: 14, background: 'var(--accent)', color: '#fff',
            borderRadius: 14, textAlign: 'center', fontSize: 14, fontWeight: 600,
            boxShadow: '0 6px 20px rgba(61,122,90,0.3)',
          }}>Добавить</div>

          {/* Your QR + ID */}
          <div style={{
            marginTop: 28, paddingTop: 20,
            borderTop: '1px solid var(--line-2)',
          }}>
            <div className="p-eyebrow" style={{ marginBottom: 8 }}>Ваш код для друзей</div>
            <div className="p-card" style={{ padding: 16, display: 'flex', alignItems: 'center', gap: 14 }}>
              <PQrCode size={72} />
              <div style={{ flex: 1 }}>
                <PCopyableId id="1126105" size={18} />
                <div className="p-annot" style={{ fontSize: 10, marginTop: 6, lineHeight: 1.4 }}>
                  Покажите QR или отправьте ID
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </PPhone>
  );
}


// ── MY QR — full screen for easy scanning ──
function PMyQr() {
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
          Мой QR-код
        </div>
        <div style={{ width: 32 }} />
      </div>

      <div style={{
        flex: 1, display: 'flex', flexDirection: 'column',
        alignItems: 'center', justifyContent: 'center',
        padding: '0 32px',
      }}>
        {/* QR card */}
        <div className="p-card" style={{
          padding: 28,
          display: 'flex', flexDirection: 'column',
          alignItems: 'center',
          boxShadow: 'var(--shadow-elevated)',
          width: '100%', maxWidth: 260,
        }}>
          <PQrCode size={180} />
          <div style={{ marginTop: 20, textAlign: 'center' }}>
            <div className="p-annot" style={{ fontSize: 9, marginBottom: 6 }}>ВАШ ID</div>
            <PCopyableId id="1126105" size={24} />
          </div>
        </div>

        <div className="p-body" style={{
          textAlign: 'center', marginTop: 20,
          color: 'var(--ink-3)', fontSize: 12, lineHeight: 1.5,
          padding: '0 8px',
        }}>
          Покажите этот код другу, чтобы он навёл камеру и добавил вас
        </div>

        {/* Share button */}
        <div style={{
          marginTop: 24, padding: '12px 32px',
          background: 'var(--accent)', color: '#fff',
          borderRadius: 14, fontSize: 13, fontWeight: 600,
          boxShadow: '0 6px 20px rgba(61,122,90,0.3)',
          display: 'inline-flex', alignItems: 'center', gap: 8,
        }}>
          <PIcon d="M4 12v6a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-6M16 6l-4-4-4 4M12 2v13" size={14} stroke="#fff" sw={1.8} />
          Поделиться
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

        <div style={{
          flex: 1, display: 'flex', flexDirection: 'column',
          alignItems: 'center', justifyContent: 'center',
          padding: '40px 28px',
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
            Добавьте друзей по ID или QR-коду, чтобы видеть их отметки на карте
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

          {/* Your QR + ID */}
          <div style={{
            marginTop: 32, padding: '16px 0', borderTop: '1px solid var(--line-2)', width: '100%',
            display: 'flex', alignItems: 'center', gap: 14,
            justifyContent: 'center',
          }}>
            <PQrCode size={60} />
            <div style={{ textAlign: 'left' }}>
              <div className="p-annot" style={{ fontSize: 9, marginBottom: 4 }}>ВАШ ID</div>
              <PCopyableId id="1126105" size={17} />
            </div>
          </div>
        </div>
      </div>
      <PTabBar active="feed" />
    </PPhone>
  );
}


Object.assign(window, { PFriendsList, PAddFriend, PAddFriendManual, PMyQr, PFriendsEmpty, PCopyableId, PQrCode });
