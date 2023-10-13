declare global {
  interface Window {
    ENV: {
      SECURITY_X_API_KEY: string;
      BACKEND_URL: string;
      REFRESH_INTERVAL: string;
      CROSS_URL: string;
    };
  }
}
export {};