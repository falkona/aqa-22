package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.conditions.ExactText;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CardDeliveryTest {

    private String baseUrl = "http://localhost:9999/";
    private int minimumDaysToDelivery = 3;
    private LocalDate earliestValidDate = LocalDate.now().plusDays(minimumDaysToDelivery);

    @Test
    void shouldBeSuccessAllValuesSetDateIsNearest () {
        open(getBaseUrl());

        $("[data-test-id=city] input").setValue("Волгоград");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").sendKeys(getEarliestValidDate());
        $("[data-test-id=name] input").setValue("Дарья");
        $("[data-test-id=phone] input").setValue("+79032596295");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        sleepSugar(2);
        SelenideElement notification = $("[data-test-id=notification]");
        $("[data-test-id=notification]").waitUntil(Condition.visible, 15000);
        $("[data-test-id=notification] .notification__title").should(Condition.exactText("Успешно!"));
        $("[data-test-id=notification] .notification__content").should(Condition.text("Встреча успешно забронирована на "));

    }

    @Test
    void shouldBeSuccessAllValuesSetDateIsLater () {
        open(getBaseUrl());

        $("[data-test-id=city] input").setValue("Краснодар");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").sendKeys(getLateDate(5));
        $("[data-test-id=name] input").setValue("Иванов Иван Иванович");
        $("[data-test-id=phone] input").setValue("+79032596200");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        sleepSugar(2);
        SelenideElement notification = $("[data-test-id=notification]");
        $("[data-test-id=notification]").waitUntil(Condition.visible, 15000);
        $("[data-test-id=notification] .notification__title").should(Condition.exactText("Успешно!"));
        $("[data-test-id=notification] .notification__content").should(Condition.text("Встреча успешно забронирована на "));
    }

    @Test
    void shouldBeUnavailableDeliveryIfCityIsInvalid (){
        open(getBaseUrl());

        $("[data-test-id=city] input").setValue("Урюпинск");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(getLateDate(1));
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

        $("[data-test-id=city] input").setValue("Воронеж");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(getLateDate(2));
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

        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(getLateDate(3));
        $("[data-test-id=name] input").setValue("Дарья");
        $("[data-test-id=phone] input").setValue("+790325962951");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        sleepSugar(2);
        $("[data-test-id=phone]").shouldHave(Condition.cssClass("input_invalid"));
        $("[data-test-id=phone] .input__sub").shouldBe(Condition.visible);
        $("[data-test-id=phone] .input__sub").shouldHave(Condition.exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldBeUnavailableDeliveryWithoutAgreement (){
        open(getBaseUrl());

        $("[data-test-id=city] input").setValue("Ростов-На-Дону");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(getLateDate(4));
        $("[data-test-id=name] input").setValue("Дарья");
        $("[data-test-id=phone] input").setValue("+79032596295");
        $$("button").find(Condition.exactText("Забронировать")).click();
        $("[data-test-id=agreement]").shouldHave(Condition.cssClass("input_invalid"));
    }

    @Test
    void shouldBeUnavailableDeliveryIfDateTooEarly (){
        open(getBaseUrl());

        $("[data-test-id=city] input").setValue("Ростов-на-Дону");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(getInvalidEarlyDate(1));
        $("[data-test-id=name] input").setValue("Дарья");
        $("[data-test-id=phone] input").setValue("+79032596295");
        $("[data-test-id=agreement]").click();
        $$("button").find(Condition.exactText("Забронировать")).click();
        sleepSugar(2);
        $("[data-test-id=date] .calendar-input__custom-control").shouldHave(Condition.cssClass("input_invalid"));
        $("[data-test-id=date] .input__sub").shouldBe(Condition.visible);
        $("[data-test-id=date] .input__sub").shouldHave(Condition.exactText("Заказ на выбранную дату невозможен"));
    }

    // возвращает ближайшую возможную дату доставки
    public String getEarliestValidDate() {
        return String.format("%02d.%02d.%d", earliestValidDate.getDayOfMonth(), earliestValidDate.getMonthValue(), earliestValidDate.getYear());
    }

    // возвращает дату на заданное количество дней раньше минимальной допустимой даты.
    public String getInvalidEarlyDate(long days) {
        LocalDate earlyDate = earliestValidDate.minusDays(days);
        return String.format("%02d.%02d.%d", earlyDate.getDayOfMonth(), earlyDate.getMonthValue(), earlyDate.getYear());
    }

    // возвращает дату на заданное количество дней позже минимальной допустимой даты.
    public String getLateDate(long days) {
        LocalDate lateDate = earliestValidDate.plusDays(days);
        return String.format("%02d.%02d.%d", lateDate.getDayOfMonth(), lateDate.getMonthValue(), lateDate.getYear());
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void sleepSugar(int sec) {
        try {
            Thread.sleep(sec*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getNearestAvailableDate () {
        LocalDate currentDate = LocalDate.now();
        return currentDate.toString();
    }

}
