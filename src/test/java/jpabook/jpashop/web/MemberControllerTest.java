package jpabook.jpashop.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberService memberService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createMember() throws Exception {
        //given
        Member member = new Member();
        member.setName("김영한");


        String memberContent = mapper.writeValueAsString(member);

        //when
        mvc.perform(post("/members/new")
                .content(memberContent)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(view().name("members/createMemberForm"))
                .andDo(MockMvcResultHandlers.print()); // test 응답 결과에 대한 모든 내용 출력
        //then
    }


    @Test
    public void memberList() throws Exception {
        //given
        Member member = new Member();
        member.setName("김영한");

        List<Member> members = new ArrayList<>();
        members.add(member);
        
        given(memberService.findMembers()).willReturn(members);

        //when
        mvc.perform(get("/members")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("members")) // model에 "jobs" 라는 key가 존재하는지 확인
                .andExpect(model().attribute("members", IsCollectionWithSize.hasSize(1))) // jobs model의 size가 1인지 확인
                .andDo(MockMvcResultHandlers.print()); // test 응답 결과에 대한 모든 내용 출력

        //then
    }
}