package mybatis.query;

/**
 * Created by Administrator on 2017-6-23.
 */
public class QueryObject {
    private int currentPage=1;
    private int pageSize=10;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStart(){
        return (currentPage-1)*pageSize;
    }
}