import { HttpErrorResponse, HttpInterceptorFn, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createErrorPayload } from '../utils';

const checkForXSS = (userInput: string): boolean => {
  const xssTags = [
    "<script>",
    "<img>",
    "<iframe>",
    "<a>",
    "<link>",
    "<style>",
    "<svg>",
    "<object>",
    "<embed>",
    "<form>",
    "<input>",
    "<textarea>",
    "<iframe>",
    "<meta>"
  ];
  return xssTags.some(tag => userInput.includes(tag));
}

const checkForSQLInjection = (userInput: string): boolean => {
  const sqlCheckList = ["--", ";--", "/*", "*/", "@@", "char", "nchar", "varchar", "nvarchar", "alter", "begin", "cast", "create", "cursor", "declare", "delete", "drop", "end", "exec", "execute", "fetch", "insert", "kill", "select", "sys", "sysobjects", "syscolumns", "table", "update"];
  return sqlCheckList.some(item => userInput.toLowerCase().includes(item));
}

export const inputSanitizerInterceptor: HttpInterceptorFn = (req, next) => {
  const input = JSON.stringify(req.body);
  if (input.length > 2 * 1024 * 1024) {
    return new Observable<HttpResponse<any>>(observer => {
      const errorPayload = createErrorPayload('Input Too Large', HttpStatusCode.PayloadTooLarge, 'Input Too Large', req.url);
      observer.error(new HttpErrorResponse({
        status: HttpStatusCode.PayloadTooLarge,
        statusText: 'Input Too Large',
        error: errorPayload
      }));
    });
  }

  if (checkForXSS(input) || checkForSQLInjection(input)) {
    return new Observable<HttpResponse<any>>(observer => {
      const errorPayload = createErrorPayload('Potentially Dangerous Input Detected', HttpStatusCode.BadRequest, 'Bad Request', req.url);
      observer.error(new HttpErrorResponse({
        status: HttpStatusCode.BadRequest,
        statusText: 'Potentially Dangerous Input Detected',
        error: errorPayload
      }));
    });
  }
  return next(req);
};
