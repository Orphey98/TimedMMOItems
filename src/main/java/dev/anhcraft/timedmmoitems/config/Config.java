package dev.anhcraft.timedmmoitems.config;

import dev.anhcraft.config.annotations.Configurable;

@Configurable(keyNamingStyle = Configurable.NamingStyle.TRAIN_CASE)
public class Config {
    public String expiryPeriodFormat;
    public String expiryDateFormat;
    public String itemExpired;
    public String itemExpiredPlacement;
    public boolean removeExpiredItem;
    public String expiredItemRemoved;
    public UnitConfig unitFormat;
    public int itemCheckInterval;
    public boolean replaceExpiryPeriod;
}
