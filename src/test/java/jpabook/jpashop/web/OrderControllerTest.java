package jpabook.jpashop.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private ItemService itemService;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void createForm() throws Exception {
        //given
        Member member = createMember();
        List<Member> members = new ArrayList<>();
        members.add(member);

        Item book = createBook("jpa 책", 30000, 10000);
        List<Item> items = new ArrayList<>();
        items.add(book);

        given(memberService.findMembers()).willReturn(members);
        given(itemService.findItems()).willReturn(items);

        //when
        mvc.perform(get("/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(view().name("order/orderForm"))
                .andExpect(model().attributeExists("members")) // model에 "members" 라는 key가 존재하는지 확인
                .andExpect(model().attribute("members", IsCollectionWithSize.hasSize(1))) // jobs model의 size가 1인지 확인
                .andExpect(model().attributeExists("items")) // model에 "items" 라는 key가 존재하는지 확인
                .andExpect(model().attribute("items", IsCollectionWithSize.hasSize(1))) // jobs model의 size가 1인지 확인
                .andDo(MockMvcResultHandlers.print()); // test 응답 결과에 대한 모든 내용 출력
        //then
    }

    @Test
    public void order() throws Exception {
        //given

        //when
        mvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("memberId", "1")
                .param("itemId", "1")
                .param("count", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/orders")) // location이 "/" 인지 확인
                .andExpect(view().name("redirect:/orders"))
                .andDo(MockMvcResultHandlers.print()); // test 응답 결과에 대한 모든 내용 출력
        //then
    }

    @Test
    public void orderList() throws Exception {
        //given
        Member member = createMember();
        Item book = createBook("jpa 책", 30000, 10000);
        Order order = createOrder(member, book, 1);

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        given(orderService.findOrders(any(OrderSearch.class))).willReturn(orders);

        //when
        mvc.perform(get("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(view().name("order/orderList"))
                .andExpect(model().attributeExists("orders")) // model에 "orders" 라는 key가 존재하는지 확인
                .andExpect(model().attribute("orders", IsCollectionWithSize.hasSize(1))) // orders model의 size가 1인지 확인
                .andDo(MockMvcResultHandlers.print()); // test 응답 결과에 대한 모든 내용 출력
        //then
    }

    @Test
    public void cancelOrder() throws Exception {
        //given
        //when
        mvc.perform(post("/orders/1/cancel")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/orders")) // location이 "/" 인지 확인
                .andExpect(view().name("redirect:/orders"))
                .andDo(MockMvcResultHandlers.print()); // test 응답 결과에 대한 모든 내용 출력
        //then
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        return book;
    }

    private Order createOrder(Member member, Item item, int count) {
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        Order order = Order.createOrder(member, delivery, orderItem);

        return order;
    }
}