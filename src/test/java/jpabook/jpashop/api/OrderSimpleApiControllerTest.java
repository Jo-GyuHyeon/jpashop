package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderRepository;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderSimpleApiController.class)
public class OrderSimpleApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderRepository orderRepository;

    @Test(expected = NestedServletException.class)
    public void ordersV1() throws Exception {
        //given
        Member member = createMember("회원1", "진주", "초전", "1234");
        Delivery delivery = createDelivery(member);
        Item book = createBook("jpa 책", 30000, 100);
        OrderItem orderItem1 = OrderItem.createOrderItem(book, 10000, 1);

        Order order = Order.createOrder(member, delivery, orderItem1);
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        given(orderRepository.findAllByString(any(OrderSearch.class))).willReturn(orders);
        //when
        mvc.perform(get("/api/v1/simple-orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(IsCollectionWithSize.hasSize(1)))
                .andExpect(jsonPath("$.member").value(member))
                .andExpect(jsonPath("$.delivery").value(delivery))
                .andDo(MockMvcResultHandlers.print());

        //then
        fail("양방향 순환 참조 발생");
    }

    @Test
    public void ordersV2() throws Exception {
        //given
        Member member = createMember("회원1", "진주", "초전", "1234");
        Delivery delivery = createDelivery(member);
        Item book = createBook("jpa 책", 30000, 100);
        OrderItem orderItem1 = OrderItem.createOrderItem(book, 10000, 1);

        Order order = Order.createOrder(member, delivery, orderItem1);
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        given(orderRepository.findAllByString(any(OrderSearch.class))).willReturn(orders);
        //when
        mvc.perform(get("/api/v2/simple-orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(IsCollectionWithSize.hasSize(1)))
                .andExpect(jsonPath("$[0]['name']").value(member.getName()))
                .andDo(MockMvcResultHandlers.print());

        //then
    }

    private Member createMember(String name, String city, String street, String zipcode) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        return book;
    }

    private Delivery createDelivery(Member member) {
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        return delivery;
    }

}