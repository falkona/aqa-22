package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CardDeliveryTest {

    private String baseUrl = "http://localhost:9999/";
    private int minimumDaysToDelivery = 3;

    @Test
    void shouldBeSuccessAllValuesSetDateIsNearest () {
        open(getBaseUrl());

        String date = getFutureDate(minimumDaysToDelivery);

        $("[data-test-id=city] input").setValue("Волгоград");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").sendKeys(date);
        $("[data-test-id=name] input").setValue("Дарья");
        $("[data-test-id=phone] input").setValue("+79032596295");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        SelenideElement notification = $("[data-test-id=notification]");
        $("[data-test-id=notification]").waitUntil(Condition.visible, 15000);
        $("[data-test-id=notification] .notification__title").should(Condition.exactText("Успешно!"));
        $("[data-test-id=notification] .notification__content").should(Condition.exactText("Встреча успешно забронирована на " + date));
    }

    @Test
    void shouldBeSuccessAllValuesSetDateIsLater () {
        open(getBaseUrl());

        String date = getFutureDate(minimumDaysToDelivery + 2);

        $("[data-test-id=city] input").setValue("Краснодар");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").sendKeys(date);
        $("[data-test-id=name] input").setValue("Иванов Иван Иванович");
        $("[data-test-id=phone] input").setValue("+79032596200");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        SelenideElement notification = $("[data-test-id=notification]");
        $("[data-test-id=notification]").waitUntil(Condition.visible, 15000);
        $("[data-test-id=notification] .notification__title").should(Condition.exactText("Успешно!"));
        $("[data-test-id=notification] .notification__content").should(Condition.exactText("Встреча успешно забронирована на " + date));
    }

    @Test
    void shouldBeSuccessUsingPopupsThisMonth () {
        open(getBaseUrl());

        String date = getFutureDate(minimumDaysToDelivery + 3);

        $("[data-test-id=city] input").setValue("Кра");
        $$(".menu-item__control").findBy(Condition.exactText("Красноярск")).click();
        $("button .icon_name_calendar").click();
        $(".calendar__layout").waitUntil(Condition.visible, 2000);
        long timestamp = getTimeStampString(LocalDate.now().plusDays(minimumDaysToDelivery + 3));
        $(String.format("[data-day='%d000']", timestamp)).click();
        $("[data-test-id=name] input").setValue("Иванов Иван Иванович");
        $("[data-test-id=phone] input").setValue("+79032596200");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        SelenideElement notification = $("[data-test-id=notification]");
        $("[data-test-id=notification]").waitUntil(Condition.visible, 15000);
        $("[data-test-id=notification] .notification__title").should(Condition.exactText("Успешно!"));
        $("[data-test-id=notification] .notification__content").should(Condition.exactText("Встреча успешно забронирована на " + date));
    }

    @Test
    void shouldBeUnavailableDeliveryIfCityIsInvalid (){
        open(getBaseUrl());

        String date = getFutureDate(minimumDaysToDelivery + 1);

        $("[data-test-id=city] input").setValue("Урюпинск");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Дарья");
        $("[data-test-id=phone] input").setValue("+79032596295");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        $("[data-test-id=city]").shouldHave(Condition.cssClass("input_invalid"));
        $("[data-test-id=city] .input__sub").shouldBe(Condition.visible);
        $("[data-test-id=city] .input__sub").shouldHave(Condition.exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldBeUnavailableDeliveryIfNameHasWrongSymbols (){
        open(getBaseUrl());

        String date = getFutureDate(minimumDaysToDelivery + 2);

        $("[data-test-id=city] input").setValue("Воронеж");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Дарья 2");
        $("[data-test-id=phone] input").setValue("+79032596295");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        $("[data-test-id=name]").shouldHave(Condition.cssClass("input_invalid"));
        $("[data-test-id=name] .input__sub").shouldBe(Condition.visible);
        $("[data-test-id=name] .input__sub").shouldHave(Condition.exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldBeUnavailableDeliveryIfPhoneIsTooLong (){
        open(getBaseUrl());

        String date = getFutureDate(minimumDaysToDelivery + 3);

        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Дарья");
        $("[data-test-id=phone] input").setValue("+790325962951");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        $("[data-test-id=phone]").shouldHave(Condition.cssClass("input_invalid"));
        $("[data-test-id=phone] .input__sub").shouldBe(Condition.visible);
        $("[data-test-id=phone] .input__sub").shouldHave(Condition.exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldBeUnavailableDeliveryWithoutAgreement (){
        open(getBaseUrl());

        String date = getFutureDate(minimumDaysToDelivery + 4);

        $("[data-test-id=city] input").setValue("Ростов-На-Дону");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Дарья");
        $("[data-test-id=phone] input").setValue("+79032596295");
        $$("button").find(Condition.exactText("Забронировать")).click();
        $("[data-test-id=agreement]").shouldHave(Condition.cssClass("input_invalid"));
    }

    @Test
    void shouldBeUnavailableDeliveryIfDateTooEarly (){
        open(getBaseUrl());

        String date = getPastDate(minimumDaysToDelivery - 1);

        $("[data-test-id=city] input").setValue("Ростов-на-Дону");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Дарья");
        $("[data-test-id=phone] input").setValue("+79032596295");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        $("[data-test-id=date] .calendar-input__custom-control").shouldHave(Condition.cssClass("input_invalid"));
        $("[data-test-id=date] .input__sub").shouldBe(Condition.visible);
        $("[data-test-id=date] .input__sub").shouldHave(Condition.exactText("Заказ на выбранную дату невозможен"));
    }

    String getFutureDate(int daysToAdd) {
        LocalDate futureDate = LocalDate.now().plusDays(daysToAdd);
        return String.format("%02d.%02d.%d", futureDate.getDayOfMonth(), futureDate.getMonthValue(), futureDate.getYear());
    }

    String getPastDate(int daysToSubstract) {
        LocalDate pastDate = LocalDate.now().minusDays(daysToSubstract);
        return String.format("%02d.%02d.%d", pastDate.getDayOfMonth(), pastDate.getMonthValue(), pastDate.getYear());
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public long getTimeStampString(LocalDate date) {
        ZoneId zoneId = ZoneId.systemDefault();
        return date.atStartOfDay(zoneId).toEpochSecond();
    }
}
