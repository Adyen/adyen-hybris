declare var ACC: any;

export const urlContextPath: string = ACC.config.encodedContextPath

const rootElement = document.getElementById('root');
export const CSRFToken = rootElement.getAttribute('csrf-token');
