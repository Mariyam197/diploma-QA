package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    private static Faker faker = new Faker(new Locale("en"));

    private DataHelper() {
    }

    @Value
    public static class CardInfo {
        String cardNumber;
        String month;
        String year;
        String cardHolder;
        String cvc;
    }

    private static String getApprovedCardNumber() {
        return "4444 4444 4444 4441";
    }

    private static String getDeclinedCardNumber() {
        return "4444 4444 4444 4442";
    }

    private static String getInvalidCardNumber() {
        return faker.number().digits(15);
    }

    private static String getMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
    }

    private static String getYear() {
        return LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("yy"));
    }

    private static String getCardHolder() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    private static String getCVC() {
        return faker.number().digits(3);
    }

    public static CardInfo getApprovedCard() {
        return new CardInfo(getApprovedCardNumber(), getMonth(), getYear(), getCardHolder(), getCVC());
    }

    public static CardInfo getDeclinedCard() {
        return new CardInfo(getDeclinedCardNumber(), getMonth(), getYear(), getCardHolder(), getCVC());
    }

    public static CardInfo getInvalidCardWithWrongFormatData() {
        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("M"));
        String year = faker.number().digits(1);
        String cvc = faker.number().digits(2);
        return new CardInfo(getInvalidCardNumber(), month, year, "№@%^", cvc);
    }

    public static CardInfo getEmptyForm() {
        return new CardInfo(" ", " ", " ", " ", " ");
    }

    public static CardInfo getApprovedCardWithWrongDate() {
        return new CardInfo(getApprovedCardNumber(), "13", "99", getCardHolder(), getCVC());
    }

    public static CardInfo getApprovedCardWithWrongYear() {
        String year = LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yy"));
        return new CardInfo(getApprovedCardNumber(), getMonth(), year, getCardHolder(), getCVC());
    }



}
