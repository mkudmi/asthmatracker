const rawApiUrl = process.env.REACT_APP_API_URL?.trim();

const API_URL = rawApiUrl ? rawApiUrl.replace(/\/+$/, '') : '/api';

export default API_URL;
