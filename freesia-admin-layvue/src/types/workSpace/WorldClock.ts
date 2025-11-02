export interface Clock {
    title?: string;
    location: string;
    timezone: string;
    time: string;
    seconds: string;
    date: string;
    timer?: number;
    flag?: string;
    sunRiseTime?: Date,
    sunSetTime?: Date,
    sunriseTimeLocal?: Date,
    sunsetTimeLocal?: Date,
    dayLengthMinutes?: number,
}

export interface FindCitySunriseSunsetReqDto {
    id?: string;
    date?: Date
    cityNameList?: string[]
}

export interface FindCitySunriseSunsetEntity {
    cityId?: string;
    cityName?: string;
    timezone?: string;
    sunriseSunsetId?: string;
    date?: Date;
    sunriseTime?: Date;
    sunsetTime?: Date;
    sunriseTimeLocal?: Date;
    sunsetTimeLocal?: Date;
    dayLengthMinutes?: number;
}
