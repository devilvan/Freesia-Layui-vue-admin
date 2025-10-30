export interface Clock {
    location: string;
    timezone: string;
    time: string;
    seconds: string;
    date: string;
    timer?: number;
    flag?: string;
}