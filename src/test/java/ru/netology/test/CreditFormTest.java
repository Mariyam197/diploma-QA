package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import jdk.jfr.Name;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQL;
import ru.netology.page.PaymentMethod;

import static com.codeborne.selenide.Selenide.open;

public class CreditFormTest {
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
