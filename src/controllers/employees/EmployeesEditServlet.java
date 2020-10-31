package controllers.employees;

import java.io.IOException;
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
import utils.DBUtil;

/**
 * Servlet implementation class EmployeesEditServlet
 */
@WebServlet("/employees/edit")
public class EmployeesEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesEditServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        // 該当のIDの従業員データ1件のみをデータベースから取得
        Employee e = em.find(Employee.class, Integer.parseInt(request.getParameter("id")));

        // 全ての部署データをデータベースから取得して、リクエストスコープに登録
        List<Department> departments = em.createNamedQuery("getAllDepartments", Department.class)
                                          .getResultList();
        request.setAttribute("departments", departments);

        // 全ての役職データをデータベースから取得して、リクエストスコープに登録
        List<Authority> authorities = em.createNamedQuery("getAllAuthorities", Authority.class)
                                         .getResultList();
        request.setAttribute("authorities", authorities);

        em.close();

        // 従業員データとセッションIDをリクエストスコープに登録
        request.setAttribute("employee", e);
        request.setAttribute("_token", request.getSession().getId());

        // 従業員IDをセッションスコープに登録
        request.getSession().setAttribute("employee_id", e.getId());

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
        rd.forward(request, response);
    }

}
