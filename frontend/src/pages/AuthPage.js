import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import logo from '../assets/logo.svg';
import API_URL from '../config/api';
import '../css/AuthPage.css';

const FieldIcon = ({ type }) => (
  type === 'policy' ? (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="M7 3.75h8.25L19 7.5V19a1.25 1.25 0 0 1-1.25 1.25H7A1.25 1.25 0 0 1 5.75 19V5A1.25 1.25 0 0 1 7 3.75Z" />
      <path d="M15 3.75V7.5h3.75M9 12h6M9 15.5h4" />
    </svg>
  ) : (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <rect x="5" y="10" width="14" height="10" rx="2" />
      <path d="M8.5 10V7.5a3.5 3.5 0 1 1 7 0V10M12 14v2" />
    </svg>
  )
);

function AuthPage({ onSuccessLogin }) {
  const [oms, setOms] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [deferredPrompt, setDeferredPrompt] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const beforeInstallHandler = (event) => {
      event.preventDefault();
      setDeferredPrompt(event);
    };

    window.addEventListener('beforeinstallprompt', beforeInstallHandler);
    return () => window.removeEventListener('beforeinstallprompt', beforeInstallHandler);
  }, []);

  const handleInstall = () => {
    if (deferredPrompt) {
      deferredPrompt.prompt();
      deferredPrompt.userChoice.finally(() => setDeferredPrompt(null));
    }
  };

  const saveCredentialsIfSupported = async (id, pwd) => {
    try {
      if ('credentials' in navigator && window.PasswordCredential) {
        const credential = new window.PasswordCredential({ id, password: pwd, name: id });
        await navigator.credentials.store(credential);
      }
    } catch {
      // The browser may block credential storage; login should still succeed.
    }
  };

  const handleLogin = async (event) => {
    event.preventDefault();
    setErrorMessage('');
    setIsSubmitting(true);

    try {
      const response = await fetch(`${API_URL}/patients/validate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ oms, password }),
      });

      const isValid = await response.json();

      if (response.ok && isValid === true) {
        const patientResponse = await fetch(`${API_URL}/patients?oms=${encodeURIComponent(oms)}`);
        const patients = await patientResponse.json();

        if (Array.isArray(patients) && patients.length > 0) {
          await saveCredentialsIfSupported(oms, password);
          onSuccessLogin({ oms, userId: patients[0].id });
        } else {
          setErrorMessage('Не найден пациент с таким полисом ОМС');
        }
      } else {
        setErrorMessage('Проверьте номер полиса ОМС и пароль');
      }
    } catch {
      setErrorMessage('Не удалось подключиться к серверу. Попробуйте ещё раз');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="auth-page">
      <div className="auth-orb auth-orb--top" />
      <div className="auth-orb auth-orb--bottom" />

      <section className="auth-layout" aria-label="Вход в Астма Трекер">
        <div className="auth-intro">
          <img className="auth-logo" src={logo} alt="Астма Трекер" draggable={false} />

          <div className="auth-intro__copy">
            <span className="auth-eyebrow">Здоровье под контролем</span>
            <h1>Дышите свободнее.<br />Каждый день.</h1>
            <p>
              Следите за самочувствием, фиксируйте показатели и оставайтесь
              на связи со своим врачом.
            </p>
          </div>

          <div className="auth-assurances" aria-label="Преимущества сервиса">
            <span>
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m7 12.5 3 3 7-7" /></svg>
              Данные защищены
            </span>
            <span>
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 4v16M4 12h16" /></svg>
              Всегда под рукой
            </span>
          </div>
        </div>

        <div className="auth-panel">
          <div className="auth-panel__heading">
            <span className="auth-panel__badge">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 3 5.5 5.8v5.3c0 4.2 2.7 8 6.5 9.4 3.8-1.4 6.5-5.2 6.5-9.4V5.8L12 3Z" /><path d="m9 12 2 2 4-4" /></svg>
            </span>
            <div>
              <h2>Добро пожаловать</h2>
              <p>Войдите в личный кабинет пациента</p>
            </div>
          </div>

          {errorMessage && (
            <div className="auth-error" role="alert">
              <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="9" /><path d="M12 7.5v5M12 16.5h.01" /></svg>
              {errorMessage}
            </div>
          )}

          <form className="auth-form" onSubmit={handleLogin} autoComplete="on">
            <label className="auth-field" htmlFor="oms">
              <span>Номер полиса ОМС</span>
              <span className="auth-input-wrap">
                <span className="auth-input-icon"><FieldIcon type="policy" /></span>
                <input
                  id="oms"
                  name="username"
                  autoComplete="username"
                  type="text"
                  inputMode="numeric"
                  autoCapitalize="off"
                  autoCorrect="off"
                  value={oms}
                  onChange={(event) => setOms(event.target.value)}
                  placeholder="Введите номер полиса"
                  required
                  autoFocus
                />
              </span>
            </label>

            <label className="auth-field" htmlFor="password">
              <span>Пароль</span>
              <span className="auth-input-wrap">
                <span className="auth-input-icon"><FieldIcon type="password" /></span>
                <input
                  id="password"
                  name="current-password"
                  autoComplete="current-password"
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="Введите пароль"
                  required
                />
                <button
                  className="auth-password-toggle"
                  type="button"
                  onClick={() => setShowPassword((visible) => !visible)}
                  aria-label={showPassword ? 'Скрыть пароль' : 'Показать пароль'}
                  aria-pressed={showPassword}
                >
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M3.5 12s3-5 8.5-5 8.5 5 8.5 5-3 5-8.5 5-8.5-5-8.5-5Z" />
                    <circle cx="12" cy="12" r="2.25" />
                    {showPassword && <path d="m5 4 14 16" />}
                  </svg>
                </button>
              </span>
            </label>

            <button className="auth-primary-button" type="submit" disabled={isSubmitting}>
              <span>{isSubmitting ? 'Входим…' : 'Войти'}</span>
              {!isSubmitting && <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14M14 7l5 5-5 5" /></svg>}
            </button>
          </form>

          <div className="auth-divider"><span>или</span></div>

          <div className="auth-actions">
            <button className="auth-secondary-button" type="button" onClick={() => navigate('/register')}>
              Создать аккаунт
            </button>
            <button className="auth-doctor-button" type="button" onClick={() => navigate('/doctor-login')}>
              <span className="auth-doctor-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 4v4a4 4 0 0 0 8 0V4M6 4h4M14 4h4M16 13v1a4 4 0 0 0 8 0v-2" /><circle cx="21" cy="10" r="2" /></svg>
              </span>
              Вход для врача
              <svg className="auth-action-arrow" viewBox="0 0 24 24" aria-hidden="true"><path d="m9 5 7 7-7 7" /></svg>
            </button>
          </div>

          {deferredPrompt && (
            <button className="auth-install-button" type="button" onClick={handleInstall}>
              Установить приложение
            </button>
          )}

          <p className="auth-legal">Продолжая, вы соглашаетесь с условиями обработки персональных данных</p>
        </div>
      </section>
    </main>
  );
}

export default AuthPage;
