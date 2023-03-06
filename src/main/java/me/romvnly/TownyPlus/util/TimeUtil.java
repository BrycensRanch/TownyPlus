/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.util;

import java.util.Date;

public class TimeUtil {
    static long getCurrentTime() {
        return System.currentTimeMillis();
    }
     static String calculateTimeAgoByTimeGranularity(Date pastTime, TimeGranularity granularity) {
        long timeDifferenceInMillis = getCurrentTime() - pastTime.getTime();
        return timeDifferenceInMillis / granularity.toMillis() + " " +
                granularity.name().toLowerCase() + " ago";
    }
     public static String calculateHumanFriendlyTimeAgo(Date pastTime) {
        long timeDifferenceInMillis = getCurrentTime() - pastTime.getTime();
        if (timeDifferenceInMillis / TimeGranularity.DECADES.toMillis() > 0) {
            return "several decades ago";
        } else if (timeDifferenceInMillis / TimeGranularity.YEARS.toMillis() > 0) {
            return "several years ago";
        } else if (timeDifferenceInMillis / TimeGranularity.MONTHS.toMillis() > 0) {
            return "several months ago";
        } else if (timeDifferenceInMillis / TimeGranularity.WEEKS.toMillis() > 0) {
            return "several weeks ago";
        } else if (timeDifferenceInMillis / TimeGranularity.DAYS.toMillis() > 0) {
            return "several days ago";
        } else if (timeDifferenceInMillis / TimeGranularity.HOURS.toMillis() > 0) {
            return "several hours ago";
        } else if (timeDifferenceInMillis / TimeGranularity.MINUTES.toMillis() > 0) {
            return "several minutes ago";
        } else {
            return "moments ago";
        }
    }
}