package controllers.reports;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsCreateServlet
 */
@WebServlet("/reports/create")
public class ReportsCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsCreateServlet() {
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

            Report r = new Report();

            r.setEmployee((Employee)request.getSession().getAttribute("login_employee"));

            // 日付欄をわざと未入力にした場合、当日の日付を入れるようにしている。
            Date report_date = new Date(System.currentTimeMillis());
            String rd_str = request.getParameter("report_date");
            if (rd_str != null && !rd_str.equals("")) {
                report_date = Date.valueOf(request.getParameter("report_date"));
            }
            r.setReport_date(report_date);

            r.setTitle(request.getParameter("title"));
            r.setContent(request.getParameter("content"));

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            r.setCreated_at(currentTime);
            r.setUpdated_at(currentTime);

            // 初めに日報を投稿（提出）するときは、必ずapproval_flagが0であるため、未承認状態で登録される。
            r.setApproval_flag(Integer.parseInt(request.getParameter("approval_flag")));

            // バリデーションを実行してエラーがあったら新規登録のフォームに戻る。
            List<String> errors = ReportValidator.validate(r);
            if (errors.size() > 0) {
                // 入力内容にエラーがあったらそれを返す。
                em.close();

                // フォームに初期値を設定、さらにエラーメッセージを送る。
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/new.jsp");
                rd.forward(request, response);
            } else {
                // エラーがなければデータベースに保存する。
                em.getTransaction().begin();
                em.persist(r);    // データベースに保存
                em.getTransaction().commit();    // データの新規登録を確定
                em.close();

                request.getSession().setAttribute("flush", "登録が完了しました。");

                // indexページにリダイレクト
                response.sendRedirect(request.getContextPath() + "/reports/index");
            }
        }
    }

}
