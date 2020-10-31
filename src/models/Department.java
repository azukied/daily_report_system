package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Table(name = "departments")

@NamedQueries({
    @NamedQuery(
            // 全ての部署をデータベースから取得
            name = "getAllDepartments",
            query = "SELECT d FROM Department d"
            ),
    @NamedQuery(
            // setParameterでセットされた:nameとデータベースに保存されているd.nameが等しい部署データをデータベースから取得
            name = "getDepartment",
            query = "SELECT d FROM Department AS d WHERE d.name = :name"
            )
})

@Entity
public class Department {

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
