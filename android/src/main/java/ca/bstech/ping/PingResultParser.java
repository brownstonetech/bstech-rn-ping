package ca.bstech.ping;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.bstech.ping.Constants.ICMP_PACKET;
import static ca.bstech.ping.Constants.STATISTIC;
import static ca.bstech.ping.Constants.SUMMARY;
import static ca.bstech.ping.Constants.UNKNOWN;

public class PingResultParser implements Consumer<String> {

    private static Pattern icmpSeqPattern = Pattern.compile("icmp_seq=([\\d]+) ttl=([\\d]+) time=([\\d.]+) ms");
    private static Pattern summaryPattern = Pattern.compile("([\\d]+) packets transmitted, ([\\d]+) received, ([\\d.]+)% packet loss");
    private static Pattern statisticPattern = Pattern.compile("min/avg/max/mdev = ([\\d.]+)/([\\d.]+)/([\\d.]+)/([\\d.]+)");
    private static Pattern ignorePattern1 = Pattern.compile("PING .* bytes of data\\.");
    private static Pattern ignorePattern2 = Pattern.compile("--- .* ---");

    private Consumer<WritableMap> pingResultHandler;

    public PingResultParser(Consumer<WritableMap> pingResultHandler) {
        this.pingResultHandler = pingResultHandler;
    }

    @Override
    public void accept(String result) {
        if ( result.length() == 0 ) return;
        Matcher matcher = ignorePattern1.matcher(result);
        if ( matcher.find()) return;
        matcher = ignorePattern2.matcher(result);
        if ( matcher.find()) return;

        WritableMap map = Arguments.createMap();
        map.putString("raw", result);
        matcher = icmpSeqPattern.matcher(result);
        if ( matcher.find()) {
            map.putString("type", ICMP_PACKET);
            WritableMap parsed = Arguments.createMap();
            map.putMap("icmpPacket", parsed);
            parsed.putInt("icmpSeq", Integer.parseInt(matcher.group(1)));
            parsed.putInt("ttl", Integer.parseInt(matcher.group(2)));
            parsed.putDouble("time", Double.parseDouble(matcher.group(3)));
            pingResultHandler.accept(map);
            return;
        }
        matcher = statisticPattern.matcher(result);
        if ( matcher.find()) {
            map.putString("type", STATISTIC);
            WritableMap parsed = Arguments.createMap();
            map.putMap("statistic", parsed);
            parsed.putInt("min", Integer.parseInt(matcher.group(1)));
            parsed.putInt("avg", Integer.parseInt(matcher.group(2)));
            parsed.putInt("max", Integer.parseInt(matcher.group(3)));
            parsed.putInt("mdev", Integer.parseInt(matcher.group(4)));
            pingResultHandler.accept(map);
            return;
        }
        matcher = summaryPattern.matcher(result);
        if ( matcher.find()) {
            map.putString("type", SUMMARY);
            WritableMap parsed = Arguments.createMap();
            map.putMap("summary", parsed);
            parsed.putInt("transmitted", Integer.parseInt(matcher.group(1)));
            parsed.putInt("received", Integer.parseInt(matcher.group(2)));
            parsed.putDouble("lostPercentage", Double.parseDouble(matcher.group(3)));
            pingResultHandler.accept(map);
            return;
        }
        map.putString("type", UNKNOWN);
        pingResultHandler.accept(map);
    }

}
