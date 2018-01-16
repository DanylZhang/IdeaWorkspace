package com.danyl;

import com.danyl.common.junit.SpringJunitTest;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.util.List;

public class TestSolr extends SpringJunitTest {
    @Test
    public void testSolr() throws SolrServerException {
        String baseUrl="http://192.168.1.16:8080/solr";
        SolrServer solrServer = new HttpSolrServer(baseUrl);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.add("q","title:*");
        QueryResponse query = solrServer.query(solrQuery);
        SolrDocumentList docs = query.getResults();
        for (SolrDocument doc : docs) {
            String id = (String) doc.get("id");
            System.out.println(id);
            List<String> title = (List<String>) doc.get("title");
            for (String s : title) {
                System.out.println(s);
            }
        }
    }
}