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
 * Servlet implementation class EmployeesNewServlet
 */
@WebServlet("/employees/new")
public class EmployeesNewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesNewServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // CSRF対策
        request.setAttribute("_token", request.getSession().getId());

        // おまじないとしてのインスタンスを生成
        request.setAttribute("employee", new Employee());

        EntityManager em = DBUtil.createEntityManager();

        // 全ての部署データをデータベースから取得して、リクエストスコープに登録
        List<Department> departments = em.createNamedQuery("getAllDepartments", Department.class)
                                          .getResultList();
        request.setAttribute("departments", departments);

        // 全ての役職データをデータベースから取得して、リクエストスコープに登録
        List<Authority> authorities = em.createNamedQuery("getAllAuthorities", Authority.class)
                                         .getResultList();
        request.setAttribute("authorities", authorities);

        em.close();

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/new.jsp");
        rd.forward(request, response);
    }

}
