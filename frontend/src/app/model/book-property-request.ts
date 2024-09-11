import { TimeInterval } from "rxjs/internal/operators/timeInterval";
import { Interval } from "./intervals";

export interface BookPropertyRequest {
    accountId: string,
    propertyId: string,
    payment: any,
    intervalTime: Interval
}