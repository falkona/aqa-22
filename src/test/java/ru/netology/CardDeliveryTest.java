package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.conditions.ExactText;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CardDeliveryTest {

    private String baseUrl = "http://localhost:9999/";

    @Test
    void shouldBeSuccessAllValuesSet () {
        open(getBaseUrl());

        $("[data-test-id=city] input").setValue("Волгоград");
        sleepSugar(2);
        $("[data-test-id=date] input").clear();
        $("[data-test-id=date] input").sendKeys("07.11.2019");
        sleepSugar(2);
        $("[data-test-id=name] input").setValue("Дарья");
        sleepSugar(2);
        $("[data-test-id=phone] input").setValue("+79032596295");
        sleepSugar(2);
        $("[data-test-id=agreement]").click();
        sleepSugar(2);
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
        sleepSugar(2);
        $("[data-test-id=date] input").clear();
        $("[data-test-id=date] input").setValue("07.11.2019");
        sleepSugar(2);
        $("[data-test-id=name] input").setValue("Дарья");
        sleepSugar(2);
        $("[data-test-id=phone] input").setValue("+79032596295");
        sleepSugar(2);
        $("[data-test-id=agreement]").click();
        sleepSugar(2);
        $$("button").find(Condition.exactText("Забронировать")).click();
        sleepSugar(2);
        $("[data-test-id=city] .input__sub").shouldBe(Condition.visible);
        $("[data-test-id=city] .input__sub").shouldHave(Condition.exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldBeUnavailableDeliveryIfNameHasWrongSymbols (){
        open(getBaseUrl());

        $("[data-test-id=city] input").setValue("Воронеж");
        sleepSugar(2);
        $("[data-test-id=date] input").clear();
        $("[data-test-id=date] input").setValue("07.11.2019");
        sleepSugar(2);
        $("[data-test-id=name] input").setValue("Дарья 2");
        sleepSugar(2);
        $("[data-test-id=phone] input").setValue("+79032596295");
        sleepSugar(2);
        $("[data-test-id=agreement]").click();
        sleepSugar(2);
        $$("button").find(Condition.exactText("Забронировать")).click();
        sleepSugar(2);
        $("[data-test-id=name] .input__sub").shouldBe(Condition.visible);
        $("[data-test-id=name] .input__sub").shouldHave(Condition.exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldBeUnavailableDeliveryIfPhoneIsTooLong (){
        open(getBaseUrl());

        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        sleepSugar(2);
        $("[data-test-id=date] input").clear();
        $("[data-test-id=date] input").setValue("10.11.2019");
        sleepSugar(2);
        $("[data-test-id=name] input").setValue("Дарья");
        sleepSugar(2);
        $("[data-test-id=phone] input").setValue("+790325962951");
        sleepSugar(2);
        $("[data-test-id=agreement]").click();
        sleepSugar(2);
        $$("button").find(Condition.exactText("Забронировать")).click();
        sleepSugar(2);
        $("[data-test-id=phone] .input__sub").shouldBe(Condition.visible);
        $("[data-test-id=phone] .input__sub").shouldHave(Condition.exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
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
