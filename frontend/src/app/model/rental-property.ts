import { Location } from "./location";
import { PriceInterval } from "./intervals";

export interface RentalProperty {
    id: string,
    propertyName: string,
    location: Location,
    maxGuests: number,
    numBathrooms: number,
    propertyDescription: string,
    amount: number,
    priceIntervals: PriceInterval[],
}