import {NativeModules, NativeEventEmitter, EmitterSubscription} from 'react-native';

const eventType = '@BST/PING';

const BSTPing = NativeModules.BSTPing;
const eventEmitter = new NativeEventEmitter(BSTPing);

export type PingEvents = 'ICMP_PACKET' | 'STATISTIC' | 'SUMMARY' | 'UNKNOWN';
export type AllEvents = PingEvents | ('E_RUNTIME_EXCEPTION' | 'E_INVALID_PARAM' | 'PING_START' | 'PING_END');

export type ICMPPacket = {
  icmpSeq: number;
  ttl: number;
  time: number;
};

export type PingStatistic = {
  min: number;
  avg: number;
  max: number;
  mdev: number;
};

export type PingSummary = {
  transmitted: number;
  received: number;
  lostPercentage: number;
};

export type PingEvent = {
  type: AllEvents;
  raw?: string;
  icmpPacket?: ICMPPacket;
  statistic?: PingStatistic;
  summary?: PingSummary;
};

export function registerListener(listener: (event: PingEvent) => any): EmitterSubscription {
    return eventEmitter.addListener(eventType, listener);
}

export type PingParams = {
  durationSeconds?: number;
  reportIntervalSeconds?: number;
};

export async function startAsync(
  domainName: string,
  params?: PingParams,
  options?: {[name: string]: string},
): Promise<void> {
  await BSTPing.startAsync(domainName, params, options);
}

export async function stopAsync(): Promise<void> {
  await BSTPing.stopAsync();
}
