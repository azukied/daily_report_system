package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Table(name = "authorities")

@NamedQueries({
    // 全ての役職をデータベースから取得
    @NamedQuery(
            name = "getAllAuthorities",
            query = "SELECT a FROM Authority a"
            ),
    // setParameterでセットされた:nameとデータベースに保存されているa.nameが等しい役職データをデータベースから取得
    @NamedQuery(
            name = "getAuthority",
            query = "SELECT a FROM Authority AS a WHERE a.name = :name"
            )
})

@Entity
public class Authority {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
