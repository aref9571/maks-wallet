package io.github.aref9571.maks.common.money;

public record Money(long minorUnits) {

    public static Money parse(String text) {
        if (!text.matches("\\d{1,7}(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException("Invalid input");
        }
        int dot = text.indexOf('.');
        if (dot == -1) {
            return new Money(Long.parseLong(text) * 100);
        }
        String centsText = text.substring(dot + 1);
        String eurosText = text.substring(0, dot);
        if (centsText.length() == 1) {
            centsText += "0";
        }
        return new Money(Long.parseLong(eurosText) * 100 + Long.parseLong(centsText));
    }

    public static Money ofMinor(long minorUnits) {
        return new Money(minorUnits);
    }

    public String toDecimalString() {
        long size = Math.absExact(minorUnits);
        long euros = size / 100;
        long cents = size % 100;
        String digits = String.format("%d.%02d", euros, cents);
        if (isNegative()) {
            return "-" + digits;
        }
        return digits;
    }

    public Money plus(Money other) {
        return new Money(Math.addExact(minorUnits, other.minorUnits));
    }

    public Money minus(Money other) {
        return new Money(Math.subtractExact(minorUnits, other.minorUnits));
    }

    public boolean isNegative() {
        return minorUnits < 0;
    }
}
