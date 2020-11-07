package controllers.reports;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsEditServlet
 */
@WebServlet("/reports/edit")
public class ReportsEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsEditServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        // 該当のIDの日報データ1件のみをデータベースから取得
        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id")));

        em.close();

        // 他者の日報を変更できてしまうと問題になるため、自分以外の日報のeditにアクセスした場合は「見つかりませんでした」を表示するようにしている。たとえばidが1の日報を作成したユーザ以外のアカウントで「http://localhost:8080/daily_report_system/reports/edit?id=1」にアクセスすると「お探しのデータは見つかりませんでした。」と表示される。
        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");
        if (r != null && login_employee.getId() == r.getEmployee().getId()) {
            // 日報データとセッションIDをリクエストスコープに登録
            request.setAttribute("report", r);
            request.setAttribute("_token", request.getSession().getId());

            // 日報IDをセッションスコープに登録
            request.getSession().setAttribute("report_id", r.getId());
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/edit.jsp");
        rd.forward(request, response);
    }

}
