package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "category_item")
@Getter
public class CategoryItem {

    @Id
    @GeneratedValue
    @Column(name = "category_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //카테고리 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categpry_id")
    private Category category; //카테고리
}
