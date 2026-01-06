package ru.practicum.interaction.api.utility;

import java.math.BigDecimal;

public final class AppConstants {
    private AppConstants() {
    }

    public static final BigDecimal NDS_RATE = BigDecimal.valueOf(0.10);
    public static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};
    public static final BigDecimal BASE_DELIVERY_RATE = BigDecimal.valueOf(5.00);
    public static final BigDecimal WAREHOUSE_1_ADDRESS_MULTIPLIER = BigDecimal.valueOf(1.00);
    public static final BigDecimal WAREHOUSE_2_ADDRESS_MULTIPLIER = BigDecimal.valueOf(2.00);
    public static final BigDecimal FRAGILE_MULTIPLIER = BigDecimal.valueOf(0.20);
    public static final BigDecimal WEIGHT_MULTIPLIER = BigDecimal.valueOf(0.30);
    public static final BigDecimal VOLUME_MULTIPLIER = BigDecimal.valueOf(0.20);
    public static final BigDecimal STREET_MULTIPLIER = BigDecimal.valueOf(0.20);

    public static final int SCALE_WEIGHT = 3;
    public static final int SCALE_VOLUME = 3;
}
