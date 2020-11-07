package controllers.employees;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Authority;
import models.Department;
import models.Employee;
import models.validators.EmployeeValidator;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class EmployeesCreateServlet
 */
@WebServlet("/employees/create")
public class EmployeesCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesCreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = (String)request.getParameter("_token");

        // CSRF対策のチェックを実行
        if (_token != null && _token.equals(request.getSession().getId())) {
            // new.jspのフォームから受け取ったデータをセットする。
            EntityManager em = DBUtil.createEntityManager();

            Employee e = new Employee();

            // フォームから受け取った部署名とデータベースに保存してある部署名が等しい部署データをデータベースから取得
            Department department = em.createNamedQuery("getDepartment", Department.class)
                                        .setParameter("name", request.getParameter("departmentName"))
                                        .getSingleResult();
            e.setDepartment(department);

            // フォームから受け取った役職名とデータベースに保存してある役職名が等しい役職データをデータベースから取得
            Authority authority = em.createNamedQuery("getAuthority", Authority.class)
                                      .setParameter("name", request.getParameter("authorityName"))
                                      .getSingleResult();
            e.setAuthority(authority);

            e.setCode(request.getParameter("code"));
            e.setName(request.getParameter("name"));
            e.setPassword(
                    EncryptUtil.getPasswordEncrypt(
                            request.getParameter("password"),
                            (String)this.getServletContext().getAttribute("pepper")
                    )
            );
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            e.setCreated_at(currentTime);
            e.setUpdated_at(currentTime);
            e.setDelete_flag(0);

            // バリデーションを実行してエラーがあったら新規登録のフォームに戻る。
            List<String> errors = EmployeeValidator.validate(e, true, true);
            if (errors.size() > 0) {
                // 入力内容にエラーがあったらそれを返す。
                em.close();

                // フォームに初期値を設定、さらにエラーメッセージを送る。
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("employee", e);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/new.jsp");
                rd.forward(request, response);
            } else {
                // エラーがなければデータベースに保存する。
                em.getTransaction().begin();
                em.persist(e);    // データベースに保存
                em.getTransaction().commit();    // データの新規登録を確定
                em.close();
                request.getSession().setAttribute("flush", "登録が完了しました。");

                // indexページにリダイレクト
                response.sendRedirect(request.getContextPath() + "/employees/index");
            }
        }
    }

}
