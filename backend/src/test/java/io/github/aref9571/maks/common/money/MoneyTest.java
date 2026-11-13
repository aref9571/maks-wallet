package io.github.aref9571.maks.common.money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {

    @ParameterizedTest
    @CsvSource({
        "12, 1200",
        "12.5, 1250",
        "12.50, 1250",
        "0.01, 1"
    })
    void parsesValidAmountIntoCents(String text, long expectedMinorUnits) {
        Money money = Money.parse(text);

        assertThat(money.minorUnits()).isEqualTo(expectedMinorUnits);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "-1", "1.234", "1e3", "1,00", "12345678"})
    void rejectsInvalidAmount(String text) {
        assertThatThrownBy(() -> Money.parse(text))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0.00",
        "1, 0.01",
        "50, 0.50",
        "1200, 12.00",
        "1250, 12.50"
    })
    void formatsWithTwoDecimals(long minorUnits, String expected) {
        Money money = Money.ofMinor(minorUnits);

        assertThat(money.toDecimalString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "100, 250, 350",
        "0, 0, 0",
        "1, 99, 100"
    })
    void addsTwoAmounts(long first, long second, long expectedMinorUnits) {
        Money sum = Money.ofMinor(first).plus(Money.ofMinor(second));

        assertThat(sum).isEqualTo(Money.ofMinor(expectedMinorUnits));
    }

    @Test
    void failsLoudlyWhenAdditionOverflows() {
        Money largest = Money.ofMinor(Long.MAX_VALUE);

        assertThatThrownBy(() -> largest.plus(Money.ofMinor(1)))
            .isInstanceOf(ArithmeticException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "350, 100, 250",
        "100, 100, 0",
        "100, 250, -150"
    })
    void subtractsTwoAmounts(long first, long second, long expectedMinorUnits) {
        Money difference = Money.ofMinor(first).minus(Money.ofMinor(second));

        assertThat(difference).isEqualTo(Money.ofMinor(expectedMinorUnits));
    }

    @Test
    void failsLoudlyWhenSubtractionOverflows() {
        Money smallest = Money.ofMinor(Long.MIN_VALUE);

        assertThatThrownBy(() -> smallest.minus(Money.ofMinor(1)))
            .isInstanceOf(ArithmeticException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "-1, true",
        "0, false",
        "1, false"
    })
    void reportsNegativeAmounts(long minorUnits, boolean expected) {
        assertThat(Money.ofMinor(minorUnits).isNegative()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "-150, -1.50",
        "-5, -0.05",
        "-1000, -10.00"
    })
    void formatsNegativeAmountsWithSignInFront(long minorUnits, String expected) {
        assertThat(Money.ofMinor(minorUnits).toDecimalString()).isEqualTo(expected);
    }
}
