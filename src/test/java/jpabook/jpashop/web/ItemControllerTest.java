package jpabook.jpashop.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
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

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void createForm() throws Exception {
        //given
        //when
        mvc.perform(get("/items/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(view().name("items/createItemForm"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        //then
    }

    @Test
    public void create() throws Exception {
        //given
        Item book = createBook("김영한의 jpa", 30000, 100);
        String bookContent = mapper.writeValueAsString(book);

        //when
        mvc.perform(post("/items/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(bookContent))
                .andExpect(header().string("Location", "/items"))
                .andExpect(view().name("redirect:/items"))
                .andExpect(status().is3xxRedirection())
                .andDo(MockMvcResultHandlers.print());
        //then
    }

    @Test
    public void list() throws Exception {
        //given
        List<Item> items = new ArrayList<>();
        Item book = createBook("김영한의 jpa", 30000, 100);
        items.add(book);

        given(itemService.findItems()).willReturn(items);
        //when
        mvc.perform(get("/items")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(view().name("items/itemList"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("items", IsCollectionWithSize.hasSize(1)))
                .andDo(MockMvcResultHandlers.print());
        //then
    }

    @Test
    public void updateItemForm() throws Exception {
        //given
        Item book = createBook("김영한의 jpa", 30000, 100);

        given(itemService.findOne(1L)).willReturn(book);
        //when
        mvc.perform(get("/items/1/edit")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(view().name("items/updateItemForm"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("form", hasProperty("name", is(book.getName()))))
                .andDo(MockMvcResultHandlers.print());
        //then
    }

    @Test
    public void updateItem() throws Exception {
        //given
        Item book = createBook("김영한의 jpa", 30000, 100);
        BookForm form = new BookForm();
        form.setName(book.getName());
        form.setPrice(book.getPrice());
        form.setStockQuantity(book.getStockQuantity());

        String formContent = mapper.writeValueAsString(form);

        //when
        mvc.perform(post("/items/1/edit")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(formContent))
                .andExpect(header().string("Location", "/items"))
                .andExpect(view().name("redirect:/items"))
                .andExpect(status().is3xxRedirection())
                .andDo(MockMvcResultHandlers.print());
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