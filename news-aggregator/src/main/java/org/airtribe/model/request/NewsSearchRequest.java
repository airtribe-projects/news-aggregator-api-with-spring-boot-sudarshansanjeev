package org.airtribe.model.request;

import java.util.List;
import java.util.Optional;

public class NewsSearchRequest {

    List<String> query;

    int pageSize;
    int page;

    public List<String> getQuery() {
        return query;
    }

    public void setQuery(List<String> query) {
        this.query = query;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "NewsSearchRequest{" +
                "query=" + query +
                ", pageSize=" + pageSize +
                ", page=" + page +
                '}';
    }
}
