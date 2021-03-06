package controllers.approval;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ApprovalIndexServlet
 */
@WebServlet("/unapproved/reports/index")
public class UnapprovedReportsIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UnapprovedReportsIndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        // ページネーション
        // 開くページ数を取得
        int page;

        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception e) {
            page = 1;
        }

        // 最大件数と開始位置を指定して未承認の日報データを取得
        List<Report> reports = em.createNamedQuery("getUnapprovedReports", Report.class)
                                  .setFirstResult(15 * (page - 1))
                                  .setMaxResults(15)
                                  .getResultList();

        // 未承認の日報の件数を取得
        long reports_count = (long)em.createNamedQuery("getUnapprovedReportsCount", Long.class)
                                       .getSingleResult();

        em.close();

        request.setAttribute("reports", reports);    // 全日報データ
        request.setAttribute("reports_count", reports_count);    // 全件数
        request.setAttribute("page", page);    // ページ数

        // フラッシュメッセージがセッションスコープにセットされていたら、リクエストスコープに保存（セッションスコープからは削除）
        if (request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/unapprovedReports/index.jsp");
        rd.forward(request, response);
    }

}
