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
 * Servlet implementation class EmployeesUpdateServlet
 */
@WebServlet("/employees/update")
public class EmployeesUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesUpdateServlet() {
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
            EntityManager em = DBUtil.createEntityManager();

            // セッションスコープから従業員のIDを取得して、該当のIDの従業員データ1件のみをデータベースから取得
            Employee e = em.find(Employee.class, (Integer)(request.getSession().getAttribute("employee_id")));

            // 現在の値と異なる社員番号が入力されていたら、重複チェックを行う指定をする。
            Boolean code_duplicate_check_flag = true;
            if (e.getCode().equals(request.getParameter("code"))) {
                code_duplicate_check_flag = false;
            } else {
                e.setCode(request.getParameter("code"));
            }

            // パスワード欄に入力があったら、パスワードの入力値チェックを行う指定をする。
            Boolean password_check_flag = true;
            String password = request.getParameter("password");
            if (password == null || password.equals("")) {
                password_check_flag = false;
            } else {
                e.setPassword(
                        EncryptUtil.getPasswordEncrypt(
                                password,
                                (String)this.getServletContext().getAttribute("pepper")
                        )
                );
            }

            // 以下、e.set～で、フォームから受け取ったデータで各フィールドを上書きする。
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

            e.setName(request.getParameter("name"));
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));
            e.setUpdated_at(new Timestamp(System.currentTimeMillis()));    // 更新日時のみ上書き
            e.setDelete_flag(0);

            // バリデーションを実行してエラーがあったら編集画面のフォームに戻る。
            List<String> errors = EmployeeValidator.validate(e, code_duplicate_check_flag, password_check_flag);
            if (errors.size() > 0) {
                // 入力内容にエラーがあったらそれを返す。
                em.close();

                // フォームに初期値を設定し、さらにエラーメッセージを送る。
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("employee", e);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
                rd.forward(request, response);
            } else {
                // エラーがなければデータベースを更新
                em.getTransaction().begin();    // データベースから取得したデータに変更をかけてコミットすれば変更が反映されるので、em.persist(e);は不要
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "更新が完了しました。");

                // セッションスコープ上の不要になったデータを削除
                request.getSession().removeAttribute("employee_id");

                // indexページへリダイレクト
                response.sendRedirect(request.getContextPath() + "/employees/index");
            }
        }
    }

}
