package dev.reed.core.streams.task1;

import dev.reed.core.streams.task1.entity.Customer;
import dev.reed.core.streams.task1.entity.Order;
import dev.reed.core.streams.task1.entity.PaymentInfo;
import dev.reed.core.streams.task1.entity.Product;
import dev.reed.core.streams.task1.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderStatsTest {

    private static final List<Customer> customers = TestUtils.generateCustomers();
    private static final List<Order> orders = TestUtils.generateOrders(10);
    private Stream<Customer> customerStream;
    private Stream<Order> orderStream;

    @BeforeEach
    public void setUp() {
        customerStream = customers.stream();
        orderStream = orders.stream();
    }

    @org.junit.jupiter.api.Order(1)
    @Test
    public void shouldReturnAllOrdersForVisaCardType() {
        final List<Order> visaOrders = OrderStats.ordersForCardType(customerStream, PaymentInfo.CardType.VISA);
        assertEquals(17, visaOrders.size(), "There are 17 orders payed with VISA card in this stream");
        assertEquals(24529, (long) visaOrders.get(4).getOrderId(), "Order #24529 was payed using VISA card");
    }

    @org.junit.jupiter.api.Order(2)
    @Test
    public void shouldReturnOrdersForVisaCardTypeForLimitedCustomers() {
        final List<Order> visaOrders = OrderStats.ordersForCardType(customerStream.limit(3), PaymentInfo.CardType.VISA);
        assertEquals(11, visaOrders.size(), "There are 11 orders payed with VISA card in this stream");
        assertEquals(47021, (long) visaOrders.get(9).getOrderId(), "Order #47021 was payed using VISA card");
    }

    @org.junit.jupiter.api.Order(3)
    @Test
    public void shouldReturnNoOrdersForEmptyCustomerStream() {
        final List<Order> visaOrders = OrderStats.ordersForCardType(Stream.empty(), PaymentInfo.CardType.MASTERCARD);
        assertEquals(0, visaOrders.size(), "There are not orders payed with VISA card in this stream");
    }

    @org.junit.jupiter.api.Order(4)
    @Test
    public void shouldCalculateSizeForAllOrders() {
        final Stream<Order> orders = orderStream;
        final Map<Integer, List<Order>> orderSizes = OrderStats.orderSizes(orders);
        assertEquals(3, orderSizes.get(15).size(), "There are 3 orders with size = 15 in this stream");
        assertEquals(108233, (long) orderSizes.get(21).get(0).getOrderId(), "Order #108233 has size = 21");
        assertNull(orderSizes.get(0), "There is no orders with size = 3 in this stream");
    }

    @org.junit.jupiter.api.Order(5)
    @Test
    public void shouldCalculateSizeForEmptyStream() {
        final Map<Integer, List<Order>> orderSizes = OrderStats.orderSizes(Stream.empty());
        assertEquals(0, orderSizes.size(), "Empty stream of order should produce empty map");
    }

    @org.junit.jupiter.api.Order(6)
    @Test
    public void shouldConfirmThatEachOrderHasRedProducts() {
        final Stream<Order> orders = orderStream.limit(2);
        final boolean hasColorProduct = OrderStats.hasColorProduct(orders, Product.Color.RED);
        assertTrue(hasColorProduct, "Each of the orders in this stream contains red product");
    }

    @org.junit.jupiter.api.Order(7)
    @Test
    public void shouldConfirmThatBlueProductsAreMissingInSomeOrders() {
        final Stream<Order> orders = orderStream.limit(4).skip(1);
        final boolean hasColorProduct = OrderStats.hasColorProduct(orders, Product.Color.BLUE);
        assertFalse(hasColorProduct, "One of the orders in this stream does not contains any blue products");
    }

    @org.junit.jupiter.api.Order(8)
    @Test
    public void shouldCountTotalNumberOfCardsForAllCustomers() {
        final Map<String, Long> cardsForCustomer = OrderStats.cardsCountForCustomer(customerStream);

        final long actual1 = cardsForCustomer.get("DonnaDonna@gmail.com");
        final long actual2 = cardsForCustomer.get("super-rory@tut.by");
        final long actual3 = cardsForCustomer.get("martha@mail.ru");
        final long actual4 = cardsForCustomer.get("john.smith@rambler.uk");
        final long actual5 = cardsForCustomer.get("r0se-tyler@gmail.com");

        assertEquals(2, actual1, "Donna was using 2 credit cards, not " + actual1);
        assertEquals(2, actual2, "Rory was using 2 credit cards, not " + actual2);
        assertEquals(3, actual3, "Martha was using 3 credit cards, not " + actual3);
        assertEquals(1, actual4, "John was using 1 credit card, not " + actual4);
        assertEquals(3, actual5, "Rory was using 3 credit cards, not" + actual5);

        final Map<String, Long> emptyMap = OrderStats.cardsCountForCustomer(Stream.empty());
        assertTrue(emptyMap.isEmpty());
    }

    @org.junit.jupiter.api.Order(9)
    @Test
    public void shouldFindTheMostPopularCountryWithinTheStreamOfCustomers() {
        final Optional<String> mostPopularCountry = OrderStats.mostPopularCountry(customerStream);
        assertEquals(Optional.of("Great Britain"), mostPopularCountry);
    }

    @org.junit.jupiter.api.Order(10)
    @Test
    public void shouldFindTheMostPopularCountryWithinTheLimitedStreamOfCustomers() {
        final Optional<String> mostPopularCountry = OrderStats.mostPopularCountry(customerStream.skip(2));
        assertEquals(Optional.of("USA"), mostPopularCountry);
    }

    @org.junit.jupiter.api.Order(11)
    @Test
    public void shouldReturnEmptyResultForEmptyStream() {
        final Optional<String> mostPopularCountry = OrderStats.mostPopularCountry(Stream.empty());
        assertEquals(Optional.empty(), mostPopularCountry);
    }

    @org.junit.jupiter.api.Order(12)
    @Test
    public void shouldCalculateAveragePriceForAllCustomersViaGivenCardNumberV1() {
        final String testCardNumber = "9785 5409 1111 5555";
        final BigDecimal avgPrice = OrderStats.averageProductPriceForCreditCard(customerStream, testCardNumber);
        assertEquals(495.83, avgPrice.setScale(2, RoundingMode.CEILING).doubleValue(), 0.01, "Invalid average product price for card " + testCardNumber);
    }

    @org.junit.jupiter.api.Order(13)
    @Test
    public void shouldCalculateAveragePriceForAllCustomersViaCardNumberV2() {
        final String testCardNumber = "4111 3456 5454 9900";
        final BigDecimal avgPrice = OrderStats.averageProductPriceForCreditCard(customerStream, testCardNumber);
        assertEquals(524.99, avgPrice.setScale(2, RoundingMode.CEILING).doubleValue(), 0.01, "Invalid average product price for card " + testCardNumber);
    }

    @org.junit.jupiter.api.Order(14)
    @Test
    public void shouldCalculateAveragePriceForAllCustomersViaCardNumberV3() {
        final String testCardNumber = "6677 5432 9587 1670";
        final BigDecimal avgPrice = OrderStats.averageProductPriceForCreditCard(customerStream, testCardNumber);
        assertEquals(505.64, avgPrice.setScale(2, RoundingMode.CEILING).doubleValue(), 0.01, "Invalid average product price for card " + testCardNumber);
    }

    @org.junit.jupiter.api.Order(15)
    @Test
    public void shouldNotCalculateAveragePriceForEmptyStreamOfCustomers() {
        final String testCardNumber = "9785 5409 1111 5555";
        final BigDecimal zeroPrice = OrderStats.averageProductPriceForCreditCard(Stream.empty(), testCardNumber);
        assertEquals(BigDecimal.ZERO, zeroPrice, "Average product price for empty stream of customers should be 0");
    }

    @org.junit.jupiter.api.Order(16)
    @Test
    public void shouldNotCalculateAveragePriceForInvalidCard() {
        final BigDecimal nonExistingCard = OrderStats.averageProductPriceForCreditCard(customerStream, "INVALID");
        assertEquals(BigDecimal.ZERO, nonExistingCard, "Average product price for non-existing card should be 0");
    }
}
