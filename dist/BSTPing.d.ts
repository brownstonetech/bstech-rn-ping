import { EmitterSubscription } from 'react-native';
export declare type PingEvents = 'ICMP_PACKET' | 'STATISTIC' | 'SUMMARY' | 'UNKNOWN';
export declare type AllEvents = PingEvents | ('E_RUNTIME_EXCEPTION' | 'E_INVALID_PARAM' | 'PING_START' | 'PING_END');
export declare type ICMPPacket = {
    icmpSeq: number;
    ttl: number;
    time: number;
};
export declare type PingStatistic = {
    min: number;
    avg: number;
    max: number;
    mdev: number;
};
export declare type PingSummary = {
    transmitted: number;
    received: number;
    lostPercentage: number;
};
export declare type PingEvent = {
    type: AllEvents;
    raw?: string;
    icmpPacket?: ICMPPacket;
    statistic?: PingStatistic;
    summary?: PingSummary;
};
export declare function registerListener(listener: (event: PingEvent) => any): EmitterSubscription;
export declare type PingParams = {
    durationSeconds?: number;
    reportIntervalSeconds?: number;
};
export declare function startAsync(domainName: string, params?: PingParams, options?: {
    [name: string]: string;
}): Promise<void>;
export declare function stopAsync(): Promise<void>;
