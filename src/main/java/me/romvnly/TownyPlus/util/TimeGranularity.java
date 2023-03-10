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

import java.util.concurrent.TimeUnit;

public enum TimeGranularity {
    SECONDS {
        public long toMillis() {
            return TimeUnit.SECONDS.toMillis(1);
        }
    }, MINUTES {
        public long toMillis() {
            return TimeUnit.MINUTES.toMillis(1);
        }
    }, HOURS {
        public long toMillis() {
            return TimeUnit.HOURS.toMillis(1);
        }
    }, DAYS {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(1);
        }
    }, WEEKS {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(7);
        }
    }, MONTHS {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(30);
        }
    }, YEARS {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(365);
        }
    }, DECADES {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(365 * 10);
        }
    };

    public abstract long toMillis();
}
