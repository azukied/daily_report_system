package controllers.approval;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ApprovalServlet
 */
@WebServlet("/unapproved/reports/update")
public class UnapprovedReportsUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UnapprovedReportsUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        // リクエストスコープから日報のIDを取得して、該当のIDの日報データ1件のみをデータベースから取得
        Report r = em.find(Report.class, Integer.valueOf(request.getParameter("id")));

        // 未承認状態(0)から承認状態(1)へ
        r.setApproval_flag(1);

        em.getTransaction().begin();    // データベースから取得したデータに変更をかけてコミットすれば変更が反映されるので、em.persist(r);は不要
        em.getTransaction().commit();
        em.close();

        request.getSession().setAttribute("flush", "承認しました。");

        response.sendRedirect(request.getContextPath() + "/unapproved/reports/index");
    }

}
