function getEnvValueOrFallback(key: string, fallbackValue: any): any {
  const value = (window.ENV as any)[key];
  return (typeof value === 'string' && value.startsWith('__') && value.endsWith('__')) ? fallbackValue : value;
}

export const API_CONFIG = {
  SECURITY_X_API_KEY: getEnvValueOrFallback('SECURITY_X_API_KEY', '25dd30ec-7b0e-477c-9d9d-123ea7f60e9b'),
  BACKEND_URL: getEnvValueOrFallback('BACKEND_URL', 'http://localhost:8080/api/'),
  REFRESH_INTERVAL: getEnvValueOrFallback('REFRESH_INTERVAL', 10000)
};
