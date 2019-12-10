package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
//Transactional 을 여기서 작성하여도 가능하다.
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    //엔티티 매니저를 통한 데이터 변경은 트렌젝션안에서 이루어져야 한다.
    @Transactional
    //데이터를 DB에 저장하고 싶다면 rollback을 false 하여야 한다.
    @Rollback(false)
    public void testMember() {
        //given
        Item item = new Book();
        item.setName("itemBook");

        //when
        itemRepository.save(item);
        Long savedId = 1L;
        Item findItem = itemRepository.findOne(savedId);

        //then
        Assertions.assertThat(findItem.getId()).isEqualTo(item.getId());
        Assertions.assertThat(findItem.getName()).isEqualTo(item.getName());
        Assertions.assertThat(findItem).isEqualTo(item); //JPA 엔티티 동일성 보장
    }

}