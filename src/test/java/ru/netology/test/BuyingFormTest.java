package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import jdk.jfr.Name;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQL;
import ru.netology.page.PaymentMethod;

import static com.codeborne.selenide.Selenide.open;


public class BuyingFormTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }


    @BeforeEach
    void setup() {
        open("http://localhost:8080/");
    }

    @AfterEach
     void cleanDataBase() {
        SQL.cleanDataBase();
    }



    @Test
    @Name("Успешная покупка тура по действующей карте" +
            "Проверка отображения транзакции в базе данных по действующей карте со статусом ОДОБРЕНО")
    void shouldBuyTourWithApprovedCard() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCard();
        payment.inputDataByCard(DataHelper.getApprovedCard());
        payment.waitSuccessfulNotificationForCard();
        Assertions.assertEquals("APPROVED", SQL.checkStatus());
    }

    @Test
    @Name("Отказ покупки по недействующей карте" +
            "Проверка отображения транзакции в базе данных по недействующей карте со статусом ОТКЛОНЕНО")
    void shouldNotBuyTourWithDeclinedCard() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCard();
        payment.inputDataByCard(DataHelper.getDeclinedCard());
        payment.waitErrorNotificationForCard();
        Assertions.assertEquals("DECLINED", SQL.checkStatus());
    }


    @Test
    @Name("Успешная покупка тура в кредит по действующей карте" +
            "Проверка отображения транзакции в базе данных по действующей карте в кредит со статусом ОДОБРЕНО")
    void shouldBuyTourByCreditWithApprovedCard() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCredit();
        payment.inputDataByCredit(DataHelper.getApprovedCard());
        payment.waitSuccessfulNotificationForCreditField();
        Assertions.assertEquals("APPROVED", SQL.checkStatusCredit());
    }


    @Test
    @Name("Отказ в покупке в кредит по недействующей карте" +
            "Проверка отображения транзакции в базе данных по недействующей карте в кредит со статусом ОТКЛОНЕНО")
    void shouldNotBuyTourByCreditWithDeclinedCard() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCredit();
        payment.inputDataByCredit(DataHelper.getDeclinedCard());
        payment.waitErrorNotificationForCreditField();
        Assertions.assertEquals("DECLINED", SQL.checkStatusCredit());
    }

    @Test
    @Name("Отказ в покупке по карте, отсутствующей в базе данных" +
            "Проверка отображения транзакции в базе данных по отсутствующей карте со статусом ОТКЛОНЕНО")
    void shouldNotBuyTourWithNonExistentCard() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCard();
        payment.inputDataByCard(DataHelper.getCardNotExistInDataBase());
        payment.waitErrorNotificationForCard();
        Assertions.assertEquals("DECLINED", SQL.checkStatus());
    }

    @Test
    @Name("Отказ в покупке по карте в кредит, отсутствующей в базе данных" +
            "Проверка отображения транзакции в базе данных по отсутствующей карте в кредит со статусом ОТКЛОНЕНО")
    void shouldNotBuyTourWithNonExistentCardByCredit() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCredit();
        payment.inputDataByCredit(DataHelper.getCardNotExistInDataBase());
        payment.waitErrorNotificationForCreditField();
        Assertions.assertEquals("DECLINED", SQL.checkStatus());
    }

    @Test
    @Name("Ввод невалидных данных в форму оплата по карте" +
            "Проверка отображения уведомления 'Неверный формат' под полями 'Номер карты', 'Месяц', 'Год', 'CVC' " +
            "и 'Поле обязательно для заполения' под полем Владелец")
    void shouldNotBuyTourWithInvalidCardWithWrongFormatData() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCard();
        payment.inputDataByCard(DataHelper.getInvalidCardWithWrongFormatData());
        payment.waitNotificationWrongFormat();
        payment.waitNotificationWrongFormatCardHolder();
    }

    @Test
    @Name("Пустые поля в форме оплата по карте, проверка отображения уведомлений о пустых полях")
    void shouldBeNotificationsDisplayedForAllFields() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCard();
        payment.inputDataByCard(DataHelper.getEmptyForm());
        payment.waitNotificationWrongFormat();
        payment.waitNotificationWrongFormatCardHolder();
    }

    @Test
    @Name("Проверка отображения уведомления о неверном сроке карты в поле покупка по карте")
    void shouldBeNotificationsAboutInvalidDateForCardField() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCard();
        payment.inputDataByCard(DataHelper.getApprovedCardWithWrongDate());
        payment.checkFieldMonthErrorWithInvalidValue();
        payment.checkFieldYearErrorWithInvalidValue();
    }

    @Test
    @Name("Проверка отображения уведомления о неверном сроке карты в поле месяц при покупке по карте")
    void shouldNotBuyTourWhenMonthIsZeroForCardForm() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCard();
        payment.inputDataByCard(DataHelper.getApprovedCardWithWrongMonth());
        payment.checkFieldMonthErrorWithInvalidValue();
    }

    @Test
    @Name("Проверка отображения уведомления об истекшем сроке карты в поле покупка по карте")
    void shouldBeNotificationsAboutWrongDateForCardField() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCard();
        payment.inputDataByCard(DataHelper.getApprovedCardWithWrongYear());
        payment.checkWrongYearError();
    }

    @Test
    @Name("Ввод невалидных данных в форму покупка в кредит" +
            "Проверка отображения уведомления 'Неверный формат' под полями 'Номер карты', 'Месяц', 'Год', 'CVC' " +
            "и 'Поле обязательно для заполения' под полем Владелец")
    void shouldNotBuyTourWithInvalidCardWithWrongFormatDataForCreditForm() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCredit();
        payment.inputDataByCredit(DataHelper.getInvalidCardWithWrongFormatData());
        payment.waitNotificationWrongFormatForCreditForm();
        payment.waitNotificationWrongFormatCardHolderForCreditForm();
    }

    @Test
    @Name("Пустые поля в форме покупка в кредит, проверка отображения уведомлений о пустых полях")
    void shouldBeNotificationsDisplayedForAllFieldsCreditForm() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCredit();
        payment.inputDataByCredit(DataHelper.getEmptyForm());
        payment.waitNotificationWrongFormatForCreditForm();
        payment.waitNotificationWrongFormatCardHolderForCreditForm();
    }

    @Test
    @Name("Проверка отображения уведомления о неверном сроке карты в поле покупка в кредит")
    void shouldBeNotificationsAboutInvalidDateForCreditField() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCredit();
        payment.inputDataByCredit(DataHelper.getApprovedCardWithWrongDate());
        payment.checkFieldMonthErrorWithInvalidValueForCreditForm();
        payment.checkFieldYearErrorWithInvalidValueForCreditForm();
    }

    @Test
    @Name("Проверка отображения уведомления об истекшем сроке карты в поле покупка в кредит")
    void shouldBeNotificationsAboutWrongDateForCreditField() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCredit();
        payment.inputDataByCredit(DataHelper.getApprovedCardWithWrongYear());
        payment.checkWrongYearErrorForCreditForm();
    }

    @Test
    @Name("Проверка отображения уведомления о неверном сроке карты в поле месяц при покупке в кредит")
    void shouldNotBuyTourWhenMonthIsZeroForCreditForm() {
        var PaymentMethod = new PaymentMethod();
        var payment = PaymentMethod.buttonByCredit();
        payment.inputDataByCredit(DataHelper.getApprovedCardWithWrongMonth());
        payment.checkFieldMonthErrorWithInvalidValueForCreditForm();
    }
}
