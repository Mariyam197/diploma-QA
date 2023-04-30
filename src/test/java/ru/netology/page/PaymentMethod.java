package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentMethod {
    private SelenideElement heading = $("[class='heading heading_size_l heading_theme_alfa-on-white']")
            .shouldHave(Condition.text("Путешествие дня"));
    private SelenideElement cardForm = $$("[class='button__content']")
            .findBy(Condition.exactText("Купить"));
    private SelenideElement creditForm = $$("[class='button__content']")
            .findBy(Condition.exactText("Купить в кредит"));

    public PaymentMethod() {
        heading.shouldBe(Condition.visible);
    }

    public BuyingFormByCard buttonByCard() {
        cardForm.click();
        return new BuyingFormByCard();
    }

    public BuyingFormByCredit buttonByCredit() {
        creditForm.click();
        return new BuyingFormByCredit();
    }
}
