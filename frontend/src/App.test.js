import { render, screen } from '@testing-library/react';
import App from './App';

test('renders the patient login screen', () => {
  render(<App />);
  expect(screen.getByRole('heading', { name: /добро пожаловать/i })).toBeInTheDocument();
  expect(screen.getByLabelText(/номер полиса омс/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /^войти$/i })).toBeInTheDocument();
});
