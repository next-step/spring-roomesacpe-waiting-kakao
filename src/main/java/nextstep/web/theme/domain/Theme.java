package nextstep.web.theme.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Theme {
    private Long id;
    private String name;
    private String desc;
    private int price;

    public Theme(String name, String desc, int price) {
        this.name = name;
        this.desc = desc;
        this.price = price;
    }
}