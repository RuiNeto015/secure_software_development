export const createErrorPayload = (message: string, status: number, error: string , path: string) => {
    return {
      timestamp: Date.now().toString(),
      status: status,
      error: error,
      message: message,
      path: path
    };
  };