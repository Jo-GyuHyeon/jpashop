package jpabook.jpashop.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.MemberService;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MemberApiController.class)
public class MemberApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberService memberService;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void saveMemberV1() throws Exception {
        //given
        Member member = createMember();
        String memberContent = mapper.writeValueAsString(member);
        Long memberId = 1L;

        given(memberService.join(any(Member.class))).willReturn(memberId);
        //when

        mvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(memberContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId))
                .andDo(MockMvcResultHandlers.print());
        //then
    }


    @Test
    public void saveMemberV2() throws Exception {
        //given
        MemberApiController.CreateMemberRequest createMemberRequest = new MemberApiController.CreateMemberRequest();
        createMemberRequest.setName("회원 1");
        String memberContent = mapper.writeValueAsString(createMemberRequest);
        Long memberId = 1L;

        given(memberService.join(any(Member.class))).willReturn(memberId);
        //when

        mvc.perform(post("/api/v2/members")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(memberContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId))
                .andDo(MockMvcResultHandlers.print());
        //then
    }

    @Test
    public void saveMemberV2ValidTest() throws Exception {
        //given
        MemberApiController.CreateMemberRequest createMemberRequest = new MemberApiController.CreateMemberRequest();
        String memberContent = mapper.writeValueAsString(createMemberRequest);
        Long memberId = 1L;

        given(memberService.join(any(Member.class))).willReturn(memberId);
        //when

        mvc.perform(post("/api/v2/members")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(memberContent))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
        //then
    }

    @Test
    public void updateMemberV2() throws Exception {
        //given
        MemberApiController.UpdateMemberRequest createMemberRequest = new MemberApiController.UpdateMemberRequest();
        createMemberRequest.setName("회원1");
        String memberContent = mapper.writeValueAsString(createMemberRequest);
        Long memberId = 1L;

        Member member = createMember();

        given(memberService.findOne(memberId)).willReturn(member);
        //when

        mvc.perform(put("/api/v2/members/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(memberContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andDo(MockMvcResultHandlers.print());
        //then
    }

    @Test
    public void membersV1() throws Exception {
        //given
        MemberApiController.UpdateMemberRequest createMemberRequest = new MemberApiController.UpdateMemberRequest();
        createMemberRequest.setName("회원1");
        String memberRequestContent = mapper.writeValueAsString(createMemberRequest);

        Member member = createMember();
        List<Member> members = new ArrayList<>();
        members.add(member);

        given(memberService.findMembers()).willReturn(members);
        //when

        mvc.perform(get("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(memberRequestContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(IsCollectionWithSize.hasSize(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void membersV2() throws Exception {
        //given
        MemberApiController.UpdateMemberRequest createMemberRequest = new MemberApiController.UpdateMemberRequest();
        createMemberRequest.setName("회원1");
        String memberContent = mapper.writeValueAsString(createMemberRequest);

        Member member = createMember();
        List<Member> members = new ArrayList<>();
        members.add(member);

        given(memberService.findMembers()).willReturn(members);
        //when

        mvc.perform(get("/api/v2/members")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(memberContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").value(IsCollectionWithSize.hasSize(1)))
                .andDo(MockMvcResultHandlers.print());
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