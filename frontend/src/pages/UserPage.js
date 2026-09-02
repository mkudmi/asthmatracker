import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import { differenceInYears, parseISO, isValid } from 'date-fns';
import API_URL from '../config/api';

function UserPage({ userOms, onLogout }) {
  const [patient, setPatient] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [medicines, setMedicines] = useState([]);

  useEffect(() => {
    const prevHtmlOverflowX = document.documentElement.style.overflowX;
    const prevBodyOverflowX = document.body.style.overflowX;
    const styleEl = document.createElement('style');
    styleEl.setAttribute('data-userpage-global', 'true');
    styleEl.innerHTML = `
      *, *::before, *::after { box-sizing: border-box; }
      html, body { overflow-x: hidden !important; width: 100%; }
    `;
    document.head.appendChild(styleEl);

    document.documentElement.style.overflowX = 'hidden';
    document.body.style.overflowX = 'hidden';

    return () => {
      document.documentElement.style.overflowX = prevHtmlOverflowX;
      document.body.style.overflowX = prevBodyOverflowX;
      styleEl.remove();
    };
  }, []);

  useEffect(() => {
    if (!userOms) return;

    const fetchPatient = async () => {
      try {
        setLoading(true);
        setError('');
        setPatient(null);

        const response = await fetch(`${API_URL}/patients?oms=${encodeURIComponent(userOms)}`);
        if (!response.ok) throw new Error('Ошибка при запросе к серверу');

        const data = await response.json();
        if (Array.isArray(data) && data.length > 0) {
          setPatient(data[0]);
        } else {
          setError('Пациент с таким ОМС не найден');
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchPatient();
  }, [userOms]);

  useEffect(() => {
    if (!patient?.id) return;

    const fetchMedicines = async () => {
      try {
        const response = await fetch(`${API_URL}/medicine/by-patient?patient_id=${patient.id}`);
        if (!response.ok) throw new Error('Ошибка загрузки лекарств');

        const data = await response.json();
        setMedicines(Array.isArray(data) ? data.slice(0, 4) : []);
      } catch (err) {
        console.error('Ошибка при получении лекарств:', err);
        setMedicines([]);
      }
    };

    fetchMedicines();
  }, [patient]);

  const getInitials = () => {
    if (!patient) return '';
    const name = (patient.name || '').trim();
    const surname = (patient.surname || '').trim();
    return `${name.charAt(0)}${surname.charAt(0)}`.toUpperCase();
  };

  const getYearWord = (age) => {
    if (age % 100 >= 11 && age % 100 <= 14) return 'лет';
    const lastDigit = age % 10;
    if (lastDigit === 1) return 'год';
    if (lastDigit >= 2 && lastDigit <= 4) return 'года';
    return 'лет';
  };

  const safeAge = () => {
    if (!patient?.birthday) return '';
    try {
      const d = parseISO(patient.birthday);
      if (!isValid(d)) return '';
      const age = differenceInYears(new Date(), d);
      return `${age} ${getYearWord(age)}`;
    } catch {
      return '';
    }
  };

  const prettyPhone = (p) => {
    if (!p) return '—';
    const digits = String(p).replace(/\D/g, '');
    if (digits.length === 11 && digits[0] === '8') {
      return digits.replace(/(\d)(\d{3})(\d{3})(\d{2})(\d{2})/, '+7 ($2) $3-$4-$5');
    }
    if (digits.length === 11 && digits[0] === '7') {
      return digits.replace(/7(\d{3})(\d{3})(\d{2})(\d{2})/, '+7 ($1) $2-$3-$4');
    }
    return p;
  };

  const heightValue = (h) => {
    if (h === null || h === undefined || h === '') return '—';
    const num = Number(h);
    if (Number.isFinite(num)) return `${num} см`;
    return `${h}`;
  };

  return (
    <>
      <Header onLogout={onLogout} />

      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          minHeight: '100dvh',
          height: '100svh',
          boxSizing: 'border-box',
          background: '#ffffff',
          overflowX: 'hidden',
          width: '100%',
        }}
      >
        <main
          style={{
            flex: 1,
            WebkitOverflowScrolling: 'touch',
            overflowY: 'auto',
            overflowX: 'hidden',
            overscrollBehavior: 'contain',
            padding: `0 12px calc(105px + env(safe-area-inset-bottom, 0px))`,
            scrollbarGutter: 'stable both-edges',
          }}
        >
          <div
            style={{
              maxWidth: 860,
              width: '100%',
              margin: '0 auto',
              padding: '24px 16px',
              fontFamily: 'system-ui, -apple-system, Segoe UI, Roboto, sans-serif',
              lineHeight: 1.4,
            }}
          >
            <div
              style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                marginBottom: 24,
                textAlign: 'center',
              }}
            >
              <div
                style={{
                  width: 90,
                  height: 90,
                  borderRadius: '50%',
                  backgroundColor: '#e9e9ee',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: 28,
                  fontWeight: 700,
                  color: '#3c3c43',
                  marginBottom: 12,
                  userSelect: 'none',
                  flex: '0 0 auto',
                }}
              >
                {getInitials()}
              </div>
              <h2 style={{ fontSize: 22, fontWeight: 700, margin: 0 }}>Профиль</h2>
            </div>

            {loading && <p>Загрузка...</p>}
            {error && <p style={{ color: 'red', marginBottom: 16 }}>{error}</p>}

            {patient && (
              <>
                <div
                  style={{
                    backgroundColor: '#f6f7fb',
                    borderRadius: 14,
                    padding: 20,
                    boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
                    display: 'grid',
                    gridTemplateColumns: '1fr',
                    gap: 14,
                    marginBottom: 20,
                  }}
                >
                  <Info label="👤 ФИО" value={`${patient.surname} ${patient.name} ${patient.patronymic}`} />
                  <Info label="♀️♂️ Пол" value={patient.sex || '—'} />
                  <Info
                    label="🎂 Дата рождения / возраст"
                    value={patient.birthday ? `${patient.birthday} / ${safeAge() || '—'}` : '—'}
                  />
                  <Info label="📏 Рост" value={heightValue(patient.height)} />
                  <Info label="📞 Телефон" value={prettyPhone(patient.phone_number)} />
                  <Info label="🩺 ОМС" value={patient.oms} />
                </div>

                <div>
                  <h3 style={{ fontSize: 18, fontWeight: 700, margin: '0 0 12px' }}>
                    💊 Назначенные лекарства
                  </h3>
                  {medicines.length === 0 ? (
                    <div style={{ fontSize: 14, color: '#6b7280', textAlign: 'center' }}>
                      Вам ничего не назначено 😊
                    </div>
                  ) : (
                    <div
                      style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
                        gap: 12,
                        width: '100%',
                      }}
                    >
                      {medicines.map((med, idx) => (
                        <div
                          key={`${med.name}-${idx}`}
                          style={{
                            backgroundColor: '#fff',
                            border: '1px solid #eee',
                            borderRadius: 12,
                            padding: 14,
                            boxShadow: '0 1px 4px rgba(0,0,0,0.04)',
                            minWidth: 0,
                            wordBreak: 'break-word',
                            overflowWrap: 'anywhere',
                          }}
                        >
                          <div style={{ fontWeight: 700, fontSize: 15, wordBreak: 'break-word' }}>
                            {med.name}
                          </div>
                          <div style={{ fontSize: 13, color: '#4b5563' }}>{med.mkg}</div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </>
            )}
          </div>
        </main>
      </div>
    </>
  );
}

function Info({ label, value }) {
  return (
    <div style={{ minWidth: 0 }}>
      <div style={{ fontSize: 13, color: '#6b7280', marginBottom: 4 }}>{label}</div>
      <div style={{ fontWeight: 600, fontSize: 15, wordBreak: 'break-word', overflowWrap: 'anywhere' }}>
        {value}
      </div>
    </div>
  );
}

export default UserPage;
